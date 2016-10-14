/*
 * TaskInitiate.java
 */
package org.ngbw.sdk.tool;


import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.WorkbenchException;
import org.ngbw.sdk.common.util.StringUtils;
import org.ngbw.sdk.core.configuration.ServiceFactory;
import org.ngbw.sdk.core.shared.TaskRunStage;
import org.ngbw.sdk.database.NotExistException;
import org.ngbw.sdk.database.RunningTask;
import org.ngbw.sdk.database.StatisticsEvent;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.TaskInputSourceDocument;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.UserDataItem;
import org.ngbw.sdk.common.util.BaseValidator;
import org.ngbw.sdk.common.util.SendError;


/**
 * 
 * @author Terri Liebowitz Schwartz
 */
public class TaskInitiate 
{
	private static final Log log = LogFactory.getLog(TaskInitiate.class);
	protected final ServiceFactory m_serviceFactory;
	private static int maxAttempts;

	static 
	{
		String tmp = Workbench.getInstance().getProperties().getProperty("submitJob.retries");
		maxAttempts = Integer.parseInt(tmp);
	}

	public TaskInitiate(ServiceFactory serviceFactory)
	{
		m_serviceFactory = serviceFactory;
	}

	/* 
		Note that throughout this code we set a local TaskRunStage variable to the stage 
		we are about to attempt.  That way if the operation throws an exception, the
		exception handler will use that stage in the error message.  If no exception
		occurs we store a success message with that stage after completing the operation.

		Task stage and messages are stored via TaskUpdater methods.  In some cases we
		let the RunningTask save the updated task so that both are updated in the same
		transaction.
	*/


	/*
		Web application calls this after saving Task in the db.  Application should 
		not modify the task ever again, once handing it off to queueTask. 
		However application can call Job.cancel which will delete the task.  
	*/
	public String queueTask(Task task, boolean loggedInViaIPlant, Long predictedSus) 
		throws DisabledResourceException, Exception
	{
		String toolId, jobHandle;
		TaskRunStage stage = task.getStage();
		RunningTask rt = null;

		try
		{
			if (!stage.equals(TaskRunStage.READY))
			{
				throw new WorkbenchException("Task is not in ready to run stage, stage is " + task.getStage());
			}
			// Check for disabled resource
			Tool tool = new Tool(task.getToolId(), m_serviceFactory.getToolRegistry());
			User user = new User(task.getUserId());
			String disabledMessage;
			if ((disabledMessage = tool.disabled(loggedInViaIPlant, user)) != null)
			{
				throw new DisabledResourceException(disabledMessage);
			}
			stage = TaskRunStage.QUEUE;

			// If front end didn't compute this, do it now.  REST does, portal2 doesn't.
			if (predictedSus == null)
			{
				predictedSus = predictSus(tool, task);
			}

			task.setJobHandle(getNewJobHandle(task.getToolId()));
			rt = WorkQueue.postWork(task, tool, tool.getTgChargeNumber(loggedInViaIPlant, user), predictedSus);


			log.debug("RunningTask " + rt.getJobhandle() + " created.  Task is Queued for submission. " +
				"Check submitJobD, checkJobD and loadResultsD logs for further status of this job.");
			return rt.getJobhandle();
		}
		catch (DisabledResourceException dre)
		{
			/*
				- Portal User can submit the job again later.
				- REST users will get an Error return, not a jobhandle, and although
				the task is stored in our db, it won't be returned when they list their submitted jobs
				since it's stage is READY which is < SUBMITTED.
			*/
			throw dre;
		}
		catch(Exception e)
		{
			//log.error("", e);
			TaskUpdater.logFatal(task, stage, e); 
			throw e;
		}
	}

	public Long predictSus(Tool tool, Task task)
	{
		try
		{
			RenderedCommand rc = tool.validateCommand(task.toolParameters(), task.input().keySet());
			Long l = tool.getPredictedSus(rc.getSchedulerProperties());
			return l;
		}
		catch(Exception e)
		{
			log.error("", e);
			log.debug("Returning null for predicted sus");
			return null;
		}
	}


