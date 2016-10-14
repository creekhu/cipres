/*
 * Job.java
 */
package org.ngbw.sdk.jobs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;

import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.Statistics;
import org.ngbw.sdk.database.Folder;
import org.ngbw.sdk.database.TaskInputSourceDocument;
import org.ngbw.sdk.database.TaskOutputSourceDocument;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.UserDataItem;
import org.ngbw.sdk.tool.RenderedCommand;
import org.ngbw.sdk.tool.TaskMonitor;
import org.ngbw.sdk.tool.Tool;
import org.ngbw.sdk.ValidationException;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.WorkbenchException;
import org.ngbw.sdk.core.shared.TaskRunStage;
import org.ngbw.sdk.tool.TaskValidator;
import org.ngbw.sdk.api.tool.FieldError;
import org.ngbw.sdk.common.util.SuperString;


/**
 * @author Terri Liebowitz Schwartz
 *
 */
public class Job
{
	/*
		Notes to self:

		- purpose of this class is to a) submit a job, b) check the status of a job, c) list a
		job's intermediate dir and return contents of intermediate dir files, d) list a job's result
		files and return their contents.

		- All methods have a User parameter.
			- Submit creates the job in a hardcoded folder, owned by the user.  Folder will
			be created if it doesn't already exists.
			- All methods that pertain to a specific job, verify that the job is owned by the User
			and throw a UserMismatchException if not.

		TODO:
		- see questions below about FileInputStream cleanup on exception.  Test this out.
		- extend this to return all info that's stored in the task.
	*/
	private static final String DEFAULT_FOLDER = "restJobs";
	private static final Log log = LogFactory.getLog(Job.class.getName());

	/*
		Caller responsible for closing the inputstream
	*/
	public static UserDataItem createUserDataItem(User user, InputStream is, String label) throws Exception
	{
		UserDataItem udi = new UserDataItem(defaultFolder(user));
		udi.setData(is);
		udi.setLabel(label);
		udi.save();
		return udi;
	}

	public static UserDataItem createUserDataItem(User user, InputStream is, String label, String folderLabel) throws Exception
	{
		Folder folder = user.getHomeFolder().findOrCreateSubFolder(folderLabel);
		UserDataItem udi = new UserDataItem(folder);
		udi.setData(is);
		udi.setLabel(label);
		udi.save();
		return udi;
	}

	/*
		Submits the job and returns a jobHandle that uniquely identifies the job.

		visibleParameterMap has name/value pairs for parameters that can be set by an end user.

		inputFileMap maps parameter_name to UserDataItem object.  In the case of the REST
		API, the files were uploaded as part of the submission request and stored with UserDataItem records
		in the cipres db.

		toolId must not be null.

		If validateOnly is true, just validates the parameters, does not submit the job, doesn't
		store a task.  Returns generated command line. Throws ValidationException if errors are found.

		There are 2 sets of validation performed.  First is performed by TaskValidator using generated
		code.  Since we can't easily generate code to test perl expressions, controls and preconds are
		checked differently.  For these we call Tool.validateCommand which instantiates a CommandRenderer
		which parses the pise xml file and runs the perl interpreter.  The Tool.validateCommand checks
		will be repeated later when submitJobD creates a CommandRenderer to generate the command line and
		submit the job.

	*/
	public static String submit(
					User user,
					Application application,
					JobMetadata clientMetadata,
					String toolId,
					Map<String, List<String>> visibleParameterMap,
					Map<String, UserDataItem> inputFileMap,
					boolean validateOnly) throws Exception
	{
		if (visibleParameterMap == null)
		{
			visibleParameterMap = new LinkedHashMap<String, List<String>>();
		}
		if (inputFileMap  == null)
		{
			inputFileMap = new LinkedHashMap<String, UserDataItem>();
		}
		if (application == null)
		{
			throw new WorkbenchException("Missing 'application' - job submission must specify name of submitting application.");
		}

		/*
			Throws an exception if tool isn't in registry.
			TaskValidator loads a tool specific validator class that was generated from the pisexml, which:
				- Checks the datatype of non-string parameters (eg ints, longs) and does range checking
				when <max> and <min> elements are used.

				- Does not check preconds and ctrls because I didn't want to generate java code from the perl precond and ctrl
				expressions.

				- Converts the List<String> values to simple String values.  There is only one pise
				element, List, where there may be multiple values and the generated code (see pise2validationJava.ftl)
				concatenates them.

				- Checks that required parameters are present and that we don't receive any unexpected parameters.
		*/
		TaskValidator validator = new TaskValidator(toolId);
		Map<String, String> toolParameters = validator.validate(visibleParameterMap, inputFileMap.keySet());
		List<FieldError> errors = validator.getErrors();
		if (errors.size() > 0)
		{
			throw new ValidationException(errors);
		}
		Tool tool = new Tool(toolId, Workbench.getInstance().getServiceFactory().getToolRegistry());

		/*
			tool.prepareCommand uses pise GuiSimulator to merge default values from the pise
			file with user supplied values, check preconds and controls.  May throw JobValidationException.
			Needs list of input file parameters that were supplied by the user to validate preconds
			and controls; doesn't look at the contents of the files.

			Note that toolParameters may be modified upon return, to include needed default values.
			We'll save this modified map in the task.

			Command Renderer will be instantiated again and validation repeated by submitJobD when it
			generates the command line to submit the job.  If we didn't want to duplicate the
			effort we'd have to either postpone the validation until submitJobD or store lots of stuff
			in the db that we don't currently store.  (Basically, serialize the CommandRenderer object)
		*/
		toolParameters = tool.prepareCommand(toolParameters, inputFileMap.keySet());

		RenderedCommand rc = tool.validateCommand(toolParameters, inputFileMap.keySet());
		String commandline = rc.getCommandAsString();
		if (validateOnly)
		{
			return commandline;
		}

		Folder enclosingFolder = defaultFolder(user);
		Task task = new Task(enclosingFolder);
		long taskId = 0;
		/*
			task.input() requires a hashmap where instead of the File we received in inputFileMap,
			we have a List of TaskInputSourceDocuments.  We create the TaskInputSourceDocuments by
			opening a FileInputStream on the File, then wrap the document in a list.  The list
			will always have just the single entry.
		*/
		HashMap<String, List<TaskInputSourceDocument>> inputMap = new HashMap<String, List<TaskInputSourceDocument>>();
		for (String k: inputFileMap.keySet())
		{
			UserDataItem udi = inputFileMap.get(k);
			TaskInputSourceDocument doc = new TaskInputSourceDocument(udi);

			// This lets us find the UserDataItem from the TaskInputSourceDocument.  We may need to get the UserDataItem's label
			// in TaskInitiate.stageInputData().
			doc.setName(Long.toString(udi.getUserDataId()));

			ArrayList<TaskInputSourceDocument> list = new ArrayList<TaskInputSourceDocument>();
			list.add(doc);

			inputMap.put(k, list);
		}
		task.input().putAll(inputMap);
		task.toolParameters().putAll(toolParameters);
		task.setToolId(toolId);
		task.setLabel("REST v1");
		task.setStage(TaskRunStage.READY);
		task.setAppname(application.getName());

		if (clientMetadata != null && !clientMetadata.isEmpty())
		{
			task.properties().putAll(clientMetadata.toMap());
		}
		task.save();

		Long predictedSus = tool.getPredictedSus(rc.getSchedulerProperties());
		Workbench.getInstance().submitTask(task, predictedSus);

		return task.getJobHandle();
	}

