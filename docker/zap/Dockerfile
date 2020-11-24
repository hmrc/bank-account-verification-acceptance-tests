FROM owasp/zap2docker-stable:2.9.0

# Temporarily change back to root to perform additional configuration
USER root

# Add rinetd to enable port mappings support for mac host
RUN apt-get -y update
RUN apt-get -y install rinetd
COPY --chown=zap:zap port-mappings-wrapper.sh /zap/port-mappings-wrapper.sh
RUN chmod 755 /zap/port-mappings-wrapper.sh && touch /var/run/rinetd.pid && chown zap:zap /var/run/rinetd.pid

# Add HMRC specific plugins
RUN wget -P plugin/ https://github.com/zaproxy/zap-extensions/releases/download/pscanrulesBeta-v21/pscanrulesBeta-beta-21.zap
RUN wget -P plugin/ https://github.com/zaproxy/zap-extensions/releases/download/ascanrulesBeta-v27/ascanrulesBeta-beta-27.zap
RUN wget -P plugin/ https://raw.githubusercontent.com/h3xstream/burp-retire-js/gh-pages/releases/zap/retirejs-alpha-3.0.2.zap
RUN sed -i 's/SET\ FILES\ CACHE\ SIZE\ 10000/SET\ FILES\ CACHE\ SIZE\ 100000/g' /zap/db/zapdb.script

USER zap

ENV ZAP_PORT 11000

ENTRYPOINT ["/bin/bash", "/zap/port-mappings-wrapper.sh"]
