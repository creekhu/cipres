/*
	Check for duplicate jobhandles in job_stats, running_tasks and tasks tables.
	Any dups will need to be removed before going on with the conversion.
*/
SELECT  *
FROM    job_stats mto
WHERE   EXISTS
(
	SELECT  1
	FROM    job_stats mti
	WHERE   mti.jobhandle = mto.jobhandle
	LIMIT 1, 1
) ;

SELECT  *
FROM    running_tasks mto
WHERE   EXISTS
(
	SELECT  1
	FROM    running_tasks mti
	WHERE   mti.jobhandle = mto.jobhandle
	LIMIT 1, 1
) ;

SELECT  *
FROM    tasks mto
WHERE   EXISTS
(
	SELECT  1
	FROM    tasks mti
	WHERE   mti.jobhandle = mto.jobhandle
	LIMIT 1, 1
) ;


/*
	Our test db somehow got dups in job_stats on 2012-07-25 and 2012-07-27.  Must have been running some buggy code then.

	delete from job_stats where t_stage != 'COMPLETED' and ( jobhandle = 'NGBW-JOB-RAXMLHPC2BB-2EEF98A702E84092BBDC38C2584D96E7' or
	jobhandle = 'NGBW-JOB-RAXMLHPC2BB-5B1C583C6D484AE9866EB0530007923E' ) ;
*/
