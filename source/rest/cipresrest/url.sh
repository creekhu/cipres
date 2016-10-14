#!/bin/bash

# This tests using the alternate form of username in url for umbrella apps.
# Originally we required that the username in the url be the same as what we store in the db
# namely appname.username, but now we also allow just username.
#
# NOTE - I backed this feature out.  All I had done to implement it was change SecurityFilter.credentialsValidFor().
# But I realized that all returned urls still had appname.username in them.  I could have fixed that, but the 
# whole thing started to seem unimportant and not worth the trouble.  So, this isn't implemented.  Don't run this
# test.

if [[ $# < 2 ]] ; then
	echo "cipresadmin username and pass required on commandline"
	exit 1
fi

# testfw.sh defines some functions and gets env vars from pycipres.conf
source "./testfw.sh" 

# Pick a name we're unlikely to have put in the db manually
EU1=foofoobarbarfoo
EUEMAIL=$EU1@yahoo.com
EU2=tlonsomersetave
EU2EMAIL=$EU2@yahoo.com

#  These come from pycipres.conf
APP=$GUIDEMO_APP
APPUSER=$GUIDEMO_ADMIN
APPPASS=$TESTPASS

APP2=$GUIDEMO2_APP
APP2USER=$GUIDEMO2_ADMIN
APP2PASS=$TESTPASS
APPKEY2=$GUIDEMO2_APPKEY

# This is set in testfw.sh/config
APPKEY=$GUIDEMO_APPKEY

Atest1()
{
	error=0

	echo "\n=================================================================="
	echo "Atest1a: Submit a clustalw job for $APP.$EU1, using just $EU1 in url." 
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$EU1" \
		-F "tool=CLUSTALW" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_=.1 
	)
	if ! shouldpass command "<jobHandle>" jobHandle; then
		echo "TEST FAILED"
		error=1
	fi
	JH1=$EXTRACTED


	echo "\n=================================================================="
	echo "Atest1b: Get its status, but use $APP.$EU1 username in url." 
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$APP.$EU1/$JH1" )
	if ! shouldpass command "$JH1" ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest1c: Get its status, but use just $EU2 username in url. Should error" 
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$EU2/$JH1" )
	if ! shouldfail command  ; then
		echo "TEST FAILED"
		error=1
	fi


	echo "\n=================================================================="
	echo "Atest1d: List the working dir, but use just $APP.$EU1 username in url." 
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$APP.$EU1/$JH1/workingdir" )
	if ! shouldpass command "<workingdir>" ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest1e: Get its status, but use just wrong app in the url.  Should error, but I think it's going to pass."
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$APP2.$EU1/$JH1" )
	if ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest1f: Get its status, but use just wrong app everywhere. "
	echo "=================================================================="
	command=(curl -k -u "$APP2USER:$APP2PASS" \
		-H "cipres-appkey:$APPKEY2"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$APP2.$EU1/$JH1" )
	if ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi

	return $error
}


Atest2()
{
	error=0

	echo "\n=================================================================="
	echo "Atest2a: Submit a clustalw job for $APP.$EU1 with $AOO,$EU1 in url." 
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$APP.$EU1" \
		-F "tool=CLUSTALW" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_=.1 
	)
	if ! shouldpass command "<jobHandle>" jobHandle; then
		echo "TEST FAILED"
		error=1
	fi
	JH1=$EXTRACTED

	echo "\n=================================================================="
	echo "Atest2b: Get its status, but use just username in url." 
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$EU1/$JH1" )
	if ! shouldpass command "<title>$JH1" ; then
		echo "TEST FAILED"
		error = 1
	fi

	echo "\n=================================================================="
	echo "Atest2c: List workingdir, but use just username in url." 
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$EU1/$JH1/workingdir" )
	if ! shouldpass command "workingdir" ; then
		echo "TEST FAILED"
		error = 1
	fi

	echo "\n=================================================================="
	echo "Atest2d: Delete the job." 
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" -X DELETE \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$EU1/$JH1" ) 
	if ! shouldpass command; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest2e: Try to get  it's status.  Should fail." 
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$EU1/$JH1" )

	if  ! shouldfail command "JobNotFound"; then
		echo "TEST FAILED"
		error=1
	fi

	return $error
}


