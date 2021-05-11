#!/usr/bin/env bash

DEFAULT_BROWSER=chrome
BROWSER_TYPE=$1
ENV=$2

if [ -z "$BROWSER_TYPE" ]; then
    echo "BROWSER_TYPE value not set, defaulting to $DEFAULT_BROWSER..."
    echo ""
fi

sbt -mem 8192 -Dbrowser=${BROWSER_TYPE:=$DEFAULT_BROWSER} -Denv=${ENV:=local} "testOnly -- -l \"uk.gov.hmrc.acceptance.tags.Accessibility uk.gov.hmrc.acceptance.tags.Excluded\""
