#!/bin/sh
sdkrun \
	-Dthreads=1\
	-Duser=t1 \
	-Dpass=t1 \
	-Djobs=examples/new_tooltest/CLUSTALW:examples/new_tooltest/READSEQ \
	org.ngbw.examples.LoadTest1 

# If the above crashes or is killed and you want to make sure you dont' leave any
# jobs still running, the following cancels all the users jobs and deletes the tasks.
# Even if no jobs are running, it's a good idea to delete the tasks after each test.

# sdkrun org.ngbw.utils.CancelUsersJobs t1
