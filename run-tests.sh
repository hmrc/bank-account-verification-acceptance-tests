#!/bin/bash -e

BROWSER=${1:-chrome}
ENVIRONMENT=${2:-local}
HEADLESS=${3:-true}

sbt clean -Dbrowser=${BROWSER} -Denvironment=${ENVIRONMENT} -Dbrowser.option.headless=${HEADLESS} "testOnly uk.gov.hmrc.ui.specs.*" testReport

