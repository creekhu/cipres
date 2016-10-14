# Where to find debug output:

# rest client web app doesn't use log4j yet, output goes to catalina.out

# rest service logs to cipresrest.log and tools.log in directory specified
# by process_worker_logs in cipresrest/pom.xml.  Should be same value specified
# in sdk properties file so that dameon logs and rest service logs end up
# in the same directory.

deploy_sdk()
{
	scripts="process_worker_run daemonize loadResultsD recoverResultsD checkJobsD piseEval userTgAllocation userCanSubmit \
	taskInspector recoverLocalAbeD recoverLocalLonestarD importTgusage tgusage.sh convertEncoding.sh userDelete \
	sdkrun submitJobD"
	tilde=`echo ~`
	timestamp=`date +%F.%T`
	latest=$SDK_VERSIONS/$timestamp
	mkdir $latest 
	for s in $scripts; do
		cp ../../sdk/target/scripts/$s  $latest
		chmod +x $latest/$s
	done
	cp ~/.m2/repository/org/ngbw/sdk/2.0/sdk-2.0-jar-with-dependencies.jar $latest 
	rm -f $SDK_VERSIONS/process_worker_run $SDK_VERSIONS/current
	ln -s $latest/process_worker_run $SDK_VERSIONS/process_worker_run
	ln -s $latest $SDK_VERSIONS/current


	# make sure the path we just deployed to is what we'll run we say "process_worker_run".
	if [ "`which process_worker_run`" !=  "$SDK_VERSIONS/process_worker_run" ]; then
		echo "Path should be configured such that 'process_worker_run' executes $SDK_VERSIONS/process_worker_run"
		echo "but 'which process_worker_run' returns `which process_worker_run`."
		return 1
	fi
	if [ "`which piseEval`" !=  "$SDK_VERSIONS/current/piseEval" ]; then
		echo "Path should be configured such that 'piseEval' executes $SDK_VERSIONS/current/piseEval"
		echo "but 'which piseEval' returns `which piseEval`."
		return 1
	fi
	return 0
}


$CATALINA_HOME/bin/shutdown.sh 
export A1=cipresrest
export A2=restclient



if [ "$1" == "skipsdk" ]; then
	echo "===================================================="
	echo "Not building sdk, not restarting daemons."  
	echo "Pausing 10 sec to let tomcat exit."
	echo "===================================================="
	sleep 10
else
	(cd ../sdk && mvn -Denv=cipres-dev clean install)
	retval3=$?
	if [ $retval3 != 0 ]; then
		echo build SDK failure;
		exit 1;
	fi
	deploy_sdk
	if [ $? -eq 0 ]; then
		echo "===================================================="
		echo "Rebuilt SDK.  Restarting daemons"
		echo "===================================================="
		submitJobD restart
		checkJobsD restart
		loadResultsD restart
	else
		echo deploy SDK failure;
		exit 1;
	fi
fi

mvn -Denv=cipres-dev clean install 

rm -rf $CATALINA_HOME/webapps/$A1 $CATALINA_HOME/webapps/$A1.war 
rm -rf $CATALINA_HOME/webapps/$A2 $CATALINA_HOME/webapps/$A2.war 
rm -rf $CATALINA_HOME/work
rm  -f $CATALINA_HOME/logs/*
rm -f ~/cipresrest.log

cp $A1/target/$A1.war $CATALINA_HOME/webapps
cp $A2/target/$A2.war $CATALINA_HOME/webapps
$CATALINA_HOME/bin/startup.sh 
