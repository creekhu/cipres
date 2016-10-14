#!/bin/bash

# Usage: convertEncoding <files>
# 	Only run this on text files.  Seems to ignore binaries but I haven't tested extensively.
#		Attempts conversion with iconv if "file -i" gives a charset and it isn't us-ascii.
#		EOL conversion with zip should ignore anything that isn't a text file.
# 	Converts from current character encoding to ascii with unix style EOL.

if [ `uname` == "Darwin" ]; then
	check_cmd="file -bI"
else
	check_cmd="file -bi"
fi


# Use "file -bi" to guess current encoding and use iconv to change it to utf8.
for f in "$@" ; do
	if test -f $f ;  then
		CHARSET="$( $check_cmd "$f"|awk -F "=" '{print $2}')"

		shopt -s nocasematch;

		# If file -bi gives a charset and it isn't binary something  and it isn't us-ascii, then convert it to us-ascii:
		if [[ "$CHARSET" != ""  && ! ( "$CHARSET" =~ .*binary.* ) && ( "$CHARSET" != "us-ascii" ) ]]; then
			iconv -c -f "$CHARSET" -t us-ascii "$f" > "$f.out"
			mv "$f.out" "$f"
		fi

		# convert line endings to linux style
		cwd=`pwd`
		cd `dirname $f`

		archive="./foo_$$.zip"
		zip -qr $archive `basename $f` && unzip -aqo $archive && rm $archive 

		cd $cwd
	fi
done

