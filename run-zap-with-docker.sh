#!/usr/bin/env bash

ZAP_CONTAINER=zap-with-rinetd:2.9.0-0.1.0

#######################################
# Requires services under test running via Service Manager
# Initializes port_mappings with all running application ports using the Service Manager status command.
# Appends ZAP_PORT 11000 to ./run-zap-spec.sh
#######################################
port_mappings=$(sm -s | grep PASS | awk '{ print $12"->"$12 }' | paste -sd "," -)

docker run \
  -d \
  --rm \
  --name "zap" \
  -u zap \
  -p 11000:11000 \
  -e PORT_MAPPINGS="$port_mappings" \
  -e TARGET_IP="host.docker.internal" \
  ${ZAP_CONTAINER} \
  -daemon \
  -host 0.0.0.0 \
  -port 11000 \
  -config "api.addrs.addr.name=.*" \
  -config "api.addrs.addr.regex=true" \
  -config "api.disablekey=true"
