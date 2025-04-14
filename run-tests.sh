#!/bin/bash -e

BROWSER=$1
ENVIRONMENT=$2

sbt clean -Dbrowser="${BROWSER:=chrome}" -Denvironment="${ENVIRONMENT:=local}" "testOnly uk.gov.hmrc.ui.specs.v3.StrideCheckPersonalAccountSpec" testReport
