package org.ngbw.sdk.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;


/**
 Locks a file against concurrent access from multiple processes.  To use:

 Filelocks aren't re-entrant, in otherwords if one thread tries to acquire
 a lock that is already held by the program, an OverlappingFileLockException
 is thrown.

 Note - java makes no guarantee that a shared lock (i.e. read lock)  is indeed shared; 
 on some platforms it may in fact be an exclusive lock, so you need to implement logic 
 that assumes the lock is exclusive, even if you ask for shared.

 Closing the underlying file, even via a different channel may release the lock.
 It's best if possible to use this object to lock, then read/write the file, then call releaseLock to 
 close the file and release the lock.
 */
public class WholeFileLock
{
	public final static boolean WRITABLE = true;
	private RandomAccessFile lockedFile;
	private FileLock lock;
	private FileInputStream fis;
	private FileOutputStream fos;
	private String filename;
	private boolean writable;


	public FileInputStream getInputStream() throws IOException
	{
		lockedFile.seek(0);

		return fis;
	}

	public FileOutputStream getOutputStream() throws IOException
	{
		assert(writable);

		lockedFile.seek(0);

		return fos;
	}

	public WholeFileLock(final String filename, boolean writable) throws FileNotFoundException, IOException 
	{
		this.filename = filename;
		this.writable = writable;

		try
		{
			final String mode;
			final boolean shared;

			if (writable) {
				mode = "rw";
				shared = false;
			}
			else {
				mode = "r";
				shared = true;
			}

			lockedFile = new RandomAccessFile(filename, mode);
			lock = lockedFile.getChannel().lock(0L, Long.MAX_VALUE, shared);


			fis = new FileInputStream(lockedFile.getFD());

			if (writable)
				fos = new FileOutputStream(lockedFile.getFD());
		}
		catch (final IOException e)
		{
			cleanup();
			throw e;
		}
	}

	public boolean isWritable()
	{
		return writable;
	}

	public void truncate() throws IOException
	{
		lockedFile.setLength(0);
	}

	/**
		calls deleteOnExit on the underlying file so that the file will be deleted
		from the filesystem when the calling process exits. It will only be deleted
		if the jvm exits normally.  SIGINT (^c) is considered normal.

	*/
	public void deleteOnExit()
	{
		(new File(filename)).deleteOnExit();
	}

	public void releaseLock()
	{
		cleanup();
	}

	private void cleanup()
	{
		try
		{
			if (lock != null)
			{
				lock.release();
				lock = null;
			}
			
			if (lockedFile != null) {
				lockedFile.close();

				lockedFile = null;
				fis = null;
				fos = null;
			}
		}
		catch (final IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	/*
	public static void main(String[] args) throws Exception
	{
		WholeFileLock fl = new WholeFileLock("/tmp/t.txt", true);
		System.out.printf("Got the lock\n");

		Util.setOwnerAccessOnly("/tmp/t.txt");

		FileOutputStream out = fl.getOutputStream();
		out.write((new String("hi there\n")).getBytes());

		System.out.printf("Wrote some text, sleeping now.\n");
		Thread.sleep(10 * 1000);

		fl.cleanup();


		System.out.printf("All done\n");
	}



	public static void writeFile(String text, File file, boolean append) throws Exception
	{
		FileWriter fw = new FileWriter(file, append);
		boolean done = false;
		int offset = 0;
		int length;

		try
		{
			while((length = Math.min(offset + 1024,  text.length() - offset)) > 0)
			{
				fw.write(text, offset, length);
				offset += length;
			}
		}
		finally
		{
			fw.close();
		}
	}
	*/

	/**
		Requires 2 command line arguments:
		1. filename
		2. read OR write (where read means shared lock, and write means exclusive).
	*/
	public static void main(String[] args) throws Exception
	{
		String filename = args[0];
		boolean write_flag = args[1].equals("write");

		try
		{
			WholeFileLock lock = new WholeFileLock(filename, write_flag);

			System.out.printf("Locked %s for %s. ^C to kill\n", filename, (write_flag ?
				"write/exclusive" : "read/shared"));
			while(true)
			{
				Thread.sleep(1000);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
