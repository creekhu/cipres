#!/bin/sh
# JOBID=NGBW-JOB-RAXMLHPC2BB-8ACAA1CDF15D4D1883BE0F1BC822317B

# Test with bogus username
# curl -u admin:berryterri http://localhost:7070/cipresrest/v1/job/foofoo/NGBW-JOB-RAXMLHPC2BB-8ACAA1CDF15D4D1883BE0F1BC822317B
# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri

# This url requires a valid username/password and username must match username in url (due to rolesAllowed)
# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri

# This url shouldn't require basic auth at all:
# curl http://localhost:7070/cipresrest/world

# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/NGBW-JOB-RAXMLHPC2BB-D12BCA667CFF461989F1C66A49B49711

# Should return list of jobs.
# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri

# Should get accessforbidden error, since terri can't access sam's jobs.
# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job/sam

# Should return list of terri's jobs.  Admin user can masquerade as anyone.
# curl -u admin:admin http://localhost:7070/cipresrest/v1/job/terri


# Should get accessforbidden error.  Ordinary user can't access this url.
# curl -u terri:berryterri http://localhost:7070/cipresrest/v1/job/admin/updateJob

# curl -u admin:admin http://localhost:7070/cipresrest/v1/job/admin/updateJob?taskId=5\&status=foo&fiddle=faddle

# Try https on billiards.
# curl -k -u terri:berryterri https://billiards1.sdsc.edu/cipresrest/v1/job/terri

# Non https should NOT work
# curl -k -u terri:berryterri http://billiards1.sdsc.edu/cipresrest/v1/job/terri

JOBID=NGBW-JOB-CLUSTALW-D288FE7C370148FE8D9BCCB428B4E802
# curl -k -u terri:berryterri https://billiards1.sdsc.edu/cipresrest/v1/job/terri/$JOBID
# curl -X DELETE -k -u terri:berryterri https://billiards1.sdsc.edu/cipresrest/v1/job/terri/$JOBID


# Test result file download, show headers
# This file is STDOUT.  Browser asks if I want to open or save.
# curl -I -k -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/NGBW-JOB-CLUSTALW-08F2C8A2E6294BFE924D57019778EE8F/output/71

# This file is infile.dnd.  For some reason browser thinks it's binary and won't let me open it, only save it. 
# curl -I -k -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/NGBW-JOB-CLUSTALW-08F2C8A2E6294BFE924D57019778EE8F/output/76

# Try to list working dir of job that is finished.
# curl -I -k -u terri:berryterri http://localhost:7070/cipresrest/v1/job/terri/NGBW-JOB-CLUSTALW-08F2C8A2E6294BFE924D57019778EE8F/workingdir


# curl -k -u cipresadmin:cack800! \
# 	http://localhost:7070/cipresrest/v1/job/guitest.test1

APPID=2
USER=scripts.test1
PASS=test


curl -k -u $USER:$PASS\
	-H "cipres-appkey:$APPID" \
	http://localhost:7070/cipresrest/v1/job/$USER

