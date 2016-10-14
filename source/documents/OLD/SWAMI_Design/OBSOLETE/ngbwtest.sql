-- MySQL dump 10.11
--
-- Host: mysql.sdsc.edu    Database: ngbwtest
-- ------------------------------------------------------
-- Server version	5.5.25-log

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
-- Table structure for table `accounting`
--

DROP TABLE IF EXISTS `accounting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accounting` (
  `ACCOUNTING_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `RESOURCE` varchar(100) NOT NULL,
  `TASK_ID` bigint(20) NOT NULL,
  `REMOTE_JOB_ID_1` varchar(1023) DEFAULT NULL,
  `REMOTE_JOB_ID_2` varchar(1023) DEFAULT NULL,
  `SUBMIT_TIME` datetime NOT NULL,
  `START_TIME` datetime DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `END_TIME_EST` datetime DEFAULT NULL,
  `NODES_REQUESTED` int(11) DEFAULT NULL,
  `MINUTES_REQUESTED` int(11) DEFAULT NULL,
  `MAX_SU` int(11) DEFAULT NULL,
  `SU_CALC` int(11) DEFAULT NULL,
  `SU_CHARGED` int(11) DEFAULT NULL,
  `USER_IP` varchar(100) DEFAULT NULL,
  `USER_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ACCOUNTING_ID`),
  KEY `TASK_ID` (`TASK_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
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
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
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
) ENGINE=MyISAM AUTO_INCREMENT=11542 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `example_table`
--

DROP TABLE IF EXISTS `example_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `example_table` (
  `EXAMPLE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `TASK_ID` bigint(20) NOT NULL,
  `ACTUAL_DATE_TIME` datetime NOT NULL,
  PRIMARY KEY (`EXAMPLE_ID`),
  KEY `TASK_ID` (`TASK_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=38 DEFAULT CHARSET=latin1;
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
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
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
) ENGINE=MyISAM AUTO_INCREMENT=8692 DEFAULT CHARSET=latin1;
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
) ENGINE=MyISAM AUTO_INCREMENT=511 DEFAULT CHARSET=latin1;
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
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `job_stats`
--

DROP TABLE IF EXISTS `job_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `job_stats` (
  `STATISTIC_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `TASK_ID` bigint(20) NOT NULL,
  `T_USER_ID` bigint(20) DEFAULT NULL,
  `T_LABEL` varchar(1023) DEFAULT NULL,
  `T_JOBHANDLE` varchar(255) DEFAULT NULL,
  `T_OK` bit(1) DEFAULT NULL,
  `T_STAGE` varchar(20) DEFAULT NULL,
  `T_TOOL_ID` varchar(100) DEFAULT NULL,
  `T_EMAIL` varchar(100) DEFAULT NULL,
  `T_USERNAME` varchar(50) DEFAULT NULL,
  `JOB_START_TIME` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `JOB_END_TIME` datetime DEFAULT NULL,
  `SUBMIT_URL` varchar(1023) DEFAULT NULL,
  `SUBMIT_HOST` varchar(255) DEFAULT NULL,
  `SUBMIT_USER` varchar(50) DEFAULT NULL,
  `SDK_VERSION` varchar(100) DEFAULT NULL,
  `PROCESS_WORKER` varchar(100) DEFAULT NULL,
  `RESOURCE` varchar(100) DEFAULT NULL,
  `REMOTE_JOB_ID` varchar(255) DEFAULT NULL,
  `PROCESS_ID` bigint(20) DEFAULT NULL,
  `TG_CHARGE_NUMBER` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`STATISTIC_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=3737 DEFAULT CHARSET=latin1;
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
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `running_tasks`
--

DROP TABLE IF EXISTS `running_tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `running_tasks` (
  `RUNNING_TASK_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `RESOURCE` varchar(100) NOT NULL,
  `TASK_ID` bigint(20) NOT NULL,
  `SUBMITTER` varchar(1023) NOT NULL,
  `REMOTE_JOB_ID` varchar(1023) DEFAULT NULL,
  `DATE_SUBMITTED` datetime NOT NULL,
  `LOCKED` datetime DEFAULT NULL,
  `STATUS` varchar(100) NOT NULL,
  `SUB_STATUS` varchar(255) DEFAULT NULL,
  `RETRY_COUNT` int(4) NOT NULL,
  `JOBHANDLE` varchar(255) NOT NULL,
  `WORKSPACE` varchar(1023) NOT NULL,
  `OUTPUT_DESC` varchar(5000) NOT NULL,
  `PROCESS_WORKER` varchar(1023) NOT NULL,
  `FILE_HANDLER` varchar(1023) NOT NULL,
  `COMMANDLINE` varchar(5000) NOT NULL,
  `CALLBACK_URL` varchar(1023) NOT NULL,
  `SPROPS` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`RUNNING_TASK_ID`),
  KEY `TASK_ID` (`TASK_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=2249 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `running_tasks_timing`;
CREATE TABLE `running_tasks_timing` (
  `RUNNING_TASK_ID` bigint(20) NOT NULL,
  `STATUS` varchar(100) NOT NULL,
  `TIMESTAMP` datetime NOT NULL,
  PRIMARY KEY (`RUNNING_TASK_ID`,`STATUS`, 'TIMESTAMP`)
)

