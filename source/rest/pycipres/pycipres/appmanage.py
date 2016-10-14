#!/usr/bin/env python

"""
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

"""
    Get a rest application's app id.  Initialize the object with the credentials for the
    adminstrator of the rest application or with a cipres administrator's credentials.  Call
    getAppId with url of the form cipresrest/v1/application/<user>/<appname>, where
    user is the application's adminstrator.

    By default, gets all the info from SDK_VERSIONS/testdata/pycipres.conf and prints the
    appid to stdout.  This is the appid of the 'pycipres' application that is registered
    by default by the script, testAndDemoAccounts. 
"""
class AppManagement(object):
    def __init__(self, 
        credentials=None,
        verbose=False):

        self.defaults = Props.Properties()
        with open(os.path.expandvars("${SDK_VERSIONS}/testdata/pycipres.conf")) as infile:
            self.defaults.load(infile)
        self.defAdminUser= self.defaults.getProperty('PYCIPRES_ADMIN')
        self.defAdminPass = self.defaults.getProperty('TESTPASS')
        self.defAdminApp = self.defaults.getProperty('PYCIPRES_APP')

        # print "user=%s, pass=%s, app=%s\n" % (self.defAdminUser, self.defAdminPass, self.defAdminApp)

        if credentials:
            self.credentials = credentials
        else:
            self.credentials = (self.defAdminUser, self.defAdminPass)
        self.verbose = verbose

        self.defUrl = (self.defaults.getProperty('URL') + "/application/" + self.defAdminUser + 
            "/" + self.defAdminApp)

        # print "default url=%s" % (self.defUrl)

    def getAppId(self, url=None):
        if url:
            useUrl = url
        else:
            useUrl = self.defUrl 
        r = requests.get(useUrl, auth=self.credentials, verify=False);
        if self.verbose:
            print "GET %s" % (url)
            print "Status = %d" % (r.status_code)
            print "Content = %s" % (r.text) 
        if r.status_code == 200:
            et = ET.fromstring(r.text)
            appid = et.find("app_id").text
            return appid
        else:
            raise Exception("GET returned http status %s, %s" % (str(r.status_code), r.text))

    def getDefaultProperties(self):
        self.defaults.setProperty("PYCIPRES_APPID", self.getAppId())
        return self.defaults


def main(argv=None):
    if argv is None:
        argv = sys.argv
    """
    app = AppManagement( ('pycipres_admin', 'test'), True)
    appid = app.getAppId('http://localhost:7070/cipresrest/v1/application/pycipres_admin/pycipres')
    print "appid is ", appid
    """

    app = AppManagement()
    appid = app.getAppId()
    print appid 
                


if __name__ == "__main__":
    sys.exit(main())


