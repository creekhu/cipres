/*
 	TGUSAGE 
*/

ALTER TABLE job_stats add (
	TG_RESOURCE			varchar(255)			NOT NULL, 
	TG_FULLJOB_ID		varchar(255)			NOT NULL, 
	TG_CHARGE			int						NOT NULL,
	TG_START_TIME		datetime				NOT NULL,
	TG_END_TIME			datetime				NOT NULL,
	TG_SUBMIT_TIME		datetime				NOT NULL,
	TG_CHARGE_NUMBER	varchar(100)			NOT NULL,
	TG_WALL_HRS			float					NOT NULL,
	TG_SU				int						NOT NULL,
	TG_NODECOUNT		int						NOT NULL,
	TG_PROCESSORS		int						NULL,
	TG_QUEUE			varchar(100)			NOT NULL
)

ALTER TABLE job_stats 
	DROP TG_RESOURCE,
	DROP TG_FULLJOB_ID,
	DROP TG_CHARGE,
	DROP TG_START_TIME,
	DROP TG_END_TIME,
	DROP TG_SUBMIT_TIME,
	DROP TG_CHARGE_NUMBER,
	DROP TG_WALL_HRS,
	DROP TG_SU,
	DROP TG_NODECOUNT,
	DROP TG_PROCESSORS,
	DROP TG_QUEUE	


CREATE TABLE tgusage (
	TGUSAGE_ID		bigint(20)				AUTO_INCREMENT NOT NULL,
	RESOURCE		varchar(100)			NOT NULL, 
	JOBID			varchar(100)			NOT NULL,
	FULL_JOBID		varchar(255)			NOT NULL, 
	FULL_RESOURCE	varchar(255)			NOT NULL,
	CHARGE			int						NOT NULL,
	START_TIME		datetime				NOT NULL,
	END_TIME		datetime				NOT NULL,
	SUBMIT_TIME		datetime				NOT NULL,
	CHARGE_NUMBER	varchar(100)			NOT NULL,
	WALL_HRS		varchar(10)				NOT NULL,
	SU				int						NOT NULL,
	NODECOUNT		int						NOT NULL,
	PROCESSORS		int						NOT NULL,
	QUEUE			varchar(100)			NOT NULL,

	PRIMARY KEY	(TGUSAGE_ID),
	UNIQUE KEY (RESOURCE, JOBID, SUBMIT_TIME)
)
	
# Queries of interest

# Set default schema to run on ngbw or ngbwtest
select job_stats.statistic_id, tgusage.tgusage_id, tgusage.resource, tgusage.jobid from job_stats, tgusage where
job_stats.resource = tgusage.resource and job_stats.remote_job_id = tgusage.jobid

# Query to join our statistics records with tgusage data.  The join only has rows for which there is both
# a statistic record and a tgusage record.   The statistic and tgusage records must have matching
# resource, remote job id, and submission date must be w/i 2 days.  We check submission date in case
# a tg resource starts reusing job ids.
select 
	job_stats.statistic_id, 
	job_stats.t_tool_id,
	job_stats.t_jobhandle,
	job_stats.t_username,
	job_stats.t_email,
	tgusage.*
from job_stats, tgusage where
job_stats.resource = tgusage.resource and 
job_stats.remote_job_id = tgusage.jobid and
abs(datediff(job_stats.job_start_time, tgusage.submit_time)) < 2
order by (tgusage.submit_time)

# Same as above, using production tgusage table but test statistics records.
select 
	ngbwtest.job_stats.statistic_id, 
	ngbwtest.job_stats.t_tool_id,
	ngbwtest.job_stats.t_jobhandle,
	ngbwtest.job_stats.t_username,
	ngbwtest.job_stats.t_email,
	tgusage.*
from ngbwtest.job_stats, tgusage where
ngbwtest.job_stats.resource = tgusage.resource and 
ngbwtest.job_stats.remote_job_id = tgusage.jobid and
abs(datediff(ngbwtest.job_stats.job_start_time, tgusage.submit_time)) < 2
order by (tgusage.submit_time)


# Combine tgusage and running_task_table
select 
	running_tasks.running_task_id, 
	running_tasks.jobhandle,
	tgusage.*
from running_tasks, tgusage where
running_tasks.resource = tgusage.resource and 
running_tasks.remote_job_id = tgusage.jobid and
abs(datediff(running_tasks.date_submitted, tgusage.submit_time)) < 2
order by (tgusage.submit_time)

# Get number of SUs per username, per month
select 
	job_stats.t_username,
	job_stats.t_email,
	month(tgusage.end_time),
	year(tgusage.end_time),
	sum(tgusage.su)
from job_stats, tgusage where
job_stats.resource = tgusage.resource and 
job_stats.remote_job_id = tgusage.jobid and
abs(datediff(job_stats.job_start_time, tgusage.submit_time)) < 2
group by year(tgusage.end_time), month(tgusage.end_time), job_stats.t_username, job_stats.t_email

