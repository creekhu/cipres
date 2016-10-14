#!/bin/bash
checkJobs_out=`checkJobsD status`
loadResults_out=`loadResultsD status`

# grep returns 0 when it finds the text
grep_out=`echo "$checkJobs_out" | grep "is NOT Running"`
status=$?

if [ $status -eq 0 ]; then
	echo checkJobsD is not running
	checkJobsD restart
fi

grep_out=`echo "$loadResults_out" | grep "is NOT Running"`
status=$?
if [ $status -eq 0 ]; then
	echo loadResultsD is not running
	loadResultsD restart
fi

