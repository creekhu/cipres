/*
  STATISTICS TABLE
*/
DROP TABLE job_stats;
CREATE TABLE job_stats (
	STATISTIC_ID	bigint(20)				AUTO_INCREMENT NOT NULL,

	TASK_ID			bigint(20)				NOT NULL,

	T_USER_ID		bigint(20) 				NULL,
	T_LABEL			varchar(1023) 			NULL,
	T_JOBHANDLE		varchar(255) 			NULL,
	T_OK			bit(1)					NULL, 
	T_STAGE			varchar(20)				NULL,
	T_TOOL_ID		varchar(100)			NULL,
	T_EMAIL			varchar(100)			NULL,
	T_USERNAME		varchar(50)				NULL,


	JOB_START_TIME	datetime				NOT NULL DEFAULT '0000-00-00 00:00:00',
	JOB_END_TIME	datetime				NULL,

	SUBMIT_URL		varchar(1023)			NULL,
	SUBMIT_HOST		varchar(255)			NULL,
	SUBMIT_USER		varchar(50)				NULL,
	SDK_VERSION		varchar(100)			NULL,
	PROCESS_WORKER	varchar(100) 			NULL,
	RESOURCE		varchar(100) 			NULL,
	REMOTE_JOB_ID	varchar(255) 			NULL,
	PROCESS_ID		bigint(20)				NULL, 

	PRIMARY KEY (STATISTIC_ID)
 );



ALTER TABLE job_stats add (
	TG_CHARGE_NUMBER	varchar(100)			
)



