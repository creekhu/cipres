Start the tooltests like this:

$ nohup ./runtooltests.sh validate &
$ nohup ./runtooltests.sh run &


Some initial messages are written to ./results.txt telling you how to monitor
the test processes and kill them, then the individual tests are launched.  Each
writes its output to a separate file named results.N, where N, is the test number.
Then we wait for all the tests to complete and concatenate results.* to results.txt.




