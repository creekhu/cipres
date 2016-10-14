#!/bin/bash

##########################################################################################
# 
##########################################################################################

if [[ $# < 2 ]] ; then
	echo "required command line arguments are: cipresadmin_username cipresadmin_pass "
	echo "Best run with stdout, but not stderr redirected to a file.  Last line of output gives summary."
	echo "Search results for 'TODO' and manually verify output where indicated."
	exit 1
fi
source "./testfw.sh" 

setup()
{
	echo "=================================================================="
	echo "Setup.  Delete $APPUSER1, and $APPUSER2 and direct users $EU1 and $EU2." 
	echo "Automatically deletes related umbrella end users."
	echo "Error expected if $APPUSER1 or $APPUSER2 or end users don't exist."
	echo "=================================================================="
	curl -k -u $AUSER:$APASS  -X DELETE $URL/user/$APPUSER1
	curl -k -u $AUSER:$APASS  -X DELETE $URL/user/$APPUSER2
	curl -k -u $AUSER:$APASS  -X DELETE $URL/user/$EU1
	curl -k -u $AUSER:$APASS  -X DELETE $URL/user/$EU2
}




Utest1()
{
	echo "=================================================================="
	echo "Utest1: SHOULD ERROR. create or update a rest app administrator w/o any valid arguments"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/user/$APPUSER1 -d "foo=bar" )
	shouldfail command "WebApplicationException|Validation Error"
}

Utest2()
{
	echo "=================================================================="
	echo "Utest2: SHOULD ERROR. create a rest app administrator w/ too long area code"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/user/$APPUSER1 
		-d "password=$APPPASS1" \
		-d "email=$APPUSER1@yahoo.com" \
		-d "first_name=new" \
		-d "last_name=one" \
		-d "area_code=123456789123456789")

	shouldfail command  "Validation Error"
}

Utest3()
{
	echo "=================================================================="
	echo "Utest3: create a rest app administrator, "
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/user/$APPUSER1\
		-d "password=$APPPASS1" \
		-d "email=$APPUSER1@yahoo.com" \
		-d "country=US" \
		-d "first_name=new" \
		-d "last_name=one" )
	shouldpass command "<user>"
}

Utest4()
{
	echo "=================================================================="
	echo "Utest4: create a rest application that uses direct auth, $DAPP1:"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/application/$APPUSER1/$DAPP1\
		-d "longname=someone's fancy scripts" \
		-d "authtype=DIRECT" \
		-d "comment=not much to say.")
	shouldpass command "<application>"
}

Utest5()
{
	echo "=================================================================="
	echo "Utest5: register a user, $EU1." 
	echo "For now, cipres admin creds are required so this should fail."
	echo "=================================================================="
	command=(curl -k  $URL/user/$EU1 \
		-d "password=$TESTPASS" \
		-d "email=$EU1EMAIL" \
		-d "country=BR" \
		-d "first_name=u1" \
		-d "last_name=u1" \
		-d "activation_url=foo")
	# Note that sending activation_url= (an empty string) is NOT the same as not sending
	# activation_url parameter.
	shouldfail command  "Authorization"
}

Utest5a()
{
	echo "=================================================================="
	echo "Utest5a: register a user, $EU1"
	echo "Use cipres admin credentials this time."
	echo "=================================================================="
	command=(curl -k  -u $AUSER:$APASS $URL/user/$EU1 \
		-d "password=$TESTPASS" \
		-d "email=$EU1EMAIL" \
		-d "country=BR" \
		-d "first_name=u1" \
		-d "last_name=u1" )
	shouldpass command "<user>"
}

Utest6()
{
	echo "=================================================================="
	echo "Utest6: modify the user $EU1, using his credentials:"
	echo "=================================================================="
	command=(curl -k  -u $EU1:$EU1PASS $URL/user/$EU1 \
		-d "first_name=uu1" \
		-d "last_name=uu1" )
	shouldpass command "<first_name>uu1</first_name>"
}

