#!/usr/bin/env bash

set -euo pipefail

DAST_PROJECT_NAME="dast-config-manager"
FILTERS_FILENAME="alert-filters.json"

function start_zap() {
  echo -e "Calculating directories...\n"

  pushd "$(dirname "$0")"
  PROJECT_DIRECTORY=~0
  pushd "$(dirname "${PROJECT_DIRECTORY}")"
  CHECKOUT_DIRECTORY=~0
  STARTING_DIRECTORY=~2
  DAST_CONFIG_MANAGER_DIRECTORY="${CHECKOUT_DIRECTORY}/${DAST_PROJECT_NAME}"
  FILTERS_FILE="${PROJECT_DIRECTORY}/${FILTERS_FILENAME}"

  echo -e "\nUsing the following: \n"
  echo -e "Checkout dir = $CHECKOUT_DIRECTORY"
  echo -e "Project dir = $PROJECT_DIRECTORY"
  echo -e "Current dir = $STARTING_DIRECTORY"
  echo -e "DAST dir = $DAST_CONFIG_MANAGER_DIRECTORY \n"

  if [ ! -d "${DAST_CONFIG_MANAGER_DIRECTORY}" ]; then
    echo -e "Changing to checkout directory... \n"
    cd ${CHECKOUT_DIRECTORY}
    git clone "git@github.com:hmrc/${DAST_PROJECT_NAME}.git"
    cd ${DAST_CONFIG_MANAGER_DIRECTORY}
  else
    cd ${DAST_CONFIG_MANAGER_DIRECTORY}
    echo -e "\nMaking sure we have the latest code... \n"
    git pull --rebase
  fi

  echo -e "\nCollecting ports of services running in service manager... \n"
  COLLECT_SM_PORTS=$(sm -s | grep -E 'PASS|BOOT' | awk '{ print $12}' | tr "\n" " ")
  export ZAP_FORWARD_ENABLE="true"
  export ZAP_FORWARD_PORTS=$COLLECT_SM_PORTS
  echo -e "\nChecking to see if an alert-filters.json is available... \n"
  if [ -f "${FILTERS_FILE}" ]; then
    export ZAP_LOCAL_ALERT_FILTERS="${FILTERS_FILE}"
  fi
  echo -e "\nBuilding and starting docker container... \n"
  make local-zap-running

  cd ${STARTING_DIRECTORY}
  exit 0
}

function stop_zap() {
  echo -e "Calculating directories...\n"

  pushd "$(dirname "$0")"
  PROJECT_DIRECTORY=~0
  pushd "$(dirname "${PROJECT_DIRECTORY}")"
  CHECKOUT_DIRECTORY=~0
  STARTING_DIRECTORY=~2
  DAST_CONFIG_MANAGER_DIRECTORY="${CHECKOUT_DIRECTORY}/${DAST_PROJECT_NAME}"

  echo -e "\nUsing the following: \n"
  echo -e "Checkout dir = $CHECKOUT_DIRECTORY"
  echo -e "Project dir = $PROJECT_DIRECTORY"
  echo -e "Current dir = $STARTING_DIRECTORY"
  echo -e "DAST dir = $DAST_CONFIG_MANAGER_DIRECTORY \n"

  if docker ps | grep zap; then
    echo -e "\nStopping the ZAP docker container... \n"
    cd ${DAST_CONFIG_MANAGER_DIRECTORY}
    make local-zap-stop

    echo -e "\nCopying ZAP report... \n"
    cp -aR "${DAST_CONFIG_MANAGER_DIRECTORY}/target/dast-reports" "${PROJECT_DIRECTORY}/target/"
    echo -e "\nReport available at: file://${PROJECT_DIRECTORY}/target/dast-reports/index.html \n"
  else
    echo -e "\nNo ZAP container running, aborting... \n"
  fi

  cd ${STARTING_DIRECTORY}
  exit 0
}

DisplayHelp() {
  echo
  echo "This script will download and start/stop a zap container."
  echo
  echo "Syntax: ./$(basename "$0") [-0start | -0stop | -h]"
  echo
  echo "options:"
  echo
  echo "--start          Start a ZAProxy container"
  echo "--stop           Stop a ZAProxy container"
  echo "-h               Print this help text."
  echo
  exit 1
}

if [ $# -eq 0 ]; then
  DisplayHelp
fi

while [ -n "$1" ]; do
  case "$1" in
  --start)
    start_zap
    ;;
  --stop)
    stop_zap
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
