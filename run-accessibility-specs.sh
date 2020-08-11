#!/usr/bin/env bash

DEFAULT_BROWSER=chrome
BROWSER_TYPE=$1
ENV=$2

if [ -z "$BROWSER_TYPE" ]; then
    echo "BROWSER_TYPE value not set, defaulting to $DEFAULT_BROWSER..."
    echo ""
fi

sbt -Dbrowser=${BROWSER_TYPE:=$DEFAULT_BROWSER} -Denv=${ENV:=local} "testOnly -- -n uk.gov.hmrc.acceptance.tags.Accessibility -l uk.gov.hmrc.acceptance.tags.Zap"
