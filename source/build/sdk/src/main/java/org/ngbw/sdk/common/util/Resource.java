package org.ngbw.sdk.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * Resource is a wrapper class to access files or url resources. <br />
 * 
 * @author hannes
 * @author Terri Liebowitz (added some methods)
 * 
 */
public class Resource {

	private static final Log log = LogFactory.getLog(Resource.class);
	private static final String FILE = "file";
	private static final String FTP = "ftp";
	private static final String JAR = "jar";
	private static final Pattern filePattern = Pattern.compile("^file:");
	private static final Pattern urlPattern = Pattern.compile("^[a-z]{3,4}://");
	private static final Pattern xmlPattern = Pattern.compile("\\.xml");
	private URL url;
	private File file;
	private boolean xml = false;
	private boolean gz = false;
	private boolean Z = false;

	private static ClassLoader currentClassLoader = Resource.class.getClassLoader();

	/**
		Returns all resources in the specified directory whose names match the
		regular expression.  This is not recursive, so 
			getResources("tools/", ".*\\.xml")
		will return all files that end in ".xml" that are in the tools directory,
		but not in any subdirectories of "tools/".

		The directory is located by loading it as a resource from the classpath.
		At the moment this only works for local filesystem resources, not jar
		resources.  If there are multiple instances of the directory on the
		classpath, only the first is searched.

		@param String directory
		@param String re
		@return URL[]
	*/
	public static URL[] listResources(String directoryName, String re) throws Exception
	{
		log.debug("listResources " + directoryName + ", " + re);
		URL resource = currentClassLoader.getResource(directoryName);
		if (resource == null)
		{
			log.debug("Looking for extra tools, didn't find resource " + directoryName);
			return new URL[0];
		}
		log.debug("Looking for extra tools, found directory " + directoryName + " at " + resource.getFile());
		File dir = new File(resource.getFile());

		List<URL> urls = findResources(dir, directoryName, re);
		return urls.toArray(new URL[urls.size()]);
	}


	/**
		Finds resources having names that match the specified pattern.

		@re must have the form <directory>/<regular expression>.  For example
		getMatchingResources("tools/.*\\.xml") returns all resources in the
		tools directory that end with ".xml".

		The directory is located by loading it as a resource from the classpath.
		At the moment this only works for local filesystem resources, not jar
		resources.  If there are multiple instances of the directory on the
		classpath, only the first is searched.


	*/
	public static Resource[] getMatchingResources(String repath) throws Exception
	{
		log.debug("getMatchingResources for " + repath);
		int i = repath.lastIndexOf('/');
		if (i < 0 || (i ==  (repath.length() - 1)))
		{
			return new Resource[0];
		}
		String directoryName = repath.substring(0, i + 1);
		String re = repath.substring(i + 1, repath.length());

		URL[] urls = listResources(directoryName, re);
		ArrayList<Resource> resources = new ArrayList<Resource>(urls.length);
		for (URL url : urls)
		{
			resources.add(new Resource(url));
		}
		return resources.toArray(new Resource[resources.size()]);
	}

	private static List<URL> findResources(File dir, String directoryName, String re) 
	{
		List<URL> urls = new ArrayList<URL>();

		if (!dir.exists())
		{
			return urls;
		}
		File[] files = dir.listFiles();
		for (File file : files)
		{
			if (file.isDirectory())
			{
				continue;
			} else if (file.getName().endsWith(".class"))
			{
				continue;
			} else if (file.getName().matches(re))
			{
				String name = directoryName  +  file.getName();
				URL url = currentClassLoader.getResource(name);

				urls.add(url);
				System.out.println("Adding url = " + url.toString());
			}
		}
		return urls;
	}

	public static Resource getResource(String resourceName) 
		throws ResourceNotFoundException {
		if (resourceName == null)
			throw new NullPointerException("resourceName argument is null");
		Resource resource = new Resource(resourceName);
		if (resource.validate() == false)
			throw new ResourceNotFoundException("Resource " + resourceName
					+ " does not exist or cannot be accessed!"); 
		resource.classify();
		return resource;
	}

	public static Resource getResource(URL url) 
		throws ResourceNotFoundException {
		if (url == null)
			throw new NullPointerException("url argument is null");
		Resource resource = new Resource(url);
		if (resource.validate() == false)
			throw new ResourceNotFoundException("Resource " + url
					+ " does not exist or cannot be accessed!"); 
		resource.classify();
		return resource;
	}

	public static Resource getResource(File file) 
		throws ResourceNotFoundException {
		if (file == null)
			throw new NullPointerException("file argument is null");
		Resource resource = new Resource(file);
		if (resource.validate() == false)
			throw new ResourceNotFoundException("Resource " + file
					+ " does not exist or cannot be accessed!"); 
		resource.classify();
		return resource;
	}

	public static Resource getUnvalidatedResource(String resourceName) {
		if (resourceName == null)
			throw new NullPointerException("resourceName argument is null");
		Resource resource = new Resource(resourceName);
		resource.classify();
		return resource;
	}

