/*
	Step 1: Shutdown tomcat and daemons.  Disable cron jobs.
	Step 2: Have Mike backup the database and store it.
	Step 3: Using mysql gui client export csv file of job_stats and running_tasks tables
	(or have Mike do this).

	Step 4: Run this script.
*/
# Modify users table
alter table users modify USERNAME varchar(200) NOT NULL;
alter table users modify EMAIL varchar(200) NOT NULL;
alter table users add APPNAME varchar(30) NOT NULL DEFAULT '' AFTER USERNAME ;
alter table users drop  INDEX EMAIL, add UNIQUE INDEX EMAIL (EMAIL, APPNAME) ;

# Add our two new tables job_events and applications
create table `job_events` (
  `SE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `JOBHANDLE` varchar(255) NOT NULL,
  `EVENT_DATE` datetime not null,
  `TASK_STAGE` varchar(20) default null,
  `NAME` varchar(100) not null,
  `VALUE` longtext default null,
  PRIMARY KEY  (`SE_ID`),
  KEY  (`JOBHANDLE`)
);

CREATE TABLE `applications` (
  `APP_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(30) NOT NULL,
  `LONGNAME` varchar(100) NOT NULL,

  #  AUTH_TYPE is UMBRELLA OR  DIRECT.  AUTH_USER_ID is non null for UMBRELLA
  `AUTH_TYPE` varchar(30) NOT NULL,
  `AUTH_USER_ID` bigint(20) DEFAULT NULL,

  # PRIMARY CONTACT INFO FOR APPLICATION DEVELOPER OR ADMIN
  `FIRST_NAME` varchar(100) NOT NULL,
  `LAST_NAME` varchar(100) NOT NULL,
  `INSTITUTION` varchar(255) DEFAULT NULL,
  `STREET_ADDRESS` varchar(255) DEFAULT NULL,
  `CITY` varchar(100) DEFAULT NULL,
  `STATE` varchar(50) DEFAULT NULL,
  `COUNTRY` varchar(50) DEFAULT NULL,
  `MAILCODE` varchar(10) DEFAULT NULL,
  `ZIP_CODE` varchar(10) DEFAULT NULL,
  `AREA_CODE` varchar(10) DEFAULT NULL,
  `PHONE_NUMBER` varchar(20) DEFAULT NULL,
  `EMAIL` varchar(200) NOT NULL,
  `WEBSITE_URL` varchar(255) DEFAULT NULL,
  `COMMENT` varchar(255) DEFAULT NULL,

  `ACTIVE` bit(1) NOT NULL,
  PRIMARY KEY (`APP_ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ;


# Modify tasks table, we'll modify is_terminal fields again later for jobs that are still running.
alter table tasks add `IS_TERMINAL` bit(1) NOT NULL DEFAULT b'0'  AFTER TOOL_ID ;
update tasks set IS_TERMINAL = 1 ;


/*
	Some changes to job_stats
		- Delete records older than 2 years and anything that doesn't has null where we require data 
		- Drop columns we don't care about anymore
		- Rename some columns
		- Make JOBHANDLE be an index.  Can't make it unique yet because we have to check for and deal 
		with dups manually.
*/

delete from job_stats where job_start_time < '2011-11-01' OR t_jobhandle is NULL OR t_user_id is NULL OR t_tool_id is NULL OR
	t_email is NULL ;
alter table job_stats drop t_label , drop submit_host , drop submit_user , drop sdk_version ,
    drop process_worker , drop process_id , drop t_username ;
alter table job_stats change t_user_id		USER_ID bigint(20) not null ;
alter table job_stats change t_jobhandle	JOBHANDLE varchar(255) not null FIRST ;
alter table job_stats change resource		RESOURCE varchar(100) not null AFTER JOBHANDLE ;
alter table job_stats change t_tool_id		TOOL_ID varchar(100) not null ;
alter table job_stats change t_email		EMAIL varchar(200) not null ;
alter table job_stats change submit_url		SUBMITTER varchar(1023) not null default 'UNKNOWN' AFTER EMAIL ;
alter table job_stats modify REMOTE_JOB_ID	varchar(1023) default NULL AFTER SUBMITTER ;
alter table job_stats modify TG_CHARGE_NUMBER varchar(100) default NULL AFTER REMOTE_JOB_ID ;

alter table job_stats change job_start_time		DATE_ENTERED datetime not null AFTER TG_CHARGE_NUMBER ;
alter table job_stats add 						DATE_SUBMITTED datetime default null AFTER DATE_ENTERED ;
alter table job_stats change job_end_time		DATE_TERMINATED datetime default null AFTER DATE_SUBMITTED ;
update job_stats set DATE_SUBMITTED = DATE_ENTERED where DATE_SUBMITTED is null ;

/*
	Adding jobhandle index speeds up searching for dup jobhandles in this table.
	See convert2.sql
*/
alter table job_stats add INDEX  (JOBHANDLE)  ;
alter table running_tasks add INDEX  (JOBHANDLE) ;
alter table tasks add INDEX  (JOBHANDLE) ;

