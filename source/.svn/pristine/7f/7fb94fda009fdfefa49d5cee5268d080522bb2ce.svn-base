package org.ngbw.sdk.api.tool;


import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.tool.RenderedCommand;


/**
 * @author Roland H. Niedner <br />
 * 
 * A CommandRenderer implements the functionality to transform client
 * submitted tool parameters, input data and the tool into a commandline,
 * ready to be issued in a execution path registered in the ToolService 
 * for the respective Tool (typically a command for the Unix shell).
 * The implementation also needs to provide a Map of all required 
 * input files and all produced output files - where the filename
 * is keyed to the respective tool parameter name. 
 *
 */
public interface CommandRenderer {

	/**
	 * Render the commandline, and populate the in/outputFileMaps
	 * using the submitted command description
	 * like for example a pise xml file, and the parameter map that
	 * includes the filenames of all staged files for this command.
	 * All parameter filenames are keyed to their parameter.
	 * The rendered command and the in/outputFileMaps are returned 
	 * in the RenderedCommand bean.
	 * 
	 * @param url - url of the tool config file
	 * @param parameters - small values keyed to parameters
	 * @return renderedCommand
	 */
	public RenderedCommand render(URL url, Map<String, String> parameters);
	public RenderedCommand validate(URL url, Map<String, String> parameters);

}