	/*
		These pass data between runTask() and it's helper methods.
	*/
	Task task = null;
	Tool tool = null;
	BaseProcessWorker pw = null;
	TaskRunStage stage;
	Map<String, String> parameters;
	RenderedCommand command;
	RunningTask rt = null;
	User user = null;

	/*
		submitJobD calls this.
		We don't have to worry about user not existing because we never delete user accounts, 
			just mark them inactive.
		RunningTask.status = NEW when this is called.   

		If submitJobD crashes after incrementing attempts, it will process the same job again when it's 
		restarted up to maxAttempts (see cipres-*.properties, submitJob.retries).
		If we haven't staged the working dir yet, it can process the job.  Once the working dir exists, we won't try
		to stage/submit again.  At the moment submitJobD handles all runningtasks with attempts >=0 , we don't
		have a recoverSubmitJobD.

		TODO: split submitJob and loadResults into separate process for each remote resource so one messed up
		resource can't hurt the others?

		TODO: should check tool.disabled again.
	*/
	public boolean runTask(RunningTask runningTask) 
		throws DisabledResourceException, Exception
	{
		boolean emailErrorMessage = false;
		rt = runningTask;
		stage = TaskRunStage.INPUTCHECK;

		try
		{
			task = rt.getTask();
			if (task == null)
			{
				throw new Exception("Task has been deleted.");
			}

			if (!WorkQueue.tryAgain(rt, RunningTask.STATUS_NEW, maxAttempts))
			{
				throw new FatalTaskException("Failed to submit job to remote host because the limit of " +
					maxAttempts + " retries has been exceeded.");
					
			}

			// Todo: whenever we're dealing with a running task record, create Tool using the resource,
			// it specifies in case the registry has been changed and specifies that the tool runs
			// on a different resource now.
			tool = new Tool(task.getToolId(), rt.getResource(),  m_serviceFactory.getToolRegistry());
			user = new User(rt.getUserId());

			/*
				Todo: if _JOBINFO.TXT has a remotejobid, just use that jobId and skip stageAndRun. 
			*/
			if (tool.getToolResource().workingDirectoryExists(rt.getJobhandle()))
			{
				// For now though, just do this to make sure we don't accidentally start job more than once in the same directory. 
				throw new Exception("Job working directory already exists.  This means there was a network or similar error, or a CIPRES process " +
					" was restarted while the job was being submitted.  There are no automatic retries in this situation." ); 
			}

			stage = TaskRunStage.COMMANDRENDERING;
			command = validateAndRender();
			String msg = "Command rendered successfully: " + StringUtils.join(command.getCommand(), ' ');
			log.debug(msg);
			TaskUpdater.logProgress(task, stage, msg); 
			WorkQueue.updateWork(rt, tool, command); // add info to WorkQueue and statistics records.

			// 1. This is the only place where we could do retries, anything else would be a fatal error.
			// 2. If an exception occurs here, email cipresadmin.
			emailErrorMessage = true;
			String jobId = stageAndRun(); 
			emailErrorMessage = false;

			WorkQueue.submitted(rt, jobId);
			return true;

		}
		catch (Exception e)
		{
			if (task == null)
			{
				log.debug("Task has been deleted.");
				WorkQueue.fail(rt, stage, e.getMessage());
				return false;
			}
			log.error("", e);
			if (emailErrorMessage == true)
			{
				// email error message to cipresadmin
				SendError.send("Job submission error: " +  e.toString());
			}
			WorkQueue.fail(rt, stage, e.toString());
			TaskMonitor.notifyJobComplete(task, "Error submitting job: " +  e.getMessage());
			throw e;
		}
	}


	private RenderedCommand validateAndRender() throws Exception
	{
		/*
			For each InFile parameter, this gives us a map of parameter name to taskInputSourceDocumentID 
			In theory this could create brand new sourceDocuments by converting format of supplied document, 
			or whatever.  
		*/
		parameters = convertTaskInput();

		/*
			renderCommand adds non input file parameters, i.e. task.toolParameters(), to "parameters", then 
			calls piseCommandRenderer.render, which also modifies "parameters".  For example, it adds a dummy 
			parameter for each param file that the pisexml generates.
		*/
		RenderedCommand command = renderCommand();
		return command;
	}



