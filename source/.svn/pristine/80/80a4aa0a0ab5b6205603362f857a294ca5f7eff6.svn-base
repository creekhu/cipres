#!/bin/bash
# Wrote this as a way to see all the different types of xml our api returns, as I tweak the xml.

source "./testfw.sh" 
if [[ $# < 2 ]] ; then
	echo "required command line arguments are: cipresadmin_username cipresadmin_pass "
	echo "if running listJobs, appid of $PYCIPRES_APP is 3rd arg on command line."
	echo "if showJob then need jobid as 3rd arg"
	exit 1
fi

# APPID=$3
APPID=pycipres-A97E5554A0424C5CAD7867C208DBE7E3
JOBID=$3

setup()
{
	config
}

showTools()
{
	echo "\n=================================================================="
	echo "showTools:"
	echo "=================================================================="
	command=(curl -k $URL/tool)
	shouldpass command
}

showClustalW()
{
	echo "\n=================================================================="
	echo "showClustalW:"
	echo "=================================================================="
	command=(curl -k $URL/tool/CLUSTALW)
	shouldpass command
}


listApps()
{
	echo "\n=================================================================="
	echo "listApps:"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS   $URL/application)
	shouldpass command
}

listUsers()
{
	echo "=================================================================="
	echo "listUsers"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/user/) 
	shouldpass command 
}

listJobs()
{
	echo "=================================================================="
	echo "listJobs without expand"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/job/$PYCIPRES_EU) 
	shouldpass command 
}

listJobsExpanded()
{
	echo "=================================================================="
	echo "listJobs with expand=true"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/job/$PYCIPRES_EU?expand=true) 
	shouldpass command 
}

showJob()
{
	echo "=================================================================="
	echo "showJob"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/job/$PYCIPRES_EU/$JOBID) 
	shouldpass command 
}

showJobResults()
{
	echo "=================================================================="
	echo "showJobResults"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/job/$PYCIPRES_EU/$JOBID/output) 
	shouldpass command 
}

showWorkingDir()
{
	echo "=================================================================="
	echo "showJobResults"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/job/$PYCIPRES_EU/$JOBID/workingdir) 
	shouldpass command 
}


getValidationError()
{
	command=(curl -k -u $PYCIPRES_EU:$TESTPASS -H "cipres-appkey:$APPID" $URL/job/$PYCIPRES_EU/ \
		-F "tool=CLUSTALW" \
		-F "input.infile_=@$DATA/CLUSTALW/CLUSTALW_in.txt" \
		-F "vparam.runtime_=0.15" \
		-F "vparam.actions_=-align" \
		-F "vparam.matrix_=-foo" )
	shouldfail command "matrix"
}

setup
# runtests listJobs listJobsExpanded 
# runtests showJob showJobResults showWorkingDir
# runtests listUsers listApps
runtests listApps