# Get number of SUs per user
select 
	job_stats.t_username,
	job_stats.t_email,
	sum(tgusage.su)
from job_stats, tgusage where
job_stats.resource = tgusage.resource and 
job_stats.remote_job_id = tgusage.jobid and
abs(datediff(job_stats.job_start_time, tgusage.submit_time)) < 2
group by job_stats.t_username, job_stats.t_email


# Find jobs that were deleted by the user during the specified period of time.
select 
	job_stats.t_username,
	jnob_stats.t_jobhandle
from job_stats where 
job_stats.job_start_time between '2011-03-15' and '2011-04-17' AND
not exists (select tasks.jobhandle from tasks where tasks.jobhandle = job_stats.t_jobhandle)

select 
	job_stats.t_username,
	sum(tgusage.su)
from job_stats, tgusage where
job_stats.resource = tgusage.resource and 
job_stats.remote_job_id = tgusage.jobid and
abs(datediff(job_stats.job_start_time, tgusage.submit_time)) < 2 and
month(tgusage.end_time) = month(curdate()) and
job_stats.t_username = "terri"

# Get userinfo including institution for all users who've run a teragrid job.
select distinct users.user_id, users.email, users.institution from
users, job_stats
where users.user_id = job_stats.t_user_id AND
(job_stats.PROCESS_WORKER = 'GlobusProcessWorker' OR job_stats.PROCESS_WORKER = 'SSHExecProcessWorker')

# Find all users that have used more than 5 SUs 
select job_stats.t_user_id, sum(tgusage.su) 
from job_stats, tgusage where
job_stats.resource = tgusage.resource and
job_stats.remote_job_id = tgusage.jobid and
abs(datediff(job_stats.job_start_time, tgusage.submit_time)) < 2
group by job_stats.t_user_id
having sum(tgusage.su) > 5

# All users who are over the minimum number of sus (1000) since start of accounting period (7/1/2009) 
select job_stats.t_user_id, users.username, users.email, sum(tgusage.su)
from job_stats, tgusage, users where
job_stats.resource = tgusage.resource and
job_stats.remote_job_id = tgusage.jobid and
abs(datediff(job_stats.job_start_time, tgusage.submit_time)) < 2 and
job_stats.t_user_id = users.user_id and
job_stats.job_start_time >= '2011-07-01'
group by job_stats.t_user_id
having sum(tgusage.su) > 1000

# Shows users who've been sent warnings, along with number of su's used.
select job_stats.t_user_id, users.username, users.email, sum(tgusage.su), user_preferences.*
from job_stats, tgusage, users, user_preferences where
user_preferences.user_id = job_stats.t_user_id and
user_preferences.preference like 'SUWARN%' and
job_stats.resource = tgusage.resource and 
job_stats.remote_job_id = tgusage.jobid and 
abs(datediff(job_stats.job_start_time, tgusage.submit_time)) < 2 and 
job_stats.t_user_id = users.user_id and 
job_stats.job_start_time >= '2011-07-01'
group by job_stats.t_user_id, user_preferences.preference
having sum(tgusage.su) > 0


# CLEAR all su warns for a user (replace 83 with userid)
delete from user_preferences where user_id = 83 and preference like "SUWARN%"

# FIND all tgusage records for a user, after 2011-07-01  (replace 83 with userid and replace date as needed)
select job_stats.t_user_id, users.username, users.email, tgusage.*
from job_stats, tgusage, users where
job_stats.resource = tgusage.resource and
job_stats.remote_job_id = tgusage.jobid and
abs(datediff(job_stats.job_start_time, tgusage.submit_time)) < 2 and
job_stats.t_user_id = users.user_id and
job_stats.job_start_time >= '2011-07-01' and
user_id = 83


How to test the overlimit email:
	- Set default schema to ngbw_test.  Don't do this on production!
	- Find a user_id to test with.  On the test server, I (terri) am 105.
	- Open a few query tabs in mysql.
	- Use one tab to clear all my SUWARN flags: delete from user_preferences where user_id = 83 and preference like "SUWARN%"
	- Use another tab to find all my tgusage records for the accounting period of interest, e.g.:
		select job_stats.t_user_id, users.username, users.email, tgusage.*
		from job_stats, tgusage, users where
		job_stats.resource = tgusage.resource and
		job_stats.remote_job_id = tgusage.jobid and
		abs(datediff(job_stats.job_start_time, tgusage.submit_time)) < 2 and
		job_stats.t_user_id = users.user_id and
		job_stats.job_start_time >= '2011-07-01' and
		user_id = 105

	- If possible, pick one (or more if necessary) tgusage records to modify, to adjust my total SUs for the period, eg:
		update tgusage set su=10001 where tgusage_id = 11428
	cd ~/scripts on portal2test and run ./usage.py -w to send the warning email.
