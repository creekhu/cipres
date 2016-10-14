#!/bin/bash

##########################################################################################
# Exercises the user api: create, delete, update.   Also exercies the application api:
# create apps, belonging to users we created earlier.  Gets apps, updates them, deletes them.
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
	echo "Utest1: create or update rest admin user $APPUSER1 with cipresadmin login:"
	echo "=================================================================="

	command=(curl -k -u $AUSER:$APASS  $URL/user/$APPUSER1 \
		-d role=REST_ADMIN \
		-d password=$APPPASS1 \
		-d email=$APPUSER1@yahoo.com \
		-d country=US \
		-d first_name=new \
		-d last_name=one) 
	shouldpass command "<user>"
}

Utest2()
{
	echo "=================================================================="
	echo "Utest2: delete the rest admin user"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  -X DELETE $URL/user/$APPUSER1)
	shouldpass command
}

Utest3()
{
	echo "=================================================================="
	echo "Utest3: try to recreate the rest admin user w/o a country code"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/user/$APPUSER1 \
		-d role=REST_ADMIN \
		-d password=$APPPASS1 \
		-d email=$APPUSER1@yahoo.com \
		-d first_name=new \
		-d "last_name=new one" )
		shouldfail command "country"
}

Utest4()
{
	echo "=================================================================="
	echo "Utest4: try to recreate the rest admin user with invalid country code"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/user/$APPUSER1 \
		-d role=REST_ADMIN \
		-d password=$APPPASS1 \
		-d email=$APPUSER1@yahoo.com \
		-d country=XX \
		-d first_name=new \
		-d "last_name=new one" )
		shouldfail command "country"
} 

Utest4a()
{
	echo "=================================================================="
	echo "Utest4a: recreate the rest admin user"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/user/$APPUSER1 \
		-d role=REST_ADMIN \
		-d password=$APPPASS1 \
		-d email=$APPUSER1@yahoo.com \
		-d country=US \
		-d first_name=new \
		-d "last_name=new one" )
		shouldpass command "<user>"
}

Utest5()
{
	echo "=================================================================="
	echo "Utest5: SHOULD ERROR. Try to delete rest admin user with no login "
	echo "=================================================================="
	command=(curl -k -X DELETE $URL/user/$APPUSER1)
	shouldfail command
}

Utest6()
{
	echo "=================================================================="
	echo "Utest6: SHOULD ERROR. Try to delete $APPUSER1 user with $APPUSER1 login - delete is cipres admin only fn."
	echo "=================================================================="
	command=(curl -k -u $APPUSER1:$APPPASS1 -X DELETE $URL/user/$APPUSER1)
	shouldfail command
}



Utest7()
{
	echo "=================================================================="
	echo "Utest7: Create another rest admin user $APPUSER2 "
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/user/$APPUSER2 \
		-d role=REST_ADMIN 	\
		-d password=$APPPASS2 \
		-d first_name=first2  \
		-d last_name=last2  \
		-d country=US  \
		-d email=$APPUSER2@yahoo.com )
	shouldpass command "<username>$APPUSER2</username>"
}


Utest8()
{
	echo "=================================================================="
	echo "Utest8: SHOULD ERROR. update $APPUSER1 with wrong login:"
	echo "=================================================================="
	command=(curl -k -u $APPUSER2:$APPPASS2 $URL/user/$APPUSER1 \
		-d role=REST_ADMIN 	\
		-d first_name=newfirstname) 
	shouldfail command "AuthorizationException"
}

Utest9()
{
	echo "=================================================================="
	echo "Utest9: SHOULD ERROR. update $APPUSER1 without login:"
	echo "=================================================================="
	command=(curl -k $URL/user/$APPUSER1 \
		-d role=REST_ADMIN 	\
		-d first_name=newfirstname)
	shouldfail command "AuthorizationException|AuthenticationException"
}

# Not sure about this test.  Which fields are required on update?
# I'm not sure any are.
Utest10()
{
	echo "=================================================================="
	echo "Utest10: SHOULD ERROR. update $APPUSER1, missing info."
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/user/$APPUSER1 \
		-d password=$APPPASS1)
	shouldfail command "Validation Error"
}

