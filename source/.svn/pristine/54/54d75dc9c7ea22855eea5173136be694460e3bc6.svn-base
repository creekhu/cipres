#!/bin/bash

if [ "$#" -ne 1 ] ; then
	echo "usage: $0 jobstatus.resultsUri.url"
	exit 1
fi

resultsdir=myresults
# rm -rf $resultsdir
mkdir $resultsdir
cd $resultsdir

resultsXml=$( curl -u $CRA_USER:$PASSWORD -H cipres-appkey:$KEY $1 )


# AWK explanation here: http://code.scottshipp.com/2013/06/27/easily-extract-data-from-xml-using-grep-and-awk/
urls=$(echo "$resultsXml" | grep 'url'  | awk -F">" '{print $2}' | awk -F"<" '{print $1}')
echo "URLS is: $urls"

for url in $urls; do
	# -J means use content-disposition header to name the file
	# -O write the output to a file
	curl -u $CRA_USER:$PASSWORD -H cipres-appkey:$KEY -O -J $url
done

