#!/bin/bash

###################################################################################################
# CONSTANTS
###################################################################################################

# Cipres Admin User
AUSER=$1
APASS=$2


# AppAdmin User
APPUSER1=appadmin1

APPUSER2=appadmin2

# UMBRELLA APP, associated with AppAdmin User
UAPP1=umbrellaApp

# DIRECT APP user, associated with AppAdmin User
DAPP1=directApp

# Direct End users associated with DIRECT APP
EU1=eu1
EU1EMAIL=ciprestester5@gmail.com
EU2=eu2
EU2EMAIL=ciprestester6@gmail.com

T7EMAIL=ciprestester7@gmail.com
T8EMAIL=ciprestester8@gmail.com
T9EMAIL=ciprestester9@gmail.com
T10EMAIL=ciprestester10@gmail.com



###################################################################################################
# FUNCTIONS
###################################################################################################
config()
{
	set -a
	source $SDK_VERSIONS/testdata/pycipres.conf
	set +a

	APPPASS1=$TESTPASS
	APPPASS2=$TESTPASS
	EU1PASS=$TESTPASS
	EU2PASS=$TESTPASS

	pycipresAppKey
	pycipres2AppKey
	guidemoAppKey
	guidemo2AppKey
	echo "PYCIPRES_APPKEY=$PYCIPRES_APPKEY, PYCIPRES2_APPKEY=$PYCIPRES2_APPKEY, GUIDEMO_APPKEY=$GUIDEMO_APPKEY, GUIDEMO2_APPKEY=$GUIDEMO2_APPKEY,"
	if [[ -z $PYCIPRES_APPKEY || -z $GUIDEMO_APPKEY || -z GUIDEMO2_APPKEY || -z $PYCIPRES2_APPKEY ]]; then
		return 1
	fi
	return 0
}

pycipresAppKey()
{
	if [[ -z $AUSER || -z $APASS ]]; then
		echo "cipresadmin_ user, cipresadmin_pass must be first two command line arguments"
		exit 1
	fi
	PYCIPRES_APPKEY=`curl -k -u $AUSER:$APASS $URL/application/$PYCIPRES_ADMIN/$PYCIPRES_APP  | \
		grep '<app_id' | cut -f2 -d">"|cut -f1 -d"<"`
}

pycipres2AppKey()
{
	if [[ -z $AUSER || -z $APASS ]]; then
		echo "cipresadmin_ user, cipresadmin_pass must be first two command line arguments"
		exit 1
	fi
	PYCIPRES2_APPKEY=`curl -k -u $AUSER:$APASS $URL/application/$PYCIPRES_ADMIN/$PYCIPRES2_APP  | \
		grep '<app_id' | cut -f2 -d">"|cut -f1 -d"<"`


	echo "Running: curl -k -u $AUSER:$APASS $URL/application/$PYCIPRES_ADMIN/$PYCIPRES2_APP" 

	echo
	echo "PYCIPRES2_APPKEY is $PYCIPRES2_APPKEY"
	echo
}


guidemoAppKey()
{
	if [[ -z $AUSER || -z $APASS ]]; then
		echo "cipresadmin_ user, cipresadmin_pass must be first two command line arguments"
		exit 1
	fi
	GUIDEMO_APPKEY=`curl -k -u $AUSER:$APASS $URL/application/$GUIDEMO_ADMIN/$GUIDEMO_APP  | \
		grep '<app_id' | cut -f2 -d">"|cut -f1 -d"<"`
}

guidemo2AppKey()
{
	if [[ -z $AUSER || -z $APASS ]]; then
		echo "cipresadmin_ user, cipresadmin_pass must be first two command line arguments"
		exit 1
	fi
	GUIDEMO2_APPKEY=`curl -k -u $AUSER:$APASS $URL/application/$GUIDEMO2_ADMIN/$GUIDEMO2_APP  | \
		grep '<app_id' | cut -f2 -d">"|cut -f1 -d"<"`
}


# 
# Call this as: shouldpass arrayvarname [string_to_match]
# See: http://stackoverflow.com/questions/18631337/bash-why-wont-command-substitution-work
# and: http://stackoverflow.com/questions/16461656/bash-how-to-pass-array-as-an-argument-to-a-function
# Originally tried to pass string var to shouldpass/shouldfail but couldn't get it to work when curl
# command had arguments that contained spaces.
#
# Todo: make 2nd arg on, be flag arg pairs, where flags indicate whether pattern 
# must be found or must not be found.  Consider changing curl commands to return
# http header (-i) and check http status.
#
# First argument is command to run, next is text to find in output.
# Optional 3rd argument is name of element to extract into variable named EXTRACTED.
# Give name of element w/o surrounding angle brackets, for example, just "user" or "jobHandle".
shouldpass()
{

	name=$1[@]
	a=("${!name}")

	echo "Running: ${a[@]}"

	results=$( "${a[@]}" )
	retval=$?

	echo "$results"

	# Retval from curl should be 0
	if [[ $retval != 0 ]]; then 
		return $retval
	fi

	# Returned xml should not contain an error element
	if [[ "$results" =~ "<error>"  ]] ; then
		return 1
	fi

	# If text to match is supplied, see if it matches.
	if [[ "$2" == "" ||  "$results" =~ $2 ]] ; then
		#If element to extract is supplied, extract and assign to variable.
		# If element ends in ":", extract what is on rhs, otherwise, treat element
		# as a simple xml element.
		if [[ "$3" != "" ]] ; then
			if [[ $3 =~ .*: ]] ; then
				# echo "extracting header"
				TMP=$( echo "$results" | grep "$3" ) 

				# remove carriage returns (see bash parameter expansion, search and replace)
				TMP=${TMP//[$'\r']}
				# echo "TMP=$TMP"

				[[ $TMP =~ ^.*"$3"(.*)$ ]]
				EXTRACTED=${BASH_REMATCH[1]}
			else
				# echo "extracting xml element"
				EXTRACTED=$( echo "$results" | grep "<$3" | cut -f2 -d">" | cut -f1 -d"<" )
			fi
		fi
		return 0
	fi
	return 2
}

# String to match, if supplied, must be found or it's considered an error
# even if the result returned isn't an <error> object.
shouldfail()
{
	name=$1[@]
	a=("${!name}")

	echo "Running: ${a[@]}"

	results=$( "${a[@]}" )
	retval=$?

	echo "$results"

	# Retval from curl should be 0 - we're not expecting curl failures. 
	if [[ $retval != 0 ]]; then 
		echo "curl failure"
		return $retval
	fi

	# Returned xml MUST  contain an error element
	if [[ ! "$results" =~ "<error>"  ]] ; then
		return 1
	fi

	# If additional text to match is supplied, see if it matches.
	if [[ "$2" == "" ||  "$results" =~ $2 ]] ; then
		return 0
	fi
	return 2
}



runtests()
{
	passed=0
	failed=0
	for fn in $*; do
		if $fn ; then 
			let passed=passed+1
			echo "*************** PASS  *******************\n"
		else
			let failed=failed+1
			echo "*************** FAIL  *******************\n"
		fi
	done
	echo "$passed tests passed and $failed tests failed"
	return $failed
}


# Get vars from pycipres.conf
if ! config ; then
	echo "testfw.sh/config() failed."
	exit 1
fi


