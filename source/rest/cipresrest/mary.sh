export KEY=phylobank-2429974CC97D470B9F2A73AF077AF251
export URL=https://bumper.sdsc.edu/cipresrest/v1


curl -k -u mary:mary \
	-H cipres-appkey:$KEY \
	-H cipres-eu:harry \
	-H cipres-eu-email:harry@ucsddd.edu \
	-H cipres-eu-institution:UCSD \
	-H cipres-eu-country:US \
	$URL/job/phylobank.harry/NGBW-JOB-CLUSTALW-CB8D053F9033487E9B4F9BAF8A3AA47A

exit


curl -k -u mary:mary \
	-H cipres-appkey:$KEY \
	$URL/job/?jh=NGBW-JOB-CLUSTALW-CB8D053F9033487E9B4F9BAF8A3AA47A\&jh=NGBW-JOB-CLUSTALW-553D534D355C4631BBDCF217BB792A01
exit

curl -k -u mary:mary \
	-H cipres-appkey:$KEY \
	-H cipres-eu:bill \
	-H cipres-eu-email:bill@ucsddd.edu \
	-H cipres-eu-institution:UCSD \
	-H cipres-eu-country:US \
	$URL/job/phylobank.bill\
	-F tool=CLUSTALW \
	-F metadata.clientJobId=012008AR \
	-F input.infile_=@./sample1_in.fasta \
	-F vparam.runtime_=1
exit




exit

exit


curl -k -u mary:mary \
	-H cipres-appkey:$KEY \
	-H cipres-eu:harry \
	-H cipres-eu-email:harry@ucsddd.edu \
	-H cipres-eu-institution:UCSD \
	-H cipres-eu-country:US \
	$URL/job/phylobank.harry\
	-F tool=CLUSTALW \
	-F metadata.clientJobId=010007AQ \
	-F input.infile_=@./sample1_in.fasta \
	-F vparam.runtime_=1
exit


curl -i -k -u mary:mary \
	-H cipres-appkey:$KEY \
	-H cipres-eu:harry \
	-H cipres-eu-email:harry@ucsddd.edu \
	-H cipres-eu-institution:UCSD \
	-H cipres-eu-country:US \
	$URL/job/phylobank.harry


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


# List results
curl -k -u tom:tom \
	-H cipres-appkey:$KEY \
	$URL/job/tom/NGBW-JOB-CLUSTALW-3957CC6EBF5E448095A5666B41EDDF90/output


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
