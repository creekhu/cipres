You need to set env vars
URL
CRA_USERNAME
PASSWORD
KEY
in order to run curl tests.  

All the tests can be run automatically from scigap/tests.  The script there takes
care of setting the env vars from ~/pycipres.conf, the same file tooltest.py uses
to get env info.

When added new tests, make sure they are executable, for example:
$ chmod +x test_newone
$ svn add test_newone
$ svn propset svn:executable test_newone
$ svn commit
