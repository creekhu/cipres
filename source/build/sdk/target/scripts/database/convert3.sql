/*
	PREREQUISITE: no dup jobhandles in running_tasks, job_stats and tasks tables.  
	See convert2.sql and resolve manually if needed.
*/

# Drop jobhandle index we added a moment ago.  We'll re add it as a PRIMARY key in a moment.
alter table job_stats drop  INDEX JOBHANDLE ;

# Have to modify autoinc column to be non-autoinc, before dropping it as an index
alter table job_stats modify statistic_id bigint(20) ;
alter table job_stats drop  PRIMARY KEY, add PRIMARY KEY JOBHANDLE (JOBHANDLE) ;
alter table job_stats drop STATISTIC_ID ;

/*
	Delete from running_tasks and running_tasks_parameters, all jobs that are finished
	or have corrupt data.
*/
delete from running_tasks WHERE
    datediff(now(), date_submitted) > 30 OR
    status in ('TASK_DELETED',  'GOT_RESULTS',  'STOP_LOAD_RESULTS') or status is NULL;
delete from running_tasks_parameters where running_task_id not in (select running_task_id from running_tasks) ;


/*
	Fix remaining running_task_parameters: 
	*) iplant=value -> chargeNumber=value
	*) populate jobhandle field
	*) key uses jobhandle now instead of running_task_id 
*/
update running_tasks_parameters set name = 'chargeNumber' where name = 'iplant' ;

alter table running_tasks_parameters add JOBHANDLE varchar(255) NOT NULL DEFAULT '' FIRST ;
update running_tasks_parameters, running_tasks 
	set running_tasks_parameters.jobhandle = running_tasks.jobhandle
	where running_tasks_parameters.running_task_id = running_tasks.running_task_id ;
	 
alter table running_tasks_parameters drop  PRIMARY KEY, add PRIMARY KEY ( JOBHANDLE , NAME ) ;
alter table running_tasks_parameters drop RUNNING_TASK_ID ;




/*
	*) Set tasks.is_terminal = false, if running_task record is still present.  These jobs may be running.
	*) We added jobhandle index a moment ago, now make it a UNIQUE index.
*/
update tasks join running_tasks set tasks.is_terminal = (0) where tasks.jobhandle = running_tasks.jobhandle ;
alter table tasks drop INDEX  JOBHANDLE ;
alter table tasks add UNIQUE INDEX ( JOBHANDLE ) ;


alter table running_tasks drop INDEX JOBHANDLE ;

/*
	Need to add running_tasks columns tool_id and user_id and get values from the corresponding task.
	If the corresponding task is gone, just delete the running task record.  
*/
alter table running_tasks add TOOL_ID varchar(100) NOT NULL AFTER RESOURCE ;
alter table running_tasks add USER_ID bigint(20) NOT NULL AFTER TOOL_ID ;

update running_tasks join tasks set running_tasks.tool_id = tasks.tool_id , running_tasks.user_id = tasks.user_id
	where running_tasks.task_id = tasks.task_id ;

delete from running_tasks where tool_id is NULL or task_id is NULL ;



alter table running_tasks modify running_task_id bigint(20) ;
alter table running_tasks change JOBHANDLE JOBHANDLE varchar(255) NOT NULL FIRST ; 
alter table running_tasks drop  PRIMARY KEY, add PRIMARY KEY JOBHANDLE (JOBHANDLE) ;
alter table running_tasks drop running_task_id ;
alter table running_tasks drop INDEX TASK_ID ;
alter table running_tasks change DATE_SUBMITTED		DATE_ENTERED datetime NOT NULL ;
alter table running_tasks drop sub_status , drop workspace , drop process_worker , drop file_handler , drop callback_url ;
alter table running_tasks add HOSTNAME varchar(200) DEFAULT NULL AFTER LOCKED ;
alter table running_tasks add `PID` bigint(20) DEFAULT NULL AFTER HOSTNAME ;
alter table running_tasks add `ATTEMPTS` int(4) NOT NULL DEFAULT 0 AFTER STATUS ;
alter table running_tasks change STATUS STATUS varchar(100) NOT NULL ;
alter table running_tasks change OUTPUT_DESC OUTPUT_DESC varchar(5000) DEFAULT NULL ;
alter table running_tasks change COMMANDLINE COMMANDLINE varchar(5000) DEFAULT NULL ;
update running_tasks set LOCKED = NULL ; 

# Fix status values and set ATTEMPTS
update running_tasks set status = 'TERMINATED' , attempts = 1 where status = 'ERROR_GETTING_RESULTS' ;
update running_tasks set status = 'TERMINATED' , attempts = 0 where status = 'DONE' ;


# Add job_events records, 1 MIGRATE event for all job_stats and FINISHED or FAILED for those that are no longer running. 
# FINISHED or FAILED event is where we capture the t_stage and t_ok info from old style job_stats records.
insert into job_events (jobhandle, event_date,  task_stage, name )  
	select jobhandle, now(), t_stage , "MIGRATE" from job_stats ;

insert into job_events (jobhandle, event_date, name, value ) 
	select jobhandle, coalesce(date_terminated, '1000-01-01'), 'FINISHED' , "MIGRATED"
	from job_stats where 
	job_stats.jobhandle not in (select jobhandle from running_tasks) AND job_stats.t_stage = "COMPLETED" ;

insert into job_events (jobhandle, event_date, task_stage, name, value ) 
	select jobhandle, coalesce(date_terminated, '1000-01-01'), t_stage, 'FAILED' , "MIGRATED"
	from job_stats where 
	job_stats.jobhandle not in (select jobhandle from running_tasks) AND job_stats.t_stage != "COMPLETED" ;

# Now we can get rid of job_stats t_stage and t_ok columns.
alter table job_stats drop t_stage , drop t_ok ;


