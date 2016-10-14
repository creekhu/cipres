#!/bin/bash

# Submits jobs and gets status using curl, as guidemo app, an umbrella app.
# Also tests the job callback url and deleting a job.
#
# What jobs actually get submitted by this script:
# 	ATest2a submits a CLUSTALW job and deletes it.
#	ATest3 submits 2 CLUSTALW jobs.
#	Atest19 submits a CLUSTALW job.
#	Atest20 submits a CLUSTALW job.

if [[ $# < 2 ]] ; then
	echo "cipresadmin username and pass required on commandline"
	exit 1
fi

# testfw.sh defines some functions and gets env vars from pycipres.conf
source "./testfw.sh" 

EU=terri1
EUEMAIL=$T7EMAIL
EUINST="San Diego Super Computer"
EU2=terri2

#  These come from pycipres.conf
APP=$GUIDEMO_APP
APPUSER=$GUIDEMO_ADMIN
APPPASS=$TESTPASS
APP2USER=$GUIDEMO2_ADMIN
APP2PASS=$TESTPASS

# This is set in testfw.sh/config
APPKEY=$GUIDEMO_APPKEY
APPKEY2=$GUIDEMO2_APPKEY

Atest1()
{       
    echo "\n=================================================================="
    echo "Atest1: List jobs for $APP.$EU - AND implicitly create account "
	echo "for end user $APP.$EU, (setting email=$EUEMAIL) if it doesn't already exist."
	echo "Institution header is optional."
    echo "=================================================================="
    command=(curl -k -u "$APPUSER:$APPPASS" \
        -H "cipres-appkey:$APPKEY"  \
        -H "cipres-eu:$EU" \
        -H "cipres-eu-email:$EUEMAIL" \
        # -H "cipres-eu-institution:$EUINST" \
        "$URL/job/$APP.$EU" \
    )
    shouldpass command '<joblist>'
}

Atest2()
{
	echo "\n=================================================================="
	echo "Atest2: SHOULD  ERROR because this will implicitly create an account"
	echo "with same appname $APP and same email $EUEMAIL for a new user $EU2 BUT"
	echo "email addresses must be unique for a given app"
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU2" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \
		"$URL/job/$APP.$EU2" \
	)
	shouldfail command 'Authentication Error'
}

Atest2a()
{
	error=0

	echo "\n=================================================================="
	echo "Atest2a: Submit a clustalw job for $APP.$EU." 
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \
		"$URL/job/$APP.$EU" \
		-F "tool=CLUSTALW" \
		-F "metadata.clientJobId=100" \
		-F "metadata.clientJobName=test job 1000"
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_=.1 
	)
	if ! shouldpass command "<jobHandle>" jobHandle; then
		error=1
	fi
	JH1=$EXTRACTED

	echo "\n=================================================================="
	echo "Atest2a: Get it's status." 
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \
		"$URL/job/$APP.$EU/$JH1" )
	if ! shouldpass command "<title>$JH1" ; then
		error = 1
	fi

	echo "\n=================================================================="
	echo "Atest2a: Delete the job." 
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" -X DELETE \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \
		"$URL/job/$APP.$EU/$JH1" ) 
	if ! shouldpass command; then
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest2a: Try to get  it's status.  Should fail." 
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \
		"$URL/job/$APP.$EU/$JH1" )

	if  ! shouldfail command "JobNotFound"; then
		error=1
	fi

	return $error
}


Atest3()
{
	error=0

	echo "\n=================================================================="
	echo "Atest3: Submit a clustalw job for $APP.$EU .  Should get job"
	echo "completion email at $EUEMAIL".
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
		# -F "metadata.statusEmail=true" \
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
		-F "metadata.clientJobName=test job 101"
		# -F "metadata.statusEmail=true" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_=.1 
	)
	if ! shouldpass command "<jobHandle>" jobHandle; then
		error=1
	fi
	JH2=$EXTRACTED

	echo "\n=================================================================="
	echo "Atest3: Make sure REST_ADMIN user, $APPUSER, can get status of the jobs"
	echo "we just submitted by jobhandle."
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" -H "cipres-appkey:$APPKEY"  \
		"$URL/job?jh=$JH1&jh=$JH2" )
	if ! shouldpass command '<jobHandle>' ; then
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest3: Make sure Cipres Admin  user can get status of the jobs"
	echo "we just submitted by jobhandle."
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" -H "cipres-appkey:$APPKEY"  \
		"$URL/job?jh=$JH1&jh=$JH2" )
	if ! shouldpass command '<jobHandle>' ; then
		error=1
	fi



	return $error
}

Atest4()
{
	echo "\n=================================================================="
	echo "Atest4: SHOULD ERROR. Use wrong appkey."
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" -H "cipres-appkey:$GUIDEMO2_APPKEY"  \
		"$URL/job?jh=$JH1&jh=$JH2" )
	shouldfail command  'Authentication Error'
}

Atest5()
{
	echo "\n=================================================================="
	echo "Atest5: SHOULD ERROR. Use wrong REST_ADMIN."
	echo "=================================================================="
	command=(curl -k -u "$APP2USER:$APP2PASS" -H "cipres-appkey:$GUIDEMO2_APPKEY"  \
		"$URL/job?jh=$JH1&jh=$JH2" )
	shouldfail command 'Mismatch'
}

