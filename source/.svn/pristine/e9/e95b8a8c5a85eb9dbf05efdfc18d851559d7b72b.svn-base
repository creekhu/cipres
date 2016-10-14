/*
 * GridFtpFileHandler.java
 */
package org.ngbw.sdk.tool;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.ftp.DataSource;
import org.globus.ftp.DataSourceStream;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.MlsxEntry;
import org.globus.ftp.Session;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.ietf.jgss.GSSCredential;
import org.ngbw.sdk.WorkbenchException;
import org.ngbw.sdk.api.tool.FileHandler;
import org.ngbw.sdk.database.Task;


/**
 *
 * @author Terri Liebowitz Schwartz
 *
 */
public class GridFtpFileHandler implements FileHandler
{
	private static final Log m_log = LogFactory.getLog(GridFtpFileHandler.class.getName());
	private static final String FILEHOST = "fileHost";
	private static final String FILEPORT  = "filePort";

	private static final SimpleDateFormat timeValFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	{
		timeValFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private String m_host;
	private int m_port = -1;
	private GSSCredential m_proxy;
	private Task task;

	private GridFTPClient getConnection() throws ServerException, IOException, ClientException
	{
		GridFTPClient ftp  = new GridFTPClient(m_host, m_port);
		ftp.authenticate(m_proxy);
		ftp.setPassive();
		ftp.setLocalActive();
		return ftp;
	}

	private void releaseConnection(GridFTPClient ftp) throws ServerException, IOException
	{
		ftp.close();
	}

	void setTransferType(GridFTPClient ftp) throws ServerException, IOException, ClientException
	{
		// transfer between teragrid host and portal web server is unix - unix, so binary
		// transfer should always be fine.
		ftp.setType(Session.TYPE_IMAGE);

		ftp.setMode(Session.MODE_STREAM);
		//ftp.setPassive();
		//ftp.setLocalActive();
	}



	// This ctor is used by Tool.getFileHandler() and it is must be followed  by a call to configure!
	public GridFtpFileHandler() 
	{
	}

	public boolean configure(Map<String, String> cfg)
	{
		if (cfg != null && cfg.containsKey(FILEHOST))
		{
			m_host = cfg.get(FILEHOST);
			m_log.debug("Configured host: " + m_host);
		} else
		{
			m_log.error("Missing parameter " + FILEHOST);
		}
		if (cfg != null && cfg.containsKey(FILEPORT))
		{
			String tmp = cfg.get(FILEPORT);
			if (tmp != null)
			{
				m_port = new Integer(tmp);
				m_log.debug("Configured port: " + m_port);
			}
		} else
		{
			m_log.error("Missing parameter " + FILEPORT);
		}
		try
		{
			m_proxy = getProxy();
		}
		catch(Exception t)
		{
			m_log.error("Exception while getting proxy certificate", t);
		}
		return isConfigured();
	}


	public boolean isConfigured()
	{
		return m_host != null && m_port != -1  && m_proxy != null;
	}

	
	// This is used by the process workers.
	public GridFtpFileHandler(String host, int port)
		throws Exception
	{
		m_host = host;
		m_port = port;
		m_proxy = getProxy();
	}

	private GSSCredential getProxy() throws Exception
	{
		GSSCredential proxy;
		String username, email, ip;
		
		proxy = GlobusCred.getInstance().get();
		return proxy;
	}

	public void close()
	{
		try
		{
			if (m_proxy != null)
			{
				// See comment in SimpleCred.java, releaseProxy().  I don't think it's safe or necessary to
				// call dispose.
				//m_proxy.dispose();
				m_proxy = null;
			}
		}
		catch(Exception e)
		{
			m_log.error("", e);
		}
	}


	public void createDirectory(String directory) throws IOException, ServerException, Exception
	{
		GridFTPClient ftp  = getConnection();
		try
		{
			ftp.makeDir(directory);

			// Try to change permissions but just log a warning if it fails.
			// Not all sites support chmod.
			try
			{
				ftp.site("chmod 0700 " + directory);
			}
			catch(Exception e)
			{
				m_log.warn("Error running chmod via gridftp", e);
			}
			m_log.debug("Created Directory: " + directory);
		}
		catch(Exception e)
		{
			m_log.debug("Error creating '" + directory + "'");
			throw e;
		}
		finally
		{
			releaseConnection(ftp);
		}
	}

	public boolean exists(String path) throws ServerException, IOException, ClientException
	{
		GridFTPClient ftp  = getConnection();
		try
		{
			return ftp.exists(path);
		}
		finally
		{
			releaseConnection(ftp);
		}
	}

	public Date getMTime(String fileName)  throws IOException, ServerException, ClientException
	{
		GridFTPClient ftp  = getConnection();
		try
		{
			return ftp.getLastModified(fileName);
		}
		finally
		{
			releaseConnection(ftp);
		}
	}

	public long getSize(String fileName)  throws IOException, ServerException, ClientException
	{
		GridFTPClient ftp  = getConnection();
		try
		{
			return ftp.getSize(fileName);
		}
		finally
		{
			releaseConnection(ftp);
		}
	}

	public boolean isDirectory(String fileName)  throws IOException, ServerException, ClientException
	{
		GridFTPClient ftp  = getConnection();
		try
		{
			MlsxEntry info = ftp.mlst(fileName);
			return (info.get(MlsxEntry.TYPE)).equals(MlsxEntry.TYPE_DIR);
		}
		catch (ServerException se)
		{
			// apparently mlst(filename) throws if filename doesn't exist.
			return false;
		}
		finally
		{
			releaseConnection(ftp);
		}
	}

	public List<FileAttributes> list(String directory) throws IOException, ServerException, ClientException
	{
		String tmp;
		Date d;
		List<FileHandler.FileAttributes> attributes = new ArrayList<FileHandler.FileAttributes>();

		GridFTPClient ftp = getConnection();

		try
		{
			Vector<MlsxEntry> v  = (Vector<MlsxEntry>)ftp.mlsd(directory);


			String filename;
			for (int i = 0; i < v.size(); i++)
			{
				MlsxEntry info = (MlsxEntry)(v.get(i));

				if (info.get(MlsxEntry.TYPE).equals(MlsxEntry.TYPE_FILE))
				{
					FileHandler.FileAttributes fa = new FileHandler.FileAttributes();
					attributes.add(fa);

					fa.filename = (new File(info.getFileName())).getName();
					fa.isDirectory = false;
					tmp = info.get(MlsxEntry.SIZE);
					fa.size = new Long(tmp);
					tmp = info.get(MlsxEntry.MODIFY);

					d = timeValToDate(tmp);
					if (d != null)
					{
						fa.mtime = d; 
					} else
					{
						fa.mtime = new Date(0L);
					}
				}
			}
			return attributes;
		}
		finally
		{
			releaseConnection(ftp);
		}
	}

	private Date timeValToDate(String timeVal)
	{
		try
		{
			return timeValFormat.parse(timeVal);
		}
		catch (Exception e)
		{
			// Not sure why, but we don't always get something we can parse for the modification date of the file.  
			// Sometimes its empty, sometimes it's a decimal number with exponent!
			m_log.debug("Unable to parse file modification timestamp: " + e.toString());
			return null;
		}
	}





	/**
		Return just the files, not subdirs.  Return just the last component
		of the pathname, not the full filename.

		Don't know what this does, or is expected to do with links.
	 * @throws IOException
	 * @throws ServerException
	 * @throws ClientException
	 * @throws Exception
	*/
	public  List<String> listFiles(String directory) throws ServerException, IOException, ClientException
	{
		GridFTPClient ftp = getConnection();
		try
		{
			return listFiles(ftp, directory);
		}
		finally
		{
			releaseConnection(ftp);
		}
	}


	@SuppressWarnings("unchecked")
	private List<String> listFiles(GridFTPClient ftp, String directory) throws ServerException, ClientException, IOException
	{
		ArrayList<String> retval = new ArrayList<String>();
		Vector<MlsxEntry> v  = (Vector<MlsxEntry>)ftp.mlsd(directory);
		String filename;
		for (int i = 0; i < v.size(); i++)
		{
			MlsxEntry info = (MlsxEntry)(v.get(i));
			if (info.get(MlsxEntry.TYPE).equals(MlsxEntry.TYPE_FILE))
			{
				filename = (info.getFileName());
				retval.add((new File(filename)).getName());
			}
		}
		return retval;
	}




	/**
		Not used
	*/
	public Map<String, List<String>> listFilesByExtension(String directory) throws IOException
	{
		throw new WorkbenchException("NOT IMPLEMENTED");
	}

	/**
		Not used
	*/
	public List<String> listSubdirectories(String directory)  throws IOException
	{
		throw new WorkbenchException("NOT IMPLEMENTED");
	}

	public void moveDirectory(String directoryName, String newDirectoryName)  throws IOException, ServerException, ClientException
	{
		GridFTPClient ftp  = getConnection();
		try
		{
			ftp.rename(directoryName, newDirectoryName);
		}
		finally
		{
			releaseConnection(ftp);
		}
	}

	public void moveFile(String fileName, String newFileName) throws IOException, ServerException, ClientException
	{
		GridFTPClient ftp  = getConnection();
		try
		{
			ftp.rename(fileName, newFileName);
		}
		finally
		{
			releaseConnection(ftp);
		}
	}

	public InputStream readFile(String fileName) 
		throws ServerException, IOException, ClientException, Exception
	{

		GridFTPClient ftp  = getConnection();
		InputStream is = null;
		try
		{
			if (!ftp.exists(fileName))
			{
				throw new FileNotFoundException();
			}
			is = getInputStream(fileName);
			return is;
		}
		catch (Exception e)
		{
			if (is != null) { is.close(); }
			throw e;
		}
		finally
		{
			releaseConnection(ftp);
		}
	}



	public InputStream getInputStream(String fileName) throws Exception
	{
		GridFTPClient client = getConnection();

		try {
			setTransferType(client);

			return new GridFtpInputStream(client, fileName);
		}
		catch (Exception err) {
			client.close();

			throw err;
		}
	}

	public void removeDirectory(String directoryPath, boolean deleteContent) throws IOException, ServerException, ClientException
	{
		GridFTPClient ftp  = getConnection();
		try
		{
			removeDirectoryRecursive(ftp, directoryPath, deleteContent);
			return;
		}
		finally
		{
			releaseConnection(ftp);
		}
	}

	/**
		ftp.mlsd can return entries of type file, dir, cdir and pdir, where cdir and pdir
		represent the current dir and it's parent.  To know if a directory is empty we need
		to make sure it has a real file or dir child and not just cdir and pdir entries.

		@param files is the result of a call to FtpClient.mlsd(directory)
	*/
	private boolean isDirEmpty( Vector<MlsxEntry> files)
	{
		for(MlsxEntry file : files)
		{
			if (file.get(MlsxEntry.TYPE).equals(MlsxEntry.TYPE_FILE) || file.get(MlsxEntry.TYPE).equals(MlsxEntry.TYPE_DIR))
			{
				return false;
			}
		}
		return true;

	}

	/**
		To do: I think some GridFtp server's provide a recursive remote.  I'm not sure how to invoke it.
		Maybe via a ftp.site() call?  Anyway, if it exists it should be much more efficient than this.
	 * @throws ServerException
	 * @throws ClientException
	*/
	@SuppressWarnings("unchecked")
	private void removeDirectoryRecursive(GridFTPClient ftp, String directory, boolean deleteContent) throws IOException, ServerException, ClientException
	{
		Vector<MlsxEntry> files = (Vector<MlsxEntry>)ftp.mlsd(directory);

		if (deleteContent == false)
		{
			if (isDirEmpty(files) == false)
			{
				m_log.error("Directory is not empty: ");
				for (MlsxEntry file : files)
				{
					m_log.error(file.getFileName());
				}
				throw new WorkbenchException("Directory is not empty");
			}
			// we fall thru since we've determined that the directory is empty.
		}
		for(MlsxEntry file : files)
		{
			String fileName = file.getFileName();

			if (file.get(MlsxEntry.TYPE).equals(MlsxEntry.TYPE_DIR))
			{
				removeDirectoryRecursive(ftp, directory + "/" + fileName, deleteContent);
			} else if (file.get(MlsxEntry.TYPE).equals(MlsxEntry.TYPE_FILE))
			{
				ftp.deleteFile(directory + "/" + fileName);
			}
		}
		ftp.deleteDir(directory);
	}

	public void removeFile(String fileName) throws IOException, ServerException, ClientException
	{
		GridFTPClient ftp  = getConnection();
		try
		{
			ftp.deleteFile(fileName);
		}
		finally
		{
			releaseConnection(ftp);
		}
	}

	public void writeFile(String fileName, String content) throws IOException, ServerException, ClientException
	{
		writeFile(fileName, content.getBytes());
	}

	public void writeFile(String fileName, byte[] content) throws IOException, ServerException, ClientException
	{
		writeFile(fileName, new ByteArrayInputStream(content));
	}

	public void writeFile(String fileName, File file) throws IOException, ServerException, ClientException
	{
		writeFile(fileName, new BufferedInputStream(new FileInputStream(file)));
	}

	public void writeFile(String fileName, InputStream inStream) throws IOException, ServerException, ClientException
	{
		GridFTPClient ftp  = getConnection();
		try
		{
			setTransferType(ftp);
			DataSource source = new DataSourceStream(inStream);
			ftp.put(fileName, source, null);
		}
		finally
		{
			releaseConnection(ftp);
		}
	}

	/**
		Test this class.

		To do: move this method to it's own test case in test directory.
	 * @throws InterruptedException
		
		FileHandler no longer has methods to read a file into a string so commented
		this out.
	*/
/*
	public static void main(String[] args) throws InterruptedException
	{
		final String host = "tg-gridftp.lonestar.tacc.teragrid.org";
		final int port = 2811;
		final String rootDir = "/home/01143/tg804218/workspace";


		class TestThread extends Thread
		{
			int i;

			TestThread(int i) { this.i = i; }
			public void run()
			{
				//###
				try
				{
					System.out.printf("T%d: Create file handler\n", i);
					GridFtpFileHandler fh = new GridFtpFileHandler(host, port);

					String mydir = rootDir + "/t" + i;
					System.out.printf("T%d: Create dir %s\n", i, mydir);
					fh.createDirectory(mydir);

					System.out.printf("T%d: Create dir %s AGAIN, should get error\n", i, mydir);
					try
					{
						fh.createDirectory(mydir);
						throw new Exception("Expected an error when creating a dir that already exists, but didn't get one.");
					}
					catch (Exception e)
					{
						System.out.printf("T%d: caught expected exception %s\n", i, e.getMessage());
					}
					System.out.printf("T%d: list empty directory\n", i);
					List<String> list = fh.listFiles(mydir);
					if (list.size() != 0)
					{
						throw new Exception("Expected listFiles to return an empty list");
					}

					String file1 = mydir + "/file1.txt";
					String file2 = mydir + "/file2.txt";
					String rename_file1 = mydir + "/file1_x.txt";
					String rename_mydir = mydir + "_x";

					System.out.printf("T%d: write file1.txt\n", i);
					fh.writeFile(file1, "This is a silly short string\n");
					System.out.printf("T%d: write file2.txt\n", i);
					fh.writeFile(file2, "This is another silly short string\n");

					System.out.printf("T%d: list directory\n", i);
					list = fh.listFiles(mydir);
					if (list.size() != 2)
					{
						throw new Exception("Expected listFiles to return 2 files");
					}
					System.out.printf("T%d: listed filenames: '%s' and '%s'\n", i, list.get(0), list.get(1));

					byte[] bytes;
					System.out.printf("T%d: read file1.txt\n", i);
					bytes = fh.readFile(file1);
					System.out.printf("T%d: read '%s'\n", i, new String(bytes));

					System.out.printf("T%d: read file2.txt\n", i);
					bytes = fh.readFile(file2);
					System.out.printf("T%d: read '%s'\n", i, new String(bytes));

					InputStream inStream;
					System.out.printf("T%d: stream file1.txt\n", i);
					inStream = fh.getInputStream(file1);
					try {
						ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
						byte[] readBuffer = new byte[8192];
						int bytesRead;

						while ((bytesRead = inStream.read(readBuffer, 0, readBuffer.length)) > 0)
							outBytes.write(readBuffer, 0, bytesRead);

						System.out.printf("T%d: got '%s'\n", i, new String(outBytes.toByteArray()));
					}
					finally {
						inStream.close();
					}
					System.out.printf("T%d: stream file2.txt\n", i);
					inStream = fh.getInputStream(file2);
					try {
						ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
						byte[] readBuffer = new byte[8192];
						int bytesRead;

						while ((bytesRead = inStream.read(readBuffer, 0, readBuffer.length)) > 0)
							outBytes.write(readBuffer, 0, bytesRead);

						System.out.printf("T%d: got '%s'\n", i, new String(outBytes.toByteArray()));
					}
					finally {
						inStream.close();
					}

					System.out.printf("T%d: readFilesWithExtension\n", i);
					Map<String, byte[]> mymap = fh.readFilesWithExtension(mydir, "txt");
					for (Iterator<Map.Entry<String, byte[]>> mapNodes = mymap.entrySet().iterator() ; mapNodes.hasNext() ; )
					{
						Map.Entry<String, byte[]> node = mapNodes.next();
						System.out.printf("T%d: read filename=%s, content=%s\n", i, node.getKey(), new String(node.getValue()));
					}

					System.out.printf("T%d: renaming %s to %s\n", i, file1, rename_file1);
					fh.moveFile(file1, rename_file1);
					System.out.printf("T%d: list directory\n", i);
					list = fh.listFiles(mydir);
					if (list.size() != 2)
					{
						throw new Exception("Expected listFiles to return 2 files");
					}
					System.out.printf("T%d: listed filenames: '%s' and '%s'\n", i, list.get(0), list.get(1));

					System.out.printf("T%d: deleting %s\n", i, rename_file1);
					fh.removeFile(rename_file1);
					list = fh.listFiles(mydir);
					if (list.size() != 1)
					{
						throw new Exception("Expected listFiles to return 1 file");
					}

					System.out.printf("T%d: renaming directory %s to %s\n", i, mydir, rename_mydir);
					fh.moveDirectory(mydir, rename_mydir);

					System.out.printf("T%d: remove directory %s\n", i, rename_mydir);
					fh.removeDirectory(rename_mydir, true);

					try
					{
						System.out.printf("T%d: read %s which has been deleted, expect file not found\n", i, file2);
						fh.readFile(file2);
						throw new Exception("Expected file not found exception, but got no exception\n");
					}
					catch(FileNotFoundException e)
					{
						System.out.printf("T%d: got file not found as expected\n", i);
					}
				}
				catch (Exception e)
				{
					System.out.printf("T%d: unexpected exception %s\n", i, e.getMessage());
					e.printStackTrace();
				}
			}
		}
		int THREAD_COUNT = 1;
		TestThread threads[]  = new TestThread[THREAD_COUNT];


		// Start the threads
		for (int i = 0; i < THREAD_COUNT; i++)
		{
			threads[i] = new TestThread(i);
			threads[i].start();
		}

		// wait for them all to complete
		for (int i = 0; i < THREAD_COUNT; i++)
		{
			threads[i].join();
		}
	}
*/



}
