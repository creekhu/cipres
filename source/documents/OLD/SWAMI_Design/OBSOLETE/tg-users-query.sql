select t_username, t_email, job_start_time, count(*) count from ngbw.job_stats where PROCESS_WORKER = 'GlobusProcessWorker' AND JOB_START_TIME between '2009-10-01' and '2009-12-31'
group by t_username

