#!/bin/bash

# Tests umbrella auth with tus_servlet, assumes tus_servlet is at $URL/tmpfiles

if [[ $# < 2 ]] ; then
	echo "cipresadmin username and pass required on commandline"
	exit 1
fi

# testfw.sh defines some functions and gets env vars from pycipres.conf
source "./testfw.sh" 

# Pick a name we're unlikely to have put in the db manually
EU1=foofoobarbarfoo
EUEMAIL=$EU1@yahoo.com
EU2=tlonsomersetave
EU2EMAIL=$EU2@yahoo.com

#  These come from pycipres.conf
APP=$GUIDEMO_APP
APPUSER=$GUIDEMO_ADMIN
APPPASS=$TESTPASS

APP2=$GUIDEMO2_APP
APP2USER=$GUIDEMO2_ADMIN
APP2PASS=$TESTPASS

# This is set in testfw.sh/config
APPKEY=$GUIDEMO_APPKEY
APPKEY2=$GUIDEMO2_APPKEY

# For the tus upload
SIZE=2299
FILE="./testdata.txt"
FILENAME="$(echo testdata.txt | base64)"
TUS_URL="$URL/tmpfiles"
FILE_URL="$URL/file/id"

Atest1()
{
	error=0

	echo "\n=================================================================="
	echo "Atest1a: start an upload for umbrella user $APP.$EU1 "
	echo "=================================================================="
	command=(curl -i -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H 'Tus-Resumable: 1.0.0' \
		-H "Upload-Length: $SIZE" \
    	-H "Upload-Metadata: filename $FILENAME"
		-X POST $TUS_URL 
	)
	if ! shouldpass command "Location" Location: ; then
		echo "TEST FAILED"
		error=1
	fi
	TUS_FILE_URL=$EXTRACTED
	echo "file_url is $TUS_FILE_URL"

	echo "\n=================================================================="
	echo "Atest1b: finish the upload for umbrella user $APP.$EU1 "
	echo "=================================================================="

	command=(curl -i -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H 'Tus-Resumable: 1.0.0' \
		-H 'Upload-Offset: 0' \
		-H 'Content-Type: application/offset+octet-stream' \
		--upload-file $FILE \
		-X PATCH $TUS_FILE_URL 
	)

	if ! shouldpass command "Upload-Offset" Upload-Offset: ; then
		echo "TEST FAILED"
		error=1
	fi

	OFFSET=$EXTRACTED
	echo "offset is $OFFSET"
	if (( $OFFSET != $SIZE )) ; then
		echo "TEST FAILED"
		error=1
	fi



	return $error
}


Atest2()
{
	error=0

	echo "\n=================================================================="
	echo "Atest2a: start an upload for umbrella user $APP.$EU1 "
	echo "=================================================================="
	command=(curl -i -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H 'Tus-Resumable: 1.0.0' \
		-H "Upload-Length: $SIZE" \
    	-H "Upload-Metadata: filename $FILENAME"
		-X POST $TUS_URL 
	)
	if ! shouldpass command "Location" Location: ; then
		echo "TEST FAILED"
		error=1
	fi
	TUS_FILE_URL=$EXTRACTED
	echo "file_url is $TUS_FILE_URL"

	echo "\n=================================================================="
	echo "Atest2b: finish the upload for umbrella user $APP.$EU1 "
	echo "=================================================================="

	command=(curl -i -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H 'Tus-Resumable: 1.0.0' \
		-H 'Upload-Offset: 0' \
		-H 'Content-Type: application/offset+octet-stream' \
		--upload-file $FILE \
		-X PATCH $TUS_FILE_URL 
	)

	if ! shouldpass command "Upload-Offset" Upload-Offset: ; then
		echo "TEST FAILED"
		error=1
	fi

	OFFSET=$EXTRACTED
	echo "offset is $OFFSET"
	if (( $OFFSET != $SIZE )) ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest2c: Do a GET of $TUS_FILE_URL "
	echo "=================================================================="

	command=(curl -i -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		-H 'Tus-Resumable: 1.0.0' \
		$TUS_FILE_URL 
	)

	if ! shouldpass command "username=" ; then
		echo "TEST FAILED"
		error=1
	fi


	echo "\n=================================================================="
	echo "Atest2d: import the upload for umbrella user $APP.$EU1 "
	echo "=================================================================="

	command=(curl -i -k -u "$APPUSER:$APPPASS" \
		-H "cipres-appkey:$APPKEY"  \
		-H "cipres-eu:$EU1" \
		-H "cipres-eu-email:$EUEMAIL" \
		$FILE_URL \
		-F source=$TUS_FILE_URL
	)
	if ! shouldpass command ; then
		echo "TEST FAILED"
		error=1
	fi

	return $serror
}

runtests Atest2 
