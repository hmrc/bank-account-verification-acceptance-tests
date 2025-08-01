#!/bin/bash -e

BROWSER=${1:-chrome}
ENVIRONMENT=${2:-local}
HEADLESS=${3:-true}
SPECS=${4:-"uk.gov.hmrc.ui.specs.*"}

sbt clean -Dbrowser=${BROWSER} -Denvironment=${ENVIRONMENT} -Dbrowser.option.headless=${HEADLESS} "testOnly ${SPECS}" testReport

