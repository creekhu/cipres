DROP TABLE IF EXISTS `cached_items`;
CREATE TABLE `cached_items` (
  `CACHED_ITEM_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `SESSION_ID` varchar(255) NOT NULL,
  `ATTRIBUTE` varchar(255) NOT NULL,
  `COMMENT` varchar(255) DEFAULT NULL,
  `VERSION` int(11) NOT NULL DEFAULT '0',
  `VALUE` longblob,
  PRIMARY KEY (`CACHED_ITEM_ID`),
  UNIQUE KEY `SESSION_ID` (`SESSION_ID`,`ATTRIBUTE`)
); 


DROP TABLE IF EXISTS `data_records`;
CREATE TABLE `data_records` (
  `RECORD_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `USERDATA_ID` bigint(20) NOT NULL,
  `RECORD_TYPE` varchar(100) NOT NULL,
  PRIMARY KEY (`RECORD_ID`),
  KEY `USERDATA_ID` (`USERDATA_ID`)
); 

DROP TABLE IF EXISTS `example_table`;


DROP TABLE IF EXISTS `folder_preferences`;
CREATE TABLE `folder_preferences` (
  `FOLDER_ID` bigint(20) NOT NULL,
  `VALUE` varchar(100) DEFAULT NULL,
  `PREFERENCE` varchar(100) NOT NULL,
  PRIMARY KEY (`FOLDER_ID`,`PREFERENCE`)
) ;


DROP TABLE IF EXISTS `folders`;
CREATE TABLE `folders` (
  `FOLDER_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `GROUP_ID` bigint(20) NOT NULL,
  `USER_ID` bigint(20) NOT NULL,
  `ENCLOSING_FOLDER_ID` bigint(20) DEFAULT NULL,
  `COMMENT` varchar(255) DEFAULT NULL,
  `CREATION_DATE` datetime NOT NULL,
  `LABEL` varchar(255) NOT NULL,
  `GROUP_READABLE` bit(1) NOT NULL DEFAULT b'0',
  `WORLD_READABLE` bit(1) NOT NULL DEFAULT b'0',
  `VERSION` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`FOLDER_ID`),
  KEY `GROUP_ID` (`GROUP_ID`),
  KEY `USER_ID` (`USER_ID`),
  KEY `ENCLOSING_FOLDER_ID` (`ENCLOSING_FOLDER_ID`)
) ;


DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups` (
  `GROUP_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ADMINISTRATOR` bigint(20) DEFAULT NULL,
  `COMMENT` varchar(255) DEFAULT NULL,
  `CREATION_DATE` datetime NOT NULL,
  `DESCRIPTION` varchar(1023) DEFAULT NULL,
  `GROUPNAME` varchar(255) NOT NULL,
  `VERSION` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`GROUP_ID`),
  UNIQUE KEY `GROUPNAME` (`GROUPNAME`),
  KEY `ADMINISTRATOR` (`ADMINISTRATOR`)
) ;


DROP TABLE IF EXISTS `item_metadata`;
CREATE TABLE `item_metadata` (
  `USERDATA_ID` bigint(20) NOT NULL,
  `VALUE` varchar(255) DEFAULT NULL,
  `FIELD` varchar(100) NOT NULL,
  PRIMARY KEY (`USERDATA_ID`,`FIELD`)
) ;


DROP TABLE IF EXISTS `job_stats`;
CREATE TABLE `job_stats` (
  `JOBHANDLE` varchar(255) NOT NULL,
  `RESOURCE` varchar(100) NOT NULL,
  `TOOL_ID` varchar(100) NOT NULL,
  `TASK_ID` bigint(20) NOT NULL,
  `USER_ID` bigint(20) NOT NULL,
  `EMAIL` varchar(100) NOT NULL,
  `SUBMITTER` varchar(1023) NOT NULL,
  `TG_CHARGE_NUMBER` varchar(100) DEFAULT NULL,
  `DATE_ENTERED` datetime NOT NULL,
  `REMOTE_JOB_ID` varchar(1023) DEFAULT NULL,
  `DATE_SUBMITTED` datetime DEFAULT NULL,
  `DATE_TERMINATED` datetime DEFAULT NULL,
  PRIMARY KEY (`JOBHANDLE`)
) ;

