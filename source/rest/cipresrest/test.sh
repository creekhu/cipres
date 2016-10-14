#!/bin/sh


# curl -u terri:berryterri -F "infile=@pom.xml" -F "tool=raxml"  http://localhost:7070/cipresrest/v1/job
# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job

# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/NGBW-JOB-CLUSTALW-C63076B965934C33BAE5404C431F3CC7 
# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/NGBW-JOB-CLUSTALW-B965934C33BAE5404C431F3CC7 
# curl -u foo:berryterri http://localhost:7070/cipresrest/v1/job/terri/NGBW-JOB-CLUSTALW-C63076B965934C33BAE5404C431F3CC7 
# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job/foo/NGBW-JOB-CLUSTALW-C63076B965934C33BAE5404C431F3CC7 
# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri

# Test with a bad username.  -i includes the http header.
# curl -i -u garbage:berryterri http://localhost:7070/cipresrest/v1/job

# Test without auth.  -i includes the http header.
# curl -i  http://localhost:7070/cipresrest/v1/job

# Test with digest auth.  -i includes the http header.
# curl -i  --digest -u terri:berryterri  http://localhost:7070/cipresrest/v1/job

# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/NGBW-JOB-CLUSTALW-D7090DC17D8C4893A9B5F8608B2DA46C


# Test with doc id that doesn't exist.
# curl -i -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/NGBW-JOB-CLUSTALW-D7090DC17D8C4893A9B5F8608B2DA46C/output/44602535555

# List outputs of a job that's been saved.
curl  -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/NGBW-JOB-CLUSTALW-D7090DC17D8C4893A9B5F8608B2DA46C/output

# Get one of the valid output docs
# curl -i -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/NGBW-JOB-CLUSTALW-D7090DC17D8C4893A9B5F8608B2DA46C/output/4460253

# Try to list working directory for a job that is finished running. Triton job
# curl  -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/NGBW-JOB-CLUSTALW-D7090DC17D8C4893A9B5F8608B2DA46C/workingdir


JOBID=NGBW-JOB-RAXMLHPC2BB-8ACAA1CDF15D4D1883BE0F1BC822317B

# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/$JOBID/workingdir

# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/$JOBID/workingdir/_JOBINFO.TXT

# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/$JOBID/

# Test with admin user.
# curl -u admin:berryterri http://localhost:7070/cipresrest/v1/job/terri/$JOBID/

# Test with admin user and bogus username in url.
# curl -u admin:berryterri http://localhost:7070/cipresrest/v1/job/foo/$JOBID/
curl -u admin:berryterri http://localhost:7070/cipresrest/v1/job/foo

# What error should a malformed url give?
# curl -i -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/NGBW-JOB-RAXMLHPC2BB-8ACAA1CDF15D4D1883BE0F1BC822317B/NGBW-JOB-RAXMLHPC2BB-8ACAA1CDF15D4D1883BE0F1BC822317B


