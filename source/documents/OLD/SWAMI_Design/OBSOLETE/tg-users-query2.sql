# Using these 2 queries to find unique cipres users who submitted xsede jobs for the specified date range
select t_username, t_email, job_start_time, count(*) count from ngbw.job_stats where (PROCESS_WORKER = 'GlobusProcessWorker' OR PROCESS_WORKER = 'SSHExecProcessWorker') AND (JOB_START_TIME between '2012-04-01' and '2012-06-30')
group by t_username 

select t_username, t_email, job_start_time, count(*) count from ngbw.job_stats where (PROCESS_WORKER = 'GlobusProcessWorker' OR PROCESS_WORKER = 'SSHExecProcessWorker') AND (JOB_START_TIME between '2012-04-01' and '2012-06-30')
AND t_username not like "Guest%"
group by t_username 




select t_username, t_email, job_start_time, count(*) count from ngbw.job_stats where (PROCESS_WORKER = 'GlobusProcessWorker' OR PROCESS_WORKER = 'SSHExecProcessWorker') AND (JOB_START_TIME between '2010-08-01' and '2010-09-30')
group by t_username

# These two queries should yield identical counts (just different ways of finding teragrid jobs)
select t_username, t_email, job_start_time, count(*) count from ngbw.job_stats where (PROCESS_WORKER = 'GlobusProcessWorker' OR PROCESS_WORKER = 'SSHExecProcessWorker') AND (JOB_START_TIME between '2010-10-01' and '2010-12-31')
group by t_username 

select t_username, t_email, job_start_time, count(*) count from ngbw.job_stats where (RESOURCE = 'abe' OR RESOURCE= 'lonestar') AND (JOB_START_TIME between '2011-04-01' and '2011-06-30')
group by t_username

# to find the guest accounts only, add "AND (t_username like "Guest%)" to the query.


select t_username, t_email, job_start_time, count(*) count from ngbw.job_stats where (PROCESS_WORKER = 'GlobusProcessWorker' OR PROCESS_WORKER = 'SSHExecProcessWorker') AND (JOB_START_TIME between '2012-04-01' and '2012-06-30')
group by t_username 

select t_username, t_email, job_start_time, count(*) count from ngbw.job_stats where (PROCESS_WORKER = 'GlobusProcessWorker' OR PROCESS_WORKER = 'SSHExecProcessWorker') AND (JOB_START_TIME between '2012-04-01' and '2012-06-30')
group by t_username 


select job_stats.t_user_id, job_stats.t_username, job_stats.t_email,  users.institution 
from ngbw.job_stats, ngbw.users 
where (job_stats.PROCESS_WORKER = 'GlobusProcessWorker' OR job_stats.PROCESS_WORKER = 'SSHExecProcessWorker') 
group by job_stats.t_user_id 
