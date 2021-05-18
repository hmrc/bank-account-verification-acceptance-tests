#!/usr/bin/env bash

set -euo pipefail

CHROME_CONTAINER="artefacts.tax.service.gov.uk/chrome-with-rinetd:latest"
FIREFOX_CONTAINER="artefacts.tax.service.gov.uk/firefox-with-rinetd:latest"

function kill_containers() {
  if docker ps | grep remote-chrome; then
    echo "Killing remote-chrome container..."
    docker kill remote-chrome
  else
    echo "Unable to find remote-chrome container"
  fi
  if docker ps | grep remote-firefox; then
    echo "Killing remote-firefox container..."
    docker kill remote-firefox
  else
    echo "Unable to find remote-firefox container"
  fi
  exit 1
}

function start_browser() {
  #######################################
  # Pulls the BROWSER image from artifactory and runs the container with the specified options.
  #
  # The latest version of the docker images are available at:
  # https://artefacts.tax.service.gov.uk/artifactory/webapp/#/artifacts/browse/tree/General/chrome-with-rinetd
  # https://artefacts.tax.service.gov.uk/artifactory/webapp/#/artifacts/browse/tree/General/firefox-with-rinetd
  #######################################

  docker pull ${BROWSER_CONTAINER}
  if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    docker run \
      -d \
      --rm \
      --name "${BROWSER_TYPE}" \
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
      --name "${BROWSER_TYPE}" \
      -p 4444:4444 \
      -p 5900:5900 \
      -e PORT_MAPPINGS="$port_mappings" \
      -e TARGET_IP='host.docker.internal' \
      ${BROWSER_CONTAINER}
  fi
  exit 0
}

DisplayHelp() {
  echo
  echo "This script will download and start/stop browser containers."
  echo
  echo "Syntax: ./$(basename "$0") [--remote-chrome | --remote-firefox | --stop | -h]"
  echo
  echo "options:"
  echo
  echo "--remote-chrome   Start a chrome container"
  echo "--remote-firefox  Start a firefox container"
  echo "--stop            Stop any running browser containers"
  echo "-h                Print this help text."
  echo
  exit 1
}

if [ $# -eq 0 ]; then
  DisplayHelp
fi

#######################################
# Requires services under test running via Service Manager
# Initializes port_mappings with all running application ports using the Service Manager status command.
#######################################
port_mappings=$(sm -s | grep PASS | awk '{ print $12"->"$12 }' | paste -sd "," -)

#######################################
#  Add a port mapping for ZAProxy that will be applied to the browser container
#######################################
port_mappings="$port_mappings,11000->11000"

while [ -n "$1" ]; do
  case "$1" in
  --remote-chrome)
    BROWSER_CONTAINER="${CHROME_CONTAINER}"
    BROWSER_TYPE="remote-chrome"
    start_browser
    ;;
  --remote-firefox)
    BROWSER_CONTAINER="${FIREFOX_CONTAINER}"
    BROWSER_TYPE="remote-firefox"
    start_browser
    ;;
  --stop)
    kill_containers
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

