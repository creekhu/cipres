/**
 * 
 */
package org.ngbw.sdk.api.tool;

import java.io.IOException;
import java.util.Map;
import org.ngbw.sdk.database.RunningTask;
import org.ngbw.sdk.tool.BaseProcessWorker;

import org.ngbw.sdk.api.core.Configurable;

/**
 * The ToolResource interface describes a physical computing resource
 * that processes tasks.
 * 
 * <br />
 * @author hannes
 */
public interface ToolResource extends Configurable<Map<String, String>> {
	
	/**
	 * @return id
	 */
	public String getId();
	/**
	 * @return type
	 */
	public ToolResourceType getType();

	public void setType(ToolResourceType type);
	public void setType(String type);

	/**
	 * @return parameters
	 */
	public Map<String, String> getParameters();

	public String getAccountGroup();
	public String getDefaultChargeNumber();

	/**
	 * Returns ready to run configured instance of the registered FileHandler.
	 * 
	 * @param userId of logged in user, ie., current workbench session
	 * @return fileHandler
	 */
	public FileHandler getFileHandler(long userId);

	/**
	 * Returns ready to run configured instance of the registered FileHandler.
	 * 
	 * Normally you should use getFileHandler(long userID).  This version w/o the
	 * userId is only used when performing an operation that isn't on behalf of a user
	 * but is done for the whole application, such as creating system directories (that
	 * will be used on behalf of multiple users).
	 *
	 * @return fileHandler
	 */
	public FileHandler getFileHandler();

	public String getFileHandlerName();

	/**
	 * Set the fully qualified class name of the registered FileHandler.
	 * 
	 * @param fileHandlerClass
	 */
	public void setFileHandlerClass(Class<FileHandler> fileHandlerClass);

	/**
	 * Returns the fully qualified class name of the registered process worker.
	 * 
	 * @return class name of the process worker
	 */
	public String getProcessWorkerName();

	/**
	 * Set the fully qualified class name of the registered process worker.
	 * 
	 * @param processWorkerName
	 */
	public void setProcessWorkerName(String processWorkerName);

	/**
		Find out if the resource has been disabled.  
		return a string explaining why it was disabled, etc, or null, if not disabled.
	*/
	public String disabled();

	public String getWorkspace();
	public void setWorkspace(String ws);

	public String getWorkingDirectory(String jobHandle);
	public String getArchiveDirectory(String jobHandle);
	public String getFailedDirectory(String jobHandle);
	public String getManualDirectory(String jobHandle);
	public boolean workingDirectoryExists(String jobHandle) throws Exception;
	public BaseProcessWorker getProcessWorker(RunningTask rt) throws Exception;
	public BaseProcessWorker getProcessWorker(String jh) throws Exception;
	public void stageInput(String wd, Map<String, String> inputData) throws IOException, Exception;

	public Long getCoresPerNode();
}
