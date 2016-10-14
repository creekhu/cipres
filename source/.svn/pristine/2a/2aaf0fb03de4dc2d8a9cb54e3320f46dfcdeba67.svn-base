#!/bin/bash

# Test
# - User submits job through multiple apps, make sure he can only see jobs via the app he used to submit the job.

if [[ $# < 2 ]] ; then
	echo "cipresadmin username and pass required on commandline"
	exit 1
fi

# testfw.sh defines some functions and gets env vars from pycipres.conf
source "./testfw.sh" 
config


EU=terri1
EUEMAIL=cipresrest@gmail.com
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
	error=0

	echo "\n=================================================================="
	echo "Atest3: Submit a clustalw job for $APP.$EU."  
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
	echo "Atest3: Submit a clustalw job for direct user $PYCIPRES_EU"
	echo "=================================================================="
	command=(curl -k -u "$PYCIPRES_EU:$TESTPASS" \
		-H "cipres-appkey:$PYCIPRES_APPKEY"  \
		"$URL/job/$PYCIPRES_EU" \
		-F "tool=CLUSTALW" \
		-F "metadata.clientJobId=102" \
		-F "metadata.clientJobName=test job 102"
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_=.1 
	)
	if ! shouldpass command "<jobHandle>" jobHandle; then
		error=1
	fi
	JH2=$EXTRACTED

	return $error
}

Atest2()
{
	echo "\n=================================================================="
	echo "Atest2: Make sure REST_ADMIN user, $APPUSER, can get status of $JH1 "
	echo "but not $JH2."
	echo "=================================================================="
	command=(curl -k -u "$APPUSER:$APPPASS" -H "cipres-appkey:$APPKEY"  \
		"$URL/job?jh=$JH1" )
	if ! shouldpass command '<jobHandle>' ; then
		error=1
	fi

	# TODO what's the error we should search output for?
	command=(curl -k -u "$APPUSER:$APPPASS" -H "cipres-appkey:$APPKEY"  \
		"$URL/job?jh=$JH2" )
	if ! shouldfail command 'Mismatch' ; then
		error=1
	fi
	return $error
}

Atest3()
{
	echo "\n=================================================================="
	echo "Atest3: Make sure $APPUSER.$EU can get status of $JH1 "
	echo "but not $JH2."
	echo "=================================================================="
    command=(curl -k -u "$APPUSER:$APPPASS" \
        -H "cipres-appkey:$APPKEY"  \
        -H "cipres-eu:$EU" \
        -H "cipres-eu-email:$EUEMAIL" \
        -H "cipres-eu-institution:$EUINST" \
        "$URL/job/$APP.$EU/$JH1" )
	if ! shouldpass command "<title>$JH1" ; then
		error=1
	fi

    command=(curl -k -u "$APPUSER:$APPPASS" \
        -H "cipres-appkey:$APPKEY"  \
        -H "cipres-eu:$EU" \
        -H "cipres-eu-email:$EUEMAIL" \
        -H "cipres-eu-institution:$EUINST" \
        "$URL/job/$APP.$EU/$JH2" )
	if ! shouldfail command  'Mismatch' ; then
		error=1
	fi
	return $error
}


Atest4()
{
	echo "\n=================================================================="
	echo "Atest4: Make sure $PYCIPRES_EU can get status of $JH2 "
	echo "but not $JH1."
	echo "=================================================================="
    command=(curl -k -u "$PYCIPRES_EU:$TESTPASS" -H "cipres-appkey:$PYCIPRES_APPKEY"  "$URL/job/$PYCIPRES_EU/$JH2" )
	if ! shouldpass command "<title>$JH2" ; then
		error=1
	fi

	# TODO what error?
    command=(curl -k -u "$PYCIPRES_EU:$TESTPASS" -H "cipres-appkey:$PYCIPRES_APPKEY"  "$URL/job/$PYCIPRES_EU/$JH1" )
	if ! shouldfail command  'Mismatch' ; then
		error=1
	fi

	return $error
}


Atest5()
{
	echo "\n=================================================================="
	echo "Atest5: Make sure $PYCIPRES_EU can get list working dir of $JH2 "
	echo "but not $JH1."
	echo "=================================================================="
    command=(curl -k -u "$PYCIPRES_EU:$TESTPASS" -H "cipres-appkey:$PYCIPRES_APPKEY"  "$URL/job/$PYCIPRES_EU/$JH2/workingdir" )
	if ! shouldpass command "<workingdir>" ; then
		error=1
	fi

    command=(curl -k -u "$PYCIPRES_EU:$TESTPASS" -H "cipres-appkey:$PYCIPRES_APPKEY"  "$URL/job/$PYCIPRES_EU/$JH1/workingdir" )
	if ! shouldfail command  'Mismatch' ; then
		error=1
	fi
	return $error
}

Atest6()
{
	error=0
	counter=0

	echo "\n=================================================================="
	echo "Atest6: Wait up to 2 minute for $JH2 to get staged."
	echo "=================================================================="
    command=(curl -k -u "$PYCIPRES_EU:$TESTPASS" -H "cipres-appkey:$PYCIPRES_APPKEY"  "$URL/job/$PYCIPRES_EU/$JH2/workingdir" )
	while ! shouldpass command "_JOBINFO.TXT" ; do 
		sleep 30
		let counter=counter+1
		if [[ $counter -gt 3 ]] ; then
			error=1
			break
		fi
	done

	if [[ $error -ne 0 ]]; then
		echo "JOB HAS NOT BEEN STAGED YET.  This is not necessarily an error."
		echo "However, we're returning an error because it should be investigated."
		echo "The next test will probably fail with a 404 NOT FOUND." 
	fi
	return $error
}

Atest7()
{
	echo "\n=================================================================="
	echo "Atest7: Make sure $PYCIPRES_EU can get download _JOBINFO.TXT of $JH2 "
	echo "but not $JH1."
	echo "=================================================================="
    command=(curl -k -u "$PYCIPRES_EU:$TESTPASS" -H "cipres-appkey:$PYCIPRES_APPKEY"   \
		"$URL/job/$PYCIPRES_EU/$JH2/workingdir/_JOBINFO.TXT" )
	if ! shouldpass command  ; then
		error=1
	fi

    command=(curl -k -u "$PYCIPRES_EU:$TESTPASS" -H "cipres-appkey:$PYCIPRES_APPKEY"   \
		"$URL/job/$PYCIPRES_EU/$JH1/workingdir/_JOBINFO.TXT" )
	if ! shouldfail command  'Mismatch' ; then
		error=1
	fi
	return $error
}

runtests Atest1 Atest2  Atest3 Atest4 Atest5 Atest6 Atest7
