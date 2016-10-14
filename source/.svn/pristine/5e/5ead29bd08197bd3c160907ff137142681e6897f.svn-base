#!/bin/bash

#
# Tests authorization for access to job submitted by direct user. 
# Makes sure a direct user can only see jobs he submitted via the same app he's using to query.
#

if [[ $# < 2 ]] ; then
	echo "cipresadmin username and pass required on commandline"
	exit 1
fi

# testfw.sh defines some functions and gets env vars from pycipres.conf
source "./testfw.sh" 


#  These come from pycipres.conf
APP1=$PYCIPRES_APP
USER1=$PYCIPRES_EU
APP2=$PYCIPRES2_APP
USER2=$PYCIPRES_EU2

# This is set in testfw.sh/config
APPKEY1=$PYCIPRES_APPKEY
APPKEY2=$PYCIPRES2_APPKEY

Atest1()
{
	error=0

	echo "\n=================================================================="
	echo "Atest1a: Submit a clustalw job for $APP1.$USER1." 
	echo "=================================================================="
	command=(curl -k -u "$USER1:$TESTPASS" \
		-H "cipres-appkey:$APPKEY1"  \
		"$URL/job/$USER1" \
		-F "tool=CLUSTALW" \
		-F "metadata.clientJobId=100" \
		-F "metadata.clientJobName=test job 1000"
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_=.1 
	)
	if ! shouldpass command "<jobHandle>" jobHandle; then
		echo "TEST FAILED"
		error=1
	fi
	JH1=$EXTRACTED

	echo "\n=================================================================="
	echo "Atest1b: Get it's status." 
	echo "=================================================================="
	command=(curl -k -u "$USER1:$TESTPASS" \
		-H "cipres-appkey:$APPKEY1"  \
		"$URL/job/$USER1/$JH1" )
	if ! shouldpass command "<title>$JH1" ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest1c: Delete the job." 
	echo "=================================================================="
	command=(curl -k -u "$USER1:$TESTPASS" -X DELETE \
		-H "cipres-appkey:$APPKEY1"  \
		"$URL/job/$USER1/$JH1" ) 
	if ! shouldpass command; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest1c: Try to get  it's status.  Should fail." 
	echo "=================================================================="
	command=(curl -k -u "$USER1:$TESTPASS" \
		-H "cipres-appkey:$APPKEY1"  \
		"$URL/job/$USER1/$JH1" )

	if  ! shouldfail command "JobNotFound"; then
		echo "TEST FAILED"
		error=1
	fi
	return $error
}

Atest2()
{
	error=0

	echo "\n=================================================================="
	echo "Atest2a: Submit a clustalw job for $APP1.$USER1." 
	echo "=================================================================="
	command=(curl -k -u "$USER1:$TESTPASS" \
		-H "cipres-appkey:$APPKEY1"  \
		"$URL/job/$USER1" \
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
	echo "Atest2b: Try to get job status using the wrong appkey. Should fail." 
	echo "=================================================================="
	command=(curl -k -u "$USER1:$TESTPASS" \
		-H "cipres-appkey:$APPKEY2"  \
		"$URL/job/$USER1/$JH1" )
	if ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest2c: Try to get job status using the wrong user. Should fail." 
	echo "=================================================================="
	command=(curl -k -u "$USER2:$TESTPASS" \
		-H "cipres-appkey:$APPKEY1"  \
		"$URL/job/$USER2/$JH1" )
	if ! shouldfail command  ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest2d: Try to get job status using the wrong user. Should fail." 
	echo "=================================================================="
	command=(curl -k -u "$USER2:$TESTPASS" \
		-H "cipres-appkey:$APPKEY1"  \
		"$URL/job/$USER1/$JH1" )
	if ! shouldfail command  ; then
		echo "TEST FAILED"
		error=1
	fi
			

	echo "\n=================================================================="
	echo "Atest2e: Try to get job status using the wrong user. Should fail." 
	echo "=================================================================="
	command=(curl -k -u "$USER1:$TESTPASS" \
		-H "cipres-appkey:$APPKEY1"  \
		"$URL/job/$USER2/$JH1" )
	if ! shouldfail command  ; then
		echo "TEST FAILED"
		error=1
	fi


	echo "\n=================================================================="
	echo "Atest2f: Delete the job using wrong appkey. Should fail." 
	echo "=================================================================="
	command=(curl -k -u "$USER1:$TESTPASS" -X DELETE \
		-H "cipres-appkey:$APPKEY2"  \
		"$URL/job/$USER1/$JH1" ) 
	if ! shouldfail command; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest2g: Delete the job using wrong user. Should fail." 
	echo "=================================================================="
	command=(curl -k -u "$USER2:$TESTPASS" -X DELETE \
		-H "cipres-appkey:$APPKEY1"  \
		"$URL/job/$USER2/$JH1" ) 
	if ! shouldfail command; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest2h: Try to get  it's status - make sure it wasn't deleted.  " 
	echo "=================================================================="
	command=(curl -k -u "$USER1:$TESTPASS" \
		-H "cipres-appkey:$APPKEY1"  \
		"$URL/job/$USER1/$JH1" )

	if  ! shouldpass command "$JH1"; then
		echo "TEST FAILED"
		error=1
	fi
	return $error
}

# Test same user submitting via different apps.  You can only access a job 
# using the same app you submitted with. 
Atest3()
{
	error=0

	echo "\n=================================================================="
	echo "Atest3a: Submit a clustalw job for $APP2.$USER1." 
	echo "=================================================================="
	command=(curl -k -u "$USER1:$TESTPASS" \
		-H "cipres-appkey:$APPKEY2"  \
		"$URL/job/$USER1" \
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
	echo "Atest3b: Submit another  clustalw job for $APP2.$USER1." 
	echo "=================================================================="
	command=(curl -k -u "$USER1:$TESTPASS" \
		-H "cipres-appkey:$APPKEY2"  \
		"$URL/job/$USER1" \
		-F "tool=CLUSTALW" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_=.1 
	)
	if ! shouldpass command "<jobHandle>" jobHandle; then
		echo "TEST FAILED"
		error=1
	fi
	JH3=$EXTRACTED

	echo "\n=================================================================="
	echo "Atest3c: Submit another  clustalw job for a different user: $APP1.$USER2." 
	echo "=================================================================="
	command=(curl -k -u "$USER2:$TESTPASS" \
		-H "cipres-appkey:$APPKEY1"  \
		"$URL/job/$USER2" \
		-F "tool=CLUSTALW" \
		-F "input.infile_=@$TESTDATA/tooltests/CLUSTALW/CLUSTALW_in.txt" \
		-F vparam.runtime_=.1 
	)
	if ! shouldpass command "<jobHandle>" jobHandle; then
		echo "TEST FAILED"
		error=1
	fi
	JH4=$EXTRACTED

	# JH1 belongs to app1.user1,  JH2 and JH3 belongs to app2.user1, and JH4 belongs to app1.user2.  
	echo "\n=================================================================="
	echo "Atest3d: $APP2.$USER1 get status of his own jobs using jh query param. "
	echo "=================================================================="
	command=(curl -k -u "$USER1:$TESTPASS" \
		-H "cipres-appkey:$APPKEY2"  \
		"$URL/job/?jh=$JH2&jh=$JH3" )
	if  ! shouldpass command ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest3e: $APP2.$USER1 try to get status of $APP1.USER1 job using jh.  SHOULD FAIL."
	echo "=================================================================="
	command=(curl -k -u "$USER1:$TESTPASS" \
		-H "cipres-appkey:$APPKEY2"  \
		"$URL/job/?jh=$JH1" )
	if  ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi

	# JH1 belongs to app1.user1,  and JH4 belongs to app1.user2.  

	echo "\n=================================================================="
	echo "Atest3f: $APP1.$USER2 can get his own job "
	echo "=================================================================="
	command=(curl -k -u "$USER2:$TESTPASS" \
		-H "cipres-appkey:$APPKEY1"  \
		"$URL/job/?jh=$JH4" )
	if  ! shouldpass command ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest3g: $APP1.$USER2 can get his own job, but not $APP1.$USER1 job. SHOULD FAIL"
	echo "=================================================================="
	command=(curl -k -u "$USER2:$TESTPASS" \
		-H "cipres-appkey:$APPKEY1"  \
		"$URL/job/?jh=$JH1" )
	if  ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi
	return $error
}

runtests Atest1 Atest2  Atest3 


