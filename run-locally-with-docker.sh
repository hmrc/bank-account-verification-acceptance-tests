#!/usr/bin/env bash

DEFAULT_BROWSER="chrome"
CHROME_CONTAINER="artefacts.tax.service.gov.uk/chrome-with-rinetd:latest"
FIREFOX_CONTAINER="artefacts.tax.service.gov.uk/firefox-with-rinetd:latest"
ZAP_CONTAINER="zap-with-rinetd:2.9.0-0.1.0"

DisplayHelp() {
  echo
  echo "This script will download and start zap/browser containers."
  echo
  echo "Syntax: ./$(basename "$0") [-zap | -browser | -browser chrome | -h]"
  echo
  echo "options:"
  echo
  echo "-zap                         Start a ZAProxy container"
  echo "-browser *(chrome|firefox)   Start a browser container (*You can optionally specify the browser type)"
  echo "-h                           Print this help text."
  echo
  exit 1
}

START_ZAP=false
START_BROWSER=false

#######################################
# Work out what actions this script needs to take based on user input
#######################################

if [ $# -eq 0 ]; then
  DisplayHelp
fi

while [ -n "$1" ]; do
  case "$1" in
  -zap)
    START_ZAP=true
    ;;
  -browser)
    START_BROWSER=true
    BROWSER_TYPE="$2"
    if [[ $BROWSER_TYPE != -* ]] && [[ ! -z "$BROWSER_TYPE" ]]; then
      shift
    else
      echo
      echo "Browser type not defined, defaulting to ${DEFAULT_BROWSER}..."
      BROWSER_TYPE="${DEFAULT_BROWSER}"
    fi
    if [ "${BROWSER_TYPE}" == "firefox" ]; then
      BROWSER_CONTAINER="${FIREFOX_CONTAINER}"
    elif [ "${BROWSER_TYPE}" == "chrome" ]; then
      BROWSER_CONTAINER="${CHROME_CONTAINER}"
    else
      echo "${BROWSER_TYPE} is unknown, exiting..."
      exit 1
    fi
    ;;
  -h)
    DisplayHelp
    ;;
  *)
    echo "Option '$1' not recognised..."
    DisplayHelp
    ;;
  esac
  shift
done

#######################################
# Requires services under test running via Service Manager
# Initializes port_mappings with all running application ports using the Service Manager status command.
#######################################
port_mappings=$(sm -s | grep PASS | awk '{ print $12"->"$12 }' | paste -sd "," -)

#######################################
# Currently expects you to have run `./build-zap-container.sh` to generate the zap container locally
#
# This will be updated in the future if this container is added to artifactory
#######################################

if [[ "$START_ZAP" == true ]]; then
  if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    docker run \
      -d \
      --rm \
      --name "zap" \
      --net=host \
      -p 11000:11000 \
      -u zap \
      -e PORT_MAPPINGS="$port_mappings" \
      -e TARGET_IP="localhost" \
      ${ZAP_CONTAINER} \
      -daemon \
      -host 0.0.0.0 \
      -port 11000 \
      -config "api.addrs.addr.name=.*" \
      -config "api.addrs.addr.regex=true" \
      -config "api.disablekey=true"
  else
    docker run \
      -d \
      --rm \
      --name "zap" \
      -u zap \
      -p 11000:11000 \
      -e PORT_MAPPINGS="$port_mappings" \
      -e TARGET_IP="host.docker.internal" \
      ${ZAP_CONTAINER} \
      -daemon \
      -host 0.0.0.0 \
      -port 11000 \
      -config "api.addrs.addr.name=.*" \
      -config "api.addrs.addr.regex=true" \
      -config "api.disablekey=true"
  fi

  #######################################
  #  Add a port mapping for ZAProxy that will be applied to the browser container (if started)
  #######################################
  port_mappings="$port_mappings,11000->11000"
fi

#######################################
# Pulls the BROWSER image from artifactory and runs the container with the specified options.
#
# The latest version of the docker images are available at:
# https://artefacts.tax.service.gov.uk/artifactory/webapp/#/artifacts/browse/tree/General/chrome-with-rinetd
# https://artefacts.tax.service.gov.uk/artifactory/webapp/#/artifacts/browse/tree/General/firefox-with-rinetd
#######################################

if [[ "$START_BROWSER" == true ]]; then
  docker pull ${BROWSER_CONTAINER}
  if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    docker run \
      -d \
      --rm \
      --name "remote-${BROWSER_TYPE}" \
      --net=host \
      -p 4444:4444 \
      -p 5900:5900 \
      -e PORT_MAPPINGS="$port_mappings" \
      -e TARGET_IP='localhost' \
      ${BROWSER_CONTAINER}
  else
    docker run \
      -d \
      --rm \
      --name "remote-${BROWSER_TYPE}" \
      -p 4444:4444 \
      -p 5900:5900 \
      -e PORT_MAPPINGS="$port_mappings" \
      -e TARGET_IP='host.docker.internal' \
      ${BROWSER_CONTAINER}
  fi
fi
