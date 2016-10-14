/*
 * Tool.java
 */
package org.ngbw.sdk.tool;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.WorkbenchException;
import org.ngbw.sdk.api.tool.CommandRenderer;
import org.ngbw.sdk.api.tool.FileHandler;
import org.ngbw.sdk.api.tool.ToolConfig;
import org.ngbw.sdk.api.tool.ToolIOMode;
import org.ngbw.sdk.api.tool.ToolRegistry;
import org.ngbw.sdk.api.tool.ToolResource;
import org.ngbw.sdk.api.tool.ToolResourceType;
import org.ngbw.sdk.common.util.StringUtils;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.database.RunningTask;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.TaskInputSourceDocument;
import org.ngbw.sdk.database.User;
import org.ngbw.pise.commandrenderer.GuiSimulator;



/**
 * @author Roland H. Niedner
 * @author Paul Hoover
 * @author Terri Schwartz
 *
 */
public class Tool {

	/**
	 *
	 */
	private static final Log log = LogFactory.getLog(Tool.class.getName());
	private final String toolId;
	private final ToolConfig toolConfig;
	private ToolResource toolResource;
	private ToolRegistry registry;
	private long userId;

	// properties we add to the command line when exec'ing the process worker
	public static final String SCHEDULER_PROPERTIES = "sProps";
	public static final String USER_NAME = "un";
	public static final String USER_EMAIL = "ue";
	public static final String USER_IP = "uip";
	public static final String TASK_ID = "taskId";
	public static final String JOB_HANDLE = "jobHandle";
	public static final String COMMAND_LINE = "command";
	public static final String WORKING_DIR = "workingDir";
	public static final String FAILED_DIR = "failedDir";
	public static final String ARCHIVE_DIR = "archiveDir";
	public static final String MANUAL_DIR = "manualDir";
	public static final String OUTPUT_DESCRIPTION = "outputDescr";
	public static final String URL = "url";
	public static final String RC = "rc";
	public static final String JOB_TIMEOUT = "timeout";
	public static final String IPLANT = "iplant";
	public static final String CHARGENUMBER = "chargeNumber";

	public static final String IPLANT_USER = "IPLANT";
	public static final String CIPRES_USER = "CIPRES";
	public static final String INDIVIDUAL_USER = "INDIVIDUAL";

	public Tool(String id, String resource, ToolRegistry registry)
	{
		this(id, registry);
		setToolResource(registry.getToolResource(resource));
	}

	public Tool(String id, ToolRegistry registry)
	{
		this.toolId = id;
		this.registry = registry;
		this.toolConfig = registry.getToolConfig(id);

		if (toolConfig == null)
			throw new NullPointerException("tool " + id + " is not registered!");

		this.toolResource = registry.getToolResource(toolConfig.getToolResourceId());
	}

	public String getToolId()
	{
		return toolId;
	}