drop table if exists `job_events`;
create table `job_events` (
  `SE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `JOBHANDLE` varchar(255) NOT NULL,
  `EVENT_DATE` datetime not null,
  `TASK_STAGE` varchar(100) default null,
  `NAME` varchar(100) not null,
  `VALUE` varchar(1023) default null,
  PRIMARY KEY  (`SE_ID`),
  KEY  (`JOBHANDLE`)
);


DROP TABLE IF EXISTS `record_fields`;
CREATE TABLE `record_fields` (
  `RECORD_ID` bigint(20) NOT NULL,
  `RECORD_FIELD` varchar(100) NOT NULL,
  `VALUE` varchar(1023) DEFAULT NULL,
  PRIMARY KEY (`RECORD_ID`,`RECORD_FIELD`)
) ;


DROP TABLE IF EXISTS `running_tasks`;
CREATE TABLE `running_tasks` (
  `JOBHANDLE` varchar(255) NOT NULL,
  `RESOURCE` varchar(100) NOT NULL,
  `TOOL_ID` varchar(100) NOT NULL,
  `USER_ID` bigint(20) NOT NULL,
  `TASK_ID` bigint(20) NOT NULL,
  `SUBMITTER` varchar(1023) NOT NULL,
  `REMOTE_JOB_ID` varchar(1023) DEFAULT NULL,
  `DATE_ENTERED` datetime NOT NULL,
  `LOCKED` datetime DEFAULT NULL,
  `HOSTNAME` varchar(200),
  `PID` bigint(20),
  `STATUS` varchar(100) NOT NULL,
  `ATTEMPTS` int(4) NOT NULL,
  `OUTPUT_DESC` varchar(5000) DEFAULT NULL,
  `COMMANDLINE` varchar(5000) DEFAULT NULL,
  `SPROPS` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`JOBHANDLE`)
) ;


DROP TABLE IF EXISTS `running_tasks_parameters`;
CREATE TABLE `running_tasks_parameters` (
  `JOBHANDLE` varchar(255) NOT NULL,
  `NAME` varchar(100) NOT NULL,
  `VALUE` varchar(1023) DEFAULT NULL,
  PRIMARY KEY (`JOBHANDLE`,`NAME`)
) ;


DROP TABLE IF EXISTS `sequence`;


DROP TABLE IF EXISTS `source_documents`;
CREATE TABLE `source_documents` (
  `SOURCE_DOCUMENT_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `FILENAME` varchar(1023) NOT NULL,
  `LENGTH` bigint(20) NOT NULL,
  PRIMARY KEY (`SOURCE_DOCUMENT_ID`)
) ;


DROP TABLE IF EXISTS `sso`;
CREATE TABLE `sso` (
  `SSO_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `USER_ID` bigint(20) NOT NULL,
  `SSO_USERNAME` varchar(255) NOT NULL,
  PRIMARY KEY (`SSO_ID`),
  UNIQUE KEY `SSO_USERNAME` (`SSO_USERNAME`),
  KEY `USER_ID` (`USER_ID`)
) ;


DROP TABLE IF EXISTS `task_input_parameters`;
CREATE TABLE `task_input_parameters` (
  `INPUT_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `TASK_ID` bigint(20) NOT NULL,
  `PARAMETER` varchar(255) NOT NULL,
  PRIMARY KEY (`INPUT_ID`),
  KEY `TASK_ID` (`TASK_ID`)
) ;


