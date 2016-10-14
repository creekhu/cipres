#!/bin/bash

curl -u $CRA_USER:$PASSWORD -H cipres-appkey:$KEY $URL/job/$CRA_USER \
	-F tool=RAXMLHPC8_REST_XSEDE  \
	-F input.infile_=@./raxml_inputphy.txt \
	-F metadata.statusEmail=true
