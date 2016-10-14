#!/bin/sh

# Tests job submission and status polling from multiple concurrent processes.
# Starts 3 processes running job.sh and 5 running submit.sh.

# Each instance of job.sh starts 2 clustalw jobs under app 'guidemo' and each instance of submit.sh starts 2
# under app 'pycipres'.

# so there should be a total of 6 guidemo jobs started and 10 pycipres jobs.  You may want to make sure there's nothing
# in the running task table or task table when you start this test.   There's nothing waiting for the jobs 
# to complete, but you can manually check that 16 are submitted and accounted for.
#
# You can empty the task table with
# 	curl -k -u <admin>:<adminpass> -X DELETE https://<host>/cipresrest/v1/job/<user>  -- repeat for each user with jobs
# Then run recoverResults to get them cleared from the running task table if it isn't already empty.

ODIR=concurrencytests_out

if [[ $# < 2 ]] ; then
	echo "cipresadmin username and pass required on commandline." 
	echo "Puts output files in a subdir of currentdir named $ODIR." 
	echo "stdout will be redirected to files named $ODIR/concurrency1.out, $ODIR/concurrency2.out, ..."
	exit 1
fi

firstType()
{
	echo "========================================================================================="
	echo "=====================================RUN job.sh=============================================="
	echo "========================================================================================="
	sh job.sh $1 $2 

}

secondType()
{
	echo "========================================================================================="
	echo "=====================================RUN submit.sh=============================================="
	echo "========================================================================================="
	sh submit.sh $1 $2 
}

if [ ! -d "$ODIR" ] ; then
	mkdir $ODIR || { echo "Error Creating $ODIR";  exit 1; }
fi
rm $ODIR/concurrency*.out

firstType "$@" > $ODIR/concurrency1.out &
pidlist="$pidlist $!"

secondType "$@" > $ODIR/concurrency2.out &
pidlist="$pidlist $!"

firstType "$@" > $ODIR/concurrency3.out &
pidlist="$pidlist $!"

secondType "$@" > $ODIR/concurrency4.out &
pidlist="$pidlist $!"

firstType "$@" > $ODIR/concurrency5.out &
pidlist="$pidlist $!"

secondType "$@" > $ODIR/concurrency6.out &
pidlist="$pidlist $!"

secondType "$@" > $ODIR/concurrency7.out &
pidlist="$pidlist $!"

secondType "$@" > $ODIR/concurrency8.out &
pidlist="$pidlist $!"

echo waiting for 8  background processes to finish ...
FAIL=0
for job in $pidlist
do
	echo "WAITING FOR $job"
	wait $job || ( let "FAIL+=1" ; echo failed )
done
exit $FAIL


