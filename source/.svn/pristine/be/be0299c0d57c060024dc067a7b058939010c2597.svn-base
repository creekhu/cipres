#!/usr/bin/env python
"""
    Run various queries against the CIPRES production database.  
    Unless otherwise noted, results are written to stdout in csv format.
    Usage:
        usage.py [-modifier] -flag

    where modifiers are:
        -c chargeNumber. Optional.  Limit search to chargeNumber (for cipres 
        allocation, iplant allocation, etc). 

        -e add up Sus by email address rather than by user account.  Can use with
        -t and -p flags.

    where flag is one of:
        -j  Updates su_charged field in job_stats table with data from tgusage table, where missing
            in job_stats table.  Also sets job_stats.tg_charge_number, if null, to charge number from
            tgusage table, though this is something we should only have to do once, since now we
            cipres stores tg_charge_number in job_stats even when using community account.
        -a  Joins job_stats and tgusage tables to print tgusage and user info for all jobs in the db
        -u  Joins job_stats and tgusage tables and prints SUs per user, per month.  
            Sorted by year and month, then user.
        -p  Shows sus for each user for the period.  
        -d  Shows sus for each user for the specified period.  For example "-p 2011-07-01,2012-06-30" 
            gets sus for jobs submitted between July 7 2011 and June 30 2012 inclusive.  
            yyyy-mm-dd date format only. 
        -t  Shows all sus for each user
        -w  Send overlimit emails

"""
import sys
import os
import re
import string
import subprocess
import tempfile
import getopt
import pymysql
from pymysql.err import IntegrityError, Error
import csv
import mailer

passwordFile = os.path.expandvars("${SDK_VERSIONS}/db_password.txt")
resttemplate = os.path.expandvars("${SDK_VERSIONS}/rest_allocation_message.txt")
portaltemplate = os.path.expandvars("${SDK_VERSIONS}/portal_allocation_message.txt")
chargeNumberClause = ""
newChargeNumberClause = ""
useEmail = False
delimiter='\t'

# START: Maven resource filtering replaces these from build.properties
startOfPeriod = "${accounting.period.start}"
portal = "${build.portal.staticUrl}"
email = "${email.serviceAddr}"
cra = "${portal.name}" 
# END: Maven resource filtering replaces these from build.properties

def getColumnNames(cur):
    return [ t[0] for t in cur.description ]

def asCSV(cur):
    header = delimiter.join(getColumnNames(cur))
    sys.stdout.write("%s\n" %  header)
    for row in cur:
        line = delimiter.join([str(field) for field in row])
        sys.stdout.write("%s\n"% line)

def updateJobStats():
    global chargeNumberClause
    # This is the query that we should only need to run one time
    query = """
        update job_stats, tgusage 
        set job_stats.tg_charge_number = tgusage.charge_number
        where job_stats.tg_charge_number is null AND
        job_stats.remote_job_id = tgusage.jobid AND
        job_stats.resource = tgusage.resource AND
        datediff(job_stats.date_submitted, tgusage.submit_time) < 2
    """
    cur = conn.cursor()
    cur.execute(query)
    conn.commit()
    cur.close()

    # This should be run each time we get new data in tgusage table:
    query = """
        update job_stats, tgusage 
        set job_stats.su_charged = tgusage.su
        where job_stats.su_charged is null AND
        job_stats.remote_job_id = tgusage.jobid AND
        job_stats.resource = tgusage.resource AND
        datediff(job_stats.date_submitted, tgusage.submit_time) < 2
    """
    cur = conn.cursor()
    cur.execute(query)
    conn.commit()
    cur.close()

def allUsage():
    global chargeNumberClause
    query = """
        select
        job_stats.tool_id,
        job_stats.jobhandle,
        job_stats.user_id,
        users.username,
        job_stats.email,
        tgusage.*
        from job_stats, tgusage, users  where
        %s
        job_stats.resource = tgusage.resource and
        job_stats.remote_job_id = tgusage.jobid and
        job_stats.user_id = users.user_id and
        abs(datediff(job_stats.date_submitted, tgusage.submit_time)) < 2
        order by (tgusage.submit_time)
        """ % chargeNumberClause
    print "All usage query:\n%s" % query
    # DictCursor isn't implemented in this version of pymysql
    # cur = conn.cursor(MySQLdb.cursors.DictCursor)
    cur = conn.cursor()
    cur.execute(query)
    asCSV(cur)
    cur.close()

