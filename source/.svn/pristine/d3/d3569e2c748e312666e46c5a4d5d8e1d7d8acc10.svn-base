# Put a dummy entry in running_tasks
insert into running_tasks 
(resource, task_id, submitter, remote_job_id, date_submitted, jobhandle, workspace, output_desc, process_worker, file_handler, commandline, callback_url)
values
('DUMMY', 7500, 'dummy', '10', '2010-04-29 09:19:31', 'dummy', 'dummy', 'dummy', 'dummy', 'dummy', 'dummy', 'dummy')

# delete it
delete from running_tasks where resource = 'DUMMY'


# Find all tasks for which there are duplicate running_task records
select task_id, count(task_id) as occurrences from running_tasks group by task_id having occurrences > 1

# Find all tasks for which there are duplicate running_task records
# select most recent running_task of each group (based on running_task_id)
SELECT running_tasks.* FROM running_tasks 
INNER JOIN (SELECT MAX(running_task_id) AS id, count(task_id) as occurrences FROM running_tasks GROUP BY task_id having occurrences > 1) ids 
ON running_tasks.running_task_id = ids.id order by date_submitted DESC

# Same as above but for job_stats table 
SELECT job_stats.* FROM job_stats 
INNER JOIN (SELECT MAX(statistic_id) AS id, count(task_id) as occurrences FROM job_stats GROUP BY task_id having occurrences > 1) ids 
ON job_stats.statistic_id = ids.id order by job_start_time DESC

