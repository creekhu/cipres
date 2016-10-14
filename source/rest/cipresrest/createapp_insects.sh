#!/bin/bash

source "./testfw.sh" 
if [[ $# < 2 ]] ; then
	echo "required command line arguments are: cipresadmin_username cipresadmin_pass"
	exit 1
fi

# config sets vars like URL from pycipres.conf
config

#
# This is a Script to easily add a rest application (and it's administrator) - until such
# time as we have a gui application to do this.
#
# To use, add values for these variables, save and run
# Then give password and appkey to user and ask him to change them via the rest api asap.
# Search output for app_id to get the assigned appkey.
#
username=
initialpass=
firstname=
lastname=
email=
country=US
phone=
institution=
comment=
appname=
longname=
website=
activate=


createAdminUser()
{
	echo "=================================================================="
	echo "Creating user $username"
	echo "=================================================================="

	command=(curl -k -u $AUSER:$APASS  $URL/user/$username \
		-d role=REST_ADMIN \
		-d password=$initialpass\
		-d email=$email\
		-d country=$country \
		-d first_name=$firstname \
		-d last_name=$lastname \
		-d phone=$phone \
		-d institution=$institution \
		-d comment=$comment) 
	shouldpass command "<user>"
}

createApplication()
{
	echo "\n=================================================================="
	echo "Create the application $appname"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/application/$username/$appname \
		-d "longname=$longname" \
		-d authtype=UMBRELLA \
		-d website=$website \
		-d activate=$activate )
	shouldpass command "<application>"
}

userExists()
{
	echo "=================================================================="
	echo "See if  user $username already exists"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/user/$username) 

	# This means we'll return true if the user exists.  If user doesn't exist
	# an error will be returned. Don't be fooled by seeing error message on stdout.
	# That's the right result, we don't want this script to modify an existing user
	# we want to create one if it doesn't already exist.
	shouldpass command "<user>"
}

applicationExists()
{
	echo "=================================================================="
	echo "See if  user $username already exists"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/application) 
	shouldpass command "<name>$appname</name>"
}

if userExists; then
	echo "User $username already exists"
	exit 1
fi
if applicationExists; then
	echo "Application $appname already exists"	
	exit 1
fi
if createAdminUser; then 
	createApplication
fi