def suPerUserTotalByEmail(date):
    global newChargeNumberClause

    # If a range is given it must include a start date.  End date is optional.
    if date:
        dateRange = date.split(",")
        dateClause = " AND job_stats.date_submitted >= '" + dateRange[0] + "' " 
        if len(dateRange) > 1 and dateRange[1]:
            dateClause = " AND job_stats.date_submitted <= '" + dateRange[1] + "' " 
    else:
        dateClause = ""
    query = """
        select job_stats.email,  sum(job_stats.su_charged)
        from job_stats 
        where 
        %s
        job_stats.su_charged > 0 
        %s
        group by job_stats.email 
    """ % (newChargeNumberClause, dateClause)

    print "suPerUserTotalByEmail query:\n%s" % query

    cur = conn.cursor()
    cur.execute(query)
    asCSV(cur)
    cur.close()




def suPerUserTotal(date):
    global chargeNumberClause

    # If a range is given it must include a start date.  End date is optional.
    if date:
        dateRange = date.split(",")
        dateClause = " and job_stats.date_submitted >= '" + dateRange[0] + "' " 
        if len(dateRange) > 1 and dateRange[1]:
            dateClause = " and job_stats.date_submitted <= '" + dateRange[1] + "' " 
    else:
        dateClause = ""
    query = """
        select
        users.username,
        users.institution,
        job_stats.user_id,
        job_stats.email,
        sum(tgusage.su)
        from job_stats, tgusage, users where
        %s
        job_stats.resource = tgusage.resource and
        job_stats.remote_job_id = tgusage.jobid and
        job_stats.user_id = users.user_id and
        abs(datediff(job_stats.date_submitted, tgusage.submit_time)) < 2
        """ % chargeNumberClause 
    query += dateClause + " group by users.username, job_stats.email"
    print "Su per user total query:\n%s" % query
    cur = conn.cursor()

    cur.execute(query)
    asCSV(cur)
    cur.close()

def suPerUser():
    global chargeNumberClause
    query = """
        select
        year(tgusage.end_time),
        monthname(tgusage.end_time),
        job_stats.user_id,
        users.username,
        job_stats.email,
        sum(tgusage.su)
        from job_stats, tgusage, users where
        %s
        job_stats.resource = tgusage.resource and
        job_stats.remote_job_id = tgusage.jobid and
        job_stats.user_id = users.user_id and
        abs(datediff(job_stats.date_submitted, tgusage.submit_time)) < 2
        group by year(tgusage.end_time), month(tgusage.end_time), users.username, job_stats.email
        """ % chargeNumberClause
    cur = conn.cursor()
    cur.execute(query)
    asCSV(cur)
    cur.close()

