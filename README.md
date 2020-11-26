Bank Account Verification Acceptance Tests
================================

Browser based acceptance tests for the Bank AccountVerification service. 

# Running the specs

Prior to executing the tests ensure you have: 
 - Appropriate webdriver binaries installed to run tests against locally installed Browser (If you don't have these you can use docker versions)
 - MongoDB installed and running (If you don't have this you can use a docker version)
 - Installed/configured [service manager](https://github.com/hmrc/service-manager).  

## Start the local services

If you don't have mongodb installed locally you can run it in docker using the following command

    docker run -d --rm --name mongodb -p 27017-27019:27017-27019 mongo:4

To start services locally, run the following:
    
    sm --start BANK_ACCOUNT_VERIFICATION -r --appendArgs '{
      "BANK_ACCOUNT_REPUTATION": [
        "-J-Dmicroservice.services.creditsafe.endpoint=http://localhost:9000/Match",
        "-J-Dmicroservice.services.callvalidate.endpoint=http://localhost:9000/callvalidateapi",
        "-J-Dmicroservice.services.surepay.hostname=http://localhost:9000/surepay/",
        "-J-Dmicroservice.services.surepay.enabled=true",
        "-J-DDev.auditing.consumer.baseUri.port=9000",
        "-J-DDev.auditing.consumer.baseUri.host=localhost",
        "-J-DDev.auditing.enabled=true",
        "-J-DProd.proxy.proxyRequiredForThisEnvironment=false",
        "-J-Dmicroservice.services.eiscd.aws.endpoint=http://localhost:8001",
        "-J-Dmicroservice.services.eiscd.aws.bucket=txm-dev-bacs-eiscd",
        "-J-Dmicroservice.services.eiscd.aws.accesskeyid=AKIAIOSFODNN7EXAMPLE",
        "-J-Dmicroservice.services.eiscd.aws.secretkey=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
        "-J-Dmicroservice.services.modcheck.aws.endpoint=http://localhost:8001",
        "-J-Dmicroservice.services.modcheck.aws.bucket=txm-dev-bacs-modcheck",
        "-J-Dmicroservice.services.modcheck.aws.accesskeyid=AKIAIOSFODNN7EXAMPLE",
        "-J-Dmicroservice.services.modcheck.aws.secretkey=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
        "-J-DDev.auditing.enabled=true",
        "-J-Dmicroservice.services.thirdPartyCache.endpoint=http://localhost:9899/cache",
        "-J-Dmicroservice.services.surepay.cache.enabled=true"
      ],
      "BANK_ACCOUNT_REPUTATION_THIRD_PARTY_CACHE": [
        "-J-Dcontrollers.confidenceLevel.uk.gov.hmrc.bankaccountreputationthirdpartycache.controllers.CacheController.needsLogging=true"
      ],
      "BANK_ACCOUNT_VERIFICATION_FRONTEND": [
        "-J-Dbankaccountreputation.validateBankDetails.url=http://localhost:9871/v2/validateBankDetails",
        "-J-Dauditing.consumer.baseUri.port=9000",
        "-J-Dauditing.consumer.baseUri.host=localhost",
        "-J-Dauditing.enabled=true"
      ]
    }'

## Running specs

Execute the `run-specs.sh` script:
    
    ./run-specs.sh

The `run-specs.sh` script defaults to the locally installed `chrome` driver binary.  For a complete list of supported param values, see:
 - `src/test/resources/application.conf` for **environment** 
 - [webdriver-factory](https://github.com/hmrc/webdriver-factory#2-instantiating-a-browser-with-default-options) for **browser-driver**

## Running specs using a containerised browser - on a developer machine

The script `./run-locally-with-docker.sh` can be used to start a Chrome or Firefox container on a developer machine. 

Read more about the script's functionality [here](run-locally-with-docker.sh), or invoke `./run-locally-with-docker.sh -h`.

To run against a containerised Chrome browser:

```bash
./run-locally-with-docker.sh -browser chrome
./run-specs.sh remote-chrome
```

***Note:** `./run-locally-with-docker.sh` should **NOT** be used when running in a CI environment!*

## Running ZAP specs - on a developer machine

Before you can use the `./run-locally-with-docker.sh` script to start up a zap container, you will need to build it locally using:

```bash
./build-zap-container.sh
```

Once the container has been successfully built, run the following commands.

```bash
./run-locally-with-docker.sh -browser chrome -zap
./run-zap-specs.sh
``` 

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

