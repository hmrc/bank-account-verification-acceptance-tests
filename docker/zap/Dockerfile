FROM owasp/zap2docker-stable:2.9.0

# We temporarily change back to root to perform additional configuration
USER root

#RUN apk add --update --no-cache openssl
RUN wget -P plugin/ https://github.com/zaproxy/zap-extensions/releases/download/pscanrulesBeta-v21/pscanrulesBeta-beta-21.zap
RUN wget -P plugin/ https://github.com/zaproxy/zap-extensions/releases/download/ascanrulesBeta-v27/ascanrulesBeta-beta-27.zap
RUN wget -P plugin/ https://raw.githubusercontent.com/h3xstream/burp-retire-js/gh-pages/releases/zap/retirejs-alpha-3.0.2.zap
RUN sed -i 's/SET\ FILES\ CACHE\ SIZE\ 10000/SET\ FILES\ CACHE\ SIZE\ 100000/g' /zap/db/zapdb.script

USER zap
ENV ZAP_PORT 11000
