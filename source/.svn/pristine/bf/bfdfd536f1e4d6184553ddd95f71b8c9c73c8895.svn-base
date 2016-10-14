To run tests and manually verify results, create a file (say "config.txt") to
set env vars listed below and source the file then look at the comment in 
each ".sh" test file to see what it should do and then run it.

BE CAREFUL NOT TO ADD YOUR CfONFIG FILE TO SVN.  ITS A HUGE PAIN IF YOU
COMMIT A FILE WITH PASSWORDS TO SVN.

At minimum, set TUS_URL.   
$CREDS will be passed to curl.  You only need to set it if you've
configured the service to use authentication.  If using cipres
direct auth (that is basic auth + cipres-appkey header) then 
set USER, PASS and APPKEY and use the CREDS line shown below.


export TUS_URL='http://localhost:7070/cipresrest/v1/tmpfiles'
export USER=
export PASS=
export APPKEY=
export CREDS="-u $USER:$PASS -H cipres-appkey:$KEY"

# export TUS_URL=http://master.tus.io:8080/files/


