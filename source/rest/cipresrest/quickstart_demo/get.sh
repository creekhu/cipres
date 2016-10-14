#!/bin/bash

# argument is job url 
if [ "$#" -ne 1 ] ; then
	echo "usage: $0 url"
	exit 1
fi
curl -u $CRA_USER:$PASSWORD -H cipres-appkey:$KEY $1 
