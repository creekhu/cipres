#!/usr/bin/env python

"""
    TestJob submits a job.  Specifics of the job are specified in a directory of
    the same type used by our java examples.TestJob class.  There are a bunch 
    of sample job dirs of this type in sdk/resources/examples/new_tooltest.
    Directories of this type must contain 2 properties files with fixed names, 
    that specify the input file parameters and other parameters for the job.  Any
    input files for the job are in the same directory.

    You need to install the "requests" http library to use this. 
    See here: http://docs.python-requests.org/en/latest/index.html.
    Just download the zipped source, unzip  and run "python setup.py install"

    Documentation for the xml parser is here:
        http://docs.python.org/2/library/xml.etree.elementtree.html#xml.etree.ElementTree.Element
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

# This stuff should be passed in.
# default_baseurl = "https://billiards1.sdsc.edu/cipresrest/v1/job/"
default_baseurl = "http://localhost:7070/cipresrest/v1/job/"
default_ruser = "test1"
default_credentials = ("test1", "test")
default_testdir = "/Users/terri/Documents/workspace/sdk/src/main/resources/examples/new_tooltest/CLUSTALW/"
default_appid = 2

class TestJob(object):
    def __init__(self, 
        baseurl=default_baseurl, 
        user=default_ruser,
        credentials = default_credentials,
        appid = None,
        testdir = default_testdir,
        jobhandle = None,
        verbose = False
        ):

        self.baseurl = baseurl
        self.user = user
        self.mainurl = baseurl + self.user
        self.credentials = credentials
        self.appid = appid
        self.testdir = testdir
        self.fileParams = self.__loadFileProperties()
        self.otherParams = self.__loadParamProperties()
        self.verbose = verbose
        self.initializeFromJobHandle = jobhandle

        if not self.appid:
            raise Exception("Can't create TestJob without appid");
        self.headers = {'cipres-appkey': appid }
            
        if jobhandle:
            r = self.doGet(urlpart=jobhandle)
            if r.status_code == 200:
                self.parseJobStatus(r.text)
            else:
                raise Exception("POST returned http status %s, %s" % (str(r.status_code), r.text))

    def doGet(self, url=None, urlpart=None):
        if not url:
            if urlpart:
                url = self.mainurl + "/" + urlpart
            else:
                return None
        r = requests.get(url, auth=self.credentials, verify=False, headers = self.headers);
        if self.verbose:
            print "GET %s" % (url)
            print "Status = %d" % (r.status_code)
            print "Content = %s" % (r.text) 
        return r
    
    def updateJobStatus(self):
        r = self.doGet(url=self.statusUri)
        if r.status_code == 200:
            self.parseJobStatus(r.text)
        else:
            raise Exception("POST returned http status %s, %s" % (str(r.status_code), r.text))

        # Todo: Get a list of the result file urls in self.resultsFiles:
        if self.jobIsTerminal:
            r = self.doGet(url=self.resultsUri)
            if r.status_code == 200:
                self.parseResults(r.text)
            else:
                raise Exception("GET returned http status %s, %s" % (str(r.status_code), r.text))
        return True

    def parseResults(self, text):
        if self.verbose:
            print text
        self.resultFiles = {}
        et = ET.fromstring(text)

        for child in et:
            url = child.find("fileUri").find("url").text
            name = child.find("filename").text
            self.resultFiles[name] = url


    def parseJobStatus(self, text):
        self.jobstatus = ET.fromstring(text)
        self.jobhandle = self.jobstatus.find("jobHandle").text
        self.jobIsTerminal = (self.jobstatus.find("terminalStage").text == "true")
        self.jobstage = self.jobstatus.find("jobStage").text
        self.statusUri = self.jobstatus.find("statusUri").find("url").text
        self.resultsUri = self.jobstatus.find("resultsUri").find("url").text

        if self.verbose:
            print "jh=%s, stage=%s, isTerminal=%s, resultsUrl=%s" % (
                self.jobhandle, self.jobstage, self.jobIsTerminal, self.resultsUri)
        if self.jobIsTerminal:
            self.isFailed = (self.jobstatus.find("failed").text.lower() == 'true'.lower())

    def waitForCompletion(self, pollInterval=60):
        while True:
            self.updateJobStatus()
            if self.jobIsTerminal:
                break
            time.sleep(pollInterval)


    def __loadFileProperties(self):
        fp = Props.Properties()
        with open(self.testdir + "testInput.properties") as infile:
            fp.load(infile)
        return fp

    def __loadParamProperties(self):
        op = Props.Properties()
        with open(self.testdir + "testParam.properties") as infile:
            op.load(infile)
        return op

    def doPost(self):
        files = {}
        try:
            for param in self.fileParams.propertyNames():
                paramname = "input.%s" % (param)
                pathname = self.fileParams.getProperty(param)
                if not os.path.isabs(pathname):
                    pathname = os.path.join(self.testdir, os.path.basename(pathname))
                files[paramname] = open(pathname, 'rb')
            payload = {}
            for param in self.otherParams.propertyNames():
                if param == "toolId":
                    payload["tool"] = self.otherParams.getProperty(param)
                else:
                    name = "vparam.%s" % (param)
                    payload[name] = self.otherParams.getProperty(param)

            r = requests.post(self.mainurl, data=payload, files=files, auth=self.credentials, 
                headers=self.headers, verify=False)
            if self.verbose:
                print "Status = %d" % ( r.status_code)
                print "Content = %s" % (r.text)

            if r.status_code == 200:
                self.parseJobStatus(r.text)
            else:
                raise Exception("POST returned http status %s, %s" % (str(r.status_code), r.text))

        finally:
            for param, openfile in files.iteritems():
                openfile.close
    
    

j1 = "NGBW-JOB-CLUSTALW-9EF7D0E0C62340B0B2651D38CB0674C9"
# j1 = "NGBW-JOB-CLUSTALW-B571B8CE129C472489734C290C3235B7"

def main(argv=None):
    if argv is None:
        argv = sys.argv
    tj = TestJob(jobhandle=j1, appid=default_appid)
    tj.waitForCompletion()
    print "Result files are:"
    for name, url in tj.resultFiles.items():
        print "%s : %s" % (name, url)
        

    """
    tj = TestJob()
    tj.doPost()
    tj.waitForCompletion()
    """
    

if __name__ == "__main__":
    sys.exit(main())


