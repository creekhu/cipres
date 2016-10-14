#!/usr/bin/env python

"""
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
import requests
import xml.etree.ElementTree as ET

baseurl = "https://billiards1.sdsc.edu/cipresrest/v1/job"

# This is the username in the url
ruser = "terri"

# These are the authentication credentials
credentials = ("terri", "berryterri")

def get(url):
    print
    print "###########################################################################################################"
    r = requests.get(url, auth=credentials, verify=False);
    print "GET %s" % (url)
    print "Status = %d" % (r.status_code)
    print "Content = %s" % (r.text) 
    return r

def showUsersJobs():
    r = get("%s/%s" % (baseurl, ruser))
    jobs = ET.fromstring(r.text)

    root = ET.fromstring(r.text)
    for job in root.findall('link'):
        url = job.find('url').text

        # Just for debugging this script
        # print url

        r = get(url)

def submitTestJob():
    infile = "/Users/terri/samplefiles/phylip/infile.phylip"
    theUrl = "%s/%s" % (baseurl, ruser)
    payload = {
        "tool" : "RAXMLHPC2BB" ,
        "metadata.clientJobId" : "100",
        "metadata.clientJobName" : "terri's raxml job",
        "vparam.runtime_" : ".15" ,
        "vparam.datatype_" : "dna",
        "vparam.empirical_" :  ""  ,
        "vparam.invariable_" :  "" ,
        "vparam.mlsearch_" : "true",
        "vparam.printbrlength_" : "false" ,
        "vparam.prot_matrix_spec_" : "JTT",
        "vparam.use_bootstopping_" : "true"
    }
    files = { 'input.infile_' : open(infile, 'rb') }

    r = requests.post(theUrl, data=payload, files=files, auth=credentials, verify=False)

    print "Status = %d" % ( r.status_code)
    print "Content = %s" % (r.text)

def testAuthErrors():
    """
        - without credentials
        - with invalid password
        - with digest auth
        - without https
        - try all of the urls with ruser in url that doesn't match the auth credential user
        - access jobstatus, list results, result file, list intermediate, intermediate file w/o credentials for that 
        particular url/job/file
    """

def testBadUrls():
    """
        - various malformed urls, job doesn't exist, file doesn't exist, etc.
    """


    

def main(argv=None):
    if argv is None:
        argv = sys.argv
    submitTestJob()
    # showUsersJobs()



if __name__ == "__main__":
    sys.exit(main())


