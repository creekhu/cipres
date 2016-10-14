/*
	JobCharge.java

	On trestles and gordon:
	- jobs that run less than 10 seconds are free (this should be specified in _JOBINFO.TXT, but isn't.)
	- charge is rounded to the nearest SU, but jobs that get rounded to 0 are charged 1.  In other
	words, any job that runs over 10 sec is charged at least 1 SU.
	-5/15/2014 UPDATE: looking at db where su_charged = 0 and su_computed =1, we do seem to be charged for
	jobs that run less than 10sec.  Maybe it's more than 10 sec by the time the system is finished with it,
	so I'm going to remove our check for < 10 seconds. 

	- _JOBINFO.TXT has :
		- ChargeFactor, a float.  ChargeFactor happens to be 1 for trestles and gordon.
		- cores, an integer >=1. 

	- We get runtime, ideally by subtracting the timestamp in start.txt from the timestamp in
	done.txt (or term.txt).  However, we are having trouble reliably getting the timestamp into these files
	when jobs timeout or are killed.  Sometimes we seem to get it, sometimes we don't.  When it
	isn't available we'll use the runhours property from scheduler.conf, which is the max the
	job could have run.
	
	Per Glenn Lockwood, the actual xsede charge is cacl'd in perl as:

		$charge = $h->{Processors} * ( $h->{WallDuration_S} / 3600.0 ) * $h->{ChargeFactor};
		$charge = 0 if $charge < 0;

		$h{Charge} = $charge;

		if ( $h{Charge} < 1 ) {
		$h{Charge} = 1;
		}

	   This is carried as a double precision and then rounded (either up or down) to the nearest integer before going to the XDCDB.
	   I verified this myself against a bunch of jobs and they all checked out, so you can do

	   nodes * ppn * (wallhr + wallmin/60.0 + wallsec/3600.0)
	   and round to get the exact charge.

	So sounds to me(Terri) like SU must be an integer (since they round to the nearest SU)  and we can do: 
		if (seconds < 10)
			return SU = 0;
		return SU = min(round(cores * hours * chargeFactor), 1)
	where hours is a double and round means round to the nearest.
*/

package org.ngbw.sdk.tool;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.api.tool.FileHandler;
import org.ngbw.sdk.api.tool.FileHandler.FileAttributes;
import org.ngbw.sdk.api.tool.ToolRegistry;
import org.ngbw.sdk.database.RunningTask;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.TaskOutputSourceDocument;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.common.util.FileUtils;
import java.lang.Math;


public class JobCharge
{
	private static final Log log = LogFactory.getLog(JobCharge.class.getName());
	private static final String CHARGEFACTOR = "ChargeFactor";
	private static final String CORES = "cores";
	private static final String JOBINFO_TXT = "_JOBINFO.TXT";
	private static final String START_TXT = "start.txt";
	private static final String DONE_TXT = "done.txt";
	private static final String TERM_TXT = "term.txt";

	// No charge for runs < 10 seconds (convert to hours here).
	// UPDATE - not using these, since we do seem to be charged for at least some of these jobs
	private static final double MIN_CHARGE_HOURS = 10.0/3600.0;

	// max size we expect these files to be - we'll truncate after this:
	private static final int START_DONE_FILESIZE = 100;
	private static final int JOBINFO_FILESIZE = 4000;

	private String startString = null;
	private String doneString = null;
	private String termString = null;
	private String  jobInfoString = null;
	private Properties jobInfo;
	private FileHandler handler;
	private Double maxRunHours;
	String workingDir;
	String doneFilename;
	/*
		Todo: we pass in pw just so we can call its readFile method.  See comment in
		BaseProcessWorker.readFile about how this should be restructured.
	*/
	BaseProcessWorker pw; 
	
	JobCharge(BaseProcessWorker pw, Double maxRunHours)
	{
		this.pw = pw;
		this.handler = pw.fh;
		this.workingDir = pw.m_workingDir;
		this.maxRunHours = maxRunHours;
	}

