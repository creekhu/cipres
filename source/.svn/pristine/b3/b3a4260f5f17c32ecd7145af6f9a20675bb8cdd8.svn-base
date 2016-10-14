Update 1/29/2015: I created a python client library and example to take the
place of most of this code.  It's under rest_client_examples/examples/tooltest.py.
The only reasons I'm leaving any of this here are 1) we still need this to
create pycipres.conf and put it in under $SDK_VERSIONS and my new code
doesn't have a replacement for the batch/load testing yet.   I also modified
pom to stop copying the test cases to $SDK_VERSIONS - we aren't using them
there.


PYTHON and BASH CODE FOR TESTING the REST SERVICE

PREQUISITES:
	1. For cipres developers: before you can run this code, do a build and install 
	everything from cipres-build directory so that you have a cipres rest service 
	to run against.  Use the script "tl.sh".  By building together we pick up the 
	same url, log file location, for this code that we use for the service itself.

	2. Specific User and Application accounts must exist in the db for certain tests.  
	See cipresAdmin and testAndDemoAccounts scripts, both of which are installed to 
	$SDK_VERSIONS.  In other words, we have to register an Application for these
	tests to run as and a User who serves as the admin for that Application, and
	thats what testAndDemoAccounts takes care of.

	3. If you aren't a cipres developer and just want to use this code, I haven't tried it 
	that way, but what I *think* you need to do is:

		- define SDK_VERSIONS env var to point to a directory somewhere.
		- create pycipres.conf in $SDK_VERSIONS. Use conf/pycipres.conf as a template.
		- run python setup.py install

Back to cipres developer info:

Yes, it's weird to use a pom file with python code, but the pom file in pycipres just does 
resource filtering on conf/pycipres.conf and copies it to $SDK_VERSIONS/testdata.  
Pycipres.conf gives the url for the rest service, location of test data, etc can can easily
be read from bash or python code.

