#!/usr/bin/env bash

# This script requires:
# 1. ZAP running in port 11000
# 2. By default runs using a containerised Chrome.
#    Use the command './run-browser-with-docker.sh remote-chrome'  to start a containerised chrome on a local machine.

BROWSER_TYPE=$1
ENV=$2

sbt -Dbrowser=${BROWSER_TYPE:=remote-chrome} -Denv=${ENV:=local} -Dzap.proxy=true "testOnly -- -l uk.gov.hmrc.acceptance.tags.Accessibility"

echo "**** Running ZapSpec ****"

sbt "testOnly uk.gov.hmrc.acceptance.spec.ZapSpec"
