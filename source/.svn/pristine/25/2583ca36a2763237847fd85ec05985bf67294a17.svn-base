#!/bin/bash

# Runs some job submission tests using the pycipres application, and pycipres_eu user.
# Tests error handling.
# Only 2 CLUSTALW jobs should be successfully submitted.  Everything else has a parameter or other error.

if [[ $# < 2 ]] ; then
	echo "You need to specify a cipresadmin username and password on the commandline"
	exit 1
fi

source "./testfw.sh" 
APPID=$PYCIPRES_APPKEY
DATA=$SDK_VERSIONS/testdata/tooltests
USER=$PYCIPRES_EU
PASS=$TESTPASS

test0()
{
	error=0

	echo "=================================================================="
	echo "test0: SHOULD ERROR. Deactive app $PYCIPRES_APP, as cipresadmin, and try to submit. "
	echo "=================================================================="
	command=(curl -k -u $AUSER:$APASS $URL/application/$PYCIPRES_ADMIN/$PYCIPRES_APP -d "activate=false" ) 
	if  !shouldpass command "<application>"  ; then
		error=1
		echo deactive failed
	fi


	command=(curl -k -u $USER:$PASS -H "cipres-appkey:$APPID" $URL/job/$USER/ \
		-F "tool=CLUSTALW" \
		-F "metadata.clientJobId=100" \
		-F "input.infile_=@$DATA/CLUSTALW/CLUSTALW_in.txt" \
		-F "vparam.runtime_=.15" )
	if ! shouldfail command ; then
		error=1
		echo "submit should have failed but didn't"
	fi

	command=(curl -k -u $AUSER:$APASS $URL/application/$PYCIPRES_ADMIN/$PYCIPRES_APP -d "activate=true" ) 
	if ! shouldpass command "<application>" ; then
		error=1
		echo activate failed
	fi	

	return $error
}

test1()
{
	echo "=================================================================="
	echo "test1: submit a valid clustalw job "
	echo "=================================================================="
	command=(curl -k -u $USER:$PASS -H "cipres-appkey:$APPID" $URL/job/$USER/ \
		-F "tool=CLUSTALW" \
		-F "metadata.clientJobId=100" \
		-F "input.infile_=@$DATA/CLUSTALW/CLUSTALW_in.txt" \
		-F "vparam.runtime_=.15" )
	shouldpass command
}

test2()
{
	echo "=================================================================="
	echo "test2: SHOULD ERROR - empty runtime, missing infile_" 
	echo "=================================================================="
	command=(curl -k -u $USER:$PASS -H "cipres-appkey:$APPID" $URL/job/$USER/ \
		-F "tool=CLUSTALW" \
		-F "metadata.clientJobId=100" \
		-F "vparam.runtime_="  )
	shouldfail command
}

test3()
{
	# Assign two values to align_ property.  Demonstrates that only latter value is used.
	echo "=================================================================="
	echo "test3: SHOULD ERROR - 2 values assigned to actions_" 
	echo "=================================================================="
	command=(curl -k -u $USER:$PASS -H "cipres-appkey:$APPID" $URL/job/$USER/ \
		-F "tool=CLUSTALW" \
		-F "input.infile_=@$DATA/CLUSTALW/CLUSTALW_in.txt" \
		-F "vparam.runtime_=0.15" \
		-F "vparam.actions_=-align" \
		-F "vparam.actions_=foo" )
	shouldfail command "actions_"
}

test4()
{
	echo "=================================================================="
	echo "test4: Submit a list of values for hgapresidues. TODO: verify manually. "
	echo "Should send completion email to cipresrest@gmail.com"
	echo "=================================================================="
	command=(curl -k -u $USER:$PASS -H "cipres-appkey:$APPID" $URL/job/$USER/ \
		-F "tool=CLUSTALW" \
		-F metadata.clientJobId=101
		-F metadata.clientJobName="my first clustalw job"
		-F metadata.statusEmail=true
		-F metadata.emailAddress="cipresrest@gmail.com"
		-F "input.infile_=@$DATA/CLUSTALW/CLUSTALW_in.txt" \
		-F "vparam.runtime_=0.15" \
		-F "vparam.actions_=-align" \
		-F "vparam.hgapresidues_=G" \
		-F "vparam.hgapresidues_=P" \
		-F "vparam.hgapresidues_=Q" )
	shouldpass command
}

test5()
{
	echo "=================================================================="
	echo "test5: SHOULD ERROR.  Violates precond." 
	echo "=================================================================="
	# Violate a precondition, when action isn't -align you can't set matrix. 
	command=(curl -k -u $USER:$PASS -H "cipres-appkey:$APPID" $URL/job/$USER/ \
		-F "tool=CLUSTALW" \
		-F "input.infile_=@$DATA/CLUSTALW/CLUSTALW_in.txt" \
		-F "vparam.runtime_=0.15" \
		-F "vparam.actions_=-profile" \
		-F "vparam.matrix_= blosum" )
	shouldfail command "recondition"
}

test6()
{
	echo "=================================================================="
	echo "test6: SHOULD ERROR.  Put invalid value in list param matrix." 
	echo "=================================================================="
	# Set an invalid value for list selection (matrix)
	command=(curl -k -u $USER:$PASS -H "cipres-appkey:$APPID" $URL/job/$USER/ \
		-F "tool=CLUSTALW" \
		-F "input.infile_=@$DATA/CLUSTALW/CLUSTALW_in.txt" \
		-F "vparam.runtime_=0.15" \
		-F "vparam.actions_=-align" \
		-F "vparam.matrix_=-foo" )
	shouldfail command "matrix"
}

test7()
{
	echo "=================================================================="
	echo "test7: has bad precond for prot_matrix_spec and bad runtime value"
	echo "but we throw exception for bad precond before checking other things"
	echo "=================================================================="
	# Violate runhours ctrl 
	command=(curl -k -u $USER:$PASS -H "cipres-appkey:$APPID" $URL/job/$USER/ \
		-F "tool=RAXMLHPC2BB" \
		-F "input.infile_=@$DATA/RAXMLHPC2BB/RAXMLHPC2BB_in.txt" \
		-F "vparam.runtime_=200" \
		-F "vparam.datatype_=dna" \
		-F "vparam.prot_matrix_spec_=JTT" \
		-F "vparam.mlsearch_=1" \
		-F "vparam.use_bootstopping_=1" \
		-F "vparam.printbrlength_=0" )
	shouldfail command "prot_matrix_spec"
}

test8()
{
	echo "=================================================================="
	echo "test8: has bad runtime"
	echo "=================================================================="
	# Violate runhours ctrl 
	command=(curl -k -u $USER:$PASS -H "cipres-appkey:$APPID" $URL/job/$USER/ \
		-F "tool=RAXMLHPC2BB" \
		-F "input.infile_=@$DATA/RAXMLHPC2BB/RAXMLHPC2BB_in.txt" \
		-F "vparam.runtime_=200" \
		-F "vparam.datatype_=dna" \
		-F "vparam.mlsearch_=1" \
		-F "vparam.use_bootstopping_=1" \
		-F "vparam.printbrlength_=0" )
	shouldfail command "runtime"
}

runtests test1 test2 test3 test4 test5 test6 test7 test8


