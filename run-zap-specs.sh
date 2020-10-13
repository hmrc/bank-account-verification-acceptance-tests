#!/usr/bin/env bash

# This script requires:
# 1. ZAP running in port 11000
# 2. By default it will try to connect to a containerised Chrome.

DEFAULT_BROWSER=remote-chrome
BROWSER_TYPE=$1

if [ -z "$BROWSER_TYPE" ]; then
    echo "BROWSER_TYPE value not set, defaulting to $DEFAULT_BROWSER..."
    echo ""
fi

sbt -Dbrowser=${BROWSER_TYPE:=$DEFAULT_BROWSER} -Dzap.proxy=true "testOnly -- -n uk.gov.hmrc.acceptance.tags.ZAP"

echo ""
echo "**** Running ZapSpec ****"
echo ""

sbt "testOnly uk.gov.hmrc.acceptance.spec.ZapSpec"
