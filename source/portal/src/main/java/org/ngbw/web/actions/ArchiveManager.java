package org.ngbw.web.actions;

import org.apache.log4j.Logger;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.String;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.ngbw.sdk.database.SourceDocument;
import org.ngbw.sdk.database.UserDataItem;

@SuppressWarnings("serial")
public class ArchiveManager
extends DataManager
{
	/*================================================================
	 * Constants
	 *================================================================*/
	private static final Logger logger = Logger.getLogger(DataManager.class.getName());
	public static final String ARCHIVE_CONTENT_TYPE = "application/zip";
	public static final int BUFFER_SIZE = 2048;

	/*================================================================
	 * Data download property accessor methods
	 *================================================================*/
	public String getFilename() {
		return "Cipres_Data.zip";
	}

	public String getContentType() {
		return ARCHIVE_CONTENT_TYPE;
	}


	public long getDataLength() {
		return 0;
	}

	/**
	 * @modified Mona - added checking for duplicate filename and will not
	 * add any duplicate entry found to the zip archive
	 **/
	public InputStream getInputStream() 
	{
		// set up piped input/output streams
		PipedInputStream input = new PipedInputStream();
		final PipedOutputStream output;
		try
		{
			output = new PipedOutputStream(input);
		}
		catch (Throwable error) 
		{
			reportError(error, "Error opening archive file output stream");
			return null;
		}
		
		// have to set up piped output stream in a separate thread
		new Thread(
			new Runnable() 
			{
				public void run()
				{
					// set up zip output stream
					ZipOutputStream zip =
						new ZipOutputStream(new BufferedOutputStream(output));
					byte[] buffer = new byte[BUFFER_SIZE];
					// get selected data items
					List<SourceDocument> documents = null;
					try 
					{
						documents = getSelectedDocuments();
					}
					catch (Throwable error) 
					{
						reportUserError(error, "Error retrieving data items to be archived");
						closeArchive(zip);
						return;
					}
					finally 
					{
						if (documents == null || documents.size() < 1) 
						{
							reportUserError("Error retrieving documents to " + "be archived: No documents were selected.");
							closeArchive(zip);
							return;
						}
					}
					
					// read selected data items into zip archive
					BufferedInputStream source = null;
					List <String> filenames_list = new ArrayList();
					List <String> duplicate_filenames = new ArrayList();
					
					for (SourceDocument document : documents) 
					{
						InputStream dataStream = null;
						try 
						{
							dataStream = document.getDataAsStream();
							source = new BufferedInputStream(dataStream, BUFFER_SIZE);
						}
						catch (Throwable error) 
						{
							reportError(error, "Error retrieving data input " + "stream from document with ID " +
								document.getSourceDocumentId());
							closeSource(dataStream);
							closeArchive(zip);
							return;
						}
						String filename = null;
						if (document instanceof UserDataItem)
						{
							filename = ((UserDataItem)document).getLabel();
						}
						else 
						{
							filename = document.getName();
						}
						
						// If filename already exists, don't add it to the archive
						if ( filenames_list.contains ( filename ) )
						{
							duplicate_filenames.add ( filename );
							continue;
						}
						else
							filenames_list.add ( filename );

						try 
						{
							filename = URLEncoder.encode(filename, "UTF-8");
						} 
						catch (UnsupportedEncodingException error) 
						{
						}
						
						ZipEntry entry = new ZipEntry(filename);
						try 
						{
							zip.putNextEntry(entry);
						}
						catch (Throwable error) 
						{
							reportError(error, "Error inserting zip entry for document with ID " +
								document.getSourceDocumentId());
							closeSource(source);
							closeArchive(zip);
							return;
						}
						int count;
						try 
						{
							while ((count = source.read(buffer, 0, BUFFER_SIZE)) != -1)
							{
				            	zip.write(buffer, 0, count);
							}
						}
						catch (Throwable error)
						{
							reportError(error, "Error copying contents of " + "document with ID " +
								document.getSourceDocumentId() +
								" into zip entry");
							closeArchive(zip);
							return;
						}
						finally
						{
							closeSource(source);
						}
						debug("SourceDocument with ID " + document.getSourceDocumentId() + " successfully added to zip archive.");
					}
					closeArchive(zip);
					
					/* Decided not to show message; see FogBugz
					 * http://phylo-bugz.sdsc.edu/fogbugz/default.asp?359071#BugEvent.841311
					if ( duplicate_filenames.size() > 0 )
						reportUserError
							( "Duplicate files are not included in the archive." );
					 */
				}
			}
		).start();
		
		return input;
	}

	protected void closeArchive(ZipOutputStream zip) {
		try {
			zip.close();
		} catch (Throwable error) {
			reportError(error, "Error closing zip archive");
			return;
		}
	}

	protected void closeSource(InputStream source)
	{
		if (source != null)
		{
			try
			{
				source.close();
			}
			catch(Throwable error)
			{
				return;
			}
		}
	}

}
