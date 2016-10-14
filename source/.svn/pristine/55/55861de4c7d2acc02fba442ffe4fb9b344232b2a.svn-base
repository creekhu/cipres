#!/bin/bash

# Tests the /file/username api using direct authentication 
#
# TODO: This is just a placeholder right now since I don't quite know how to use the api.

if [[ $# < 2 ]] ; then
	echo "cipresadmin username and pass required on commandline"
	exit 1
fi

# testfw.sh defines some functions and gets env vars from pycipres.conf
source "./testfw.sh" 

#  These come from pycipres.conf
APP1=$PYCIPRES_APP
USER1=$PYCIPRES_EU
APP2=$PYCIPRES2_APP
USER2=$PYCIPRES_EU2
PASS=$TESTPASS

# This is set in testfw.sh/config
APPKEY1=$PYCIPRES_APPKEY
APPKEY2=$PYCIPRES2_APPKEY

FILE_URL="$URL/file"

Atest1()
{
	error=0

	echo "\n=================================================================="
	echo "Atest1. "
	echo "=================================================================="

	command=(curl -i -k -u "$USER1:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
		$FILE_URL/$USER1//foofolder/dummy.txt \
		-d sourceUri=http://www.google.com
	)
	if ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi

	return $error
}

runtests Atest1