	public static Resource getUnvalidatedResource(URL url) {
		if (url == null)
			throw new NullPointerException("url argument is null");
		Resource resource = new Resource(url);
		resource.classify();
		return resource;
	}

	public static Resource getUnvalidatedResource(File file) {
		if (file == null)
			throw new NullPointerException("file argument is null");
		Resource resource = new Resource(file);
		resource.classify();
		return resource;
	}

	/**
	 * Construct a new Resource from the submitted resource name. The
	 * constructor will try to detect whether it is a file or url resource. For
	 * a file resource it will first try to access it with the general
	 * ClassLoader, then with the Resource.class ClassLoader and finally via a
	 * File instance.
	 * 
	 * @param resourceName
	 */
	private Resource(String resourceName) {
		handleResourceName(resourceName);
	}

	/**
	 * Construct a new Resource from the submitted resource URL.
	 * 
	 * @param resourceURL
	 * @throws ResourceNotFoundException 
	 */
	private Resource(URL resourceURL) {
		if (resourceURL == null)
			throw new NullPointerException(
					"The submitted constructor argument is null!");
		if (FILE.equalsIgnoreCase(resourceURL.getProtocol())) {
			file = new File(resourceURL.getPath());
			if (file.exists()) 
				url = null;
			else {
				file = null;
				url = resourceURL;
			}
		}
		else
			url = resourceURL;
	}

	/**
	 * Construct a new Resource from the submitted resource file.
	 * 
	 * @param resourceFile
	 * @throws ResourceNotFoundException 
	 */
	private Resource(File resourceFile) {
		if (resourceFile == null)
			throw new NullPointerException(
					"The submitted constructor argument is null!");
		file = resourceFile;
	}

	/**
	 * Method return either the name of a file or the external form of a URL.
	 * 
	 * @return name
	 */
	public String getName() {
		if (url == null)
			return file.getPath().replaceFirst("file:", "");
		else
			return url.toExternalForm();
	}

	/**
	 * Method attempts to load Properties from the resource. Call this method if
	 * you are sure that you deal with a plain or xml properties file.
	 * 
	 * @return properties
	 */
	public Properties getProperties() {
		Properties properties = new Properties();
		try {
			if (xml)
				properties.loadFromXML(getInputStream());
			else
				properties.load(getInputStream());
			return properties;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	@SuppressWarnings("rawtypes")
	public Map<String,String> getPropertiesAsMap()
	{
		@SuppressWarnings("unchecked")
		Map<String, String> myMap = new HashMap(this.getProperties());
		return myMap;
	}

	/**
	 * Method gives access to the InputStream from this resource.
	 * 
	 * @return is
	 */
	public InputStream getInputStream() {
		if (isDirectory())
			throw new RuntimeException("Resource is not a file");
		InputStream is;
		if (url == null) {
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Cannot find file "
						+ file.getAbsolutePath(), e);
			}
		} else {
			try {
				is = url.openStream();
			} catch (IOException e) {
				throw new RuntimeException("Cannot read from url " + url, e);
			}
		}
		try {
			if (Z)
				is = new UncompressInputStream(is);
			else if (gz)
				is = new GZIPInputStream(is);
		} catch (IOException e) {
			throw new RuntimeException(
					"Error dispatching the stream from the resource!", e);
		}
		return is;
	}

	public BufferedReader getReader() {
		InputStreamReader isReader = new InputStreamReader(getInputStream());
		BufferedReader br = new BufferedReader(isReader);
		return br;
	}

	/**
	 * Methods reads all the bytes from the InputStream of this resource into a
	 * byte array.
	 * 
	 * @return bytes
	 */
	public byte[] getBytes() {
		InputStream is = getInputStream();
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int bytesRead = 0;
		try {
			while (0 < (bytesRead = is.read(buffer))) {
				os.write(buffer, 0, bytesRead);
				buffer = new byte[1024];
			}
			os.flush();
			return os.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("Error reading from resource!", e);
		}
	}

	/**
	 * Methods reads all the bytes from the InputStream of this resource into a
	 * byte array and return them as String.
	 * 
	 * @return content
	 */
	public String getString() {
		return new String(getBytes());
	}

	/**
	 * Method return a new Resource instance with the submitted fileName
	 * argument appended to the file/url path.
	 * 
	 * @param fileName
	 * @return extendedResource
	 * @throws ResourceNotFoundException 
	 */
	public Resource extend(String fileName) throws ResourceNotFoundException {
		return extend(fileName, true);
	}
	
	public Resource extend(String fileName, boolean validate) throws ResourceNotFoundException {
		if (fileName == null)
			throw new NullPointerException(
					"The submitted fileName argument is null!");
		String resourceName = getName();
		if (resourceName.endsWith("/") == false)
			resourceName += "/";
		if (validate)
			return Resource.getResource(resourceName + fileName);
		return Resource.getUnvalidatedResource(resourceName + fileName);
	}

