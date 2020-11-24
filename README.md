Bank Account Verification Acceptance Tests
================================

Browser based acceptance uk.gov.hmrc.integration.tests for address lookup user. 

## Running the tests

Prior to executing the tests ensure you have:
 - Docker - Required for the services, and useful if you want to run a browser (Chrome or Firefox) inside a container 
 - Appropriate [drivers installed](#installing-local-driver-binaries) - to run tests against locally installed Browser
 - Installed/configured [service manager](https://github.com/hmrc/service-manager).  

You will need a mongodb instance running locally for the services to connect to.  If you do not have mongodb installed locally you can run it in docker 

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


Then execute the `run-specs.sh` script:
    
    ./run-specs.sh <browser-driver>

The `run-specs.sh` script defaults to the locally installed `chrome` driver binary.  For a complete list of supported param values, see:
 - `src/test/resources/application.conf` for **environment** 
 - [webdriver-factory](https://github.com/hmrc/webdriver-factory#2-instantiating-a-browser-with-default-options) for **browser-driver**

## Running tests against a containerised browser - on a developer machine

The script `./run-browser-with-docker.sh` can be used to start a Chrome or Firefox container on a developer machine. 
The script requires `remote-chrome` or `remote-firefox` as an argument.

Read more about the script's functionality [here](run-browser-with-docker.sh).

To run against a containerised Chrome browser:

```bash
./run-browser-with-docker.sh remote-chrome 
./run-specs.sh remote-chrome
```

`./run-browser-with-docker.sh` is **NOT** required when running in a CI environment. 

## Installing local driver binaries

This project supports UI test execution using Firefox (Geckodriver) and Chrome (Chromedriver) browsers. 

See the `drivers/` directory for some helpful scripts to do the installation work for you.  They should work on both Mac and Linux by running the following command:

    ./installGeckodriver.sh <operating-system> <driver-version>
    or
    ./installChromedriver <operating-system> <driver-version>

- *<operating-system>* defaults to **linux64**, however it also supports **macos**
- *<driver-version>* defaults to **0.21.0** for Gecko/Firefox, and the latest release for Chrome.  You can, however, however pass any version available at the [Geckodriver](https://github.com/mozilla/geckodriver/tags) or [Chromedriver](http://chromedriver.storage.googleapis.com/) repositories.

**Note 1:** *You will need to ensure that you have a recent version of Chrome and/or Firefox installed for the later versions of the drivers to work reliably.*

**Note 2** *These scripts use sudo to set the right permissions on the drivers so you will likely be prompted to enter your password.*

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