DROP TABLE IF EXISTS `task_input_source_documents`;
CREATE TABLE `task_input_source_documents` (
  `INPUT_DOCUMENT_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `INPUT_ID` bigint(20) NOT NULL,
  `SOURCE_DOCUMENT_ID` bigint(20) NOT NULL,
  `DATAFORMAT` varchar(255) NOT NULL,
  `DATATYPE` varchar(255) NOT NULL,
  `ENTITYTYPE` varchar(255) NOT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `VALIDATED` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`INPUT_DOCUMENT_ID`),
  KEY `INPUT_ID` (`INPUT_ID`),
  KEY `SOURCE_DOCUMENT_ID` (`SOURCE_DOCUMENT_ID`)
) ;


DROP TABLE IF EXISTS `task_log_messages`;
CREATE TABLE `task_log_messages` (
  `TASK_ID` bigint(20) NOT NULL,
  `MESSAGE_INDEX` int(11) NOT NULL,
  `CREATION_DATE` datetime NOT NULL,
  `STAGE` varchar(20) NOT NULL,
  `ERROR` bit(1) NOT NULL DEFAULT b'0',
  `MESSAGE` longtext,
  PRIMARY KEY (`TASK_ID`,`MESSAGE_INDEX`)
) ;


DROP TABLE IF EXISTS `task_output_parameters`;
CREATE TABLE `task_output_parameters` (
  `OUTPUT_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `TASK_ID` bigint(20) NOT NULL,
  `PARAMETER` varchar(255) NOT NULL,
  `INTERMEDIATE` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`OUTPUT_ID`),
  KEY `TASK_ID` (`TASK_ID`)
) ;


DROP TABLE IF EXISTS `task_output_source_documents`;
CREATE TABLE `task_output_source_documents` (
  `OUTPUT_DOCUMENT_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `OUTPUT_ID` bigint(20) NOT NULL,
  `SOURCE_DOCUMENT_ID` bigint(20) NOT NULL,
  `DATAFORMAT` varchar(255) NOT NULL,
  `DATATYPE` varchar(255) NOT NULL,
  `ENTITYTYPE` varchar(255) NOT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `VALIDATED` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`OUTPUT_DOCUMENT_ID`),
  KEY `OUTPUT_ID` (`OUTPUT_ID`),
  KEY `SOURCE_DOCUMENT_ID` (`SOURCE_DOCUMENT_ID`)
) ;


DROP TABLE IF EXISTS `task_properties`;
CREATE TABLE `task_properties` (
  `TASK_ID` bigint(20) NOT NULL,
  `VALUE` varchar(4095) DEFAULT NULL,
  `PROPERTY` varchar(100) NOT NULL,
  PRIMARY KEY (`TASK_ID`,`PROPERTY`)
) ;


DROP TABLE IF EXISTS `tasks`;
CREATE TABLE `tasks` (
  `TASK_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `GROUP_ID` bigint(20) NOT NULL,
  `USER_ID` bigint(20) NOT NULL,
  `ENCLOSING_FOLDER_ID` bigint(20) NOT NULL,
  `COMMENT` varchar(255) DEFAULT NULL,
  `CREATION_DATE` datetime NOT NULL,
  `LABEL` varchar(255) NOT NULL,
  `JOBHANDLE` varchar(255) DEFAULT NULL,
  `OK` bit(1) NOT NULL DEFAULT 1,
  `STAGE` varchar(20) NOT NULL,
  `TOOL_ID` varchar(100) DEFAULT NULL,
  `IS_TERMINAL` bit(1) NOT NULL DEFAULT 0,
  `VERSION` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`TASK_ID`),
  KEY `GROUP_ID` (`GROUP_ID`),
  KEY `USER_ID` (`USER_ID`),
  KEY `ENCLOSING_FOLDER_ID` (`ENCLOSING_FOLDER_ID`)
) ;


DROP TABLE IF EXISTS `test_table`;


