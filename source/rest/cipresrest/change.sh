#!/bin/bash

#
# Give this to users to show them how to change application administrator password
# and application key.
#

# URL="https://bumper.sdsc.edu/cipresrest/v1"
URL="http://localhost:7070/cipresrest/v1"

username=morpho_admin
oldpassword=changeme
newpassword=changeme2
appname=morphobank

updateAdminUser()
{
	echo "=================================================================="
	echo "Updating user $username"
	echo "=================================================================="

	curl -k -u $username:$oldpassword  $URL/user/$username \
		-d role=REST_ADMIN \
		-d password=$newpassword
}

updateAppKey()
{
	echo "\n=================================================================="
	echo "Update the application $appname, generate new appkey"
	echo "=================================================================="
	curl -k -u $username:$newpassword  $URL/application/$username/$appname -d generateId=true 
}

updateAdminUser
updateAppKey