	/*
		Returns the remote jobId if any.
	*/
	public String stageAndRun() throws Exception
	{
		stage = TaskRunStage.INPUTSTAGING;
		String jobId = null;

		try
		{
			TaskUpdater.logProgress(task, stage, "Staging input files to " + rt.getResource()); 
			String workingDir = stageInputData();

			StatisticsEvent se = new StatisticsEvent(rt.getJobhandle());
			se.setEventName("DATA_STAGED");
			se.setEventValue("");
			se.save();

			String msg = "Input files staged successfully to " + workingDir;
			log.debug(msg);
			TaskUpdater.logProgress(task, stage, msg); 

			stage = TaskRunStage.SUBMITTING;
			pw = tool.getToolResource().getProcessWorker(rt);

			return pw.submit();
		}
		catch (Exception e)
		{
			if (pw != null)
			{
				pw.moveToFailedDirectory();
			}
			throw e;
		}
	}

	/*
		Can throw JobValidationException (a type of RuntimeException)
	*/
	private RenderedCommand renderCommand() throws IOException, SQLException
	{
		// iterate over the task's non-file parameters and add  them to the file parameters.
		for (Iterator<Map.Entry<String, String>> toolParams = task.toolParameters().entrySet().iterator() ; toolParams.hasNext() ; ) 
		{
			Map.Entry<String, String> param = toolParams.next();
			parameters.put(param.getKey(), param.getValue());
		}
		// Pass all the parameters for which the user has supplied data to the command renderer.
		return tool.renderCommand(parameters);
	}

	/*
	 */
	private String stageInputData() throws IOException, SQLException, Exception
	{
		// This is a map of  parameter name to filename (to be created in the working directory)
		Map<String, String> paramNameToFileName = command.getInputFileMap();

		// This will be a map of filename to the file's contents (usually it's a taskInputSourceDocument ID)
		Map<String, String> fileNameToContents= new TreeMap<String, String>();

		// Build the map of parameter name to file content.
		for (Iterator<Map.Entry<String, String>> names = paramNameToFileName.entrySet().iterator() ; names.hasNext() ; ) 
		{
			Map.Entry<String, String> entry = names.next();
			String parameter = entry.getKey();
			String fileName = entry.getValue();

			if (fileName == null)
			{
				throw new NullPointerException("Filename for parameter " + parameter + " is null!");
			}

			// The value of the InFile parameter is the contents of the file OR the source document ID, prefixed
			// with "TaskInputSourceDocument " (see TaskInitiate.convertTaskInput()).
			String value = parameters.get(parameter);
			if (value == null)
			{
				throw new NullPointerException("Value for parameter " + parameter + " is null!");
			}

			// If there was no filename attribute in the pise for this InFile (see PiseCommandRenderer.java)
			// filename will be empty string (see PiseCommandRenderer.java).  In this case we will use the
			// SourceDocument's label as the filename, if possible.  
			if (fileName.equals(""))
			{
				fileName = getSourceDocumentLabel(value);
				if (fileName == null)
				{
					throw new NullPointerException("User data item for parameter " + parameter + " not found." +
						" This can happen if you deleted the data or if the data has a label that contains" +
						"characters other than ascii letters, numbers, period and underscore.");
				}
			}

			// Update the filename in the map we're iterating over. 
			paramNameToFileName.put(parameter, fileName);

			// Put the content or source doc ID in our filename <-> content map
			fileNameToContents.put(fileName, value);
		}

		// Add a file to the list of those to be staged. File is for debugging.
		fileNameToContents.put("_COMMANDLINE", (StringUtils.join(command.getCommand(), " ")));
		String workingDir = tool.getToolResource().getWorkingDirectory(task.getJobHandle());


		// We're using DefaultToolResource to stageInput and to submitJob.  TODO: Maybe we should also use it to cancelJob?
		// DefaultToolResource of course knows how to interpret the content, either as actual content or a source doc ID.
		tool.getToolResource().stageInput(workingDir, fileNameToContents);

		return workingDir;
	}