Atest6()
{
	echo "\n=================================================================="
	echo "Atest6: SHOULD ERROR. Use correct REST_ADMIN with wrong appkey."
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" -H "cipres-appkey:$GUIDEMO2_APPKEY"  \
		"$URL/job?jh=$JH1&jh=$JH2" )
	shouldfail command 'Authentication Error'
}

Atest7()
{
	echo "\n=================================================================="
	echo "Atest7: SHOULD ERROR. Omit appkey." 
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS"  "$URL/job?jh=$JH1&jh=$JH2" )
	shouldfail command 'Authentication Error'
}

Atest8()
{
	echo "\n=================================================================="
	echo "Atest8: List jobs for $APP.$EU, logged in as $APP.$EU.  Should include"
	echo "the jobs we just submitted. This script checks for one of them."
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \
		"$URL/job/$APP.$EU" \
	)
	shouldpass command "<title>$JH1"
}

Atest9()
{
	echo "\n=================================================================="
	echo "Atest9: Get full job status. "
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \
		"$URL/job/$APP.$EU/$JH1" )
	shouldpass command "<title>$JH1"
}


Atest10()
{
	echo "\n=================================================================="
	echo "Atest10: SHOULD ERROR - runtime is invalid, align is invalid"
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \ 
		"$URL/job/$APP.$EU" \
		-F "tool=CLUSTALW" \
		-F "metadata.clientJobId=100" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F runtime_=.1 \
		-F vparam.align_=foo \
	)
	shouldfail command "<error>"
}

Atest11()
{
	echo "\n=================================================================="
	echo "Atest11: SHOULD ERROR - Test callback URL as cipresadmin user"
	echo "but sending a bogus url.  Demonstrates that we don't get a "
	echo "permissions or other unexpected error."
	echo "=================================================================="
	command=(curl -k -u "$AUSER:$APASS" "$URL/admin/updateJob?jh=foo" )
	shouldfail command "Job not found."
}

Atest12()
{
	echo "\n=================================================================="
	echo "Atest12: - Test callback URL as cipresadmin user with valid jh"
	echo "but status=START which doesn't do anything. " 
	echo "=================================================================="
	command=(curl -k -u "$AUSER:$APASS" "$URL/admin/updateJob?jh=${JH1}&status=START" )
	shouldpass command "updated"
}

Atest13()
{
	echo "\n=================================================================="
	echo "Atest13: - SHOULD ERROR. Test callback URL w/o login"
	echo "=================================================================="
	command=(curl -k  "$URL/admin/updateJob?jh=${JH1}&status=START" )
	shouldfail command "Authentication"
}

Atest14()
{
	echo "\n=================================================================="
	echo "Atest14: - SHOULD ERROR. Test callback URL rest admin login, cipresadmin is what's required."
	echo "=================================================================="
	command=(curl -k    -u "$APPUSER:$APPPASS"  "$URL/admin/updateJob?jh=${JH1}&status=START" )
	shouldfail command "Authorization"
}

Atest15()
{
	echo "\n=================================================================="
	echo "Atest15: SHOULD ERROR, tool not specified. "
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \ 
		"$URL/job/$APP.$EU" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_=1
	)
	shouldfail command "tool"
}

Atest16()
{
	echo "\n=================================================================="
	echo "Atest16: SHOULD ERROR, tool not valid. "
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \ 
		"$URL/job/$APP.$EU" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F tool=FOO \
		-F vparam.runtime_=1
	)
	shouldfail command "tool"
}

Atest17()
{
	echo "\n=================================================================="
	echo "Atest17: SHOULD ERROR, runtime not a double. "
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \ 
		"$URL/job/$APP.$EU" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_="one hour" \
		-F tool=CLUSTALW
	)
	shouldfail command "runtime"
}

Atest18()
{
	echo "\n=================================================================="
	echo "Atest18: SHOULD ERROR, actions_  value not in list. "
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \ 
		"$URL/job/$APP.$EU" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F tool=CLUSTALW \
		-F vparam.runtime_="1" \
		-F vparam.actions_=terri
	)
	shouldfail command "actions"
}

Atest19()
{
	echo "\n=================================================================="
	echo "Atest19: SHOULD PASS and SUBMIT JOB, actions_  value is in list. "
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \ 
		"$URL/job/$APP.$EU" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F tool=CLUSTALW \
		-F vparam.runtime_="1" \
		-F vparam.actions_=-align
	)
	shouldpass command 
}

Atest20()
{
	echo "\n=================================================================="
	echo "Atest20: SHOULD PASS and SUBMIT JOB. "
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \ 
		"$URL/job/$APP.$EU" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F tool=CLUSTALW \
		-F vparam.runtime_="1" \
		-F vparam.actions_=-align \
		-F vparam.quicktree_=fast
	)
	shouldpass command 
}

Atest21()
{
	echo "\n=================================================================="
	echo "Atest21: SHOULD ERROR, precond for quicktree is actions_=align. "
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H "cipres-eu-institution:$EUINST" \ 
		"$URL/job/$APP.$EU" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F tool=CLUSTALW \
		-F vparam.runtime_="1" \
		-F vparam.actions_=-profile \
		-F vparam.quicktree_=fast
	)
	shouldfail command "quicktree"
}




runtests Atest1 Atest2  Atest3 Atest4 Atest5 Atest6 Atest7 Atest8 Atest9 \
	Atest10 Atest11 Atest12 Atest13 Atest14 \
	Atest15 Atest16 Atest17 Atest18 Atest19 Atest20 Atest21