Utest11()
{
	echo "=================================================================="
	echo "Utest11: Get $APPUSER2 , with cipres admin credentials"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/user/$APPUSER2) 
	shouldpass command "<username>$APPUSER2</username>"
}

Utest12()
{
	echo "=================================================================="
	echo "Utest12: Get $APPUSER2 , with $APPUSER2 credentials"
	echo "=================================================================="
	command=(curl -k -u $APPUSER2:$APPPASS2  $URL/user/$APPUSER2) 
	shouldpass command "<username>$APPUSER2</username>"
}

Utest13()
{
	echo "=================================================================="
	echo "Utest13: SHOULD ERROR. Get $APPUSER2 , with another users credentials"
	echo "=================================================================="
	command=(curl -k -u $APPUSER1:$APPPASS1  $URL/user/$APPUSER2) 
	shouldfail command "AuthorizationException"
}

Utest14()
{
	echo "=================================================================="
	echo "Utest14: SHOULD ERROR. Get $APPUSER2 , with invalid password"
	echo "=================================================================="
	command=(curl -k -u $APPUSER2:bogus $URL/user/$APPUSER2) 
	shouldfail command "AuthorizationException|AuthenticationException"
}

Utest15()
{
	echo "=================================================================="
	echo "Utest15: SHOULD ERROR. Get $APPUSER2 , without credentials"
	echo "=================================================================="
	command=(curl -k $URL/user/$APPUSER2) 
	# shouldfail command "AuthorizationException|AuthenticationException"
}

Utest16()
{
	echo "=================================================================="
	echo "Utest16: List all users, with cipres admin creds.  Look for $APPUSER1 in output. "
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/user/) 
	shouldpass command "$APPUSER1"
}

Utest16a()
{
	echo "=================================================================="
	echo "Utest16a: List all users, with cipres admin creds. Look for $APPUSER2 in output"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/user/) 
	shouldpass command "$APPUSER2"
}


Utest16b()
{
	error=0
	echo "\n=================================================================="
	echo "Utest16b: Make sure we have an umbrella user created: $GUIDEMO_APP.terri33"
	echo "=================================================================="
    command=(curl -k -u "$GUIDEMO_ADMIN:$TESTPASS" \
        -H "cipres-appkey:$GUIDEMO_APPKEY"  \
        -H "cipres-eu:terri33" \
        -H "cipres-eu-email:terri33@yahooyahoo.com" \
        -H "cipres-eu-institution:sdsc" \
        "$URL/job/$GUIDEMO_APP.terri33" \
    )
	if ! shouldpass command ; then
		echo "TEST FAILED"
		error=1
	fi
	echo "=================================================================="
	echo "Utest16b: List all users, with cipres admin creds. Look for $GUIDEMO_APP.terri33 is in output"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/user/) 
	if ! shouldpass command "$GUIDEMO_APP.terri33" ; then
		echo "TEST FAILED"
		error=1
	fi
	return $error
}

Utest18()
{
	echo "=================================================================="
	echo "Utest18: List users having REST_USER role .  Look for  user $APPUSER2."
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/user?filter=role:REST_USER) 
	shouldpass command "$APPUSER2"
}

Utest18a()
{
	error=0
	echo "=================================================================="
	echo "Utest18a: List users having REST_USER role.  SHOULD NOT HAVE $GUIDEMO_APP.terri33."
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/user?filter=role:REST_USER) 
	if shouldpass command "$GUIDEMO_APP.terri33" ; then
		error=1
		echo "We found $GUIDEMO_APP.terri33 but it should NOT have been there."
	fi
	return $error
}

Utest19()
{
	echo "=================================================================="
	echo "Utest19: SHOULD ERROR. List users, without credentials. "
	echo "=================================================================="
	command=(curl -k $URL/user) 
	shouldfail command "AuthorizationException|AuthenticationException"
}



