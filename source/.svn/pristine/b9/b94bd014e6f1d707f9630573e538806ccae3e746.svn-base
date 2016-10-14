#!/bin/sh
# export URL=http://localhost:7070/cipresrest/v1
# export RES=/Users/terri/Documents/workspace/sdk/src/main/resources

export URL=https://billiards1.sdsc.edu/cipresrest/v1
export RES=../../../sdk/src/main/resources
export NUMBER=10

# see ngbwalpha.sql: it initializes db with some test users and applications
# 2nd application (appid=2) is the one this test uses.  We  use authentication
# mechanism = DIRECT, where user must have pre-existing account.
export APPID=2


./test1.py \
	-u scripts.test1 \
	-p test \
	-a $APPID \
	-t  $RES/examples/new_tooltest/CLUSTALW/ \
	-s $URL/job/ \
	-n $NUMBER \
	-w \
	> t1.txt 2>&1 &

./test1.py \
	-u scripts.test1 \
	-p test \
	-a $APPID \
	-t  $RES/examples/new_tooltest/READSEQ/ \
	-s $URL/job/ \
	-n $NUMBER \
	-w \
	> t2.txt 2>&1 &

./test1.py \
	-u scripts.test1 \
	-p test \
	-a $APPID \
	-t  $RES/examples/new_tooltest/CLUSTALW/ \
	-s $URL/job/ \
	-n $NUMBER \
	-w \
	> t3.txt 2>&1 &

./test1.py \
	-u scripts.test1 \
	-p test \
	-a $APPID \
	-t  $RES/examples/new_tooltest/BEAST_TG/ \
	-s $URL/job/ \
	-n $NUMBER \
	-w \
	> t4.txt 2>&1 &

./test1.py \
	-u scripts.test1 \
	-p test \
	-a $APPID \
	-t  $RES/examples/new_tooltest/SLEEP/ \
	-s $URL/job/ \
	-n $NUMBER \
	-w \
	> t5.txt 2>&1 &

./test1.py \
	-u scripts.test1 \
	-p test \
	-a $APPID \
	-t  $RES/examples/new_tooltest/RAXMLHPC2BB/ \
	-s $URL/job/ \
	-n $NUMBER \
	-w \
	> t6.txt 2>&1 &