Atest3()
{
	error=0

	echo "\n=================================================================="
	echo "Atest3a: Submit a clustalw job for $EU.  "
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$EU1" \
		-F "tool=CLUSTALW" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_=.1 
	)
	if ! shouldpass command "<jobHandle>" jobHandle; then
		echo "TEST FAILED"
		error=1
	fi
	JH1=$EXTRACTED
	
	echo "\n=================================================================="
	echo "Atest3b: Make sure REST_ADMIN user, $APPUSER, can get status of the jobs"
	echo "we just submitted by jobhandle."
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" -H "cipres-appkey:$APPKEY"  \
		"$URL/job?jh=$JH1" )
	if ! shouldpass command '<jobHandle>' ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest3c: Make sure Cipres Admin  user can get status of the jobs"
	echo "we just submitted by jobhandle."
	echo "=================================================================="
	command=(curl -k -u "$AUSER:$APASS" -H "cipres-appkey:$APPKEY"  \
		"$URL/job?jh=$JH1" )
	if ! shouldpass command '<jobHandle>' ; then
		echo "TEST FAILED"
		error=1
	fi
	return $error
}

Atest4()
{
	echo "\n=================================================================="
	echo "Atest4a: List jobs for $EU1, logged in as $APP.$EU1.  Should include"
	echo "the jobs we just submitted. This script checks for one of them."
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$EU1" \
	)
	shouldpass command "<title>$JH1"
}

Atest5()
{
	echo "\n=================================================================="
	echo "Atest5a: Get full job status. "
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$EU1/$JH1" )
	shouldpass command "<resultsUri>" 
}

Atest7()
{
	echo "\n=================================================================="
	echo "Atest7: Create or update a DIRECT user with the same username, $APPUSER.  We can use the same EMAIL address."
	echo "=================================================================="
	command=(curl -k -u "$AUSER:$APASS"  \
		"$URL/user/$EU1"  \
		-d "password=$TESTPASS" \
		-d "email=$EUEMAIL" \
		-d "country=US" \
		-d "first_name=new" \
		-d "last_name=one" \
		-d "activate=true" ) 

	if  ! shouldpass command  ; then
		echo "TEST FAILED "
		error=1
	fi
}

Atest6()
{
	echo "\n=================================================================="
	echo "Atest6a: Create or update a DIRECT user with the same username, $APPUSER.  We can use the same EMAIL address."
	echo "=================================================================="
	command=(curl -k -u "$AUSER:$APASS"  \
		"$URL/user/$EU1"  \
		-d "password=$TESTPASS" \
		-d "email=$EUEMAIL" \
		-d "country=US" \
		-d "first_name=new" \
		-d "last_name=one" \
		-d "activate=true" ) 

	if  ! shouldpass command  ; then
		echo "TEST FAILED "
		error=1
	fi


	echo "\n=================================================================="
	echo "Atest6b: Try to get  status for umbrella user's job with direct user's creds and app $PYCIPRES_APP.  Should fail." 
	echo "=================================================================="
	command=(curl -k -u "$EU1:$TESTPASS" -H "cipres-appkey:$PYCIPRES_APP"  "$URL/job/$EU1/$JH1" )
	if  ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest6c: Submit a clustalw job for direct user $EU1, using appkey $PYCIPRES_APPKEY. "
	echo "=================================================================="
	command=(curl -k -u "$EU1:$TESTPASS" \
		-H "cipres-appkey:$PYCIPRES_APPKEY"  \
		"$URL/job/$EU1" \
		-F "tool=CLUSTALW" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_=.1 
	)
	if ! shouldpass command "<jobHandle>" jobHandle; then
		echo "TEST FAILED"
		error=1
	fi
	JH2=$EXTRACTED

	echo "\n=================================================================="
	echo "Atest6d: Get it's status by jobhandle. "
	echo "=================================================================="
	command=(curl -k -u "$EU1:$TESTPASS" -H "cipres-appkey:$PYCIPRES_APPKEY"   "$URL/job?jh=$JH2" )
	if ! shouldpass command "<jobHandle>" ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest6d: Get it's status by jobhandle, using wrong appkey. "
	echo "=================================================================="
	command=(curl -k -u "$EU1:$TESTPASS" -H "cipres-appkey:$PYCIPRES2_APPKEY"   "$URL/job?jh=$JH2" )
	if ! shouldfail command "<jobHandle>" ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest6e: Get it's status by jobhandle but use umbrella apps creds.  Should error."
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"   
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		"$URL/job/$EU1/$JH2" )
	if ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi

	return $error

}


runtests Atest1 Atest2 Atest3 Atest4 Atest5 Atest6


