Database Manipulation Notes:

I keep database connection info for use with "mysql" and related commands in files like:

-rwx------ 1 terri cipres   67 Nov  7 16:21 /users/u4/terri/.mycipresprod.cnf

in my home directory, so that my password won't be on the command line and therefore visible to anyone
running ps.  It also won't be in our svn repo.  The format of these files is:

[mysql]
user=database_username
password=database_password
host=database_server_hostname
port=database_server_port



See DatabaseChanges.txt.  This is more detailed instructions on how to make a particular set of database schema 
changes.  The first 2 steps apply to any schema change.

1) Shutdown cipres and daemons and disable cron jobs so nothing is using the db.

2) Dump a backup of the data.

3) run the convert?.sql scrips in order.  

	- if all goes well, none output anything, except for convert.2.sql.

	- The purpose of convert2.sql is to look for duplicate jobhandles in tables where we're going
	to make jobhandle the primary key.  If dups are found, manually get rid of them (however seems best)
	before going on to convert3.sql.  
	
	- Convert4.sql converts the tables to innodb and takes the longest.

	- Make sure old tables got dropped.


DatabaseChanges.txt
	Keep track of schema changes here.

cipres.sql
	The current schema.  If installing cipres from scratch, initialize the database with this schema.

aug_19_2014.sql
convert1.sql
convert2.sql
convert3.sql
convert4.sql
may_2014.sql
may_29_2014.sql
	Migration scripts.  See DatabaseChanges.txt

copy
	Example of using mysql to copy contents of one db to another.

dropall.sql
	Example dhowing how to drop all tables from a cipres db. 

dump_cipres_data
	Example showing how to create a backup of a cipres db.

ngbwalpha_create
	Example showing how to create a new database using cipres.sql.

ngbwalpha_dump_schema.sql
