-- MySQL dump 10.13  Distrib 5.1.73, for redhat-linux-gnu (x86_64)
--
-- Host: mysql2.sdsc.edu    Database: cipres
-- ------------------------------------------------------
-- Server version	5.7.13-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `application_preferences`
--

DROP TABLE IF EXISTS `application_preferences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application_preferences` (
  `NAME` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `VALUE` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFERENCE` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`NAME`,`PREFERENCE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `applications`
--

DROP TABLE IF EXISTS `applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `applications` (
  `NAME` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `APP_ID` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `LONGNAME` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `AUTH_TYPE` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `AUTH_USER_ID` bigint(20) NOT NULL,
  `WEBSITE_URL` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `COMMENT` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ACTIVE` bit(1) NOT NULL,
  `DATE_CREATED` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`NAME`),
  UNIQUE KEY `APP_ID` (`APP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cached_items`
--

DROP TABLE IF EXISTS `cached_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cached_items` (
  `CACHED_ITEM_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `SESSION_ID` varchar(255) NOT NULL,
  `ATTRIBUTE` varchar(255) NOT NULL,
  `COMMENT` varchar(255) DEFAULT NULL,
  `VERSION` int(11) NOT NULL DEFAULT '0',
  `VALUE` longblob,
  PRIMARY KEY (`CACHED_ITEM_ID`),
  UNIQUE KEY `SESSION_ID` (`SESSION_ID`,`ATTRIBUTE`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_records`
--

DROP TABLE IF EXISTS `data_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_records` (
  `RECORD_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `USERDATA_ID` bigint(20) NOT NULL,
  `RECORD_TYPE` varchar(100) NOT NULL,
  PRIMARY KEY (`RECORD_ID`),
  KEY `USERDATA_ID` (`USERDATA_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=281221 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `folder_preferences`
--

DROP TABLE IF EXISTS `folder_preferences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `folder_preferences` (
  `FOLDER_ID` bigint(20) NOT NULL,
  `VALUE` varchar(100) DEFAULT NULL,
  `PREFERENCE` varchar(100) NOT NULL,
  PRIMARY KEY (`FOLDER_ID`,`PREFERENCE`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `folders`
--

DROP TABLE IF EXISTS `folders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=177799 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=91929 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item_metadata`
--

DROP TABLE IF EXISTS `item_metadata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item_metadata` (
  `USERDATA_ID` bigint(20) NOT NULL,
  `VALUE` varchar(255) DEFAULT NULL,
  `FIELD` varchar(100) NOT NULL,
  PRIMARY KEY (`USERDATA_ID`,`FIELD`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `job_events`
--

DROP TABLE IF EXISTS `job_events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `job_events` (
  `SE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `JOBHANDLE` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `EVENT_DATE` datetime NOT NULL,
  `TASK_STAGE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `NAME` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `VALUE` longtext COLLATE utf8_unicode_ci,
  PRIMARY KEY (`SE_ID`),
  KEY `JOBHANDLE` (`JOBHANDLE`)
) ENGINE=InnoDB AUTO_INCREMENT=5759653 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `job_stats`
--

DROP TABLE IF EXISTS `job_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `job_stats` (
  `JOBHANDLE` varchar(255) NOT NULL,
  `RESOURCE` varchar(100) NOT NULL,
  `TASK_ID` bigint(20) NOT NULL,
  `USER_ID` bigint(20) NOT NULL,
  `TOOL_ID` varchar(100) NOT NULL,
  `EMAIL` varchar(200) NOT NULL,
  `SUBMITTER` varchar(1023) NOT NULL DEFAULT 'UNKNOWN',
  `REMOTE_JOB_ID` varchar(100) DEFAULT NULL,
  `TG_CHARGE_NUMBER` varchar(100) DEFAULT NULL,
  `DATE_ENTERED` datetime NOT NULL,
  `DATE_SUBMITTED` datetime DEFAULT NULL,
  `DATE_TERMINATED` datetime DEFAULT NULL,
  `SU_PREDICTED` bigint(20) DEFAULT NULL,
  `SU_COMPUTED` bigint(20) DEFAULT NULL,
  `SU_CHARGED` bigint(20) DEFAULT NULL,
  `SU_OVERRIDE` int(11) DEFAULT NULL,
  `APPNAME` varchar(30) NOT NULL DEFAULT '',
  PRIMARY KEY (`JOBHANDLE`),
  KEY `idx_user_chage_date` (`USER_ID`,`TG_CHARGE_NUMBER`,`DATE_ENTERED`),
  KEY `resource_remote_job_user_id` (`RESOURCE`,`REMOTE_JOB_ID`,`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `record_fields`
--

DROP TABLE IF EXISTS `record_fields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `record_fields` (
  `RECORD_ID` bigint(20) NOT NULL,
  `RECORD_FIELD` varchar(100) NOT NULL,
  `VALUE` varchar(1023) DEFAULT NULL,
  PRIMARY KEY (`RECORD_ID`,`RECORD_FIELD`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `running_tasks`
--

DROP TABLE IF EXISTS `running_tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `running_tasks` (
  `JOBHANDLE` varchar(255) NOT NULL,
  `RESOURCE` varchar(100) NOT NULL,
  `TOOL_ID` varchar(100) NOT NULL,
  `USER_ID` bigint(20) NOT NULL,
  `TASK_ID` bigint(20) NOT NULL,
  `SUBMITTER` varchar(1023) NOT NULL,
  `REMOTE_JOB_ID` varchar(100) DEFAULT NULL,
  `DATE_ENTERED` datetime NOT NULL,
  `LOCKED` datetime DEFAULT NULL,
  `HOSTNAME` varchar(200) DEFAULT NULL,
  `PID` bigint(20) DEFAULT NULL,
  `STATUS` varchar(100) NOT NULL,
  `ATTEMPTS` int(4) NOT NULL DEFAULT '0',
  `OUTPUT_DESC` varchar(5000) DEFAULT NULL,
  `COMMANDLINE` varchar(5000) DEFAULT NULL,
  `SPROPS` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`JOBHANDLE`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `running_tasks_parameters`
--

DROP TABLE IF EXISTS `running_tasks_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `running_tasks_parameters` (
  `JOBHANDLE` varchar(255) NOT NULL DEFAULT '',
  `NAME` varchar(100) NOT NULL,
  `VALUE` varchar(1023) DEFAULT NULL,
  PRIMARY KEY (`JOBHANDLE`,`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `source_documents`
--

DROP TABLE IF EXISTS `source_documents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `source_documents` (
  `SOURCE_DOCUMENT_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `FILENAME` varchar(1023) NOT NULL,
  `LENGTH` bigint(20) NOT NULL,
  `SIGNATURE` varchar(255) NOT NULL,
  PRIMARY KEY (`SOURCE_DOCUMENT_ID`),
  UNIQUE KEY `SIGNATURE` (`SIGNATURE`)
) ENGINE=InnoDB AUTO_INCREMENT=21472232 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sso`
--

DROP TABLE IF EXISTS `sso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sso` (
  `SSO_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `USER_ID` bigint(20) NOT NULL,
  `SSO_USERNAME` varchar(255) NOT NULL,
  PRIMARY KEY (`SSO_ID`),
  UNIQUE KEY `SSO_USERNAME` (`SSO_USERNAME`),
  KEY `USER_ID` (`USER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=571 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_input_parameters`
--

DROP TABLE IF EXISTS `task_input_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_input_parameters` (
  `INPUT_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `TASK_ID` bigint(20) NOT NULL,
  `PARAMETER` varchar(255) NOT NULL,
  PRIMARY KEY (`INPUT_ID`),
  KEY `TASK_ID` (`TASK_ID`),
  CONSTRAINT `task_input_parameters_ibfk_1` FOREIGN KEY (`TASK_ID`) REFERENCES `tasks` (`TASK_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1620631 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_input_source_documents`
--

DROP TABLE IF EXISTS `task_input_source_documents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  KEY `SOURCE_DOCUMENT_ID` (`SOURCE_DOCUMENT_ID`),
  CONSTRAINT `task_input_source_documents_ibfk_1` FOREIGN KEY (`INPUT_ID`) REFERENCES `task_input_parameters` (`INPUT_ID`),
  CONSTRAINT `task_input_source_documents_ibfk_2` FOREIGN KEY (`SOURCE_DOCUMENT_ID`) REFERENCES `source_documents` (`SOURCE_DOCUMENT_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1816548 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_log_messages`
--

DROP TABLE IF EXISTS `task_log_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_log_messages` (
  `TASK_ID` bigint(20) NOT NULL,
  `MESSAGE_INDEX` int(11) NOT NULL,
  `CREATION_DATE` datetime NOT NULL,
  `STAGE` varchar(20) NOT NULL,
  `ERROR` bit(1) NOT NULL DEFAULT b'0',
  `MESSAGE` longtext,
  PRIMARY KEY (`TASK_ID`,`MESSAGE_INDEX`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_output_parameters`
--

DROP TABLE IF EXISTS `task_output_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_output_parameters` (
  `OUTPUT_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `TASK_ID` bigint(20) NOT NULL,
  `PARAMETER` varchar(255) NOT NULL,
  `INTERMEDIATE` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`OUTPUT_ID`),
  KEY `TASK_ID` (`TASK_ID`),
  CONSTRAINT `task_output_parameters_ibfk_1` FOREIGN KEY (`TASK_ID`) REFERENCES `tasks` (`TASK_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2367050 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_output_source_documents`
--

DROP TABLE IF EXISTS `task_output_source_documents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  KEY `SOURCE_DOCUMENT_ID` (`SOURCE_DOCUMENT_ID`),
  CONSTRAINT `task_output_source_documents_ibfk_1` FOREIGN KEY (`OUTPUT_ID`) REFERENCES `task_output_parameters` (`OUTPUT_ID`),
  CONSTRAINT `task_output_source_documents_ibfk_2` FOREIGN KEY (`SOURCE_DOCUMENT_ID`) REFERENCES `source_documents` (`SOURCE_DOCUMENT_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=26051357 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_properties`
--

DROP TABLE IF EXISTS `task_properties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_properties` (
  `TASK_ID` bigint(20) NOT NULL,
  `VALUE` varchar(4095) DEFAULT NULL,
  `PROPERTY` varchar(100) NOT NULL,
  PRIMARY KEY (`TASK_ID`,`PROPERTY`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tasks`
--

DROP TABLE IF EXISTS `tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tasks` (
  `TASK_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `GROUP_ID` bigint(20) NOT NULL,
  `USER_ID` bigint(20) NOT NULL,
  `ENCLOSING_FOLDER_ID` bigint(20) NOT NULL,
  `COMMENT` varchar(255) DEFAULT NULL,
  `CREATION_DATE` datetime NOT NULL,
  `LABEL` varchar(255) NOT NULL,
  `JOBHANDLE` varchar(255) DEFAULT NULL,
  `OK` bit(1) NOT NULL DEFAULT b'1',
  `STAGE` varchar(20) NOT NULL,
  `TOOL_ID` varchar(100) DEFAULT NULL,
  `IS_TERMINAL` bit(1) NOT NULL DEFAULT b'0',
  `APPNAME` varchar(30) NOT NULL DEFAULT '',
  `VERSION` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`TASK_ID`),
  UNIQUE KEY `JOBHANDLE` (`JOBHANDLE`),
  KEY `GROUP_ID` (`GROUP_ID`),
  KEY `USER_ID` (`USER_ID`),
  KEY `ENCLOSING_FOLDER_ID` (`ENCLOSING_FOLDER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=945221 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tgusage`
--

DROP TABLE IF EXISTS `tgusage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  UNIQUE KEY `RESOURCE` (`RESOURCE`,`JOBID`,`SUBMIT_TIME`),
  KEY `charge_number_submit_time` (`CHARGE_NUMBER`,`SUBMIT_TIME`)
) ENGINE=InnoDB AUTO_INCREMENT=10755786 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tool_parameters`
--

DROP TABLE IF EXISTS `tool_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tool_parameters` (
  `TASK_ID` bigint(20) NOT NULL,
  `value` varchar(1024) DEFAULT NULL,
  `PARAMETER` varchar(100) NOT NULL,
  PRIMARY KEY (`TASK_ID`,`PARAMETER`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_group_lookup`
--

DROP TABLE IF EXISTS `user_group_lookup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group_lookup` (
  `USER_ID` bigint(20) NOT NULL,
  `GROUP_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`USER_ID`,`GROUP_ID`),
  KEY `GROUP_ID` (`GROUP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_preferences`
--

DROP TABLE IF EXISTS `user_preferences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_preferences` (
  `USER_ID` bigint(20) NOT NULL,
  `VALUE` varchar(100) DEFAULT NULL,
  `PREFERENCE` varchar(100) NOT NULL,
  PRIMARY KEY (`USER_ID`,`PREFERENCE`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userdata`
--

DROP TABLE IF EXISTS `userdata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  KEY `SOURCE_DOCUMENT_ID` (`SOURCE_DOCUMENT_ID`),
  CONSTRAINT `userdata_ibfk_1` FOREIGN KEY (`SOURCE_DOCUMENT_ID`) REFERENCES `source_documents` (`SOURCE_DOCUMENT_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1229816 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `WEBSITE_URL` varchar(255) DEFAULT NULL,
  `ZIP_CODE` varchar(10) DEFAULT NULL,
  `ACTIVE` bit(1) NOT NULL DEFAULT b'1',
  `VERSION` int(11) NOT NULL DEFAULT '0',
  `CAN_SUBMIT` bit(1) NOT NULL DEFAULT b'1',
  `LAST_LOGIN` datetime DEFAULT NULL,
  `UMBRELLA_APPNAME` varchar(30) NOT NULL DEFAULT '',
  `ACTIVATION_CODE` varchar(50) DEFAULT NULL,
  `ACTIVATION_SENT` datetime DEFAULT NULL,
  `DATE_CREATED` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`USER_ID`),
  UNIQUE KEY `USERNAME` (`USERNAME`),
  UNIQUE KEY `EMAIL` (`EMAIL`,`ROLE`,`UMBRELLA_APPNAME`),
  KEY `DEFAULT_GROUP_ID` (`DEFAULT_GROUP_ID`),
  KEY `ACTIVATION` (`ACTIVATION_SENT`)
) ENGINE=InnoDB AUTO_INCREMENT=91983 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-07-06  9:52:25
