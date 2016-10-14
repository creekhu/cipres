Email form Aaron Hunter about how to dump the cipres and ngbw database schema
so that I can give it to people who are installing their own copy of cipres.
>
> You can do a schema dump with mysqldump, just add the --no-data=true --add-drop-table=false tags when running the dump, and you'll just get the create table statements.
>
> No problem regarding load balancing, if you ever get the time and want to try it, we'll be here waiting.
>
> As far as the InnoDB conversion, I'd be more than happy to give it a shot as long as I knew what to test afterwards (if there are no explicit MySQL errors), and if you guys have 30 minutes to an hour of time I could recharge for the conversion.
>
> Let me know?
>
> Thanks,
> Aaron


So, I think it's:

mysqldump --no-data=true --add-drop-table=false --user=ciprestest --password=PASSWORD_GOES_HERE -h mysql.sdsc.edu  --port=3312 ngbwtest

Our document structure:

A data item in a user's folder area:
userdata
	- points to it's enclosing folder
	- points to user who owns it
	- points to source_document
	- has a creation date


Several things can point to a source_document:
	userdata
	task_input_source_document (which is logically wrapped by a task_input_parameter)
	task_output_source_document (which is logically wrapped by a task_output_parameter)

	When we go to delete a SourceDocumentRow the code does three queries to check
	if there are any refs to the row in userdata, task_input_source_document and task_output_source_document,
	and only deletes if not.
source_document
	- filename (place the content is really stored as a .bin.gz file)
	-length (uncompressed size in bytes)


These are the tasks parameters that don't correspond to files/documents.
tool_parameters
	task_id
	parameter
	value


These are task parameters that do correspond to files/documents:
task_input_parameters
	task_id
	parameter name (eg, the most common is "infile_")
	pointer to task_input_source_document record
	
task_input_source_documents
	- points to a source_document
	- points back to task_input_parameter record
	- name (parameter name)

task_output_parameters
	task_id
	parameter name 	(corresponds to the ResultFile element in the pisexml, if this is something like *.* there
					will be lots of task_output_source_documents that point back to this record )
	pointer to task_output_source_document record
	
task_output_source_documents
	- points to a source_document
	- points back to task_output record
	- name (actual filename that the task produced in it's working directory, like stdout.txt or align.dnd) 



These 2 tables are no longer used.  We used them when we parsed certain types of files into records. 
datarecords
	- points to a userdata record
record_fields
	- points to datarecord it belongs to 


	
