#!/bin/bash

# 
# Call these methods  as: shouldpass arrayvarname [string_to_match]
# See: http://stackoverflow.com/questions/18631337/bash-why-wont-command-substitution-work
# and: http://stackoverflow.com/questions/16461656/bash-how-to-pass-array-as-an-argument-to-a-function
#
# Originally tried to pass string var to shouldpass/shouldfail but couldn't get it to work when curl
# command had arguments that contained spaces.
#
# First argument is command to run, next is text to find in output.
# I think 2nd argument can be a regular expression, eg, "AuthorizationException|AuthenticationException" seems to work.

# This is for running tooltest.py and other commands, but not our curl tests.
shouldpass()
{
	name=$1[@]
	a=("${!name}")

	echo "Running: ${a[@]}"

	results=$( "${a[@]}" )
	retval=$?

	# Show output of the command
	echo "$results"

	# Retval from command should be 0
	if [[ $retval != 0 ]]; then 
		return $retval
	fi

	# If text to match is supplied, see if it matches.
	if [[ "$2" == "" ||  "$results" =~ $2 ]] ; then
		return 0
	fi
	return 2
}

# Optional 3rd argument is name of an xml element to extract into variable named EXTRACTED.
# Give name of element w/o surrounding angle brackets, for example, just "user" or "jobHandle".
# Don't use 3rd argument unless the command you're running returns xml.
shouldpass_curl()
{
	name=$1[@]
	a=("${!name}")

	echo "Running: ${a[@]}"

	results=$( "${a[@]}" )
	retval=$?
	echo "$results"

	# Retval from command should be 0
	if [[ $retval != 0 ]]; then 
		return $retval
	fi

	# Returned xml should not contain an error element when running curl command.
	if [[ "$results" =~ "<error>"  ]] ; then
		return 1
	fi
	# If text to match is supplied, see if it matches.
	if [[ "$2" == "" ||  "$results" =~ $2 ]] ; then
		#If element to extract is supplied, extract and assign to variable.
		if [[ "$3" != "" ]] ; then
			EXTRACTED=$( echo "$results" | grep "<$3" | cut -f2 -d">" | cut -f1 -d"<" )
		fi
		return 0
	fi
	return 2
}

# Use with tooltest.py and similar commands but not with our curl tests.
# Optional 2nd argument, string to match,  must be found or it's considered an error
shouldfail()
{
	name=$1[@]
	a=("${!name}")
	echo "Running: ${a[@]}"
	results=$( "${a[@]}" )
	retval=$?
	echo "$results"

	if [[ $retval == 0 ]] ; then 
		echo "test that should return an error exit status didn't"
		return 1
	fi

	# If additional text to match is supplied, see if it matches.
	if [[ "$2" == "" ||  "$results" =~ $2 ]] ; then
		return 0
	else
		echo "expected output - $2 - not found"
	fi
	return 2
}

shouldfail_curl()
{
	name=$1[@]
	a=("${!name}")

	echo "Running: ${a[@]}"
	results=$( "${a[@]}" )
	retval=$?
	echo "$results"

	# Retval from curl command should be 0 - we're not expecting curl failures. 
	# But retval from tooltest should be non-zero since it returns non zero on validation errors.
	if [[ $retval != 0 ]] ; then 
		echo "curl failure - not an expected error"
		return $retval
	fi

	# returned xml MUST  contain an error element
	if [[ ! "$results" =~ "<error>"  ]] ; then
		return 1
	fi

	# If additional text to match is supplied, see if it matches.
	if [[ "$2" == "" ||  "$results" =~ $2 ]] ; then
		return 0
	else
		echo "expected output - $2 - not found"
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