	public String[] listDirectoryEntries(String fileName) {
		ArrayList<String> fileList = new ArrayList<String>();
		String[] files;
		FilenameFilter filter = new SimpleFilenameFilter(fileName);
		// its a file
		if (url == null) {
			if (file.isFile())
				throw new RuntimeException("This resource is NOT a directory!");
			else {
				files = file.list(filter);
			}
			// its a url
		} else {
			String protocol = url.getProtocol();
			if (FTP.equals(protocol)) {
				FTPClient ftp = getFTPClient(url.getHost());
				try {
					if (ftp.changeWorkingDirectory(url.getPath()) == false)
						throw new RuntimeException(
								"This resource is NOT a directory!");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				FTPFile[] ftpFiles;
				try {
					ftpFiles = ftp.listFiles();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				for (FTPFile ftpFile : ftpFiles) {
					if (ftpFile.isDirectory())
						continue;
					String name = ftpFile.getName();
					if (filter.accept(null, name))
						fileList.add(name);
				}
				closeFTPClient(ftp);
				files = new String[fileList.size()];
				files = fileList.toArray(files);
			} else {
				throw new RuntimeException(getName() + " The protocol "
						+ protocol + " is not supported yet!");
			}
		}
		return files;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName();
	}

	public boolean isLocalFile() {
		return file != null;
	}

	public File getLocalFile() {
		if (isLocalFile())
			return file;
		throw new RuntimeException("Resource is not a local file or directory");
	}

	public URL getURL() {
		if (url == null)
			try {
				return file.toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(
						"Cannot convert file into URL for resource: "
								+ getName(), e);
			}
		else
			return url;
	}

	public boolean isDirectory() {
		boolean isDirectory;
		if (url == null) {
			isDirectory = file.isDirectory();
		} else {
			if (JAR.equalsIgnoreCase(url.getProtocol()))
					return false;
			else if (FILE.equalsIgnoreCase(url.getProtocol()))
				return false;
			FTPClient ftp = getFTPClient(url.getHost());
			try {
				isDirectory = ftp.changeWorkingDirectory(url.getPath());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			closeFTPClient(ftp);
		}
		return isDirectory;
	}

	private void handleResourceName(String resourceName) {
		if (resourceName == null)
			throw new NullPointerException(
					"The submitted resourceName constructor argument is null!");
		if (filePattern.matcher(resourceName).find()) {
			file = new File(resourceName.replaceFirst("file:", ""));
		} else if (urlPattern.matcher(resourceName).find()) {
			try {
				url = new URL(resourceName);
				if (FILE.equals(url.getProtocol())) {
					file = new File(url.getPath());
					if (file.exists()) 
						url = null;
					else 
						file = null;
				}
			} catch (MalformedURLException e) {
				throw new RuntimeException("Invalid URL");
			}
		} else {
			url = Resource.class.getClassLoader().getResource(resourceName);
			if (url == null)
				url = Resource.class.getResource(resourceName);
			if (url == null)
				file = new File(resourceName);
			else
				file = new File(url.getPath());
			if (file.exists()) 
				url = null;
			else 
				if (url != null) file = null;
		}
	}

	/*
	 * Method validates the constructor arguments and makes sure there are
	 * actually data present.
	 */
	private boolean validate() {
		if (url == null) {
			return file.exists();
		} else {
			URLConnection con;
			try {
				con = url.openConnection();
				con.setConnectTimeout(0);
				con.connect();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	/*
	 * Method determines whether the submitted resource is an xml or/and
	 * compressed file.
	 */
	private void classify() {
		String input;
		if (file == null && url == null)
			throw new NullPointerException("No file or url");
		else if (url == null)
			input = file.getName();
		else
			input = url.getFile();

		if (xmlPattern.matcher(input).find())
			xml = true;

		if (input.endsWith(".Z"))
			Z = true;
		else if (input.endsWith(".gz"))
			gz = true;
		else if (input.endsWith(".zip"))
			throw new RuntimeException("File can contain multiple zip entries");
	}

	private void closeFTPClient(FTPClient ftp) {
		try {
			ftp.disconnect();
		} catch (IOException e) {
			// ignore
			e.printStackTrace();
		}
	}

	private FTPClient getFTPClient(String host) {
		FTPClient ftp = new FTPClient();
		try {
			ftp.connect(host);
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new RuntimeException("FTP server refused connection.");
			}
			if (!ftp.login("anonymous", "bot@ngbw.org")) {
				throw new RuntimeException("Unable to log in to FTP server.");
			}
			ftp.enterLocalPassiveMode();
		} catch (SocketException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return ftp;
	}

	public static void main(String[] args) throws ResourceNotFoundException {
		Resource res = Resource.getResource(
				"ftp://ftp.ebi.ac.uk/pub/databases/swissprot/release/");
		for (String r : res.listDirectoryEntries("uniprot_sprot.dat.gz")) {
			System.err.println(r);
		}
	}

}
