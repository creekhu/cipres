export KEY=insects-095D20923FAE439982B6D5EBD2E339C9
export URL=http://localhost:7070/cipresrest/v1

# List workingdir
curl -k -u tom:tom \
	-H cipres-appkey:$KEY \
	$URL/job/tom/NGBW-JOB-CLUSTALW-3254293C1610438B8480538FB042ED91/workingdir
exit

curl  -k -u tom:tom \
	-H cipres-appkey:$KEY \
	$URL/job/tom \
	-F tool=CLUSTALW \
	-F metadata.clientJobId=200 \
	-F input.infile_=@./sample1_in.fasta \
	-F vparam.runtime_=1
exit

exit

# List results
curl -k -u tom:tom \
	-H cipres-appkey:$KEY \
	$URL/job/tom//NGBW-JOB-CLUSTALW-0171A3F1BFA0477CAF35B79CE075DF9C/output/
exit

curl  -D - -k -u tom:tom \
	-H cipres-appkey:$KEY \
	$URL/job/tom/$JOB
exit


curl  -k -u tom:tom \
	-H cipres-appkey:$KEY \
	$URL/job/tom \
	-F tool=CLUSTALW \
	-F metadata.clientJobId=200 \
	-F input.infile_=@./sample1_in.fasta \
	-F vparam.runtime_=1
exit

JOB=NGBW-JOB-CLUSTALW-A13F6570A8A74BA59EB96ABE7B898E47

	
	

# Download a working dir file 
curl -k -u tom:tom \
	-H cipres-appkey:$KEY \
	-O -J \
	$URL/job/tom/NGBW-JOB-CLUSTALW-0171A3F1BFA0477CAF35B79CE075DF9C/workingdir/stdout.txt
exit

curl  -k -u tom:tom \
	-H cipres-appkey:$KEY \
	$URL/job/tom/NGBW-JOB-CLUSTALW-0171A3F1BFA0477CAF35B79CE075DF9C/workingdir
exit




curl -k -u cipresadmin:cack800! $URL/user?role=REST_USER
exit
curl  -k -u tom:tom $URL/application/tom 
exit


curl -i -k -u tom:tom \
	-H cipres-appkey:$KEY \
	$URL/job/?jh=NGBW-JOB-CLUSTALW-3957CC6EBF5E448095A5666B41EDDF90







exit

# Try to get deleted job
curl -i -k -u tom:tom \
	-H cipres-appkey:$KEY \
	$URL/job/tom/NGBW-JOB-CLUSTALW-CC460782E5FF464CB96791B1E6053AA4


# Cancel and delete a job
curl -k -u tom:tom \
	-H cipres-appkey:$KEY \
	-X DELETE \
	$URL/job/tom/NGBW-JOB-CLUSTALW-CC460782E5FF464CB96791B1E6053AA4


# Download a result file
curl -k -u tom:tom \
	-H cipres-appkey:$KEY \
	-O -J \
	$URL/job/tom/NGBW-JOB-CLUSTALW-3957CC6EBF5E448095A5666B41EDDF90/output/1544




# 3rd clustalw job
curl -k -u tom:tom \
	-H cipres-appkey:$KEY \
	$URL/job/tom \
	-F tool=CLUSTALW \
	-F metadata.clientJobId=103 \
	-F input.infile_=@./sample1_in.fasta \
	-F vparam.runtime_=1



curl -k -u tom:tom \
	-H cipres-appkey:$KEY \
	$URL/job/tom/NGBW-JOB-CLUSTALW-3957CC6EBF5E448095A5666B41EDDF90

curl -k -u tom:tom \
	-H cipres-appkey:$KEY \
	$URL/job/tom?expand=true


# first clustal job I submitted
curl -k -u tom:tom \
	-H cipres-appkey:$KEY \
	$URL/job/tom \
	-F tool=CLUSTALW \
	-F metadata.clientJobId=101 \
	-F input.infile_=@./sample1_in.fasta \
	-F vparam.runtime_=1

#second job I submitted
curl -k -u tom:tom \
	-H cipres-appkey:$KEY \
	$URL/job/tom \
	-F tool=CLUSTALW \
	-F metadata.clientJobId=102 \
	-F input.infile_=@./sample2_in.fasta \
	-F vparam.runtime_=1
