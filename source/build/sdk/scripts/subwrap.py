#!/usr/bin/python
# wrap workbench_submit_airavata.py to remove ssh-specific args
# and quote the command line

REMOTECOMMAND = 'workbench_submit_airavata.py'

import sys
import os
import os.path
import re
import subprocess
import string
import time
import signal
#import logging
#import logging.handlers

def timedrun(command,timeout=120) :
    child_obj = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
    returncode = None
    iterations = 0
    while returncode == None and iterations <= timeout :
        returncode = child_obj.poll()
        if returncode == None :
            iterations = iterations + 1
            time.sleep(1)
        else :
            break
    if returncode == None :
        # child failed to beat timeout
        print "(%s) failed to return in (%s)!" % (command, timeout)
        #FO = open('/users/u4/nsgdevuser/kennethtest/scigap/PythonClients/samples/s.timeout', 'w')
        #FO.write("(%s) failed to return in (%s)!" % (command, timeout))
        #FO.close()
        os.kill(child_obj.pid,signal.SIGKILL)
        (stdout, stderr) = child_obj.communicate()
        return ("timedrun (%s) failed (%s)!" % (command,stdout), -1, stderr)
    else :
        (stdout, stderr) = child_obj.communicate()
        return (stdout, returncode, stderr)

#sysloghandler = new logging.handlers.SysLogHandler()
#sysloghandler.emit("start of subwrap.py")
os.system("logger -p local3.info 'start of subwrap.py'")

dashi_pat = '-i'
dash_reo = re.compile(dashi_pat)
idfile_pat = r".*/.ssh/.*"
idfile_reo = re.compile(idfile_pat)
user_pat = r".*@*"
user_reo = re.compile(user_pat)
# jobid=989882
jobid_pat = r"jobid=\S+"
jobid_reo = re.compile(jobid_pat)

#LOGFILE = '/users/u4/nsgdevuser/kennethtest/scigap/PythonClients/samples/s.out'
#if os.path.exists(LOGFILE):
#    LOGFO = open(LOGFILE, 'a')
#else:
#    LOGFO = open(LOGFILE, 'w')
#LOGFO.write(string.join(sys.argv))
#FO.close()

kept_args = []
for index,arg in enumerate(sys.argv[1:]):
    if index == 0 and dash_reo.match(arg) != None:
        continue
    if index == 1 and idfile_reo.match(arg) != None:
        continue
    if index == 2 and user_reo.match(arg) != None:
        continue
    kept_args.append(arg)

commandline = '"' + string.join(kept_args) + '"'
runline = REMOTECOMMAND + " -v -commandline \"%s\"" % commandline
runline = REMOTECOMMAND + " -v -commandline %s" % commandline
#FO = open('/users/u4/nsgdevuser/kennethtest/scigap/PythonClients/samples/s.stdout', 'w')
#LOGFO.write(runline)
stdout, returncode, stderr = timedrun(runline)
#LOGFO.write(stdout)
#FO.close()
#FO = open('/users/u4/nsgdevuser/kennethtest/scigap/PythonClients/samples/s.stderr', 'w')
#LOGFO.write(stderr)
#FO.close()
sys.stdout.write(stdout)
sys.stderr.write(stderr)
#jobid_mo = jobid_reo.search(stdout)
#if jobid_mo == None:
#    sys.stderr.write("Failed to find jobid in (%s), returning 1\n" % stdout)
#    sys.exit(1)
#else:
#    sys.exit(returncode)
#LOGFO.close()
#sysloghandler.emit("end of subwrap.py")
os.system("logger -p local3.info '%s'" % stdout)
os.system("logger -p local3.info '%s'" % stderr)
os.system("logger -p local3.info 'end of subwrap.py'")
sys.exit(returncode)
