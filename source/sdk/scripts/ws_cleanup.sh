#!/bin/sh
# This script is installed on teragrid machines to cleanup old job directories.
# It can be run via cron on the webserver (in case cron isn't allowed on the teragrid
# host). See tg_ws_cleanup.sh for the script we invoke from cron.
#
# TO DO: customize this script for the specific teragrid host and install it there in 
# ~/ngbw/contrib/tools/bin. 
#
rootdir=~/ngbw/workspace
basedirs="$rootdir $rootdir/ARCHIVE $rootdir/FAILED"

echo `date` : Running ws_cleanup. > ~/tmp/ws_cleanup.log
echo "Removing job dirs where nothing has been accessed or modified in 2 days." >> ~/tmp/ws_cleanup.log


for basedir in $basedirs; do
	for jobdir in $basedir/NGBW-*; do
		if [ -d $jobdir ]; then

			# Find any files modified, or accessed recently, skipping "." itself.
			new_file_count=`find $jobdir -mindepth 1 -atime -2 -or -ctime -2 | wc -l`

			# If no young files delete the directory
			if [ $new_file_count -eq 0 ]; then
				echo "Deleting $jobdir" >> ~/tmp/ws_cleanup.log
			fi
		fi
	done
done

# cat ~/tmp/ws_cleanup.log | mail -s "ws_cleanup ABE" terri@sdsc.edu 
cat ~/tmp/ws_cleanup.log
~    
