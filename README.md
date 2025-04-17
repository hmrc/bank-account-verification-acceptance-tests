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

    sm2 --start BANK_ACCOUNT_VERIFICATION -r --appendArgs '{
      "BANK_ACCOUNT_REPUTATION": [
        "-J-Dplay.http.router=testOnlyDoNotUseInAppConf.Routes",
        "-J-Dmicroservice.services.modulr.protocol=http",
        "-J-Dmicroservice.services.modulr.host=localhost",
        "-J-Dmicroservice.services.modulr.port=6001",
        "-J-Dmicroservice.services.modulr.enabled=true",
        "-J-Dmicroservice.services.modulr.business.cache.enabled=false",
        "-J-Dmicroservice.services.modulr.personal.cache.enabled=false",
        "-J-Dauditing.consumer.baseUri.port=6001",
        "-J-Dauditing.consumer.baseUri.host=localhost",
        "-J-Dauditing.enabled=true",
        "-J-Dproxy.proxyRequiredForThisEnvironment=false",
        "-J-Dmicroservice.services.eiscd.aws.endpoint=http://0.0.0.0:6002",
        "-J-Dmicroservice.services.eiscd.aws.bucket=txm-dev-bacs-eiscd",
        "-J-Dmicroservice.services.eiscd.cache-schedule.initial-delay=86400",
        "-J-Dmicroservice.services.modcheck.cache-schedule.initial-delay=86400",
        "-J-Dmicroservice.services.thirdPartyCache.endpoint=http://localhost:9899/cache",
        "-J-Dmicroservice.services.access-control.endpoint.verify.enabled=true",
        "-J-Dmicroservice.services.access-control.endpoint.verify.allow-list.0=bank-account-verification-frontend",
        "-J-Dmicroservice.services.access-control.endpoint.validate.enabled=true",
        "-J-Dmicroservice.services.access-control.endpoint.validate.allow-list.0=bank-account-verification-frontend",
        "-J-Dmicroservice.services.modcheck.useLocal=true"
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

### Docker Selenium Grid

Confirm that [docker-selenium-grid](https://github.com/hmrc/docker-selenium-grid) is up-to-date and follow the provided [instructions](https://github.com/hmrc/docker-selenium-grid/blob/main/README.md).

## Tests

Run tests as follows:

* Argument `<browser>` must be `chrome`, `edge`, or `firefox`.
* Argument `<environment>` must be `local`, `dev`, `qa` or `staging`.

```bash
./run-tests.sh <browser> <environment>
```

## Scalafmt

Check all project files are formatted as expected as follows:

```bash
sbt scalafmtCheckAll scalafmtCheck
```

Format `*.sbt` and `project/*.scala` files as follows:

```bash
sbt scalafmtSbt
```

Format all project files as follows:

```bash
sbt scalafmtAll
```

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

