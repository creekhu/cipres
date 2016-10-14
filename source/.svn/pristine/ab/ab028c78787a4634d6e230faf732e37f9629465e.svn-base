package org.ngbw.sdk.core.shared;

/*
	20 char limit in the db for these enum strings

	The first group of values are the only stages that are used since
	we updated the sdk for the REST api, around 11/2013.

	The 2nd group has some old stages that we no longer set, but since
	there are records in the database that have these stages, we need to
	keep them in this enum to avoid errors when reading those records
	into memory.

	Also note that in old job_stats records an * is appended to the stage
	when a retry is attempted.
*/
public enum TaskRunStage {
	// currently used stages
	NEW,
	READY,
	QUEUE,
	INPUTCHECK,
	COMMANDRENDERING,
	INPUTSTAGING,
	SUBMITTING,
	SUBMITTED,
	LOAD_RESULTS,
	COMPLETED,

	// additional stages in old records
	SUBMIT,
	INITIALIZE,
	PROCESSING,
	ERR_LOAD_RESULTS
}
