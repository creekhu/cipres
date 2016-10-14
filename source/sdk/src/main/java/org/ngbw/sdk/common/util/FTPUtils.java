package org.ngbw.sdk.common.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * @author nita
 *
 * This class has FTP client utilities.
 */
public class FTPUtils {

	public static void getFile(FTPClient ftpIn, String remoteFile,
			String localFile, boolean binaryTransfer, boolean debug) {
		fileTransfer("doesntmatter", "doesntmatter", "doesntmatter",
				remoteFile, localFile, binaryTransfer, debug, false, ftpIn);
	}

	public static void getFile(String ftpServerName, String remoteFile,
			String localFile, boolean binaryTransfer) {
		getFile(ftpServerName, "anonymous", "ftputil@service.net", remoteFile,
				localFile, binaryTransfer, true);
	}

	public static void getFile(String ftpServerName, String username,
			String password, String remoteFile, String localFile,
			boolean binaryTransfer) {
		getFile(ftpServerName, username, password, remoteFile, localFile,
				binaryTransfer, true);
	}

	public static void getFile(String ftpServerName, String username,
			String password, String remoteFile, String localFile,
			boolean binaryTransfer, boolean debug) {
		fileTransfer(ftpServerName, username, password, remoteFile, localFile,
				binaryTransfer, debug, false, null);
	}

	public static void putFile(String ftpServerName, String username,
			String password, String remoteFile, String localFile,
			boolean binaryTransfer) {
		putFile(ftpServerName, username, password, remoteFile, localFile,
				binaryTransfer, true);
	}

	public static void putFile(String ftpServerName, String username,
			String password, String remoteFile, String localFile,
			boolean binaryTransfer, boolean debug) {
		fileTransfer(ftpServerName, username, password, remoteFile, localFile,
				binaryTransfer, debug, true, null);
	}

	public static void fileTransfer(String ftpServerName, String username,
			String password, String remoteFile, String localFile,
			boolean binaryTransfer, boolean debug, boolean putting,
			FTPClient ftpIn) {
		FTPClient ftp = null;
		int reply;
		InputStream input = null;
		OutputStream output = null;
		try {
			if (ftpIn == null) {
				ftp = new FTPClient();
				ftp.connect(ftpServerName);
				// After connection attempt, you should check the reply code to verify success.
				reply = ftp.getReplyCode();
				if (!FTPReply.isPositiveCompletion(reply)) {
					System.err.println("FTP server refused connection.");
					throw new RuntimeException("FTP server refused connection.");
				}
				if (debug) {
					System.err.println("Connected to " + ftpServerName + ".");
				}
				if (!ftp.login(username, password)) {
					System.err.println("Unable to log in to FTP server.");
					throw new RuntimeException(
							"Unable to log in to FTP server.");
				}
				if (debug) {
					System.err.println("Remote system is "
							+ ftp.getSystemName());
				}
			} else {
				ftp = ftpIn;
			}
			if (binaryTransfer) {
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
			}
			// Use passive mode as default because most of us are
			// behind firewalls these days.
			ftp.enterLocalPassiveMode();
			if (putting) {
				input = new FileInputStream(localFile);
				ftp.storeFile(remoteFile, input);
				input.close();
				input = null;
			} else {
				output = new FileOutputStream(localFile);
				ftp.retrieveFile(remoteFile, output);
				output.flush();
				output.close();
				output = null;
			}
			if (ftpIn == null) {
				ftp.logout();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (ftpIn == null && ftp != null) {
				try {
					ftp.disconnect();
				} catch (IOException f) {
					// do nothing
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (Exception anye) {
					// do nothing
				}
			}
			if (output != null) {
				try {
					output.close();
				} catch (Exception anye) {
					// do nothing
				}
			}
		}
	}

	public static FTPFile[] getFileListing(String ftpServerName,
			String ftpDirectory, String username, String password) {
		return getFileListing(ftpServerName, ftpDirectory, username, password,
				true);
	}

	public static FTPFile[] getFileListing(String ftpServerName,
			String ftpDirectory, String username, String password, boolean debug) {
		FTPClient ftp = new FTPClient();
		FTPFile[] ans = null;
		try {
			int reply;
			ftp.connect(ftpServerName);
			// After connection attempt, you should check the reply code to verify
			// success.
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				System.err.println("FTP server refused connection.");
				throw new RuntimeException("FTP server refused connection.");
			}
			if (debug) {
				System.err.println("Connected to " + ftpServerName + ".");
			}
			if (!ftp.login(username, password)) {
				System.err.println("Unable to log in to FTP server.");
				throw new RuntimeException("Unable to log in to FTP server.");
			}
			if (debug) {
				System.err.println("Remote system is " + ftp.getSystemName());
			}
			// Use passive mode as default because most of us are
			// behind firewalls these days.
			ftp.enterLocalPassiveMode();
			if (ftpDirectory == null) {
				ans = ftp.listFiles();
			} else {
				ans = ftp.listFiles(ftpDirectory);
			}
			ftp.logout();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				ftp.logout();
			} catch (Exception anye) {
				// do nothing
			}
			try {
				ftp.disconnect();
			} catch (IOException f) {
				// do nothing
			}
		}
		return ans;
	}

