
$CATALINA_HOME/bin/shutdown.sh 
sleep 10
rm -rf $CATALINA_HOME/webapps/cipresrest $CATALINA_HOME/webapps/cipresrest.war $CATALINA_HOME/work
rm  $CATALINA_HOME/logs/*
rm ~/cipresrest.log


# (cd ../sdk && mvn -Denv=cipres-dev clean install)
# retval3=$?
# if [ $retval3 != 0 ]; then
	# echo build SDK failure;
	# exit 1;
# fi

mvn -Denv=cipres-dev clean package

cp target/cipresrest.war $CATALINA_HOME/webapps
$CATALINA_HOME/bin/startup.sh 
