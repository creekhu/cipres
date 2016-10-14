#!/bin/bash

source "./testfw.sh" 
if [[ $# < 2 ]] ; then
	echo "required command line arguments are: cipresadmin_username cipresadmin_pass"
	exit 1
fi

# config sets vars like URL from pycipres.conf
config

#
# This is test to add a rest application and it's administrator.  It's based on the script/template
# that we use to add apps and their admins, createapp.template.
#
#
username=foo
initialpass=test
firstname=foo
lastname=foo
email=cipresrest2@gmail.com
phone=
institution=sdsc
comment=
appname=fooapp	
longname='foo application'
website=
activate=true
#activate=false


createAdminUser()
{
	echo "=================================================================="
	echo "Creating user $username"
	echo "=================================================================="

	command=(curl -k -u $AUSER:$APASS  $URL/user/$username \
		-d role=REST_ADMIN \
		-d password=$initialpass\
		-d email=$email\
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
	echo "See if  user $username already exists by asking for $username record"
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
	echo "See if  application $appname already exists by listing applications"
	echo "and searching for $appname"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/application) 
	shouldpass command "<name>$appname</name>"
}

deleteUser()
{
	echo "=================================================================="
	echo "Delete user $username.  This should also delete the user's apps."
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  -X DELETE $URL/user/$username) 
	shouldpass command 
}

deleteApplication()
{
	echo "=================================================================="
	echo "Delete application $application"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  -X DELETE $URL/application/$username/$appname) 
	shouldpass command 
}

if userExists; then
	echo "User $username already exists, deleting."
	if deleteUser; then
		echo "User deleted"
	else
		echo "Error deleting user"
		exit 1
	fi
fi

if applicationExists; then
	echo "Application $appname already exists"	
	exit 1
else
	echo "Application $appname DOES NOT exist, will create user $username, then application $appname"	
fi

if createAdminUser; then 
	if createApplication; then
		echo "SUCCESS"
		exit 0
	fi
fi
echo "FAIL"
exit 1
