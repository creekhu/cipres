/* 
	These are the schema changes I made between 5/1/2014 and 5/27/2014.
*/
DROP TABLE IF EXISTS `application_preferences`;
CREATE TABLE `application_preferences` (
  `NAME` varchar(30) NOT NULL,
  `VALUE` varchar(100) DEFAULT NULL,
  `PREFERENCE` varchar(100) NOT NULL,
  PRIMARY KEY (`NAME`,`PREFERENCE`)
) ;

alter table users drop  INDEX EMAIL, add UNIQUE INDEX EMAIL (EMAIL, ROLE) ;
alter table users  drop appname ;

alter table job_stats add SU_OVERRIDE int(11) NULL AFTER SU_CHARGED ;
alter table job_stats add APPNAME varchar(30) NOT NULL DEFAULT '' AFTER SU_OVERRIDE;

alter table tasks add APPNAME varchar(30) NOT NULL DEFAULT '' AFTER IS_TERMINAL;

