#!/usr/bin/env python

"""
    -u
        username
    -p
        password
    -a
        appid
    -t
        toolspec directory
    -s
        rest service url
    -n
        number of times to submit toolspec
    -w
        wait for submissions to finish
        

"""
import sys
import os
import re
import string
import subprocess
import tempfile
import getopt
import time
import requests
import xml.etree.ElementTree as ET
import pyjavaproperties as Props
import testjob 


def main(argv=None):
    if argv is None:
        argv = sys.argv
    
    user = password = appid = testspec = service = jobcount = None
    waitForCompletion = False
    options, remainder = getopt.getopt(argv[1:], "u:p:a:t:s:n:wh")
    for opt, arg in options:
        if opt in ("-u"):
            user = arg
        elif opt in ("-p"):
            password = arg
        if opt in ("-t"):
            testspec = arg
        elif opt in ("-s"):
            service = arg
        elif opt in ("-n"):
            jobcount = int(arg)
        elif opt in ("-w"):
            waitForCompletion = True
        elif opt in ("-a"):
            appid = int(arg)
        elif opt in ("-h"):
            print __doc__
            return 0
    if not (user and password and testspec and service and jobcount and appid):
        print __doc__
        return -1
    
    jobs = []
    for i in range(jobcount):
        tj = testjob.TestJob(
            baseurl=service, 
            user=user,
            credentials = (user, password),
            appid=appid,
            testdir = testspec
        )
        tj.doPost()
        jobs.append(tj)
        print "Submitted %s" % (tj.jobhandle)
        sys.stdout.flush()
    
    if waitForCompletion:
        while True: 

            unfinishedJobs = 0


            # jobs[:] makes a copy of the list in jobs.  You need to work on a copy if
            # you want to remove items from a list you're iterating over.
            for job in jobs[:]:
                if job.jobIsTerminal:
                    print "%s finished. %s." % (job.jobhandle, ('OK', 'FAILED')[job.isFailed])
                    sys.stdout.flush()
                    jobs.remove(job)
                    continue
                else:
                    job.updateJobStatus()
                    if job.jobIsTerminal:
                        print "%s finished. %s." % (job.jobhandle, ('OK', 'FAILED')[job.isFailed])
                        sys.stdout.flush()
                        jobs.remove(job)
                        continue
                    unfinishedJobs += 1

            if not unfinishedJobs:
                break;
            time.sleep(30)

if __name__ == "__main__":
    sys.exit(main())