Utest7()
{
	echo "=================================================================="
	echo "Utest7: modify the user $EU1, using cipres admin credentials:"
	echo "=================================================================="
	command=(curl -k  -u $AUSER:$APASS $URL/user/$EU1 \
		-d "first_name=uuuu1" \
		-d "last_name=uu1" )
	shouldpass command "<first_name>uuuu1</first_name>"
}

Utest8()
{
	echo "=================================================================="
	echo "Utest8: SHOULD ERROR.  Modify $EU1 w/o logging in:"
	echo "=================================================================="
	command=(curl -k  $URL/user/$EU1 \
		-d "first_name=uu1" \
		-d "last_name=uu1" )
	shouldfail command "AuthorizationException"
}

Utest9()
{
	echo "=================================================================="
	echo "Utest9: Modify $EU1. "
	echo "=================================================================="
	command=(curl -k  -u $EU1:$EU1PASS $URL/user/$EU1 \
		-d "first_name=uuu1" \
		-d "last_name=uu1" )
	shouldpass command "uuu1"
}

# This test is obsolete, we don't have the field User.appname anymore.
Utest10()
{
	echo "=================================================================="
	echo "Utest10: Try to change appname for user $EU1.  It shouldn't change."
	echo "=================================================================="
	command=(curl -k  -u $EU1:$EU1PASS $URL/user/$EU1 \
		-d "appname=foo" \
		-d "last_name=uu1" )
	shouldpass command "<appname>RestDirectEndUser</appname>"
}

# This test is obsolete, rest api only creates accounts with role=REST_USER
# so we no longer have a role parameter (and extra params are quietly ignored - though
# I want to change that).  Umbrella accounts are created automatically when request
# headers for a new user come in, but there is no way to explicitly create an umbrella
# end user account.
Utest11()
{
	echo "=================================================================="
	echo "Utest11: SHOULD ERROR.  Try to do explict registration with role = UMBRELLA "
	echo "=================================================================="
	command=(curl -k  $URL/user/$EU2 \
		-d "role=REST_END_USER_UMBRELLA" \
		-d "password=$TESTPASS" \
		-d "appname=thisonedoesntexist" \
		-d "email=$EU2EMAIL" \
		-d "first_name=u1" \
		-d "last_name=u1" )
	shouldfail command "Validation Error"
}

Utest12()
{
	echo "=================================================================="
	echo "Utest12: get info about $DAPP1, logged in as cipres admin"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/application/$APPUSER1/$DAPP1)
	shouldpass command "<application>"
}

Utest13()
{
	echo "=================================================================="
	echo "Utest13: get info about $DAPP1, logged in as $APPUSER1"
	echo "=================================================================="
	command=(curl -k -u $APPUSER1:$APPPASS1 $URL/application/$APPUSER1/$DAPP1)
	shouldpass command "<application>"
}

Utest14()
{
	echo "=================================================================="
	echo "Utest14: SHOULD ERROR. get info about $DAPP1 w/o login."
	echo "=================================================================="
	command=(curl -k $URL/application/$APPUSER1/$DAPP1)
	shouldfail command "AuthenticationException"
}

Utest15()
{
	echo "=================================================================="
	echo "Utest15: SHOULD ERROR. get info about $DAPP1 w/ wrong login."
	echo "=================================================================="
	command=(curl -k -u $EU1:$EU1PASS $URL/application/$APPUSER1/$DAPP1)
	shouldfail command "AuthorizationException"
}

