/**
 * 
 */
package org.ngbw.sdk.common.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.dataresources.lucene.RecordSourceFilter;

/**
 * @author hannes
 *
 */
public class SimpleFilenameFilter implements FilenameFilter {

	private static Log log = LogFactory.getLog(RecordSourceFilter.class);
	private final Pattern pattern;
	private final File currentDir;
	
	
	/**
	 * Constructor for an all pass filter
	 */
	public SimpleFilenameFilter() {
		this.currentDir = null;
		this.pattern = null;
	}
	
	/**
	 * Constructor for a directory independent FilenameFilter.
	 * The Filter will only evaluate the filename and not
	 * the current directory.
	 * 
	 * @param fileNamePattern
	 */
	public SimpleFilenameFilter(String fileNamePattern) {
		this(null, fileNamePattern);
	}
	
	/**
	 * Constructor for a directory dependent FilenameFilter.
	 * The Filter will only evaluate the filename if
	 * the current directory equals the submitted the currentDir
	 * argument. If the directories are Not equal all files
	 * are rejected.
	 * 
	 * @param currentDir
	 * @param fileNamePattern
	 */
	public SimpleFilenameFilter(File currentDir, String fileNamePattern) {
		this.currentDir = currentDir;
		if (fileNamePattern == null || fileNamePattern.trim().length() > 0) {
			pattern = null;
		} else {
			//translate shell wildcards into propper regex
			//escape a dot
			fileNamePattern = fileNamePattern.replaceAll("\\.", "\\.");
			//a * is replaced by .*
			fileNamePattern = fileNamePattern.replaceAll("\\*", "\\.\\*");
			if (log.isDebugEnabled()) log.debug("All file that match this regex will pass: " + fileNamePattern);
			this.pattern = Pattern.compile(fileNamePattern);
		}
	}

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File dir, String name) {
		boolean pass = false;
		if (currentDir != null && dir != null && currentDir.equals(dir) == false)
			//if the current directory is set then it is required to match
			//the submitted dir argument
			pass = false;
		if (pattern == null)
			//null gives automatic pass for all
			pass = true;
		else
			pass = pattern.matcher(name).find();
		if (log.isDebugEnabled() && pass == false) log.debug(name + " filtered out");
		return pass;
	}

}
