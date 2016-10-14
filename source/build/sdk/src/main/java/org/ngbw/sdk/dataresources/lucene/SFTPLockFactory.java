package org.ngbw.sdk.dataresources.lucene;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;

class SFTPLockFactory extends LockFactory {

	private static Log log = LogFactory.getLog(SFTPLockFactory.class);
	private static final String lockFileName = "write.lock";
	SFTPDirectory directory;

	SFTPLockFactory(SFTPDirectory directory) {
		this.directory = directory;
	}

	/* (non-Javadoc)
	 * Attempt to clear (forcefully unlock and remove) the specified lock. 
	 * Only call this at a time when you are certain this lock is no longer in use.
	 * 
	 * @see org.apache.lucene.store.LockFactory#clearLock(java.lang.String)
	 */
	@Override
	public void clearLock(String lockDirName) throws IOException {
		if (directory.fileExists(lockFileName)) {
			if (log.isDebugEnabled())
				log.debug(lockFileName + " exists");
			directory.deleteFile(lockFileName);
		}
		if (directory.fileExists(lockFileName)) {
			log.error(lockFileName + " exists");
		}
	}

	/* (non-Javadoc)
	 * Return a new Lock instance identified by lockName.
	 *     
	 * @see org.apache.lucene.store.LockFactory#makeLock(java.lang.String)
	 */
	@Override
	public Lock makeLock(String lockDirName) {
		Lock lock = new SFTPLock(directory, lockFileName);
		try {
			lock.obtain();
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
		return lock;
	}

};

class SFTPLock extends Lock {

	private static Log log = LogFactory.getLog(SFTPLock.class);
	private SFTPDirectory directory;
	private String lockFileName;

	public SFTPLock(SFTPDirectory directory, String lockFileName) {
		if (log.isDebugEnabled())
			log.debug("get new instance");
		this.directory = directory;
		this.lockFileName = lockFileName;
	}

	/* (non-Javadoc)
	 * Returns true if the resource is currently locked. 
	 * Note that one must still call obtain() before using the resource.
	 * 
	 * @see org.apache.lucene.store.Lock#isLocked()
	 */
	@Override
	public boolean isLocked() {
		if (log.isDebugEnabled())
			log.debug("is it locked");
		//check whether the lock file exists in the
		//locked directory
		try {
			return directory.fileExists(lockFileName);
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	/* (non-Javadoc)    
	 * Attempts to obtain exclusive access and immediately return upon success or failure.
	 * Returns: true if exclusive access is obtained
	 * 
	 * @see org.apache.lucene.store.Lock#obtain()
	 */
	@Override
	public boolean obtain() throws IOException {
		if (log.isDebugEnabled())
			log.debug("lock obtained");
		// TODO Auto-generated method stub
		//place the marker lock file in the directory
		//that is to be locked
		directory.touchFile(lockFileName);
		return isLocked();
	}

	/* (non-Javadoc)
	 * Releases exclusive access.
	 * @see org.apache.lucene.store.Lock#release()
	 */
	@Override
	public void release() {
		if (log.isDebugEnabled())
			log.debug("lock released");
		// TODO Auto-generated method stub
		//remove the marker lock file from the directory
		//that was locked
		try {
			directory.deleteFile(lockFileName);
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}
}
