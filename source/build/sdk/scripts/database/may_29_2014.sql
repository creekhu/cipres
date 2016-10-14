
/* 
	This is what needs to be applied to resttest db and eventually
	to cipres production.  I need to manually fill in user.umbrella_appname
	for REST_END_USER_UMBRELLA records in dbs that have them, where noted, before
	creating the new email index.
*/
DROP TABLE IF EXISTS `application_preferences`;
CREATE TABLE `application_preferences` (
  `NAME` varchar(30) NOT NULL,
  `VALUE` varchar(100) DEFAULT NULL,
  `PREFERENCE` varchar(100) NOT NULL,
  PRIMARY KEY (`NAME`,`PREFERENCE`)
) ;

alter table users drop index EMAIL ;
alter table users  drop APPNAME ;
alter table users add UMBRELLA_APPNAME varchar(30) NOT NULL DEFAULT '' AFTER LAST_LOGIN;

/* STOP HERE and manually set umbrella_appname for existing REST_END_USER_UMBRELLA USERs */
/* Then do the next 4 table alters. */

alter table users add UNIQUE INDEX EMAIL (EMAIL, ROLE, UMBRELLA_APPNAME) ;

alter table job_stats add SU_OVERRIDE int(11) NULL AFTER SU_CHARGED ;
alter table job_stats add APPNAME varchar(30) NOT NULL DEFAULT '' AFTER SU_OVERRIDE;

alter table tasks add APPNAME varchar(30) NOT NULL DEFAULT '' AFTER IS_TERMINAL;


/*
	For dbs where I already applied may_2014 (i.e. ngbwalpha and cipres_copy), here's what's needed.
*/
/*
alter table users drop index EMAIL ;
alter table users add UMBRELLA_APPNAME varchar(30) NOT NULL DEFAULT '' AFTER LAST_LOGIN ;
alter table users add UNIQUE INDEX EMAIL (EMAIL, ROLE, UMBRELLA_APPNAME) ;
*/

