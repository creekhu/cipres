package org.ngbw.tus;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.UserDataItem;
import org.tus.servlet.upload.Config;
import org.tus.servlet.upload.FileInfo;
import org.tus.servlet.upload.Locker;
import org.tus.servlet.upload.Store;
import org.tus.servlet.upload.TusException;


/*
	TODO:
		- test what happens when there are database errors.  Does run() get SQLException?

		- send ourselves email when exceptions occur.  Retries won't happen until
		this app is restarted and it scans the directory again.
*/
public class DBDatastore extends Store
{
	private static final Logger log = Logger.getLogger(DBDatastore.class.getName());
	private static final int POOL_SIZE = 10;
	private static final String IMPORT_FOLDER = "Uploads";
	private static final int MAX_LOCK_RETRIES = 10;

	private ThreadPoolExecutor pool;
	private int poolsize = POOL_SIZE;
	private String importFolder = IMPORT_FOLDER;

	/*
		On startup, create a pool of worker threads that will move completed uploads into the cipres
		database.  On startup it also needs to scan for completed uploads and put them in the work
		queue.  This is because the service may have been killed with pending uploads in the queue.

		Once running, files are added to the queue as they finish being uploaded.

		This is called by the upload servlet init() method.
	*/
	@Override
	public void init(Config config, Locker locker) throws Exception
	{
		super.init(config, locker);

		// Instantiates the database connection pool.
		Workbench.getInstance();

		Long l;
		String s;

		l = config.getLongValue("dbdatastore.poolSize");
		poolsize = (l == null) ? POOL_SIZE : (int)l.longValue();

		s = config.getStringValue("dbdatastore.importFolder");
		importFolder = (s == null) ? IMPORT_FOLDER : s;

		log.debug("threadpoolsize=" + poolsize + ", importFolder=" + importFolder);

		// This would be a good place to remove uploads older than 2 weeks?

		pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(poolsize);
		enqueueFinishedUploads();
	}

	/*
		Shutdown the thread pool.
	*/
	@Override
	public void destroy() throws Exception
	{
		log.debug("Shutdown threadpool");
		pool.shutdownNow(); // disable new tasks from being submitted and interrupt running threads.

		// Wait briefly for tasks to respond to being cancelled
		if (!pool.awaitTermination(1, TimeUnit.SECONDS))
		{
			log.error("Threads still running");
		}
		super.destroy();
		log.debug("Leaving servlet destroy");
	}

	/*
		Add the file to the work queue.
	*/
	@Override
	public void finish(String id) throws Exception
	{
		log.debug("Queue a worker job for " + id);
		pool.execute(this.new Worker(id));
	}


	private void enqueueFinishedUploads() throws Exception
	{
		List<FileInfo> fiList = getCompletedUploads();

		/*
			Find the ones that haven't been imported yet by checking to see if there's
			a UserDataItem in the db with comment = file ID.
		*/
		Iterator<FileInfo> it = fiList.iterator();
        while(it.hasNext())
        {
            FileInfo fileInfo = it.next();
			List<UserDataItem> matches = UserDataItem.findDataItemsWithComment(fileInfo.id);
			if (matches.size() == 0)
			{
				finish(fileInfo.id);
			} else
			{
				log.info("DON'T Add " + fileInfo.id + " to import queue.  Already imported.");
			}
        }
	}

	/*
		This runs on a thread pool thread, not the http request thread.
		Need to lock the upload while we're doing this; otherwise the
		user doing a DELETE could remove the file out from under us.

		It's possible that user has done a DELETE before this code
		starts so we need to check whether the upload still exists.

		Returns true if upload is imported to the DB, false if there's a reason
		we'll never be able to import it (like missing username in the .info).
	*/
	private void importToDB(String id) throws Exception
	{
		boolean locked = false;
        try
        {
			int retries = 0;
			locked = locker.lockUpload(id);
			while (!locked && retries < MAX_LOCK_RETRIES)
			{
				// Someone may be doing a HEAD or a TERMINATE, wait 50 millisecs  ..
				Thread.sleep(50);
				locked = locker.lockUpload(id);
				retries++;
			}
            if (!locked)
            {
                log.error("Couldn't lock " + id);
                throw new TusException.FileLocked();
            }
            whileLocked(id);
        }
        finally
        {
            if (locked)
            {
                locker.unlockUpload(id);
            }
        }
	}

	private void whileLocked(String id) throws Exception
	{
		FileInfo fileInfo = getFileInfo(id);

		if (fileInfo == null)
		{
			// The upload was deleted. Nothing more to do.
			log.info("Upload " + id + " must have been deleted.");
			return;
		}

		String username = fileInfo.username;
		if (username == null)
		{
			log.error("FileInfo for " + id + " doesn't have a username!  Removing file.");
			terminate(id);
			return;
		}
		User user = User.findUserByUsername(username);
		if (user == null)
		{
			log.info("User " + username + " who owns file: " + id + " doesn't exist. Removing file.");
			terminate(id);
			return;
		}
		log.debug("Creating a UserDataItem from: " + getBinPath(id));

		UserDataItem udi = new UserDataItem(getBinPath(id), user.getHomeFolder().findOrCreateSubFolder(importFolder));
		if (fileInfo.suggestedFilename == null)
		{
			log.debug("Set UserDataItem label=" + id);
			udi.setLabel(id);
		}
		else 
		{
			log.debug("Set UserDataItem label=" + fileInfo.suggestedFilename);
			udi.setLabel(fileInfo.suggestedFilename);
		}
		udi.setComment(id);
		udi.save();
		log.debug("Created user data item for upload " + id + ": " + udi.getUserDataId());

		// Removes .info and then .bin file (although UserDataItem will have already mv'd the .bin).
		terminate(id);

	}


	private class Worker implements Runnable
	{
		String id;

		Worker(String id)
		{
			this.id = id;
		}

		public void run()
		{
			NDC.push("[" + id + "] ");
			log.debug("db import running");
			try
			{
				importToDB(id);
			}
			catch(InterruptedException ie)
			{
				log.info("Interrupted processing of " + id);
				Thread.currentThread().interrupt();
			}
			// We want to be alerted to database errors and we'll want to try the import again later.
			// For the time being, the easiest way to retry is to leave the uploaded file on disk
			// and restart this application.  Maybe this is how we want to handle all exceptions.
			catch(Exception e)
			{
				log.error("", e);
			}
			finally
			{
				log.debug("db import finished");
				NDC.pop();
				NDC.remove();
			}
		}
	}

}
