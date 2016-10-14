/*
 * SFTPDirectory.java
 */
package org.ngbw.sdk.dataresources.lucene;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.store.BufferedIndexOutput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.ngbw.sdk.core.io.SSLConnectionManager;
import org.ngbw.sdk.common.util.InputStreamCollector;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SFTPv3Client;
import com.trilead.ssh2.SFTPv3DirectoryEntry;
import com.trilead.ssh2.SFTPv3FileHandle;
import com.trilead.ssh2.Session;


/**
 * Remote access Directory implementation that uses SFTP and SSH.
 * 
 * @author Roland H. Niedner
 * 
 */
public class SFTPDirectory extends Directory {

	private static Log log = LogFactory.getLog(SFTPDirectory.class);
	private final String poolName;
	private String indexLocation;
	private SFTPv3Client sftpClient;
	private Connection con;

	/**
	 * Constructor
	 */
	private SFTPDirectory(String indexLocation, String poolName) {
		super();
		if (indexLocation == null)
			throw new NullPointerException("Index Location is null!");
		if (indexLocation.endsWith("/") == false)
			throw new IllegalArgumentException(
					"Index Location must end with a /!");
		this.indexLocation = indexLocation;
		if (poolName == null)
			throw new NullPointerException("poolName is null!");
		this.poolName = poolName;
		this.lockFactory = new SFTPLockFactory(this);
		try {
			if (log.isTraceEnabled())
				log.trace("Checking whether " + indexLocation + " exists.");
			if (fileExists(indexLocation) == false) {
				createDirectory(indexLocation);
				if (log.isTraceEnabled())
					log.trace(indexLocation + " did not exist and was created.");
			}
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	public static SFTPDirectory getDirectory(String indexLocation,
			String poolName) {
		return new SFTPDirectory(indexLocation, poolName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.store.Directory#close()
	 */
	@Override
	public void close() throws IOException {
		sftpClient.close();
		if (con == null) return;
		con.close();
		this.con = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.store.Directory#createOutput(java.lang.String)
	 */
	@Override
	public IndexOutput createOutput(String fileName) throws IOException {
		if (fileName == null)
			throw new NullPointerException("fileName is null!");
		SFTPv3Client client = getSFTPClient();
		SFTPv3FileHandle handle = client.createFile(indexLocation + fileName);
		SFTPIndexOutput out = new SFTPIndexOutput(handle);
		return out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.store.Directory#deleteFile(java.lang.String)
	 */
	@Override
	public void deleteFile(String fileName) throws IOException {
		if (fileName == null)
			throw new NullPointerException("fileName is null!");
		SFTPv3Client client = getSFTPClient();
		client.rm(indexLocation + fileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.store.Directory#fileExists(java.lang.String)
	 */
	@Override
	public boolean fileExists(String fileName) throws IOException {
		if (fileName == null)
			throw new NullPointerException("fileName is null!");
		Connection con = getConnection();
		Session session = con.openSession();
		String cmd = "cd " + indexLocation + "; ls " + fileName;
		if (log.isDebugEnabled())
			log.debug("Checking command: " + cmd);
		Future<String> stdout = InputStreamCollector.readInputStream(session.getStdout());
		Future<String> stderr = InputStreamCollector.readInputStream(session.getStderr());
		boolean exists;
		session.execCommand(cmd);
		try {
			String err = stderr.get();
			if (err.contains("No such file or directory"))
				exists = false;
			else
				exists = true;
			if (log.isTraceEnabled())
				log.trace("STDERR:\n" + err);
			if (log.isTraceEnabled())
				log.trace("STDOUT:\n" + stdout.get());
		} catch (InterruptedException e) {
			throw new RuntimeException(e.toString(), e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e.toString(), e);
		}
		session.close();
		recycleConnection(con);
		return exists;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.store.Directory#fileLength(java.lang.String)
	 */
	@Override
	public long fileLength(String fileName) throws IOException {
		if (fileName == null)
			throw new NullPointerException("fileName is null!");
		SFTPv3Client client = getSFTPClient();
		SFTPv3FileHandle handle = client.createFile(indexLocation + fileName);
		Long fileLength = client.fstat(handle).size;
		return fileLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.store.Directory#fileModified(java.lang.String)
	 */
	@Override
	public long fileModified(String fileName) throws IOException {
		if (fileName == null)
			throw new NullPointerException("fileName is null!");
		SFTPv3Client client = getSFTPClient();
		SFTPv3FileHandle handle = client.createFile(indexLocation + fileName);
		Long mtime = new Long(client.fstat(handle).mtime);
		return mtime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.store.Directory#list()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String[] list() throws IOException {
		ArrayList<String> fileList = new ArrayList<String>();
		String[] files;
		SFTPv3Client client = getSFTPClient();
		Vector<SFTPv3DirectoryEntry> entries = client.ls(indexLocation);
		for (SFTPv3DirectoryEntry file : entries) {
			String fileName = file.filename;
			if (".".equals(fileName.trim()) == false
					&& "..".equals(fileName.trim()) == false
					&& file.attributes.isRegularFile()) {
				fileList.add(fileName);
			}
		}
		files = new String[fileList.size()];
		files = fileList.toArray(files);
		return files;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.store.Directory#openInput(java.lang.String)
	 */
	@Override
	public IndexInput openInput(String fileName) throws IOException {
		if (fileName == null)
			throw new NullPointerException("fileName is null!");
		SFTPv3Client client = getSFTPClient();
		SFTPv3FileHandle handle = client.createFile(indexLocation + fileName);
		SFTPIndexInput out = new SFTPIndexInput(handle);
		return out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.store.Directory#renameFile(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void renameFile(String fileName, String newFileName)
			throws IOException {
		if (fileName == null)
			throw new NullPointerException("fileName is null!");
		if (newFileName == null)
			throw new NullPointerException("newFileName is null!");
		if (fileName.equals(newFileName))
			throw new RuntimeException("Old and new file name are identical!");
		SFTPv3Client client = getSFTPClient();
		client.mv(indexLocation + fileName, indexLocation + newFileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.store.Directory#touchFile(java.lang.String)
	 */
	@Override
	public void touchFile(String fileName) throws IOException {
		if (fileName == null)
			throw new NullPointerException("fileName is null!");
		Connection con = getConnection();
		Session session = con.openSession();
		String cmd = "cd " + indexLocation + "; touch " + fileName;
		Future<String> stdout = InputStreamCollector.readInputStream(session.getStdout());
		Future<String> stderr = InputStreamCollector.readInputStream(session.getStderr());
		session.execCommand(cmd);
		try {
			Integer exitValue = session.getExitStatus();
			if (exitValue == null || exitValue != 0)
				log.warn("Exit Value is not 0: "
								+ session.getExitStatus());
			if (log.isDebugEnabled() || exitValue == null || exitValue != 0)
				log.debug("STDERR:\n" + stderr.get());
			if (log.isDebugEnabled() || exitValue == null || exitValue != 0)
				log.debug("STDOUT:\n" + stdout.get());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		session.close();
		recycleConnection(con);
	}

	private void recycleConnection(Connection con) {
		con.close();
	}

	private Connection getConnection() {
		Connection con = null;
		try {
			con = SSLConnectionManager.getInstance().getConnection(poolName);
		} catch (IOException e) {
			throw new RuntimeException("Connection retrieval for "  + poolName + " failed.", e);
		}
		if (con == null)
			throw new NullPointerException("No connection could be acquired from pool: " + poolName);
		return con;
	}

	private SFTPv3Client getSFTPClient() {
		if (con == null)
			con = getConnection();
		if (sftpClient == null)
			try {
				sftpClient = new SFTPv3Client(con);
			} catch (IOException e) {
				throw new RuntimeException("Could not create SFTPv3Client for "
						  + poolName, e);
			}
		return sftpClient;
	}

	private void createDirectory(String directory) {
		SFTPv3Client client = getSFTPClient();
		try {
			client.mkdir(directory, 0711);
			if(log.isDebugEnabled()) log.debug("Created Directory: " + directory);
		} catch (IOException e) {
			throw new RuntimeException("Could not create directory " + directory, e);
		}
	}

	protected static class SFTPIndexInput extends BufferedIndexInput {

		private SFTPv3FileHandle handle;
		private long fileOffset = 0;

		/**
		 * Constructor
		 */
		public SFTPIndexInput(SFTPv3FileHandle handle) {
			super();
			if (handle == null)
				throw new NullPointerException("SFTPv3FileHandle is null!");
			if (handle.isClosed())
				throw new IllegalArgumentException(
						"SFTPv3FileHandle is closed!");
			this.handle = handle;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.lucene.store.IndexInput#close()
		 */
		@Override
		public void close() throws IOException {
			handle.getClient().closeFile(handle);
			handle = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.lucene.store.IndexInput#length()
		 */
		@Override
		public long length() {
			Long fileLength;
			try {
				fileLength = handle.getClient().fstat(handle).size;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return fileLength;
		}

		@Override
		protected void readInternal(byte[] buffer, int offset, int length)
				throws IOException {
			int bytesRead = 0;
			int max = 32768;
			if (length > max) {
				int len = max;
				int bufferOffset = 0;
				while (0 < (bytesRead = handle.getClient().read(handle, fileOffset, buffer,
						bufferOffset, len))) {
					fileOffset += bytesRead;
					bufferOffset += bytesRead;
					if  (length - bufferOffset == 0)
						break;
					else if (length - bufferOffset < max)
						len = length - bufferOffset;
				}
			} else {
				bytesRead = handle.getClient().read(handle, fileOffset, buffer,
						offset, length);
			}
			fileOffset += bytesRead;
		}

		@Override
		protected void seekInternal(long fileOffset) throws IOException {
			if (fileOffset > length())
				throw new IOException(
						"Requested offset lies beyond the current length of the file.");
			this.fileOffset = fileOffset;
		}
	}

	protected static class SFTPIndexOutput extends BufferedIndexOutput {

		private SFTPv3FileHandle handle;
		private long fileOffset = 0;

		/**
		 * Constructor
		 */
		public SFTPIndexOutput(SFTPv3FileHandle handle) {
			super();
			if (handle == null)
				throw new NullPointerException("SFTPv3FileHandle is null!");
			if (handle.isClosed())
				throw new IllegalArgumentException(
						"SFTPv3FileHandle is closed!");
			this.handle = handle;
		}

		/**
		 * Closes this stream to further operations.
		 * 
		 * @see org.apache.lucene.store.IndexOutput#close()
		 */
		@Override
		public void close() throws IOException {
			super.flush();
			super.close();
			handle.getClient().closeFile(handle);
			handle = null;
		}

		/**
		 * The number of bytes in the file.
		 * 
		 * @see org.apache.lucene.store.IndexOutput#length()
		 */
		@Override
		public long length() throws IOException {
			Long fileLength;
			try {
				fileLength = handle.getClient().fstat(handle).size;
			} catch (Exception e) {
				throw new IOException("Cannot fstat the current file length: "
						+ e.getMessage());
			}
			if (fileLength == null)
				throw new IOException("Cannot fstat the current file length.");
			return fileLength;
		}

		@Override
		protected void flushBuffer(byte[] src, int srcoff, int len)
				throws IOException {
			if (handle.getClient() == null)
				throw new NullPointerException("SFTPClient is null");
			if (handle == null)
				throw new NullPointerException("SFTPFileHandle is null");
			try {
				handle.getClient().write(handle, fileOffset, src, srcoff, len);
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
			fileOffset += len;
		}
	}
}