Utest16()
{
	error=0
	echo "=================================================================="
	echo "Utest16a: Get new appid for $DAPP1, logged in as cipresadmin"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/application/$APPUSER1/$DAPP1 -d generateId=true)
	if ! shouldpass command "<app_id>"; then
		echo "16a failed."
		error=1
	fi

	echo "=================================================================="
	echo "Utest16b: Get new appid for $DAPP1, logged in as $APPUSER1. SHOULD ERROR, only cipresadmin can change application records. "
	echo "=================================================================="
	command=(curl -k -u $APPUSER1:$APPPASS1 $URL/application/$APPUSER1/$DAPP1 -d generateId=true)
	if ! shouldfail command "Authorization" ; then
		echo "16b failed."
		error=1
	fi
	return $error
}

Utest17()
{
	echo "=================================================================="
	echo "Utest17: Deleting $DAPP1 "
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  -X DELETE $URL/application/$APPUSER1/$DAPP1)
	shouldpass command
}

Utest18()
{
	echo "=================================================================="
	echo "Utest18: SHOULD ERROR. Create $DAPP1, not as admin and try to make it active. " 
	echo "=================================================================="
	command=(curl -k -u $APPUSER1:$APPPASS1 $URL/application/$APPUSER1/$DAPP1\
		-d "longname=someone's fancy scripts" \
		-d "authtype=DIRECT" \
		-d "comment=not much to say." \
		-d "activate=true")
	shouldfail command "AuthorizationException"
}


Utest19()
{
	echo "=================================================================="
	echo "Utest19 preperation: Deleting $DAPP1 "
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  -X DELETE $URL/application/$APPUSER1/$DAPP1)
	shouldpass command

	echo "=================================================================="
	echo "Utest20: Create $DAPP1, not as admin, don't try to set active. Should work this time."
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/application/$APPUSER1/$DAPP1\
		-d "longname=someone's fancy scripts" \
		-d "authtype=DIRECT" \
		-d "comment=not much to say." ) 
	shouldpass command "<active>false</active>"
}

Utest20()
{
	echo "=================================================================="
	echo "Utest20 preperation: Deleting $DAPP1 "
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  -X DELETE $URL/application/$APPUSER1/$DAPP1)
	shouldpass command

	echo "=================================================================="
	echo "Utest19: Create $DAPP1, as admin and make it active. "
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/application/$APPUSER1/$DAPP1\
		-d "longname=someone's fancy scripts" \
		-d "authtype=DIRECT" \
		-d "comment=not much to say." \
		-d "activate=true")
	shouldpass command "<active>true</active>"
}

Utest21()
{
	echo "=================================================================="
	echo "Utest21: GET User Info for $APPUSER1 as cipres admin - should contain encryptedPassword" 
	echo "Only time we return it is when cipresadmin asks for a specific User record, so that we"
	echo "can send url for user to change lost password."
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/user/$APPUSER1)
	shouldpass command '<encryptedPassword>'
}

Utest22()
{
	echo "=================================================================="
	echo "Utest22: GET User Info as $APPUSER1  - should not contain encryptedPassword" 
	echo "=================================================================="
	command=(curl -k -u $APPUSER1:$APPPASS1 $URL/user/$APPUSER1)
	shouldpass command '<user>' 'encryptedPassword'
	if [[ ! -z $EXTRACTED ]] ; then 
		echo "Error: found encryptedPassword: $EXTRACTED"
		return 1
	fi
	return 0
}

Utest23()
{
	echo "=================================================================="
	echo "Utest23: As cipresadmin, get a list of all users." 
	echo "We can partially check the results by looking for a specific user that should be in the list."
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/user/ )
	shouldpass command "$APPUSER1"
}

Utest24()
{
	echo "=================================================================="
	echo "Utest24: As cipresadmin, get a list of all users." 
	echo "Then look for a 2nd user we know whould be there too."
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/user/ )
	shouldpass command "$EU1"
}



setup

runtests \
	Utest1 Utest2 Utest3 Utest4 Utest5 Utest5a Utest6 \
	Utest7 Utest8 Utest9 \
	Utest12 Utest13 Utest14 Utest15 Utest16 \
	Utest17 Utest18 Utest19 Utest20 Utest21 Utest22 \
	Utest23 Utest24

