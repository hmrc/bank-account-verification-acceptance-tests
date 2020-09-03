
# Bank Account Verification acceptance tests

This is a placeholder README.md for a new repository

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

## Running the tests

#### Running locally

You will need a docker instance running on your machine (this can be done via docker) before usinf service manager to spin up the services.

```
docker run -p 27017-27019:27017-27019 mongo:4
sm --start BANK_ACCOUNT_VERIFICATION -r --appendArgs '{"BANK_ACCOUNT_REPUTATION":["-J-Dmicroservice.services.creditsafe.endpoint=http://localhost:9000/Match", "-J-Dmicroservice.services.callvalidate.endpoint=http://localhost:9000/callvalidateapi", "-J-Dmicroservice.services.aws.endpoint=http://localhost:8001", "-J-Dmicroservice.services.aws.bucket=txm-dev-bacs-eiscd", "-J-DDev.auditing.consumer.baseUri.port=9000", "-J-DDev.auditing.consumer.baseUri.host=localhost", "-J-DDev.auditing.enabled=true", "-J-Dmicroservice.services.surepay.hostname=http://localhost:9000/surepay/", "-J-Dmicroservice.services.surepay.enabled=true", "-J-DProd.proxy.proxyRequiredForThisEnvironment=false", "-J-Dmicroservice.services.aws.accesskeyid=AKIAIOSFODNN7EXAMPLE", "-J-Dmicroservice.services.aws.secretkey=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY", "-J-Dmicroservice.services.aws.bucket=txm-dev-bacs-eiscd", "-J-DDev.auditing.traceRequests=true", "-J-DDev.auditing.enabled=true", "-J-Dakka.http.client.socket-options.tcp-keep-alive=false", "-J-Dmicroservice.services.thirdPartyCache.endpoint=http://localhost:9899/cache", "-J-Dmicroservice.services.surepay.cache.enabled=true"], "BANK_ACCOUNT_REPUTATION_THIRD_PARTY_CACHE": ["-J-Dcontrollers.confidenceLevel.uk.gov.hmrc.bankaccountreputationthirdpartycache.controllers.CacheController.needsLogging=true"], "BANK_ACCOUNT_VERIFICATION_FRONTEND":["-J-Dbankaccountreputation.validateBankDetails.url=http://localhost:9871/v2/validateBankDetails"]}'
```
To run all acceptance tests against local services do the following (You can use chrome, or firefox if you have ChromeDriver/Geckodriver saved to your PATH):
```
sbt -Dbrowser=chrome test
```

To exclude the tests tagged as accesibility, do the following:
```
sbt -Dbrowser=chrome 'testOnly -- -l uk.gov.hmrc.integration.tests.Accessibility'
```

