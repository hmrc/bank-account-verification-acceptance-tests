Bank Account Verification Acceptance Tests
================================

Browser based acceptance tests for the Bank Account Verification service.

# Running the acceptance tests

Prior to executing the tests ensure you have:

- Docker - If you want to run a browser (Chrome or Firefox) inside a container, or a ZAP container
- MongoDB installed and running (If you don't have this you can use a docker version)
- LocalStack installed and running to mock AWS functionality for S3 [install guide](https://github.com/localstack/localstack?tab=readme-ov-file#install)  ('localstack start -d' if running via Homebrew)
- Installed/configured [service manager](https://github.com/hmrc/service-manager).

## Service Manager profile

These acceptance tests expect the backend services to be started via Service Manager using the `BANK_ACCOUNT_VERIFICATION` profile.

You can start everything with the helper script:

    ./start_services.sh

Or run the equivalent Service Manager command directly:

    sm2 --start BANK_ACCOUNT_VERIFICATION

(Additional JVM args are applied by `start_services.sh` via `--appendArgs` to configure the services for local UI testing and LocalStack S3.)

## Start the local services

If you don't have mongodb installed locally you can run it in docker using the following command

    docker run --rm -d -p 27017:27017 --name mongo percona/percona-server-mongodb:7.0

To start services locally, run this helper script: `./start_services.sh`. 

When running bank-account-verification-frontend locally, ensure you are using:
[run_for_ui_tests.sh](../bank-account-verification-frontend/run_for_ui_tests.sh) script to start the service so that it uses the correct configuration for local testing.

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