	/*
		Returns Sus.  0 is a valid value if there is no charge (eg. job ran < 10 seconds).
		Returns NULL if we can't parse date from done.txt.  Not throwing an
			exception in this case because this happens too often and we don't want it to 
			stop loadResults from working.
		Throws exception in other cases, eg. can't read files from remote, can't find working dir, etc.
	*/
	public Long getCharge() throws Exception
	{
		double runHours;
		int cores;
		double chargeFactor;


		runHours = getRunHours();
		if (runHours == 0.0)
		{
			return 0L;
		}

		jobInfo = new Properties();
		jobInfo.load(new StringReader(getJobInfo()));
		try
		{
			cores = Integer.valueOf(jobInfo.getProperty(CORES));
		} 
		catch(Exception e)
		{
			throw new Exception(JOBINFO_TXT + " invalid or  missing integer property: " + CORES);
		}
		try
		{
			chargeFactor = Double.valueOf(jobInfo.getProperty(CHARGEFACTOR));
		}
		catch (Exception e)
		{
			throw new Exception(JOBINFO_TXT + " invalid or  missing float property: " + CHARGEFACTOR);
		}
		return calculateSUs(runHours, cores, chargeFactor);
	}

	// This is also called from Tool.java getPredictedSus
	public static Long calculateSUs(double  runHours, int cores, double chargeFactor)
	{
		/*
		if (runHours < MIN_CHARGE_HOURS)
		{
			log.debug("Run hours of " + runHours + " is less than 10.0 seconds =  " + MIN_CHARGE_HOURS + " hrs, so charge is 0"); 
			return 0L;
		} else
		{
			log.debug("runHours=" + runHours + " is not less than 10 seconds=" + MIN_CHARGE_HOURS + " hours");
		}
		*/
		long retval = Math.max(Math.round(cores * runHours * chargeFactor), 1);
		log.debug("cores=" + cores + ", ChargeFactor=" + chargeFactor + ", hour=" + runHours + ", product=" + 
			(cores * runHours * chargeFactor) + ", rounds to =" + retval);
		return retval;
	}

	public Double getRunHours() throws Exception
	{
		try
		{
			return getActualRunHours();
		}
		catch(Exception e)
		{
			if (this.maxRunHours == null)
			{
				throw new Exception("Couldn't get actual runHours and couldn't get maximum runhours from scheduler.conf");
			}
			log.debug("Unable to get actual runhours: " + e.toString() + ", using maximum value of " + 
				this.maxRunHours);
			return this.maxRunHours;
		}
	}

	/*
		Returns runhours or 0L if start.txt not found.  Throws exception
		if other files not found or if unable to parse date from them.
	*/
	public double getActualRunHours() throws Exception
	{
		long start;
		long end;
		try
		{
			start = readTimeStamp(workingDir + "/" + START_TXT);
		}
		catch(FileNotFoundException fe)
		{
			log.info(START_TXT + " not found.  Job was probably cancelled before it started");
			return 0L;
		}
		try
		{
			end = readTimeStamp(workingDir + "/" + DONE_TXT);
		}
		catch(Exception e)
		{
			end = readTimeStamp(workingDir + "/" + TERM_TXT);
		}
		long runseconds = end - start ;
		log.debug("end=" + end + " - start=" + start + " is " + (end - start) + " seconds.  Hours=" + (runseconds / 3600.0));
		return runseconds / 3600.0;
	}

	/*
	*/
	public long readTimeStamp(String filename) throws FileNotFoundException, Exception
	{
		InputStream is = null;
		String str;
		long date;
		try
		{
			log.debug("Trying to read: " + filename);
			is  = pw.readFile(filename, this.handler);
			if (is == null)
			{
				log.debug("got null");
				throw new FileNotFoundException(filename);
			}
			str = FileUtils.streamToString(is, START_DONE_FILESIZE);
			date = parseDate(str);
			log.debug("read " + date);
			return date;
		}
		catch(Exception e)
		{
			log.warn("Error extracting date from " + filename + ": " + e.toString());
			throw e;
		}
		finally
		{
			if (is != null) { try { is.close(); } catch(Exception ee) { } }
		}
	}

	public String getJobInfo() throws FileNotFoundException, Exception
	{
		InputStream is = null;
		try
		{
			is  = pw.readFile(workingDir + "/" + JOBINFO_TXT, this.handler);
			if (is == null)
			{
				throw new FileNotFoundException(JOBINFO_TXT);
			}
			return FileUtils.streamToString(is, JOBINFO_FILESIZE);
		}
		finally
		{
			if (is != null) { try { is.close(); } catch(Exception ee) { } }
		}
	}





	/*
		First word in start.txt, done.txt, term.txt is expected to
        seconds since epoch - a long value.
	*/
	private long parseDate(String str) throws Exception
	{
		// Bryan put seconds since beginning of epoch in these files (Java Date(long) takes milliseconds). 
		Scanner scanner = new Scanner(str);
		return scanner.nextLong();
	}
}	


