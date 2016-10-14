#!/usr/bin/env python

#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
#

import os
CONFIGFILE = os.path.expandvars("${SDK_VERSIONS}/airavata-client.properties")
import sys, ConfigParser, argparse, time

parser = argparse.ArgumentParser(description='Airavata client wrapper script to '
                                             'submit remote jobs to cipres/nsg submit.py')
parser.add_argument("-commandline", help="Command line arguments to be passed to remote submit.py")
parser.add_argument("-r", "--resource", choices=['comet','stampede'], default='comet',
                    help="Abbreviated Compute Resource Name: comet, stampede")
#parser.add_argument("-id", default='ind123', help="Compute Resource Allocation")
#parser.add_argument("-url", help="Monitoring URL to be called back by application upon execution")
parser.add_argument("-c", "--healthcheck", action="store_true", help="Perform a health check to Airavata Server")
parser.add_argument("-v", "--verbose", action="store_true", help="Print verbose logging")

args = parser.parse_args()

# Read Airavata Client properties
airavataConfig = ConfigParser.RawConfigParser()
airavataConfig.read(CONFIGFILE)

host = airavataConfig.get('AiravataServer', 'host')
port = airavataConfig.getint('AiravataServer', 'port')
append_tuple_string = airavataConfig.get('ExperimentProperties', 'append_tuple')
for append_item in eval(append_tuple_string):
    sys.path.append(append_item)

from apache.airavata.api import Airavata
from apache.airavata.model.experiment.ttypes import ExperimentModel, UserConfigurationDataModel, ExperimentType
from apache.airavata.model.application.io.ttypes import InputDataObjectType
from apache.airavata.model.scheduling.ttypes import ComputationalResourceSchedulingModel
from apache.airavata.model.security.ttypes import AuthzToken
from apache.airavata.model.status.ttypes import ExperimentState

from thrift import Thrift
from thrift.transport import TSocket, TSSLSocket, TTransport
from thrift.protocol import TBinaryProtocol

# Create transport object. Comment and uncomment TLS or nonTLS according to server configuration
# transport = TSocket.TSocket(host, port)
transport = TSSLSocket.TSSLSocket(host, port, "True", \
     airavataConfig.get('AiravataServer', 'server_ca_cert'), \
     airavataConfig.get('AiravataServer', 'client_key'), \
     airavataConfig.get('AiravataServer', 'server_cert'))

# Use Buffered Protocol to speedup over raw sockets
transport = TTransport.TBufferedTransport(transport)

# Airavata currently uses Binary Protocol
protocol = TBinaryProtocol.TBinaryProtocol(transport)

# Create a Airavata client to use the protocol encoder
airavataClient = Airavata.Client(protocol)

# Connect to Airavata Server
transport.open()

# Create a dummy OAuth2 Token object
oauthDummyToken =  AuthzToken("default-token")

if args.verbose:
    print("Connecting to Airavata Server on host {}, port {}".format(host, port))
    print("Using server certificate {}, "
          "server CA certificate {}, client key {}"
          .format(airavataConfig.get('AiravataServer', 'server_cert'), \
                  airavataConfig.get('AiravataServer', 'server_ca_cert'), \
                  airavataConfig.get('AiravataServer', 'client_key')))

if args.healthcheck:
    print 'Connection to Airavata Server version {} succeeded.'.format(airavataClient.getAPIVersion(oauthDummyToken))

gateway_id = airavataConfig.get('GatewayProperties', 'gateway_id')

# create ExperimentModel for the experiment
experiment = ExperimentModel()
experiment.gatewayId = gateway_id
experiment.projectId = airavataConfig.get('ExperimentProperties', 'project_id')
experiment.experimentType = ExperimentType.SINGLE_APPLICATION
experiment.userName = "nsgtest"
experiment.experimentName = "NSG Test Experiment"
experiment.description = "Testing experiment executions"
experiment.executionId = airavataConfig.get('ApplicationProperties', 'application_id')

if (args.resource == 'comet'):
    compute_host_id = airavataConfig.get('ApplicationProperties', 'comet_host_id')
    queue_name = 'compute'
elif (args.resource == 'stampede'):
    compute_host_id = airavataConfig.get('ApplicationProperties', 'stampede_host_id')
    queue_name = 'normal'

# Create a computational resource scheduling model
scheduling = ComputationalResourceSchedulingModel()
scheduling.resourceHostId = compute_host_id
scheduling.queueName = queue_name
scheduling.totalCPUCount = 1
scheduling.nodeCount = 1
scheduling.wallTimeLimit = 5

# Create a User configuration data model
userconfig = UserConfigurationDataModel()
userconfig.computationalResourceScheduling = scheduling
userconfig.airavataAutoSchedule = False
userconfig.overrideManualScheduledParams = False
experiment.userConfigurationData  = userconfig

application_inputs = airavataClient.getApplicationInputs(oauthDummyToken,
                                airavataConfig.get('ApplicationProperties', 'application_id'))
for application_input in application_inputs:
    if application_input.name == "Commandline-Args":
        application_input.value = args.commandline
    # if application_input.name == "Allocation-Id":
    #     application_input.value = args.id
    # elif application_input.name == "Monitoring-URL":
    #     application_input.value = args.url
    # elif application_input.name == "Commandline-Args":
    #     application_input.value = args.commandline
experiment.experimentInputs = application_inputs

# Create experiment
exp_id = airavataClient.createExperiment(oauthDummyToken, gateway_id, experiment)

# launch experiment
airavataClient.launchExperiment(oauthDummyToken, exp_id, airavataConfig.get('GatewayProperties','cred_token_id'))
if args.verbose:
    print('Experiment id {} launched successfully to Airavata'.format(exp_id))

while (1):
    experiment = airavataClient.getExperiment(oauthDummyToken, exp_id)
    expStatusInt = experiment.experimentStatus.state
    expStatus = ExperimentState._VALUES_TO_NAMES[experiment.experimentStatus.state]
    if args.verbose:
        print 'Current Experiment Status is: ' , expStatus
    if expStatusInt == ExperimentState.COMPLETED or expStatusInt == ExperimentState.CANCELED \
            or expStatusInt == ExperimentState.FAILED:
        break
    time.sleep(1)

jobdetailsreturn = airavataClient.getJobDetails(oauthDummyToken, exp_id)
#print "jobdetailsreturn (%s)" % (jobdetailsreturn,)
jobdetails = airavataClient.getJobDetails(oauthDummyToken, exp_id)[0]

if args.verbose:
    print 'Exit Code of submission is: ', jobdetails.exitCode

if (jobdetails.exitCode == 0):
    print 'Job submitted successfully'
    sys.stdout.write(jobdetails.stdOut)
    sys.stderr.write(jobdetails.stdErr)
    exit(0)
else:
    sys.stdout.write(jobdetails.stdOut)
    sys.stderr.write(jobdetails.stdErr)
    exit(127)

if (expStatusInt == ExperimentState.COMPLETED):
    print 'Job submitted successfully'
    # Close Connection to Airavata Server
    transport.close()
    exit(0)
else:
    print 'Job submition failed, please check stdout, stderr for details.'
    # Close Connection to Airavata Server
    transport.close()
    exit(127)

