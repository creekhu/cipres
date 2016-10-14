#!/usr/bin/env python

"""
    jobtemplate submits a job.  Specifics of the job are specified in a directory of
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

class JobTemplate(object):
    def __init__(self, 
        baseurl=None,
        user=None,
        credentials = None,
        appid = None,
        testdir = None,
        jobhandle = None,
        verbose = False,
        validateOnly = False,
        ):

        self.baseurl = baseurl
        self.user = user
        self.mainurl = baseurl + self.user
        self.credentials = credentials
        self.appid = appid
        self.testdir = testdir
        if self.testdir:
            self.testdir = os.path.normpath(testdir) + os.sep
            self.fileParams = self.__loadFileProperties()
            self.otherParams = self.__loadParamProperties()
        self.verbose = verbose
        self.initializeFromJobHandle = jobhandle
        self.validateOnly = validateOnly
        self.validationUrl = self.mainurl + "/validate" 

        if not self.appid:
            raise Exception("Can't create JobTemplate without appid");
        self.headers = {'cipres-appkey': appid }
            
        if jobhandle:
            r = self.doGet(urlpart=jobhandle)
            if r.status_code == 200:
                self.parseJobStatus(r.text)
            else:
                print child.find("selfUri").find("title").text
                raise Exception("POST returned http status %s, %s" % (str(r.status_code), r.text))

    def listJobs(self, expand=True):
        if expand:
            r = self.doGet(url=self.mainurl + "/" + "?expand=true")
        else:
            r = self.doGet(url=self.mainurl)
        if r.status_code == 200:
            self.parseJobList(r.text)
        else:
            raise Exception("GET %s returned status %d" % (self.mainurl, str(r.status_code)) )
        return True

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
            rr = self.doGet(url=self.resultsUri)
            if rr.status_code == 200:
                self.parseResults(rr.text)
            else:
                raise Exception("GET returned http status %s, %s" % (str(rr.status_code), rr.text))
        return  ET.fromstring(r.text)

    def parseResults(self, text):
        if self.verbose:
            print text
        self.resultFiles = {}
        et = ET.fromstring(text)

        for child in et.find("jobfiles"):
            url = child.find("downloadUri").find("url").text
            name = child.find("filename").text
            self.resultFiles[name] = url


    def parseJobStatus(self, text):
        self.jobstatus = ET.fromstring(text)
        if self.validateOnly:
            self.commandline = self.jobstatus.find("commandline").text
            if self.verbose:
                print "Parameters are valid.  Command line is: '%s'" % (self.commandline)
        else:
            self.jobhandle = self.jobstatus.find("jobHandle").text
            self.jobIsTerminal = (self.jobstatus.find("terminalStage").text == "true")
            self.jobstage = self.jobstatus.find("jobStage").text
            self.statusUri = self.jobstatus.find("selfUri").find("url").text
            self.resultsUri = self.jobstatus.find("resultsUri").find("url").text

            if self.verbose:
                print "jh=%s, stage=%s, isTerminal=%s, resultsUrl=%s" % (
                    self.jobhandle, self.jobstage, self.jobIsTerminal, self.resultsUri)
            if self.jobIsTerminal:
                self.isFailed = (self.jobstatus.find("failed").text.lower() == 'true'.lower())

    """
        When list of jobs is requested, resulting document has top level <joblist>,
        then <jobs>, then multiple <jobstatus> objects.
    """
    def parseJobList(self, text):
        et = ET.fromstring(text)

        # This loops over children of <jobs> which are the <jobstatus> nodes
        for jobstatus in et.find("jobs"):
            self.printJobStatus(jobstatus)
            print "\n"
      
    def printJobStatus(self, jobstatus, messages=True):
        print jobstatus.find("selfUri").find("title").text 
        if not jobstatus.find("jobHandle") == None:
            print "    terminal=%s, failed=%s, stage=%s" % ( jobstatus.find("terminalStage").text, 
                jobstatus.find("failed").text, jobstatus.find("jobStage").text)
            if messages:
                for m in jobstatus.find("messages"):
                    print "- %s" % (m.find("text").text)
        

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

            if self.validateOnly:
                url = self.validationUrl
            else:
                url = self.mainurl
            r = requests.post(url, data=payload, files=files, auth=self.credentials, 
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
    
    

if __name__ == "__main__":
    sys.exit(main())