DROP TABLE IF EXISTS `tgusage`;
CREATE TABLE `tgusage` (
  `TGUSAGE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `RESOURCE` varchar(100) NOT NULL,
  `JOBID` varchar(100) NOT NULL,
  `FULL_JOBID` varchar(255) NOT NULL,
  `FULL_RESOURCE` varchar(255) NOT NULL,
  `CHARGE` int(11) NOT NULL,
  `START_TIME` datetime NOT NULL,
  `END_TIME` datetime NOT NULL,
  `SUBMIT_TIME` datetime NOT NULL,
  `CHARGE_NUMBER` varchar(100) NOT NULL,
  `WALL_HRS` varchar(10) NOT NULL,
  `SU` int(11) NOT NULL,
  `NODECOUNT` int(11) NOT NULL,
  `PROCESSORS` int(11) NOT NULL,
  `QUEUE` varchar(100) NOT NULL,
  PRIMARY KEY (`TGUSAGE_ID`),
  UNIQUE KEY `RESOURCE` (`RESOURCE`,`JOBID`,`SUBMIT_TIME`)
) ;


DROP TABLE IF EXISTS `tool_parameters`;
CREATE TABLE `tool_parameters` (
  `TASK_ID` bigint(20) NOT NULL,
  `value` varchar(1024) DEFAULT NULL,
  `PARAMETER` varchar(100) NOT NULL,
  PRIMARY KEY (`TASK_ID`,`PARAMETER`)
) ;


DROP TABLE IF EXISTS `user_group_lookup`;
CREATE TABLE `user_group_lookup` (
  `USER_ID` bigint(20) NOT NULL,
  `GROUP_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`USER_ID`,`GROUP_ID`),
  KEY `GROUP_ID` (`GROUP_ID`)
) ;


DROP TABLE IF EXISTS `user_preferences`;
CREATE TABLE `user_preferences` (
  `USER_ID` bigint(20) NOT NULL,
  `VALUE` varchar(100) DEFAULT NULL,
  `PREFERENCE` varchar(100) NOT NULL,
  PRIMARY KEY (`USER_ID`,`PREFERENCE`)
) ;