def overLimitWarning():
    global startOfPeriod, chargeNumberClause
    # maxSusLevel0 = 1
    maxSusLevel0 = 10000
    maxSusLevel1 = 20000
    maxSusLevel2 = 30000
    maxSusLevel3 = 50000
    maxSusLevel4 = 80000
    maxSusLevel5 = 100000

    query = """
    select job_stats.user_id, users.username, users.email, sum(tgusage.su), users.umbrella_appname, 
        applications.longname, users.role
    from users left join applications
    on users.umbrella_appname = applications.name
    inner join job_stats
    on job_stats.user_id = users.user_id
    inner join tgusage
    on job_stats.resource = tgusage.resource and job_stats.remote_job_id = tgusage.jobid and 
    abs(datediff(job_stats.date_submitted, tgusage.submit_time)) < 2
    where 
    %s
    job_stats.date_submitted >= '%s'
    group by job_stats.user_id
    having sum(tgusage.su) > %d 
    """ % (chargeNumberClause, startOfPeriod, maxSusLevel0)

    print "OverLimitWarning query:\n%s" % query

    cur = conn.cursor()
    cur.execute(query)

    for row in cur:
        # I wasn't sure whether to pass in template name from cron job or hardcode
        # it here based on the type of record.  Decided on the latter because it makes
        # the cron config simpler, but would be easy to change if needed.  
        if (row[6] == "REST_END_USER_UMBRELLA" or row[6] == "REST_USER"):
            template = resttemplate
        else:
            template = portaltemplate

        if row[5] and len(row[5]) > 0:
            application = row[5]
        else:
            application = cra; 

        # print row, application, template

        # Depending on the "level" of overusage we'll add a different type of 
        # SUWARNED preference to their user preferences.
        sus = long(row[3])
        if sus > maxSusLevel5:
            level = 5
            hours = maxSusLevel5
            key = "SUWARNED5"
        if sus > maxSusLevel4:
            level = 4
            hours = maxSusLevel4
            key = "SUWARNED4"
        elif sus > maxSusLevel3:
            level = 3
            hours = maxSusLevel3
            key = "SUWARNED3"
        elif sus > maxSusLevel2:
            level = 2
            hours = maxSusLevel2
            key = "SUWARNED2"
        elif sus > maxSusLevel1:
            level = 1
            hours = maxSusLevel1
            key = "SUWARNED1"
        else:
            level = 0
            hours = maxSusLevel0
            key = "SUWARNED0"

        # If the insert suceedes we send the email.  Else if we get an IntegrityError
        # we know the record already existed and the email was already sent.
        query2 = """
            insert into user_preferences (user_id, value, preference)
            values(%d, now(), "%s")
        """ % (long(row[0]), key)

        cur2 = conn.cursor()
        try:
            cur2.execute(query2)
            conn.commit()
            print("Sending warning email to %s, uid=%d, %s, role=%s for level %d" % (row[2], row[0], row[1], row[6], level))
            mailer.overLimitWarning(row[2], row[1], template, hours, application, portal, email);

        except IntegrityError, ie:
            conn.rollback()
            # print str(ie)
            print "%s already issued for %d" % (key, long(row[0]))
        except Error, e:
            conn.rollback()
            print str(e)
            print "insert row for %d FAILED unexpectedly." % long(row[0])
        cur2.close()

    cur.close()


conn = None
def main(argv=None):
    global startOfPeriod
    global conn, chargeNumberClause, useEmail
    if argv is None:
        argv = sys.argv


    # Get the database name and password
    properties = {} 
    pf = open(passwordFile, "r");
    for line in iter(pf):
        s = line.split('=')
        properties[s[0].strip()] = s[1].strip()
    
    conn = pymysql.connect(host=properties["host"], port=int(properties["port"]), user=properties["username"], 
        passwd=properties["password"], db=properties["db"])

    options, remainder = getopt.getopt(argv[1:], "ejhauwtpc:d:")
    for opt, arg in options:
        # either the name of the ref file or the directory that contains the ref files.
        if opt in ("-j"):
            updateJobStats()
            return 0
        if opt in ("-a"):
            allUsage()
            return 0
        elif opt in ("-u"):
            suPerUser()
            return 0
        elif opt in ("-w"):
            overLimitWarning()
            return 0
        elif opt in ("-t"):
            if useEmail:
                suPerUserTotalByEmail(None)
            else:       
                suPerUserTotal(None)
            return 0
        elif opt in ("-d"):
            dateRange = arg
            suPerUserTotal( dateRange )
            return 0
        elif opt in ("-p"):
            dateRange = arg
            if not dateRange:
                dateRange = startOfPeriod
            if useEmail:
                suPerUserTotalByEmail( dateRange )
            else:
                suPerUserTotal( dateRange )
            return 0
        elif opt in ("-h"):
            print __doc__
            return 0
        elif opt in ("-c"):
            chargeNumber = arg
            chargeNumberClause = " tgusage.charge_number = '%s' and " % chargeNumber
            newChargeNumberClause = " job_stats.charge_number = '%s' and " % chargeNumber
        elif opt in ("-e"):
            useEmail = True
    print __doc__
    return 0

if __name__ == "__main__":
    sys.exit(main())