##########################################################################################
# TEST APPLICATION MANAGEMENT API - CREATE/UPDATE
##########################################################################################
Atest1()
{
	echo "\n=================================================================="
	echo "Atest1: cipresadmin login, create or update $UAPP1 for $APPUSER1 user "
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/application/$APPUSER1/$UAPP1 \
		-d "longname=morpho bank" \
		-d authtype=UMBRELLA \
		-d website=http://www.morphobank.org/index.php/LoginReg/form/showRegister/1)
	shouldpass command "<application>"

}

Atest2()
{
	echo "\n=================================================================="
	echo "Atest2: cipresadmin login, activate, should be active."
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/application/$APPUSER1/$UAPP1 \
		-d activate=true)
	shouldpass command "<active>true</active>"
}

Atest3()
{
	echo "\n=================================================================="
	echo "Atest3: cipresadmin login, deactivate, should show active=false"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/application/$APPUSER1/$UAPP1 \
		-d activate=false)
	shouldpass command "<active>false</active>"
}

Atest4()
{
	echo "\n=================================================================="
	echo "Atest4: cipresadmin login, delete $UAPP1 "
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  -X DELETE $URL/application/$APPUSER1/$UAPP1 )
	shouldpass command
}

Atest5()
{
	echo "\n=================================================================="
	echo "Atest5: cipresadmin list apps, make sure $UAPP1  is gone.  TODO: VERIFY MANUALLY."
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS   $URL/application)
	shouldpass command
}

Atest6()
{
	echo "\n=================================================================="
	echo "Atest6: cipresadmin recreate $UAPP1 "
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/application/$APPUSER1/$UAPP1 \
		-d "longname=morpho bank" \
		-d authtype=UMBRELLA \
		-d website=http://www.morphobank.org/index.php/LoginReg/form/showRegister/1)
	shouldpass command
}

Atest7()
{
	error=0

	echo "\n=================================================================="
	echo "Atest7a: $APPUSER1 login, create $DAPP1 app.  Should error, cipresadmin is required to create or update apps. "
	echo "=================================================================="

	command=(curl -k -u $APPUSER1:$APPPASS1 $URL/application/$APPUSER1/$DAPP1 \
		-d "longname=someone's fancy scripts" \
		-d authtype=DIRECT \
		-d "comment=not much to say." )
	if ! shouldfail command "Authorization" ; then
		echo "TEST FAILED"
		error=1
	fi
		
	echo "\n=================================================================="
	echo "Atest7b: cipres admin login, create $DAPP1 app. " 
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/application/$APPUSER1/$DAPP1 \
		-d "longname=someone's fancy scripts" \
		-d authtype=DIRECT \
		-d "comment=not much to say." )
	if ! shouldpass command ; then
		echo "TEST FAILED"
		error=1
	fi
	return $error
}

Atest8()
{
	error=0
	echo "\n=================================================================="
	echo "Atest8a: $APPUSER1 login, generate new key for $DAPP1. Should fail, need to be cipresadmin"
	echo "=================================================================="
	command=(curl -k -u $APPUSER1:$APPPASS1 $URL/application/$APPUSER1/$DAPP1  -d generateId=true)
	if ! shouldfail command ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Atest8b: cipres admin login, generate new key for $DAPP1. VERIFY MANUALLY"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/application/$APPUSER1/$DAPP1  -d generateId=true)
	if ! shouldpass command ; then
		echo "TEST FAILED"
		error=1
	fi
	return $error
}

Atest9()
{
	echo "\n=================================================================="
	echo "Atest9: SHOULD ERROR. $APPUSER1 login, try to delete $DAPP1 app. Cipres admin only fn."
	echo "=================================================================="
	command=(curl -k  -u $APPUSER1:$APPPASS1 -X DELETE $URL/application/$APPUSER1/$DAPP1)
	shouldfail command "AuthorizationException"
}

Atest10()
{
	echo "\n=================================================================="
	echo "Atest10: SHOULD ERROR. $APPUSER1 login, activate $DAPP1 app. Cipres admin only fn. "
	echo "=================================================================="
	command=(curl -k -u $APPUSER1:$APPPASS1 $URL/application/$APPUSER1/$DAPP1  -d activate=true)
	shouldfail command "AuthorizationException"
}

