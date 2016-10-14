#!/bin/bash

if [ "$#" -ne 1 ] ; then
	echo "usage: $0 jobfile.downloadUri.url"
	exit 1
fi

# -J means use content-disposition header to name the file
# -O write the output to a file
curl -u $CRA_USER:$PASSWORD -H cipres-appkey:$KEY -O -J $1

