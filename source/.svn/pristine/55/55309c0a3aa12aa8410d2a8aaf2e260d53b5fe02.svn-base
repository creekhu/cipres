package org.ngbw.tus;

import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.tus.servlet.upload.Config;
import org.tus.servlet.upload.FileInfo;
import org.tus.servlet.upload.Locker;
import org.tus.servlet.upload.Store;


/*
	Extends the basic file based Tus Store class to do some cleaning up on startup.
	Removes uploads that were last written to more than EXPIRE_DAYS ago.  The assumption
	is that these were abandoned upload attempts.
*/
public class SimpleDatastore extends Store 
{
	private static final Logger log = Logger.getLogger(SimpleDatastore.class.getName());
	private static final int MAX_LOCK_RETRIES = 10;
	private static final long EXPIRE_DAYS = 2;
    private static final long MILLISECS_PER_DAY = 24L * 60L * 60L * 1000L;
    private static final long EXPIRE_MILLI_SECS = EXPIRE_DAYS * MILLISECS_PER_DAY;


	@Override 
	public void init(Config config, Locker locker) throws Exception
	{
		super.init(config, locker);
		try
		{
			cleanup();
		}
		catch(Exception e)
		{
			log.error("", e);
			throw e;
		}

	}

	private void cleanup() throws Exception
    {
        Iterator<FileInfo> it = getAllUploads().iterator();
        while(it.hasNext())
        {
            FileInfo fileInfo = it.next();

			long diff = new Date().getTime() - fileInfo.lastModified;
			if (diff > EXPIRE_MILLI_SECS)
			{
				terminateUpload(fileInfo.id);
			}
        }
    }

    private void terminateUpload(String id) throws Exception
    {
        boolean locked = false;
        try
        {
            locked = locker.lockUpload(id);
            if (locked)
            {
                terminate(id);
            }
        }
        finally
        {
            if (locked)
            {
                locker.unlockUpload(id);
            }
        }
    }


}
