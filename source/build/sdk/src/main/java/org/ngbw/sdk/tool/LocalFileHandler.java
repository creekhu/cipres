package org.ngbw.sdk.tool;

import java.util.Date;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ngbw.sdk.api.tool.FileHandler;

public class LocalFileHandler implements FileHandler {

	private static final Log log = LogFactory.getLog(LocalFileHandler.class);

	public void close() {;}


	public boolean configure(Map<String, String> cfg) {
		//TODO well there might be things to add like workspaces ect.
		return isConfigured();
	}

	public boolean isConfigured() {
		return true;
	}

	public boolean exists(final String path)
	{
		return (new File(path)).exists();
	}

	public void createDirectory(String directory) {
		if (directory == null)
			throw new NullPointerException("Directory cannot be null!");
		File dir = new File(directory);
		if (dir.mkdir() == false)
		{
			log.debug("Unable to create directory " + dir.getAbsolutePath());
			//throw new RuntimeException("The submitted 'directory' could not be created!");
			throw new RuntimeException("LocalFileHandler was unable to create:'" + dir.getAbsolutePath() + "'");
		}
	}

	public void removeDirectory(String directory, boolean deleteContent)
	{
		if (directory == null)
			throw new NullPointerException("Directory cannot be null!");
		File dir = new File(directory);
		log.debug("absolute path is " + dir.getAbsolutePath());
		if (dir.isFile())
			throw new RuntimeException("The submitted 'directory' " + directory + " is actually a file!");
		if (dir.exists() == false)
			throw new RuntimeException("The submitted 'directory' " + directory + " does not exist!");
		if (dir.isDirectory() == false)
			throw new RuntimeException("The submitted 'directory' " + directory + " is not a directory!");
		if (dir.listFiles().length > 0 && deleteContent == false)
			throw new RuntimeException("The submitted 'directory' " + directory + " is not empty!");
		boolean retval;
		for(File myfile : dir.listFiles())
		{
			if (myfile.isDirectory())
			{
				removeDirectory(myfile.getAbsolutePath(), deleteContent);
			} else
			{
				retval = myfile.delete();
				if (retval == false)
				{
					log.error("failed to remove file " + myfile.getAbsolutePath());
				}
			}
		}
		if (dir.listFiles().length > 0)
		{
			log.error("dir " + dir.getAbsolutePath() + " isn't empty.  One of the files or subdirs of this directory may still be open.");
		}
		if (dir.delete() == false)
			throw new RuntimeException("The submitted 'directory' " + directory + " could not be deleted!");
	}

	public List<String> listSubdirectories(String directory) {
		if (directory == null)
			throw new NullPointerException("Directory cannot be null!");
		File dir = new File(directory);
		List<String>  subdirNames = new ArrayList<String>();
		for(File file : dir.listFiles()) {
			if(file.isDirectory())
				subdirNames.add(file.getName());
		}
		return subdirNames;
	}

	public List<String> listFiles(String directory) {
		if (directory == null)
			throw new NullPointerException("Directory cannot be null!");
		File dir = new File(directory);
		if (dir.exists() == false)
			throw new RuntimeException(directory + " does not exist!");
		if (dir.isFile())
			throw new RuntimeException(directory + " is a file, not a directory!");
		List<String>  fileNames = new ArrayList<String>();
		for(File file : dir.listFiles()) {
			if(file.isFile())
				fileNames.add(file.getName());
		}
		return fileNames;
	}

	public List<FileHandler.FileAttributes> list(String directory) {
		if (directory == null)
			throw new NullPointerException("Directory cannot be null!");
		File dir = new File(directory);
		if (dir.exists() == false)
			throw new RuntimeException(directory + " does not exist!");
		if (dir.isFile())
			throw new RuntimeException(directory + " is a file, not a directory!");

		List<FileHandler.FileAttributes>  attributes = new ArrayList<FileHandler.FileAttributes>();
		for(File file : dir.listFiles())
		{
			FileHandler.FileAttributes fa = new FileHandler.FileAttributes();
			fa.filename = file.getName();
			fa.isDirectory = file.isDirectory();
			fa.size = file.length();
			fa.mtime = new Date(file.lastModified());
			attributes.add(fa);
		}
		return attributes;
	}

