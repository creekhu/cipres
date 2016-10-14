#!/bin/bash

SIZE=2299
FILE="./testdata.txt"
FILENAME="$(echo testdata.txt | base64)"

echo "encoded filename is $FILENAME"

OUTPUT=$(curl $CREDS -X POST -i $TUS_URL -H 'Tus-Resumable: 1.0.0'  -H "Upload-Length: $SIZE" \
	-H "Upload-Metadata: filename $FILENAME") 
echo "$OUTPUT"

URL=$(echo "$OUTPUT" | \
	grep 'Location:' |  \
	sed "s/$(printf '\r')\$//" | \
	awk '{print $2}')

echo "URL is $URL"

curl $CREDS -X PATCH -i $URL \
	-H 'Tus-Resumable: 1.0.0'  \
	-H 'Upload-Offset: 0' \
	-H 'Content-Type: application/offset+octet-stream' \
	--upload-file $FILE 

