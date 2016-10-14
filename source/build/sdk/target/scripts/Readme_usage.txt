cipres runs usage.sh from cron nightly to get information from the XSEDE (tgusage) database
about how many SUs each CIPRES job has consumed and to match this up with our users to keep
track of how many SUs each user has consumed.  

usage.sh
	1. cds to ~/tgusage.
	2. Runs tgusage.sh to get data from XSEDE accounting database and import it to cipres db 
	in 'tgusage' table.
	3. Makes a number of calls to usage.py to create .csv files in ~/tgusage
	containing su/per user for cipres and iplant accounts.  
	4. Uses usage.py to send overlimit warning emails to users. 

tgusage.sh
	Does an ssh to cipres@trestles, cd's to ~/tgusage there, and runs the cipres-tgusage command to
	create tgusage_report.txt, then copies the file back to the local machine (~/tusage directory)
	and runs importTgUsage to import the data to the cipres db, 'tgusage' table.

	cipres-tgusage is a copy of the 'official' tgusage script that I modified to give the exact
	data I wanted, in a format I found easier to parse.  I believe the same data can be generated 
	with the standard 'tgusage' command, or its XSEDE replacement 'xdsusage'.

importTgusage
	Wrapper to run the  org.ngbw.utils.ImportTgusage class.

usage.py
	Runs queries that join job_stats and tgusage tables to report on number of SUs
	consumed by users for different time periods.  Also sends overlimit emails.

mailer.py
	Originally this was a python script just for sending overlimit warnings.
	Later I added a general purpose 'sendmail' method so that I could also
	use it to send mail from deleteUserData.py

allocation_message?.txt
	These contain the email mesage body for the overlimit warning messages.
	Different ones for different SU cutoff points.  The .rtf files aren't used.

cipres-tgusage 
	Modified version of 'tgusage' command which is usually installed in a system location
	on xsede hosts.  My version is installed at ~cipres/bin/cipres-tgusage.  There's a copy 
	in svn at sdk/scripts/remote_resource/trestles/cipres-tgusage-new.  