	public CommandRenderer getCommandRenderer()
	{
		String className = toolConfig.getCommandRendererClassName();

		try {
			return (CommandRenderer) Class.forName(className).newInstance();
		}
		catch (InstantiationException e) {
			throw new RuntimeException("No argument constructor missing for " + className, e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException("Cannot access constructor for " + className, e);
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("ClassNotFoundException for " + className, e);
		}
	}


	public RenderedCommand renderCommand(Map<String, String> parameters)
	{
		CommandRenderer cr = getCommandRenderer(); 
		return cr.render(toolConfig.getConfigFileURL(), parameters);
	}


	public Map<String, String> prepareCommand(Map<String, String> parameters, Set<String> inputFileParams) throws Exception
	{
		GuiSimulator gs = new GuiSimulator();
		return gs.merge(parameters, inputFileParams, toolConfig.getConfigFileURL());
	}


	/*
		Throws JobValidationException if there's a problem with the parameters.
		Otherwise, returns the command line.

		Modifies parameter map, adding default params from the pise xml, where the don't
		conflict with existing entries.

		addDefaults: if true, adds default values for missing visible parameters and verifies
		preconds on all params.  Don't do this for the gui/portal2 jobs - they will fail, especially
		in case where tool gui is never opened, there will be preconds violated.
	*/
	public RenderedCommand validateCommand(Map<String, String> parameters, Set<String> inputFileParams)
	{
		CommandRenderer cr = getCommandRenderer(); 
		
		/*
			CommandRenderer.validate() calls CommandRenderer.render() which modifies the parameter 
			map, adding parameters like scheduler.conf, that shouldn't be stored in the task
			and passed to command render again, when we actually go to submit the job.
			So we need to give it a copy of the map, not the actual map that's passed to us.

			We need just the names of the input file parameters that the user entered to generate the 
			full command line.
		*/
		Map<String, String> parametersCopy = new HashMap<String, String>(parameters.size()); 
		parametersCopy.putAll(parameters);
		for (String param : inputFileParams)
		{
			parametersCopy.put(param, "");
		}
		RenderedCommand rc = cr.validate(toolConfig.getConfigFileURL(), parametersCopy);
		//return StringUtils.join(rc.getCommand(), " "); 
		return rc;
	}

	/*
		This just provides a very rough estimate of SUs so that we have
		some value to use for throttling of job submissions.  This will 
		overcharge when jobs run in shared queue on xsede.  It will also
		be wrong if chargeFactor should be something other than 1.0.
	*/
	public Long getPredictedSus(Properties p) throws Exception
	{
		Double hours = SchedulerProperties.getRunHours(p);
		if (hours == null)
		{
			/*
			throw new Exception("Scheduler.conf is missing runhours property.");
			*/
			return 0L;
		}
		Long nodes = SchedulerProperties.getNodes(p);
		Long threadsPerProcess = SchedulerProperties.getThreadsPerProcess(p);
		Long mpiProcesses = SchedulerProperties.getMpiProcesses(p);
		if (nodes == null && threadsPerProcess == null && mpiProcesses == null)
		{
			// On tscc, we have only runhours in scheduler.conf and all jobs are serial, on a single core.
			log.debug("PSU - this is a serial job, predicted sus will be runhours rounded to an int, min of 1");
			return JobCharge.calculateSUs(hours, 1, 1.0);
		}
		if (nodes == null)
		{
			nodes = 1L;
		}
		Long coresPerNode = toolResource.getCoresPerNode();
		if (coresPerNode == null)
		{
			throw new Exception("Scheduler.conf is missing or has invalid coresPerNode property.");
		}
		return JobCharge.calculateSUs(hours, (int)(coresPerNode * nodes), 1.0);
	}






	/*
		Returns:
			- null if this isn't a teragrid tool. Otherwise:
			- iplant charge number, if logged in as iplant
			- user's xsede charge nubmer if he has one, otherwise
			- cipres community account charge number 
		For this to work, in tool registry, xsede resources must have these parameters:
			- accountGroup = teragrid
			- chargeNumber = cipres.charge.number property
	*/
	public String getTgChargeNumber(boolean loggedInViaIplant, User user) throws Exception 
	{
		String chargeNumber= null;
		String accountGroup = toolResource.getAccountGroup();
		String iplantChargeNumber = (String)Workbench.getInstance().getProperties().get("iplant.charge.number");
		if (accountGroup != null)
		{
			if (loggedInViaIplant && accountGroup.equals(User.TERAGRID))
			{
				chargeNumber = iplantChargeNumber;
				log.debug("returning iplantChargeNumber " + chargeNumber);
			} else
			{
				chargeNumber = (user.getAccount(accountGroup));
				log.debug("returning user chargeNumber " + chargeNumber);
				if (chargeNumber == null)
				{
					chargeNumber = toolResource.getDefaultChargeNumber();
				}
			} 
		}
		return chargeNumber;
	}

	public boolean runsOnXSEDE()
	{
		String accountGroup = toolResource.getAccountGroup();
		return accountGroup != null && accountGroup.equals(User.TERAGRID);
	}

	public boolean usesCipresCommunityAccount(boolean loggedInViaIplant, User user) throws Exception
	{
		String cn = getTgChargeNumber(loggedInViaIplant, user);
		return (cn != null && cn.equals(toolResource.getDefaultChargeNumber()));
	}


	/*
		This method ignores the question of whether the tool is a teragrid tool or not
		and simply returns: 
			- IPLANT_USER if user logged in via iplant
			- CIPRES_USER if user doesn't have an individual allocation
			- INDIVIDUAL_USER if user has an individual allocation
	*/
	private String getUserType(boolean loggedInViaIPlant, User user) 
	{
		if (loggedInViaIPlant)
		{
			return IPLANT_USER;
		} 
		String chargeNumber = (user.getAccount(User.TERAGRID));
		if (chargeNumber != null)
		{
			return INDIVIDUAL_USER;
		}
		return CIPRES_USER;
	}


	public SourceDocumentType getSourceDocumentType(String parameter)
	{
		if (parameter == null)
			throw new NullPointerException("parameter must not be null!");

		SourceDocumentType type = toolConfig.getSourceDocumentType(parameter);

		if (type == null)
			type = SourceDocumentType.UNKNOWN;

		return type;
	}

	public ToolResource getToolResource()
	{
		return toolResource;
	}

	public void setToolResource(ToolResource tr)
	{
		this.toolResource = tr;
		// todo:
		// toolConfig.setToolResourceId(tr.getToolResourceId());
	}

	public String getToolResourceId()
	{
		return toolConfig.getToolResourceId();
	}

	public ToolConfig getToolConfig()
	{
		return toolConfig;
	}

	public URL getToolConfigFile()
	{
		return toolConfig.getConfigFileURL();
	}

	public ToolIOMode getInputMode(String parameter)
	{
		return toolConfig.getInputMode(parameter);
	}

	public Set<String> getInputParameters()
	{
		return toolConfig.getInputParameters().keySet();
	}

	public ToolIOMode getOutputMode(String parameter)
	{
		return toolConfig.getOutputMode(parameter);
	}

	public Set<String> getOutputParameters()
	{
		return toolConfig.getOutputParameters().keySet();
	}


	/*
		Todo: distinguish between permanently disabled tools (like those marked inactive in the registry) 
		and temporarily disabled.  Continue using disabledresource for temp conditions but throw a different
		error for permanent so client knows whether he should try again later or not.
	*/
	public String disabled()
	{
		// Set tools inactive flag in registry when we want to get rid of the tool for good, for all users.
		// Use a disabled resource file, when we need to temporarily prevent submissions.
		if (toolConfig.isActive() == false)
		{
			return "The specified tool, " + this.toolId + ", is not in the active tool registry." +
				"  Cipres no longer runs this tool.";
		}
		// See if the resource is disabled.
		String message = toolResource.disabled();
		if (message != null)
		{
			return message;
		}
		// Now see if the tool is disabled.
		return readDisabledResourceFile(this.toolId);
	}

	/*
		Find out if the tool is disabled by a particular category of user.
		userType is one of the constants IPLANT_USER, CIPRES_USER or INDIVIDUAL_USER.
		Returns null if the tool is NOT disabled, otherwise returns a string message to
		display.
	*/
	public String disabled(boolean loggedInViaIPlant, User user)
	{
		// First see if disabled for everyone
		String message = disabled();
		if (message != null)
		{
			return message;
		}

		// Now see if disabled for this type of user.
		String userType = getUserType(loggedInViaIPlant, user);
		String filename = this.toolId + "." + userType;
		return readDisabledResourceFile(this.toolId + "." + userType);

	}

	static String readDisabledResourceFile(String name)
	{
		String dpath = Workbench.getInstance().getProperties().getProperty("disabled.resources.file");
		if (dpath == null)
		{
			return null;
		}
		File f = new File(dpath, name);
		if (!f.exists())
		{
			return null;
		}
		String filename = f.getAbsolutePath();
		FileInputStream fs = null;
		try
		{
			fs = new FileInputStream(filename);
			Properties p = new Properties();
			p.load(fs);

			String start = p.getProperty("start");
			String end = p.getProperty("end");
			String message = p.getProperty("message");

			//log.debug("Loaded " + start + ", " + end + ", " + message);

			/*
				Set defaults such that if start date is missing or unparseable, disabled = true
				and if end date is missing or unparseable, disabled = true

				Dates are specified w/o times, so the effective time is the beginning
				of the day 00:00:00 = midnight.  If you expect the machine go be back
				up at 8am on 10/27/2009, you should give the end date as 10/28/2009.  

				If you check and see that the host is in fact back up at 8am you can remove
				the disabled_resource file.

				The purpose of these files is that when one of sees that a resource is going
				to be down on a certain date, we can create a disabled_resource file with a 
				start date that's a couple of days earlier than the planned outage (so user's
				won't submit jobs that will fail during the outage) and a end date that's a
				day after the outage, in case we forget to remove the file when the resource
				comes back up.
			*/
			if (start == null && end == null)
			{
				return message;
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

			long now = (new Date()).getTime();
			long startDate = now, endDate = now + (1000 * 60);
			if (start != null)
			{
				startDate = dateFormat.parse(start).getTime();
			}
			if (end != null)
			{
				endDate = dateFormat.parse(end).getTime();
			}
			log.debug("now=" + now  + ",start=" + startDate + ",end=" + endDate);
			if ((now >= startDate) && (now <= endDate))
			{
				log.debug("returning " + message);
				return message;
			}
			log.debug("returning " + null);
			return null;
		}
		catch(Exception t)
		{
			log.error("", t);
			return null;
		}
		finally
		{
			try { fs.close(); } catch (Exception t) {;}
		}
	}

	/*
		Handle PSUEDO resource type.  When registry specifies a psuedo resource, we pick the real
		resource to use after the command has been rendered and the scheduler properties determined).
	*/
	public void selectResource(RenderedCommand command) throws Exception
	{
		ToolResource resource = getToolResource();
		if (resource.getType() == ToolResourceType.PSUEDO)
		{
			Map<String,String> parameters = resource.getParameters();
			String resources = parameters.get("resources");

			String[] resourceArray = resources.split(",");
			List<String> resourceList = Arrays.asList(resourceArray);
			try 
			{
				String resourceID = hardcodedSelectResource( resourceList, command.getSchedulerProperties());  
				setToolResource(registry.getToolResource(resourceID));
			}
			catch (Exception e)
			{
				log.error("Error selecting tool resource for psuedo resource " + resource.getId());
				throw e;
			}
		} 
	}

	/**
		todo: add a PsuedoResourceHandler interface.  Add a PsuedoResourceClass parameter to
		the psuedo resources in the registry.  It would take the tool, the resources, and the
		scheduler.conf file as parameters and return the resource ID string of the real resource
		to run on.  I don't know, this needs some thought to avoid hardcoded job names and resource
		names.

		todo: create a scheduler config class with enums and methods for the property names and values.

		Instead, for the short run I'm hardcoding the logic for our tools on abe and trestles
		here.

	*/
	private String hardcodedSelectResource(List<String> resourceList, Properties schedulerProperties)
		throws DisabledResourceException
	{
		log.debug("tool is '" + getToolId() + "'");
		if (getToolId().startsWith("RAXMLHPC2"))
		{
			String jobType = schedulerProperties.getProperty("jobtype", "");
			log.debug("jobtype is '" + jobType + "'");
			if (jobType.equals("mpi"))
			{
				return "trestles";	
			} 
			return "abe";
		} 
		if (getToolId().startsWith("MRBAYES"))
		{
			String tmp = schedulerProperties.getProperty("mpi_processes");
			int ppn = new Integer(tmp);
			if (ppn > 6)
			{
				return "trestles";
			}
			return "abe";
		}
		// If a tool is configured on a PSUEDO resource, and we have no logic here for
		// choosing a real resource for that tool ...
		throw new DisabledResourceException("Cipres portal configuration error.  Missing information to " + 
			"select a resource for running " + getToolId() + " jobs.");
	}
}
