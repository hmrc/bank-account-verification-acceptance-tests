
# Bank Account Verification acceptance tests

This is a placeholder README.md for a new repository

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

## Running the tests

#### Running locally

You will need a docker instance running on your machine (this can be done via docker) before usinf service manager to spin up the services.

```
docker run -p 27017-27019:27017-27019 mongo:4
sm --start BANK_ACCOUNT_VERIFICATION -r
```
To run all acceptance tests against local services do the following (You can use chrome, or firefox if you have ChromeDriver/Geckodriver saved to your PATH):
```
sbt -Dbrowser=chrome test
```

To exclude the tests tagged as accesibility, do the following:
```
sbt -Dbrowser=chrome 'testOnly -- -l uk.gov.hmrc.integration.tests.Accessibility'
```