	public Map<String, List<String>> listFilesByExtension(String directory) {
		if (directory == null)
			throw new NullPointerException("Directory cannot be null!");
		File dir = new File(directory);
		if (dir.exists() == false)
			throw new RuntimeException(directory + " does not exist!");
		if (dir.isFile())
			throw new RuntimeException(directory + " is a file, not a directory!");
		Map<String, List<String>> fileNamesMap = new HashMap<String, List<String>>();
		for(File file : dir.listFiles()) {
			if(file.isFile()) {
				String fileName = file.getName();
				int beginIndex = fileName.lastIndexOf('.') + 1;
				if (beginIndex == 0) continue;
				String ext = fileName.substring(beginIndex);
				if (fileNamesMap.containsKey(ext) == false)
					fileNamesMap.put(ext, new ArrayList<String>());
				List<String>  fileNames = fileNamesMap.get(ext);
				fileNames.add(fileName);
				fileNamesMap.put(ext, fileNames);
			}
		}
		return fileNamesMap;
	}

	public void removeFile(String file) {
		if (file == null)
			throw new NullPointerException("File cannot be null!");
		File myfile = new File(file);
		if (myfile.isDirectory())
			throw new RuntimeException("The submitted 'file' " + file + " is actually a directory!");
		if (myfile.exists() == false)
			throw new RuntimeException("The submitted 'file' " + file + " does not exist!");
		if (myfile.delete() == false)
			throw new RuntimeException("The submitted 'file' " + file + " could not be deleted!");
	}



	public InputStream readFile(String fileName) 
			throws FileNotFoundException, Exception 
	{
		return getInputStream(fileName);
	}


	public InputStream getInputStream(String fileName) throws Exception
	{
		return new FileInputStream(fileName);
	}

	public void writeFile(String fileName, String content) {
		if (fileName == null)
			throw new NullPointerException("File name cannot be null!");
		if (content == null)
			throw new NullPointerException("Content cannot be null!");
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileName);
			fw.append(content);
		} catch (IOException e) {
			throw new RuntimeException("Can't write to file " + fileName, e);
		} finally {
			try { if(fw != null) fw.close(); } catch (IOException e) { ;}
		}
	}

	public void writeFile(String fileName, byte[] content) {
		if (fileName == null)
			throw new NullPointerException("File name cannot be null!");
		if (content == null)
			throw new NullPointerException("Content cannot be null!");
	    FileOutputStream fos = null;
	    try {
	      fos = new FileOutputStream(fileName);
	      fos.write(content);

	    } catch (IOException e) {
			throw new RuntimeException("Can't write to file " + fileName, e);
		} finally
		{
			try
			{
				if(fos != null)
				{
					fos.close();
				}
			}
			catch (IOException e) {  ; }
		}
	}

	public void writeFile(String newFileName, File file) throws Exception
	{
		if (newFileName == null)
			throw new NullPointerException("New file name name cannot be null!");
		if (file == null)
			throw new NullPointerException("File cannot be null!");
		try {
			writeFile(newFileName, readFile(file.getPath()));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(file + " does not exist!", e);
		}
	}

	public void writeFile(String fileName, InputStream inStream) throws IOException
	{
		OutputStream outStream = new BufferedOutputStream(new FileOutputStream(fileName));

		try {
			byte[] readBuffer = new byte[8192];
			int bytesRead;

			while ((bytesRead = inStream.read(readBuffer, 0, readBuffer.length)) >= 0)
				outStream.write(readBuffer, 0, bytesRead);
		}
		finally {
			outStream.close();
		}
	}

	public void moveDirectory(String directoryName, String newDirectoryName) {
		if (directoryName == null)
			throw new NullPointerException("Old directory name name cannot be null!");
		if (newDirectoryName == null)
			throw new NullPointerException("New directory name name cannot be null!");
		File directory = new File(directoryName) ;
		if (directory.exists() == false)
			throw new NullPointerException(directory + " does not exist!");
		if (directory.canRead() == false)
			throw new NullPointerException(directory + " cannot be read!");
		File targetDirectory = new File(newDirectoryName) ;
		//directory.renameTo(targetDirectory);
		try
		{
			Files.move(directory.toPath(), targetDirectory.toPath());
		}
		catch(Exception e)
		{
			log.error("MOVE DIRECTORY ERROR: ", e);
		}
	}

	public void moveFile(String fileName, String newFileName) {
		if (fileName == null)
			throw new NullPointerException("Old directory name name cannot be null!");
		if (newFileName == null)
			throw new NullPointerException("New directory name name cannot be null!");
		File orgFile = new File(fileName) ;
		if (orgFile.exists() == false)
			throw new NullPointerException(orgFile + " does not exist!");
		if (orgFile.canRead() == false)
			throw new NullPointerException(orgFile + " cannot be read!");
		File targetFile = new File(newFileName) ;
		orgFile.renameTo(targetFile);
	}

	public boolean isDirectory(String filename)
	{
		return new File(filename).isDirectory();
	}

	public long getSize(String filename)
	{
		return new File(filename).length();
	}

	public Date getMTime(String filename)
	{
		return new Date(new File(filename).lastModified());
	}
}