	private static Folder defaultFolder(User user) throws Exception
	{
		String folderLabel = DEFAULT_FOLDER;
		Folder folder = user.getHomeFolder().findOrCreateSubFolder(folderLabel);
		return folder;
	}

/*
	public static JobStatus getStatus(String jobHandle, User user) throws Exception
	{
		return new JobStatus(jobHandle, user);
	}
*/

	public static JobOutput getOutput(String jobHandle, User user, Application application) throws Exception
	{
		return new JobOutput(jobHandle, user, application);
	}


	public static JobWorkingDir getWorkingDir(String jobHandle, User user, Application application) throws Exception
	{
		return new JobWorkingDir(jobHandle, user, application);
	}

	/*
		This can take a long time to run!  Be careful about how you expose it
		in an application.
	*/
	public static void cancelAll(User user, Application application) throws Exception
	{
		List<Task> tasks = user.findTasks();
		for (Task task : tasks)
		{
			try
			{
				log.debug("Request to cancel " + task.getJobHandle());
				if (application == null || application.getName().equals(task.getAppname()))
				{
					cancel(task.getJobHandle(), user, application);
				} else
				{
					log.warn("Calling application " + application.getName() + " doesn't own the job.  Owner is " + task.getAppname());
				}
			}
			// Don't let a problem deleting one prevent us from trying to do the rest.
			catch(Exception e)
			{
				log.error("", e);
			}
		}
	}

	public static void cancel(String jobHandle, User user, Application application) throws Exception
	{
		log.debug("Cancelling " + jobHandle);
		JobStatus status = new JobStatus(jobHandle, user, application);
		Task task = status.getTask();

		// This sends "qdel" or similar command to remote host where job is queued or running.
		TaskMonitor.cancelJob(task);

		// This removes the task from our db and prevents loadResults from trying to transfer
		// any files back from the working directory.
		task.delete();
	}

	public static List<String> getJobList(User user) throws Exception
	{
		ArrayList<String> retval = new ArrayList<String>();
		List<Task> list = user.findTasks();
		for (Task t : list)
		{
			retval.add(t.getJobHandle());
		}
		return retval;
	}

	public static List<String> getSubmittedJobList(User user, Application application) throws Exception
	{
		ArrayList<String> retval = new ArrayList<String>();
		List<Task> list = user.findTasks();
		for (Task t : list)
		{
			if (!t.getStage().equals(TaskRunStage.NEW) && !t.getStage().equals(TaskRunStage.READY))
			{
				if (application == null || application.getName().equals(t.getAppname()))
				{
					retval.add(t.getJobHandle());
				}
			}
		}
		return retval;
	}

}
