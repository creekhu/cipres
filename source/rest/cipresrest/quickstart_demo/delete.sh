#!/bin/bash

# argument is jobs url
if [ "$#" -ne 1 ] ; then
	echo "usage: $0 joburl"
	exit 1
fi
curl -u $CRA_USER:$PASSWORD -H cipres-appkey:$KEY -X DELETE $1
