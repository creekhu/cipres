#!/bin/bash

#Uncomment TESTS1, TESTS2 or CURL_TESTS if you want to skip those tests.

# TESTS1 are tooltest.py tests that should all run OK
TESTS1="$PWD/../rest_client_examples/examples/python_cipres/python_cipres/examples/* "

# TESTS2 are tooltest.py tests that should give validation error 
TESTS2="$PWD/../rest_client_examples/examples/python_cipres/python_cipres/error_examples/* "

# CURL_TESTS are just curl command lines to cipresrest/v1/jobs/user/validate. 
# Those that end in "_F" should return a validation error, the rest should pass.
CURL_TESTS="$PWD/../rest_client_examples/examples/python_cipres/python_cipres/curl_tests/"


if [[ $1 == "validate" ]]; then
	ACTION="validate"
elif [[ $1 == "run" ]]; then
	ACTION="run"
else
	echo "Argument 'validate' or 'run' is required"
	exit 1
fi

source ./testfw.sh

config()
{
	# Get url, username, etc from pycipres.conf, then copy to the vars
	# the curl_tests use.
	set -a
	source ~/pycipres.conf
	set +a

	export CRA_USER=$USERNAME
	export KEY=$APPID
}

# arg1 is test dir, arg2=1 means test should pass, arg=0 means they
# should give a validation error.  TODO: We don't currently use arg2.
run_tooltests()
{
	counter=0
	echo "\n\n##########################################################################################################################" >> $RESULTS
	echo "    Launching tooltests $1 $2" >> $RESULTS
	echo "##############################################################################################################################" >> $RESULTS
	for i in $1; do
		counter=$((counter+1))
		if [ -d $i ]; then
			( echo "========================================================================================"
			echo     "$counter: $i"
			echo "========================================================================================" 

			command=(tooltest.py $i $ACTION )
			err=0
			if [ "$2" == 1 ]
			then
				shouldpass command || let err=1
			else
				shouldfail command "validation" || let err=1
			fi
			if (( err == 0 ))
			then
				echo "### PASSED ###"
			else
				echo "### FAILED ###"
			fi
			echo ""
			) >> $RESULTS.$counter 2>&1 &
			sleep 5 
		fi
	done  

	echo "Started $counter tests, waiting for them to complete ..." >> $RESULTS 
	echo ""
	wait

	passed=$(grep -l PASSED $RESULTS.* | wc -l)
	failed=$(grep -l FAILED $RESULTS.* | wc -l)

	for i in $RESULTS.*; do
		cat $i >> $RESULTS
	done
	rm $RESULTS.*
	echo "SUMMARY - tooltests passed: $passed .  tooltests failed: $failed ." >> $RESULTS
}


run_curl_tests()
{
	failed=0
	passed=0

	echo "\n\n##########################################################################################################################" 
	echo "    Launching curltests, job validation tests on $CURL_TESTS"
	echo "##############################################################################################################################" 

	for tool in $CURL_TESTS/*; do
		if [[ -d $tool ]]
		then
			echo "\n========== Testing" $(basename $tool) " ==========="
			cd $tool
			for case in test_*
			do
				echo "\n========= Test case: $(basename $tool)/$case ========="

				command=(sh $case)
				err=0
				if [[ $case =~ .*_F ]]
				then
					shouldfail_curl command  || let err=1 
				else
					shouldpass_curl command || let err=1
				fi
				if (( err == 0 ))
				then
					echo "### PASSED ###"
					let passed=passed+1
				else
					echo "### FAILED ###"
					let failed=failed+1
				fi
			done
			echo "\n"
		fi
	done
	cd $PWD
	echo "SUMMARY - curltests passed: $passed .  Curl tests failed: $failed ."
}



# Initialize rest connection and account vars from ~/pycipres.conf
config

PWD=`pwd`
MAILTO="terri@sdsc.edu,mmiller@sdsc.edu"
# MAILTO="terri@sdsc.edu"
RESULTS=`pwd`/results.txt
WHO=`whoami`@`hostname -f`
USER=`whoami`
START_TIME=`date`
HOST=`hostname`

rm -rf $RESULTS $RESULTS.* NGBW-*

{  echo "Started tooltests as $WHO in $PWD at $START_TIME." 
echo
echo "Results will be summarized in $RESULTS.  Search for 'SUMMARY' for summary of each test suite." 
echo
echo "My pid is $$.  To kill me and all the tests, run: kill -TERM -$$" 
echo "But be aware that this doesn't kill any cipres jobs that have been started. " 
echo "Notice that there is a hyphen before "TERM" and before the pgid (which happens to equal my pid.)"
echo
echo "To see all related processes try: ps x -o  \"%p %r %y %x %c\" -U $USER | grep $$"
echo "or on a mac, try: ps -ej -ww | grep $$"
echo
echo 
} > $RESULTS

mail -s "$ACTION: started automated tooltests " $MAILTO < $RESULTS

run_tooltests "$TESTS1" "1"
run_tooltests "$TESTS2" "0"
if [[ $CURL_TESTS  ]]  && [[ -d $CURL_TESTS ]]
then
	# Only redirect stderr if debugging is needed
	# run_curl_tests $CURL_TESTS >> $RESULTS 2>&1

	run_curl_tests $CURL_TESTS >> $RESULTS 
fi



mail -s "$ACTION: finished automated tooltests" $MAILTO < $RESULTS


