#!/bin/bash

# Use curl to get job status of a job submitted with job.sh. Also to list working dir, download results, etc.
# This is meant for manual testing.  You'll need to pass in the jobhandle, document to set JOB and DOC vars.

if [[ $# < 2 ]] ; then
	echo "required command line arguments cipresadmin username and password."
	echo "Additional args depend on fn you call.   Can be job, then document"
	exit 1
fi
source "./testfw.sh" 

# TODO: add these to our config info
EU=terri
EUEMAIL=terriberry@yahoo.com
EUINST="SDSC"

APP=$GUIDEMO_APP
APPUSER=$GUIDEMO_ADMIN
APPPASS=$TESTPASS

# This is set in testfw.sh/config
APPKEY=$GUIDEMO_APPKEY

JOB=$3
DOC=$4


listJobs()
{
	echo "\n=================================================================="
	echo "Atest1: List jobs for $APP.$EU "
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \ 
#		"$URL/job/$APP.$EU" \
		"$URL/job/$APP.$EU?expand=true" \
	)
	shouldpass command
}

listGuiDemoJobs()
{
	echo "\n=================================================================="
	echo "listGuiDemoJobs: List all jobs for $APPUSER"
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" -H "cipres-appkey:$APPKEY"  "$URL/job")
	shouldpass command
}

getJobStatus()
{
	echo "\n=================================================================="
	echo "GET STATUS"
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \ 
		"$URL/job/$APP.$EU/$JOB" \
	)
	shouldpass command 
}

listWorkingDir()
{
	echo "\n=================================================================="
	echo "LIST WORKING DIR"
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \ 
		"$URL/job/$APP.$EU/$JOB/workingdir" \
	)
	shouldpass command 
}

listResults()
{
	echo "\n=================================================================="
	echo "LIST OUTPUT"
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \ 
		"$URL/job/$APP.$EU/$JOB/output" \
	)
	shouldpass command 
}

download()
{
	echo "\n=================================================================="
	echo "DOWNLOAD FILENAME: $FILE"
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \ 
		"$URL/job/$APP.$EU/$JOB/output/$DOC" \
	)
	shouldpass command
}

download2()
{
	echo "\n=================================================================="
	echo "DOWNLOAD FILENAME: $FILE"
	echo "=================================================================="
	curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \
		-O -J \
		$URL/job/$APP.$EU/$JOB/output/$DOC
}

cancel()
{
	echo "\n=================================================================="
	echo "CANCEL AND DELETE JOB: $JOB"
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-X DELETE \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \
		$URL/job/$APP.$EU/$JOB \
	)	
	shouldpass command
}

# runtests getJobStatus  listWorkingDir listResults
# runtests download 
# download2
# runtests listJobs cancel listJobs
# runtests listResults
# runtests getJobStatus  
runtests listGuiDemoJobs  
# runtests listJobs