Atest11()
{
	echo "\n=================================================================="
	echo "Atest11: SHOULD ERROR. wrong login, create or update $DAPP1 app "
	echo "=================================================================="
	command=(curl -k -u $APPUSER2:$APPPASS1  $URL/application/$APPUSER1/$DAPP1 \
		-d "longname=someone's fancy scripts" \
		-d authtype=DIRECT \
		-d "comment=not much to say." )
	shouldfail command
}

Atest12()
{
	echo "\n=================================================================="
	echo "Atest12: SHOULD ERROR. no login, create or update $DAPP1 app "
	echo "=================================================================="
	command=(curl -k  $URL/application/$APPUSER1/$DAPP1 \
		-d "longname=someone's fancy scripts" \
		-d authtype=DIRECT \
		-d "comment=not much to say." )
	shouldfail command
}


Atest13()
{
	echo "\n=================================================================="
	echo "Atest13: SHOULD ERROR. Omit the appname from applications create/update:"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/application/$APPUSER1 -d longname=foo)
	shouldfail command
}

Atest14()
{
	echo "\n=================================================================="
	echo "Atest14: SHOULD ERROR. Omit the username from applications create/upate:"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/application -d longname=foo)
	shouldfail command
}

Atest15()
{
	echo "\n=================================================================="
	echo "Atest15: List apps "
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/application)
	shouldpass command
}

##########################################################################################
# TEST APPLICATION MANAGEMENT API - GET
##########################################################################################
Gtest1()
{
	echo "\n=================================================================="
	echo "Gtest1: Login as cipresadmin and list all apps  - full info, should have $UAPP1 and $DAPP1"
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  $URL/application/)
	shouldpass command
}

Gtest2()
{
	echo "\n=================================================================="
	echo "Gtest2: No login and list all apps - gives limited info"
	echo "=================================================================="
	command=(curl -k $URL/application/)
	shouldpass command
}

Gtest3()
{
	echo "\n=================================================================="
	echo "Gtest3: Login as regular user and list all apps - gives limited info"
	echo "=================================================================="
	command=(curl -k -u $PYCIPRES_EU:$TESTPASS  $URL/application/)
	shouldpass command
}

Gtest4()
{
	echo "\n=================================================================="
	echo "Gtest4: No login, list DIRECT apps - gives limited info, should see $DAPP1 but not $UAPP1"
	echo "=================================================================="
	command=(curl -k   $URL/application?authtype=DIRECT)
	shouldpass command
}

Gtest5()
{
	echo "\n=================================================================="
	echo "Gtest5: \n Use $APPUSER1 credentials to GET his application, $UAPP1."
	echo "Should give full info."
	echo "=================================================================="
	command=(curl -k -u $APPUSER1:$TESTPASS  $URL/application/$APPUSER1/$UAPP1 ) 
	shouldpass command "<app_id>"
}

Gtest6()
{
	echo "\n=================================================================="
	echo "Gtest6: Try to use $APPUSER2 credentials to GET application, $UAPP1, which"
	echo "belongs to $APPUSER1.  SHOULD FAIL because username in url doesn't match "
	echo "username in credentials."
	echo "=================================================================="
	command=(curl -k -u $APPUSER2:$TESTPASS  $URL/application/$APPUSER1/$UAPP1 ) 
	shouldfail command "Authorization"

	echo "\n=================================================================="
	echo "Gtest6a: Try to use $APPUSER2 credentials to GET application, $UAPP1, which"
	echo "belongs to $APPUSER1, but put $APPUSER2 in the url instead of $APPUSER1.  SHOULD FAIL"
	echo "because user doesn't own the app."
	echo "=================================================================="
	command=(curl -k -u $APPUSER2:$TESTPASS  $URL/application/$APPUSER2/$UAPP1 ) 
	shouldfail command 
}

