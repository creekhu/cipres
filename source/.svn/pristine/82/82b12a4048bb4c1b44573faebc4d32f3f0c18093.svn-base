#!/bin/bash

if [[ $# < 3 ]] ; then
	echo "cipresadmin username and pass and option (-l, -r or -k)  required on commandline"
	echo 
	echo "To run this test, set active_limit=2 in build properties file and rebuild."
	echo "Or set the limit just for user guidemo.terri1 by running:"
	echo "	usageLimits -u guidemo.terri1 -j 2"
	echo "Restart tomcat, then run:"
	echo "	limit.sh <adminuser> <adminpass> -r"
	echo "The test will try to submit 4 jobs.  First 2 should be allowed, next 2 should"
	echo "fail with a UsageLimit exception. (Active_limit is the number of jobs in running_task_table.)"
	echo
	echo "You can also use this script to test xsede_su_limit and other_su_limit by setting low values"
	echo "for them and using this script to submit jobs.  The predicted sus of the jobs you submit"
	echo "first will be factored in.  Search cipresrest.log for "has used" to see what the job_stats records"
	echo "show the user has already used.  Modify code at bottom of script to run either tscc or xsede jobs."
	echo
	echo "You can set the limits for the indivual user instead of using build.properties, but"
	echo "still need to restart tomcat after changing the user_preferences records."
	echo "See also usageLimits script for setting limits.  Run 'usageLimits -h'"
	echo
	echo "Invoke with -l to list jobs and ensure there are no active jobs before running test."
	echo "Invoke with -r to run the test."
	echo "Invoke with -k to kill any active jobs"
	echo
	exit 1
fi



# testfw.sh defines some functions and gets env vars from pycipres.conf
source "./testfw.sh" 
config


EU=terri1
EUEMAIL=cipresrest@gmail.com
EUINST="San Diego Super Computer"

#  These come from pycipres.conf
APP=$GUIDEMO_APP
APPUSER=$GUIDEMO_ADMIN
APPPASS=$TESTPASS
# This is set in testfw.sh/config
APPKEY=$GUIDEMO_APPKEY

list()
{       
    command=(curl -k -u "$APPUSER:$APPPASS" \
        -H "cipres-appkey:$APPKEY"  \
        -H "cipres-eu:$EU" \
        -H "cipres-eu-email:$EUEMAIL" \
        -H "cipres-eu-institution:$EUINST" \
        "$URL/job/$APP.$EU?expand=true" )
    shouldpass command '<joblist>'
}

kill()
{
	# Doing this as cipres admin.  Todo: is that required? Enforced?
    command=(curl -k -u "$AUSER:$APASS" \
		-X DELETE \
        -H "cipres-appkey:$APPKEY"  \
        -H "cipres-eu:$EU" \
        -H "cipres-eu-email:$EUEMAIL" \
        -H "cipres-eu-institution:$EUINST" \
        "$URL/job/$APP.$EU" ) 

    shouldpass command
}


run()
{
	error=0

	echo "\n=================================================================="
	echo "Atest3: Submit a clustalw job for $APP.$EU .  "
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \
		"$URL/job/$APP.$EU" \
		-F "tool=CLUSTALW" \
		-F "metadata.clientJobId=100" \
		-F "metadata.clientJobName=test job 100"
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_=.1 
	)
	if ! shouldpass command "<jobHandle>" jobHandle; then
		error=1
	fi
	JH1=$EXTRACTED

	echo "\n=================================================================="
	echo "Atest3: Submit another clustalw job for $APP.$EU. "
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \
		"$URL/job/$APP.$EU" \
		-F "tool=CLUSTALW" \
		-F "metadata.clientJobId=101" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_=.1 
	)
	if ! shouldpass command "<jobHandle>" jobHandle; then
		error=1
	fi
	JH2=$EXTRACTED

	echo "\n=================================================================="
	echo "Atest3: Submit another clustalw job for $APP.$EU. "
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \
		"$URL/job/$APP.$EU" \
		-F "tool=CLUSTALW" \
		-F "metadata.clientJobId=102" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_=.1 
	)
	if ! shouldfail command "UsageLimit" ; then
		error=1
	fi

}

runxsede()
{
	echo "\n=================================================================="
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \
		"$URL/job/$APP.$EU" \
		-F "tool=RAXMLHPC2BB" \
		-F "metadata.clientJobId=102" \
		-F "input.infile_=@$TESTDATA/tooltests/RAXMLHPC2BB/RAXMLHPC2BB_in.txt" \
		-F vparam.runtime_=.25 
	)
	if ! shouldfail command "UsageLimit" ; then
		error=1
	fi
}


# Shift past the username and pass 
shift; shift

echo "Argument is $1"

if [[ "$1" == "-l" ]] ; then
	runtests list
fi
if [[ "$1" == "-k" ]] ; then
	runtests kill  
fi

if [[ "$1" == "-r" ]] ; then
	# runtests run  
	runtests runxsede  
	# runtests runxsede  
	# runtests runxsede  
	# runtests runxsede  
fi




