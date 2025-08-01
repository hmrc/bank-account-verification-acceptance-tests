Bank Account Verification Acceptance Tests
================================

Browser based acceptance tests for the Bank Account Verification service.

# Running the acceptance tests

Prior to executing the tests ensure you have:

- Docker - If you want to run a browser (Chrome or Firefox) inside a container, or a ZAP container
- MongoDB installed and running (If you don't have this you can use a docker version)
- LocalStack installed and running to mock AWS functionality for S3 [install guide](https://github.com/localstack/localstack?tab=readme-ov-file#install)
- Installed/configured [service manager](https://github.com/hmrc/service-manager).

## Start the local services

If you don't have mongodb installed locally you can run it in docker using the following command

    docker run -d --rm --name mongodb -p 27017-27019:27017-27019 mongo:4

To start services locally, run this helper script: `.start_services.sh`. Alternatively, you can manually run the following command:

    sm2 --start BANK_ACCOUNT_VERIFICATION -r --appendArgs '{
      "BANK_ACCOUNT_REPUTATION": [
        "-Dplay.http.router=testOnlyDoNotUseInAppConf.Routes",
        "-Dmicroservice.services.modulr.protocol=http",
        "-Dmicroservice.services.modulr.host=localhost",
        "-Dmicroservice.services.modulr.port=6001",
        "-Dmicroservice.services.modulr.enabled=true",
        "-Dmicroservice.services.modulr.business.cache.enabled=false",
        "-Dmicroservice.services.modulr.personal.cache.enabled=false",
        "-Dauditing.consumer.baseUri.port=6001",
        "-Dauditing.consumer.baseUri.host=localhost",
        "-Dauditing.enabled=true",
        "-Dproxy.proxyRequiredForThisEnvironment=false",
        "-Dmicroservice.services.eiscd.aws.endpoint=http://localhost:4566",
        "-Dmicroservice.services.eiscd.aws.bucket=txm-dev-bacs-eiscd",
        "-Dmicroservice.services.eiscd.cache-schedule.initial-delay=86400",
        "-Dmicroservice.services.modcheck.cache-schedule.initial-delay=86400",
        "-Dmicroservice.services.thirdPartyCache.endpoint=http://localhost:9899/cache",
        "-Dmicroservice.services.access-control.endpoint.verify.enabled=true",
        "-Dmicroservice.services.access-control.endpoint.verify.allow-list.0=bars-acceptance-tests",
        "-Dmicroservice.services.access-control.endpoint.verify.allow-list.1=some-upstream-service",
        "-Dmicroservice.services.access-control.endpoint.verify.allow-list.2=bank-account-reputation-frontend",
        "-Dmicroservice.services.access-control.endpoint.verify.allow-list.3=bank-account-verification-frontend",
        "-Dmicroservice.services.access-control.endpoint.validate.enabled=true",
        "-Dmicroservice.services.access-control.endpoint.validate.allow-list.0=bars-acceptance-tests",
        "-Dmicroservice.services.access-control.endpoint.validate.allow-list.1=some-upstream-service",
        "-Dmicroservice.services.access-control.endpoint.validate.allow-list.2=bank-account-reputation-frontend",
        "-Dmicroservice.services.access-control.endpoint.validate.allow-list.3=bank-account-verification-frontend",
        "-Dmicroservice.services.modcheck.useLocal=true"
      ],
      "BANK_ACCOUNT_REPUTATION_THIRD_PARTY_CACHE": [
        "-Dcontrollers.confidenceLevel.uk.gov.hmrc.bankaccountreputationthirdpartycache.controllers.CacheController.needsLogging=true"
      ],
      "BANK_ACCOUNT_VERIFICATION_FRONTEND": [
        "-Dmicroservice.hosts.allowList.1=localhost",
        "-Dauditing.consumer.baseUri.port=6001",
        "-Dauditing.consumer.baseUri.host=localhost",
        "-Dauditing.enabled=true",
        "-Dmicroservice.services.access-control.enabled=true",
        "-Dmicroservice.services.access-control.allow-list.0=bavfe-acceptance-tests"
      ],
      "BANK_ACCOUNT_REPUTATION_FRONTEND": [
        "-Dauditing.enabled=true",
        "-Dauditing.consumer.baseUri.port=6001",
        "-Dauditing.consumer.baseUri.host=localhost"
      ]
    }'

## Tests

Run tests as follows:

* Argument `<browser>` must be `chrome`, `edge`, or `firefox`. (defaults to `chrome`)
* Argument `<environment>` must be `local`, `dev`, `qa` or `staging`. (defaults to `local`)
* Argument `<headless>` `true` or `false` (defaults to `true`)

```bash
./run-tests.sh <browser> <environment> <headless>
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