Gtest7()
{
	error=0

	echo "\n=================================================================="
	echo "Gtest7: Use cipresadmin credentials to update his application, $UAPP1."
	echo "=================================================================="
	command=(curl -k -u $APPUSER1:$TESTPASS  $URL/application/$APPUSER1/$UAPP1 \
		-d longname=barbar ) 
	if ! shouldfail command "Authorization" ; then
		echo "TEST FAILED"
		error=1
	fi

	echo "\n=================================================================="
	echo "Gtest7: Use cipresadmin credentials to update his application, $UAPP1."
	echo "=================================================================="
	command=(curl -k -u $AUSER:APASS $URL/application/$APPUSER1/$UAPP1 \
		-d longname=barbar ) 
	if shouldpass command "barbar" ; then
		echo "TEST FAILED"
		error=1
	fi
}

Gtest8()
{
	echo "\n=================================================================="
	echo "Gtest8: Use $APPUSER2 credentials to try to update another users app, $UAPP1. SHOULD FAIL."
	echo "=================================================================="
	command=(curl -k -u $APPUSER2:$TESTPASS  $URL/application/$APPUSER1/$UAPP1 \
		-d longname=goshgolly ) 
	shouldfail command "Authorization"

	echo "\n=================================================================="
	echo "Gtest8a: Use $APPUSER2 credentials to try to update another users app, $UAPP1."
	echo "But put $APPUSER2 in the url instead of $APPUSER1.  Will be interpreted as an"
	echo "attempt to create an app, since $APPUSER2/$UAPP1 doesn't exist, but will give"
	echo "an error, since there already is an app with that name."
	echo "=================================================================="
	command=(curl -k -u $APPUSER2:$TESTPASS  $URL/application/$APPUSER2/$UAPP1 \
		-d longname=goshgolly ) 
	shouldfail command
}

Gtest9()
{
	echo "\n=================================================================="
	echo "Gtest9: Use $APPUSER1 credentials to get a list of his apps." 
	echo "=================================================================="
	command=(curl -k -u $APPUSER1:$TESTPASS $URL/application/$APPUSER1) 
	shouldpass command

	echo "\n=================================================================="
	echo "Gtest9a: Use cipresadmin credentials to get a list of $APPUSER1's apps." 
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/application/$APPUSER1) 
	shouldpass command
}

Gtest10()
{
	echo "\n=================================================================="
	echo "Gtest10: Use $APPUSER2 credentials to get a list of $APPUSER1's apps. SHOULD FAIL" 
	echo "=================================================================="
	command=(curl -k -u $APPUSER2:$TESTPASS $URL/application/$APPUSER1) 
	shouldfail command

	echo "\n=================================================================="
	echo "Gtest10a: Use NO credentials to get a list of $APPUSER1's apps. SHOULD FAIL" 
	echo "=================================================================="
	command=(curl -k $URL/application/$APPUSER1) 
	shouldfail command
}


Gtest100()
{
	echo "\n=================================================================="
	echo "Gtest100: Delete $APPUSER1 user. Should delete associated apps, $UAPP1  and  $DAPP1."
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS  -X DELETE $URL/user/$APPUSER1)
	shouldpass command
}

Gtest101()
{
	echo "\n=================================================================="
	echo "Gtest101: Make sure $UAPP1 and $DAPP1 are gone.  TODO: VERIFY MANUALLY"
	echo "=================================================================="
	command=(curl -k $URL/application/)
	shouldpass command
}



setup
runtests Utest1 Utest2 Utest3 Utest4 Utest4a Utest5 Utest6 Utest7 Utest8 Utest9 \
Utest11 Utest12 Utest13 Utest14 Utest15 Utest16 Utest16a Utest16b \
Utest18 Utest18a Utest19 \
Atest1 Atest2 Atest3 Atest4 Atest5 \
Atest6 Atest7 Atest8 Atest9 Atest10 \
Atest11 Atest12 Atest13 Atest14 Atest15 \
Gtest1 Gtest2 Gtest3 Gtest4 Gtest5 Gtest6 \
Gtest7 Gtest8 Gtest9 Gtest10 \
Gtest100 Gtest101


