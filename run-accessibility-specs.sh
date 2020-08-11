#!/usr/bin/env bash

BROWSER_TYPE=$1
ENV=$2

if [ -z "$BROWSER_TYPE" ]; then
    echo "BROWSER_TYPE value not set. This should be passed as a parameter"
    exit 1
fi

sbt -Dbrowser=${BROWSER_TYPE} -Denv=${ENV:=local} "testOnly -- -n uk.gov.hmrc.acceptance.tags.Accessibility"