--
-- Table structure for table `running_tasks_parameters`
--

DROP TABLE IF EXISTS `running_tasks_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `running_tasks_parameters` (
  `RUNNING_TASK_ID` bigint(20) NOT NULL,
  `NAME` varchar(100) NOT NULL,
  `VALUE` varchar(1023) DEFAULT NULL,
  PRIMARY KEY (`RUNNING_TASK_ID`,`NAME`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
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
  PRIMARY KEY (`SOURCE_DOCUMENT_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=48382 DEFAULT CHARSET=latin1;
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
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `statistics`
--

DROP TABLE IF EXISTS `statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `statistics` (
  `STATISTIC_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `TASK_ID` bigint(20) NOT NULL,
  `JOB_START_TIME` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `JOB_END_TIME` datetime DEFAULT NULL,
  `PROCESS_WORKER` varchar(50) DEFAULT NULL,
  `REMOTE_JOB_ID` varchar(50) DEFAULT NULL,
  `PROCESS_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`STATISTIC_ID`),
  KEY `TASK_ID` (`TASK_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=249 DEFAULT CHARSET=latin1;
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
  KEY `TASK_ID` (`TASK_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=13168 DEFAULT CHARSET=latin1;
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
  KEY `SOURCE_DOCUMENT_ID` (`SOURCE_DOCUMENT_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=14785 DEFAULT CHARSET=latin1;
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
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
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
  KEY `TASK_ID` (`TASK_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=26604 DEFAULT CHARSET=latin1;
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
  KEY `SOURCE_DOCUMENT_ID` (`SOURCE_DOCUMENT_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=62629 DEFAULT CHARSET=latin1;
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
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
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
  `OK` bit(1) NOT NULL,
  `STAGE` varchar(20) NOT NULL,
  `TOOL_ID` varchar(100) DEFAULT NULL,
  `VERSION` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`TASK_ID`),
  KEY `GROUP_ID` (`GROUP_ID`),
  KEY `USER_ID` (`USER_ID`),
  KEY `ENCLOSING_FOLDER_ID` (`ENCLOSING_FOLDER_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=9070 DEFAULT CHARSET=latin1;
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
  UNIQUE KEY `RESOURCE` (`RESOURCE`,`JOBID`,`SUBMIT_TIME`)
) ENGINE=MyISAM AUTO_INCREMENT=108878 DEFAULT CHARSET=utf8;
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
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
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
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
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
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
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
  KEY `SOURCE_DOCUMENT_ID` (`SOURCE_DOCUMENT_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=13095 DEFAULT CHARSET=latin1;
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
  `EMAIL` varchar(100) NOT NULL,
  `FIRST_NAME` varchar(100) NOT NULL,
  `LAST_NAME` varchar(100) NOT NULL,
  `MAILCODE` varchar(10) DEFAULT NULL,
  `PASSWORD` varchar(50) NOT NULL,
  `PHONE_NUMBER` varchar(20) DEFAULT NULL,
  `ROLE` varchar(50) NOT NULL,
  `STATE` varchar(50) DEFAULT NULL,
  `STREET_ADDRESS` varchar(255) DEFAULT NULL,
  `USERNAME` varchar(50) NOT NULL,
  `WEBSITE_URL` varchar(255) DEFAULT NULL,
  `ZIP_CODE` varchar(10) DEFAULT NULL,
  `ACTIVE` bit(1) NOT NULL,
  `VERSION` int(11) NOT NULL DEFAULT '0',
  `CAN_SUBMIT` bit(1) NOT NULL DEFAULT b'1',
  `LAST_LOGIN` datetime DEFAULT NULL,
  PRIMARY KEY (`USER_ID`),
  UNIQUE KEY `USERNAME` (`USERNAME`),
  UNIQUE KEY `EMAIL` (`EMAIL`),
  KEY `DEFAULT_GROUP_ID` (`DEFAULT_GROUP_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=516 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-03-28 15:20:53