DROP TABLE IF EXISTS `userdata`;
CREATE TABLE `userdata` (
  `USERDATA_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `GROUP_ID` bigint(20) NOT NULL,
  `USER_ID` bigint(20) NOT NULL,
  `ENCLOSING_FOLDER_ID` bigint(20) NOT NULL,
  `SOURCE_DOCUMENT_ID` bigint(20) NOT NULL,
  `COMMENT` varchar(255) DEFAULT NULL,
  `CREATION_DATE` datetime NOT NULL,
  `LABEL` varchar(1023) NOT NULL,
  `DATAFORMAT` varchar(255) NOT NULL,
  `DATATYPE` varchar(255) NOT NULL,
  `ENTITYTYPE` varchar(255) NOT NULL,
  `VALIDATED` bit(1) NOT NULL DEFAULT b'0',
  `VERSION` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`USERDATA_ID`),
  KEY `GROUP_ID` (`GROUP_ID`),
  KEY `USER_ID` (`USER_ID`),
  KEY `ENCLOSING_FOLDER_ID` (`ENCLOSING_FOLDER_ID`),
  KEY `SOURCE_DOCUMENT_ID` (`SOURCE_DOCUMENT_ID`)
) ;


DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `USER_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DEFAULT_GROUP_ID` bigint(20) DEFAULT NULL,
  `COMMENT` varchar(255) DEFAULT NULL,
  `INSTITUTION` varchar(255) DEFAULT NULL,
  `AREA_CODE` varchar(10) DEFAULT NULL,
  `CITY` varchar(100) DEFAULT NULL,
  `COUNTRY` varchar(50) DEFAULT NULL,
  `EMAIL` varchar(200) NOT NULL,
  `FIRST_NAME` varchar(100) NOT NULL,
  `LAST_NAME` varchar(100) NOT NULL,
  `MAILCODE` varchar(10) DEFAULT NULL,
  `PASSWORD` varchar(50) NOT NULL,
  `PHONE_NUMBER` varchar(20) DEFAULT NULL,
  `ROLE` varchar(50) NOT NULL,
  `STATE` varchar(50) DEFAULT NULL,
  `STREET_ADDRESS` varchar(255) DEFAULT NULL,

  `USERNAME` varchar(200) NOT NULL,
  `APPNAME` varchar(30) NOT NULL DEFAULT '',

  `WEBSITE_URL` varchar(255) DEFAULT NULL,
  `ZIP_CODE` varchar(10) DEFAULT NULL,
  `ACTIVE` bit(1) NOT NULL,
  `VERSION` int(11) NOT NULL DEFAULT '0',
  `CAN_SUBMIT` bit(1) NOT NULL DEFAULT b'1',
  `LAST_LOGIN` datetime DEFAULT NULL,
  PRIMARY KEY (`USER_ID`),
  UNIQUE KEY `USERNAME` (`USERNAME`),
  UNIQUE KEY `EMAIL` (`EMAIL`, `APPNAME`),
  KEY `DEFAULT_GROUP_ID` (`DEFAULT_GROUP_ID`)
) ;

DROP TABLE IF EXISTS `applications`;
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


/*
# Add a test user like this.  Password in these test users is md5 hash of "test" as computed by
# cipres.  Cipres does something weird so you can't figure out the the hash with: echo test | openssl md5.
# You need to let the java code do it, then find the value in the db and paste it in here.
# Password for admin user is a secret.
*/


# Create an admin account.  Need one for job callbacks.   Userid will be 1. 
insert into users (default_group_id, email, first_name, last_name, password, role, username, active) values 
	(1, 'ciprestester3@gmail.com', 'cipresadmin', 'cipresadmin', '01394aa2cb426e28273ef54a9e814817', 'ADMIN', 'cipresadmin', 1);




# Creates an application that uses an umbrella account and creates the umbrella user record (userid = 2)
insert into applications (name, longname, auth_type, auth_user_id, email, first_name, last_name, active) values 
	('guitest', 'Cipres Struts Test Client', 'UMBRELLA', 2, 'ciprestester3@gmail.com', 'guiapp', 'adminstrator', 1);

insert into users (default_group_id, email, first_name, last_name, password, role, username, appname, active) values 
	(1, 'ciprestester2@gmail.com', 'test2', 'test2', '098f6bcd4621d373cade4e832627b4f6', 'REST_UMBRELLA', 'guitest_admin', 
	'guitest', 1);

/*
insert into users (default_group_id, email, first_name, last_name, password, role, username, appname, active) values 
	(1, 'ciprestester1@gmail.com', 'test1', 'test1', '', 'REST_END_USER', 'guitest.test1', 
	'guitest', 1);

insert into users (default_group_id, email, first_name, last_name, password, role, username, appname, active) values 
	(1, 'ciprestester2@gmail.com', 'test2', 'test2', '', 'REST_END_USER', 'guitest.test2', 
	'guitest', 1);
*/



# Creates an application that uses direct end user login and 2 users that can be used with it.
insert into applications (name, longname, auth_type, email, first_name, last_name, active) values 
	('scripts', 'Cipres Python Test Clients', 'DIRECT', 'ciprestester3@gmail.com', 'scripts', 'adminstrator', 1);

insert into users (default_group_id, email, first_name, last_name, password, role, username, appname, active) values 
	(1, 'ciprestester1@gmail.com', 'test1', 'test1', '098f6bcd4621d373cade4e832627b4f6', 'REST_END_USER', 
	'scripts.test1', 'scripts', 1);

insert into users (default_group_id, email, first_name, last_name, password, role, username, appname, active) values 
	(1, 'ciprestester2@gmail.com', 'test2', 'test2', '098f6bcd4621d373cade4e832627b4f6', 'REST_END_USER', 'scripts.test2', 
	'scripts', 1);



/*
	This query gets the last job_event record for each jobhandle in the table.
	See: http://stackoverflow.com/questions/1313120/retrieving-the-last-record-in-each-group

select m1.*
from job_events m1 left join job_events m2
on (m1.jobhandle = m2.jobhandle and m1.event_date < m2.event_date)
where m2.event_date is null

*/
