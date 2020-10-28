#!/usr/bin/env bash

DEFAULT_BROWSER=remote-chrome
ZAP_VERSION="2.9.0"
ZAP_CONTAINER="artefacts.tax.service.gov.uk/build-zap:${ZAP_VERSION}-latest"
BROWSER_TYPE=${1:-$DEFAULT_BROWSER}

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
# Defines the CONTAINER variable from the argument passed to the script
#######################################
if [ "${BROWSER_TYPE}" = "remote-chrome" ]; then
  CONTAINER="artefacts.tax.service.gov.uk/chrome-with-rinetd:83.0.4103.61-latest"
elif [ "${BROWSER_TYPE}" = "remote-firefox" ]; then
  CONTAINER="artefacts.tax.service.gov.uk/firefox-with-rinetd:76.0.1-latest"
else
  echo "ERROR: Browser type not recognised. Re-run the script with the option remote-chrome or remote-firefox."
  exit 1
fi

#######################################
# Pulls the CONTAINER image from artifactory and runs the container with the specified options.
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

docker pull ${CONTAINER} &&
  docker run -d --rm --name "${BROWSER_TYPE}" \
    -p 4444:4444 -p 5900:5900 \
    -e PORT_MAPPINGS="$port_mappings" \
    -e TARGET_IP='host.docker.internal' \
    ${CONTAINER} &&
  docker run -d --rm --name "zap-${ZAP_VERSION}" \
  -p 11000:11000 ${ZAP_CONTAINER}