	public static void cleanupFtpClient(FTPClient oldone) {
		if (oldone != null) {
			try {
				oldone.disconnect();
			} catch (Exception f) {
				// do nothing
			}
		}
	}

	public static FTPClient setupFtpClient(FTPClient oldone, String ftpSite,
			String userName, String passWord, String ftpDirectory)
			throws SocketException, IOException {
		cleanupFtpClient(oldone);
		FTPClient fTPClient = new FTPClient();
		fTPClient.connect(ftpSite);
		// After connection attempt, you should check the reply code to verify
		// success.
		int reply = fTPClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			System.err.println("FTP server refused connection.");
			throw new RuntimeException("FTP server refused connection.");
		}
		if (!fTPClient.login(userName, passWord)) {
			System.err.println("Unable to log in to FTP server.");
			throw new RuntimeException("Unable to log in to FTP server.");
		}
		// Use passive mode as default because most of us are
		// behind firewalls these days.
		fTPClient.enterLocalPassiveMode();
		if (ftpDirectory != null) {
			fTPClient.changeWorkingDirectory(ftpDirectory);
		}
		fTPClient.setFileType(FTP.BINARY_FILE_TYPE);
		return fTPClient;
	}

	//	/**
	//	 * This method fetches the file from the ftp site sent in as the parameter.
	//	 *
	//	 * @param ftpName
	//	 * @param ftpBaseDir
	//	 * @param login
	//	 * @param password
	//	 * @param filename
	//	 */
	//	public static void getFile(String ftpName, String ftpBaseDir, String
	// login, String password,
	//			String filename)
	//	{
	//		FTPClient loggedInClient = null;
	//		try
	//		{
	//			loggedInClient = new FTPClient();
	//			loggedInClient.connect(ftpName);
	//			int reply = loggedInClient.getReplyCode();
	//			if (!FTPReply.isPositiveCompletion(reply))
	//			{
	//				throw new RuntimeException("FTP Connect problem: " + reply);
	//			}
	//			loggedInClient.login(login, password);
	//			loggedInClient.changeWorkingDirectory(ftpBaseDir);
	//			loggedInClient.get(filename, filename);
	//			loggedInClient.quit();
	//			loggedInClient = null;
	//		}
	//		catch (IOException e)
	//		{
	//
	//			e.printStackTrace();
	//		}
	//		catch (FTPException e)
	//		{
	//			e.printStackTrace();
	//			try
	//			{
	//				if (loggedInClient != null)
	//				{
	//					loggedInClient.quit();
	//				}
	//			}
	//			catch (IOException e1)
	//			{
	//
	//				e1.printStackTrace();
	//			}
	//			catch (FTPException e1)
	//			{
	//
	//				e1.printStackTrace();
	//			}
	//		}
	//	}
	//
	//	public static void mymain(String[] args)
	//	{
	//		getListing("beta.rcsb.org", "pub/pdb", "anonymous", "foo", false);
	//		getListing("surfer.sdsc.edu", "derived/mbt", "derivedloader",
	// "derivedloader", true);
	//	}
	//
	//	public static List getFileListing(String ftpServerName, String
	// ftpBaseDirectory, String user,
	//			String pass)
	//	{
	//		FTPClient loggedInClient = null;
	//		ArrayList ans = new ArrayList();
	//		String[] names = null;
	//		try
	//		{
	//			loggedInClient = new FTPClient(ftpServerName);
	//			loggedInClient.login(user, pass);
	//			loggedInClient.debugResponses(false);
	//			loggedInClient.chdir(ftpBaseDirectory);
	//			names = loggedInClient.dir();
	//			System.err.println(names.length + " files in FTP directory using
	// dir(\".\", true)");
	//			loggedInClient.quit();
	//			loggedInClient = null;
	//			for (int i = 0; i < names.length; i++)
	//			{
	//				String aline = names[i].trim();
	//				if (aline.length() == 0 || aline.startsWith(".") || aline.endsWith(":"))
	//				{
	//					continue;
	//				}
	//				ans.add(aline);
	//			}
	//		}
	//		catch (IOException e)
	//		{
	//
	//			e.printStackTrace();
	//		}
	//		catch (FTPException e)
	//		{
	//			e.printStackTrace();
	//			try
	//			{
	//				if (loggedInClient != null)
	//				{
	//					loggedInClient.quit();
	//				}
	//			}
	//			catch (IOException e1)
	//			{
	//
	//				e1.printStackTrace();
	//			}
	//			catch (FTPException e1)
	//			{
	//
	//				e1.printStackTrace();
	//			}
	//		}
	//		return ans;
	//	}
	//
	//	public static List getListing(String ftpServerName, String
	// ftpBaseDirectory, String user,
	//			String pass, boolean isWindows)
	//	{
	//		FTPClient loggedInClient = null;
	//		ArrayList ans = new ArrayList();
	//		String[] names = null;
	//		try
	//		{
	//			loggedInClient = new FTPClient(ftpServerName);
	//			loggedInClient.login(user, pass);
	//			loggedInClient.debugResponses(false);
	//			loggedInClient.chdir(ftpBaseDirectory);
	//			//String[] names0 = loggedInClient.dir();
	//			//System.err.println(names0.length + " files in FTP directory using
	//			// dir()");
	//			//String[] names1 = loggedInClient.dir();
	//			//System.err.println(names1.length + " files in FTP directory using
	//			// dir(\".\")");
	//			names = loggedInClient.dir(".", true);
	//			System.err.println(names.length + " files in FTP directory using
	// dir(\".\", true)");
	//			loggedInClient.quit();
	//			loggedInClient = null;
	//			for (int i = 0; i < names.length; i++)
	//			{
	//				String aline = names[i].trim();
	//				if (aline.length() == 0 || aline.charAt(0) == 'd' || aline.startsWith(".")
	//						|| aline.startsWith("total") || aline.endsWith(":"))
	//				{
	//					continue;
	//				}
	//				int sizepos = 20;
	//				int datepos = 32;
	//				int namepos = 45;
	//				if (isWindows)
	//				{
	//					sizepos = 29;
	//					datepos = 41;
	//					namepos = 55;
	//				}
	//				String aname = aline.substring(namepos).trim();
	//				int pos = aname.indexOf("->");
	//				if (pos >= 0)
	//				{
	//					aname = aname.substring(0, pos).trim();
	//				}
	//				XmlFileInfo foo = new XmlFileInfo(aname, aline.substring(datepos, datepos
	// + 13).trim(),
	//						aline.substring(sizepos, sizepos + 12).trim());
	//				foo.getDate();
	//				ans.add(foo);
	//				//System.err.println(names[i].trim());
	//				//System.err.println(foo.toString());
	//				//System.err.println();
	//			}
	//		}
	//		catch (IOException e)
	//		{
	//
	//			e.printStackTrace();
	//		}
	//		catch (FTPException e)
	//		{
	//			e.printStackTrace();
	//			try
	//			{
	//				if (loggedInClient != null)
	//				{
	//					loggedInClient.quit();
	//				}
	//			}
	//			catch (IOException e1)
	//			{
	//
	//				e1.printStackTrace();
	//			}
	//			catch (FTPException e1)
	//			{
	//
	//				e1.printStackTrace();
	//			}
	//		}
	//		return ans;
	//	}

}