#!/bin/bash

# Tests that we don't  mix up an umbrella user and direct end user having the same username.

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
	if ! shouldfail command ;  then
		echo "TEST FAILED"
		error=1
	fi
	echo "\n=================================================================="
	echo "Atest1a: Submit a clustalw job for $APP.$EU1, but do it right this time." 
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
	return $error
}

Atest6()
{
	echo "\n=================================================================="
	echo "Atest6a: Create or update a DIRECT user with the same username, as our"
	echo "umbrella app end user, $EU1.  Note that we can use the same EMAIL address."
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
	echo "Atest6b: Repeat with the umbrella user's appkey: $APPKEY "
	echo "=================================================================="
	command=(curl -k -u "$EU1:$TESTPASS" -H "cipres-appkey:$APPKEY"  "$URL/job/$EU1/$JH1" )
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
	if ! shouldfail command ; then
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

runtests Atest1 Atest6
