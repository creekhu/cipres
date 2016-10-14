package org.ngbw.sdk.api.tool;

import java.util.List;

/**
 * @author Roland H. Niedner <br />
 * 
 * A ProcessResult specifies the access to the Runtime process parameters
 * of a wrapped process.
 *
 */
public interface ProcessResult {

	/**
	 * Method returns the command processed.
	 * 
	 * @return command
	 */
	public String getCommand();

	/**
	 * Method sets the command processed.
	 * 
	 * @param command
	 */
	public void setCommand(String command);

	/**
	 * Method returns the captured STDOUT messages.
	 * 
	 * @return STDOUT
	 */
	public String getSTDOUT();

	/**
	 * Method captures STDOUT messages.
	 * 
	 * @param out
	 */
	public void addToSTDOUT(String out);

	/**
	 * Method returns the captured STDERR messages.
	 * 
	 * @return STDERR
	 */
	public String getSTDERR();

	/**
	 * Method captures STDERR messages.
	 * 
	 * @param err
	 */
	public void addToSTDERR(String err);

	/**
	 * Method returns the exitValue of the process.
	 * 
	 * @return exitValue
	 */
	public Integer getExitValue();

	/**
	 * Method sets the exitValue of the process.
	 * 
	 * @param exitValue
	 */
	public void setExitValue(Integer exitValue);

	/**
	 * Method allows to add a message to the message list.
	 * 
	 * @param message
	 */
	public void addMessage(String message);

	/**
	 * Method retrieves the list of programmatically 
	 * (produced by the java wrapper and NOT by the wrapped process)
	 * issued messages.
	 * 
	 * @return messages
	 */
	public List<String> getMessages();

	/**
	 * Method retrieves all programmatically 
	 * (produced by the java wrapper and NOT by the wrapped process)
	 * issued messages in one string.
	 * 
	 * @return messages
	 */
	public String getMessagesString();

}