	private String getSourceDocumentLabel(String identifier)
	{
		if (identifier.startsWith("TaskInputSourceDocument "))
		{
			identifier = identifier.replaceAll("TaskInputSourceDocument ", "");
			try
			{
				long docid = Long.valueOf(identifier);
				log.debug("Task Input Source Document ID is " + docid);
				
				TaskInputSourceDocument sd = new TaskInputSourceDocument(docid);

				/*
					Note that there are TaskInputSourceDocuments in the db, where the name field
					is not numeric (for instance it may be "infile").  I'm not sure how those
					records were created.  When we select a user data item for a task, we set
					the TaskInputSourceDocument.name = UserDataItem.getUserDataId(), which is the key,
					USERDATA_ID, for the UserDataItem.
				*/
				String docId = sd.getName();
				log.debug("Task Input Source Document name is " + docId);

				Long udId = Long.valueOf(docId);
				log.debug("Corresponding userDataItem ID=" + docId);
				UserDataItem udi = new UserDataItem(udId);

				String label = udi.getLabel();
				log.debug("Label, to be used as filename in working dir, is: " + label);

				if (label != null)
				{
					label = label.trim();
					if (BaseValidator.isSimpleFilename(label))
					{
						return label;
					}
					log.warn("Label '" + label + "' is not an acceptable filename.  Filenames are restricted due to security concerns.");
				}
			}
			catch(Exception e)
			{
				log.warn("Trying to find UserDataItem for the TaskInputSourceDocument with id=" + identifier + ". " + e.toString());
				log.debug("This can happen if the UserDataItem has been deleted.");
			}
		} else
		{
			log.warn("This parameter doesn't have a corresponding SourceDocument");
		}
		return null;
	}

	/**
	 	The task.input() has a map with one entry for each parameter that corresponds to a file to be staged.
		In other words, each parameter in the tool xml that is of type "InFile" and specifies a filename
		with a <filename> element.  task.input() is a map of parameter name -> list of TaskInputSourceDocuments.  This method 
		returns a map of parameter name -> a string that identifies the TaskInputSourceDocument in the database.  

		We need to modify the UI so that user can only choose a single source document for each parameter. This method
		will ignore all but the first.
	 */
	private Map<String, String> convertTaskInput() 
		throws IOException, SQLException, ParseException
	{
		String jobHandle = task.getJobHandle();
		Map<String, String> params = new TreeMap<String, String>();

		//task.input() returns a map of parameter name (with trailing "_") to a list of TaskInputSourceDocument records.
		Map<String, List<TaskInputSourceDocument>> inputData = task.input();

		log.debug(jobHandle + ": processing " + inputData.size() + " input parameter collection(s).");

		//Iterate over inputData, the map of param name to list of TaskInputSourceDocuments	
		for (Iterator<Map.Entry<String, List<TaskInputSourceDocument>>> inputParams = inputData.entrySet().iterator() ; 
			inputParams.hasNext() ; ) 
		{
			Map.Entry<String, List<TaskInputSourceDocument>> paramEntry = inputParams.next();

			// param: This is the parameter name
			String param = paramEntry.getKey();

			// srcDocumentList: This is the list of source documents for the current parameter.
			List<TaskInputSourceDocument> srcDocumentList = paramEntry.getValue();

			// This is the first one.
			TaskInputSourceDocument document = srcDocumentList.get(0);

			String documentID = "";
			if (document != null)
			{
				documentID = "TaskInputSourceDocument " + document.getInputDocumentId();
			}
			params.put(param, documentID);
			log.debug(jobHandle + ": For parameter " + param + " added document: " + documentID); 
		}
		return params;
	}



	// protected methods


	/**
	 * 
	 * @param toolId
	 * @return
	 */
	protected String getNewJobHandle(String toolId)
	{
		String uuid = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");

		return "NGBW-JOB-" + toolId + "-" + uuid;
	}
}


