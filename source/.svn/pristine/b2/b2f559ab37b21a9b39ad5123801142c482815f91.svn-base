#!/bin/bash

# Tests umbrella auth with tus_servlet, assumes tus_servlet is at $URL/tmpfiles

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
	echo "Atest1a: start an upload for user $USER1"
	echo "=================================================================="
	command=(curl -i -k -u "$USER1:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
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
	echo "Atest1b: finish the upload for user $USER1"
	echo "=================================================================="

	command=(curl -i -k -u "$USER1:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
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
	echo "Atest1c: wrong user, $USER2 , tries to import"
	echo "=================================================================="

	command=(curl -i -k -u "$USER2:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
		$FILE_URL \
		-F source=$TUS_FILE_URL
	)
	if ! shouldfail command "AuthorizationException|AuthenticationException" ; then
		echo "TEST FAILED"
		error=1
	fi

	return $error
}


Atest2()
{
	error=0

	echo "\n=================================================================="
	echo "Atest2a: start an upload for user $USER1"
	echo "=================================================================="
	command=(curl -i -k -u "$USER1:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
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
	echo "Atest2b: finish the upload for user $USER1"
	echo "=================================================================="

	command=(curl -i -k -u "$USER1:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
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
		echo "TEST FAILED : $OFFSET != $SIZE "
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest2c: Do a GET of $TUS_FILE_URL .  No credentials needed."
	echo "=================================================================="

	# command=(curl -i -k -u "$USER1:$PASS" \
		# -H "cipres-appkey:$APPKEY1"  \
		# -H 'Tus-Resumable: 1.0.0' \
		# $TUS_FILE_URL 
	# )

	command=(curl -i -k -H 'Tus-Resumable: 1.0.0' $TUS_FILE_URL  )

	if ! shouldpass command "username=" ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest2d: import the upload for user $USER1"
	echo "=================================================================="

	command=(curl -i -k -u "$USER1:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
		$FILE_URL \
		-F source=$TUS_FILE_URL
	)
	if ! shouldpass command ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest2d: import the upload for user $USER1 AGAIN.  SHOULD FAIL"
	echo "=================================================================="
	command=(curl -i -k -u "$USER1:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
		$FILE_URL \
		-F source=$TUS_FILE_URL
	)
	if ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi

	return $serror
}


Atest3()
{
	error=0

	echo "\n=================================================================="
	echo "Atest3a: start an upload for user $USER1"
	echo "=================================================================="
	command=(curl -i -k -u "$USER1:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
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
	echo "Atest3b: finish the upload for user $USER1"
	echo "=================================================================="

	command=(curl -i -k -u "$USER1:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
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
		echo "TEST FAILED : $OFFSET != $SIZE "
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest3c: import the upload but omit the source parameter.  SHOULD FAIL"
	echo "=================================================================="

	command=(curl -i -k -u "$USER1:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
		-X POST
		$FILE_URL 
	)
	if ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest3d: import the upload with a malformedsource URL.  SHOULD FAIL"
	echo "=================================================================="
	command=(curl -i -k -u "$USER1:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
		$FILE_URL \
		-F source=www.msn.com
	)
	if ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest3e: import the upload with a non TUS URL.  SHOULD FAIL"
	echo "=================================================================="
	command=(curl -i -k -u "$USER1:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
		$FILE_URL \
		-F source=http://www.msn.com
	)
	if ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest3f: import the upload with a bad TUS source URL.  SHOULD FAIL"
	echo "=================================================================="
	command=(curl -i -k -u "$USER1:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
		$FILE_URL \
		-F source=$TUS_URL/foobar
	)
	if ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest3g: omit source param, include a bogus param.  SHOULD FAIL"
	echo "=================================================================="
	command=(curl -i -k -u "$USER1:$PASS" \
		-H "cipres-appkey:$APPKEY1"  \
		$FILE_URL \
		-F foo=bar
	)
	if ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi

	return $serror
}

runtests Atest1 Atest2 Atest3
# runtests Atest1

