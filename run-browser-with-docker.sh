#!/usr/bin/env bash

#######################################
# The script starts remote-chrome or remote-firefox docker container for running Browser tests on a developer machine only.
# The container directs TCP requests from the container to the host machine enabling testing services running via Service Manager.
# WARNING: Do not use this script in the Jenkins Continuous Integration environment
#
# Arguments:
#   remote-chrome or remote-firefox
#
# Output:
#   Starts  chrome or firefox docker containers from chrome-with-rinetd or firefox-with-rinetd image respectively
#######################################

#######################################
# Requires services under test running via Service Manager
# Initializes port_mappings with all running application ports using the Service Manager status command.
# Appends ZAP_PORT 11000 to ./run-zap-spec.sh
#######################################
port_mappings=$(sm -s | grep PASS | awk '{ print $12"->"$12 }' | paste -sd "," -)
port_mappings="$port_mappings,11000->11000"

# Alternatively, port_mappings can be explicitly initialised as below:
#port_mappings="9032->9032,9250->9250,9080->9080"

#######################################
# Defines the BROWSER variable from the argument passed to the script
#######################################

DEFAULT_BROWSER=remote-chrome
BROWSER_TYPE=$1

if [ -z "$BROWSER_TYPE" ]; then
    echo "BROWSER_TYPE value not set, defaulting to $DEFAULT_BROWSER..."
    echo ""
fi

if [ "${BROWSER_TYPE}" = "remote-firefox" ]; then
  BROWSER="artefacts.tax.service.gov.uk/firefox-with-rinetd:latest"
elif [ "${BROWSER_TYPE:=$DEFAULT_BROWSER}" = "remote-chrome" ]; then
  BROWSER="artefacts.tax.service.gov.uk/chrome-with-rinetd:latest"
else
  echo "${BROWSER_TYPE} is unknown, exiting..."
  exit 1
fi

#######################################
# Pulls the BROWSER image from artifactory and runs the container with the specified options.
#
# Accepted Environment Variables:
# PORT_MAPPINGS: List of the ports of the services under test.
# TARGET_IP: IP of the host machine. For Mac this is 'host.docker.internal'. For linux this is 'localhost'
#
# The latest version of the docker images are available at:
# https://artefacts.tax.service.gov.uk/artifactory/webapp/#/artifacts/browse/tree/General/chrome-with-rinetd
# https://artefacts.tax.service.gov.uk/artifactory/webapp/#/artifacts/browse/tree/General/firefox-with-rinetd
#
# NOTE:
# When using on a Linux OS, add "--net=host" to the docker run command.
#######################################

docker pull ${BROWSER}
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
  docker run \
    -d \
    --rm \
    --name "${BROWSER_TYPE:=$DEFAULT_BROWSER}" \
    --net=host \
    -p 4444:4444 \
    -p 5900:5900 \
    -e PORT_MAPPINGS="$port_mappings" \
    -e TARGET_IP='host.docker.internal' \
    ${BROWSER}
else
  docker run \
    -d \
    --rm \
    --name "${BROWSER_TYPE:=$DEFAULT_BROWSER}" \
    -p 4444:4444 \
    -p 5900:5900 \
    -e PORT_MAPPINGS="$port_mappings" \
    -e TARGET_IP='host.docker.internal' \
    ${BROWSER}
fi
