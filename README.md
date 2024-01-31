Bank Account Verification Acceptance Tests
================================

Browser based acceptance tests for the Bank Account Verification service.

# Running the acceptance tests

Prior to executing the tests ensure you have:

- Docker - If you want to run a browser (Chrome or Firefox) inside a container, or a ZAP container
- MongoDB installed and running (If you don't have this you can use a docker version)
- Installed/configured [service manager](https://github.com/hmrc/service-manager).

## Start the local services

If you don't have mongodb installed locally you can run it in docker using the following command

    docker run -d --rm --name mongodb -p 27017-27019:27017-27019 mongo:4

To start services locally, run the following:

    sm --start BANK_ACCOUNT_VERIFICATION -r --appendArgs '{
      "BANK_ACCOUNT_REPUTATION": [
        "-J-Dmicroservice.services.callvalidate.endpoint=http://localhost:6001/callvalidateapi",
        "-J-Dmicroservice.services.surepay.hostname=http://localhost:6001/surepay/",
        "-J-Dmicroservice.services.surepay.enabled=true",
        "-J-Dauditing.consumer.baseUri.port=6001",
        "-J-Dauditing.consumer.baseUri.host=localhost",
        "-J-Dauditing.enabled=true",
        "-J-Dproxy.proxyRequiredForThisEnvironment=false",
        "-J-Dmicroservice.services.eiscd.aws.endpoint=http://localhost:6002",
        "-J-Dmicroservice.services.eiscd.aws.bucket=txm-dev-bacs-eiscd",
        "-J-Dmicroservice.services.eiscd.aws.accesskeyid=EXAMPLEID",
        "-J-Dmicroservice.services.eiscd.aws.secretkey=EXAMPLEKEY",
        "-J-Dmicroservice.services.modcheck.aws.endpoint=http://localhost:6002",
        "-J-Dmicroservice.services.modcheck.aws.bucket=txm-dev-bacs-modcheck",
        "-J-Dmicroservice.services.modcheck.aws.accesskeyid=EXAMPLEID",
        "-J-Dmicroservice.services.modcheck.aws.secretkey=EXAMPLEKEY",
        "-J-Dmicroservice.services.thirdPartyCache.endpoint=http://localhost:9899/cache",
        "-J-Dmicroservice.services.surepay.cache.enabled=true",
        "-J-Dmicroservice.services.access-control.enabled=true",
        "-J-Dmicroservice.services.access-control.allow-list.0=bank-account-verification-frontend"
      ],
      "BANK_ACCOUNT_REPUTATION_THIRD_PARTY_CACHE": [
        "-J-Dcontrollers.confidenceLevel.uk.gov.hmrc.bankaccountreputationthirdpartycache.controllers.CacheController.needsLogging=true"
      ],
      "BANK_ACCOUNT_VERIFICATION_FRONTEND": [
        "-J-Dmicroservice.hosts.allowList.1=localhost",
        "-J-Dauditing.consumer.baseUri.port=6001",
        "-J-Dauditing.consumer.baseUri.host=localhost",
        "-J-Dauditing.enabled=true",
        "-J-Dmicroservice.services.access-control.enabled=true",
        "-J-Dmicroservice.services.access-control.allow-list.0=bavfe-acceptance-tests"
      ]
    }'

## Running specs

Execute the `run-tests.sh` script:

    ./run-tests.sh

The `run-tests.sh` script defaults to the locally installed `chrome` driver binary. For a complete list of supported param values, see:

- `src/test/resources/application.conf` for **environment**
- [webdriver-factory](https://github.com/hmrc/webdriver-factory#2-instantiating-a-browser-with-default-options) for **browser-driver**

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

