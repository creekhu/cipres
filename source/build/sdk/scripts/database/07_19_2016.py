
#!/usr/bin/env python
"""
"""
import sys
import os
import re
import string
import subprocess
import getopt
import pymysql
from contextlib import closing
from pymysql.err import IntegrityError, Error
passwordFile = os.path.expandvars("${SDK_VERSIONS}/db_password.txt")

conn = None
def updateUserDataItem():
    query = "show columns from userdata like 'UUID' "
    cur = conn.cursor()
    cur.execute(query)
    uuid_exists = cur.fetchall()
    if not uuid_exists:
        try:
            cur.execute("alter table userdata add UUID varchar(46) not null after validated")

            query = "update userdata set UUID = (select(replace( UUID(), '-', '_' )))" 
            cur.execute(query)

            cur.execute( "alter table userdata add unique index UUID ( UUID )" )
            conn.commit()
        except Error, e:
            print e
            conn.rollback()
    cur.close()

def main():
    global conn

    # Get the database name and password
    properties = {} 
    pf = open(passwordFile, "r");
    for line in iter(pf):
        s = line.split('=')
        properties[s[0].strip()] = s[1].strip()
    
    conn = pymysql.connect(
        host=properties["host"], 
        port=int(properties["port"]), 
        user=properties["username"], 
        passwd=properties["password"], 
        db=properties["db"])

    print conn
    updateUserDataItem()

if __name__ == "__main__":
    sys.exit(main())
