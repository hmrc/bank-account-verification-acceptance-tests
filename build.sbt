name := "bank-account-verification-acceptance-tests"

organization := "uk.gov.hmrc"

scalaVersion := "2.12.12"

version := "0.1.0"

resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"
resolvers += "hmrc-releases" at "https://artefacts.tax.service.gov.uk/artifactory/hmrc-releases/"
resolvers += Resolver.bintrayRepo("hmrc", "releases")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % "test"
libraryDependencies += "org.scalatestplus" %% "selenium-3-141" % "3.2.0.0" % "test"
libraryDependencies += "com.vladsch.flexmark" % "flexmark-all" % "0.35.10" % "test"
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.24.0" % "test"
libraryDependencies += "uk.gov.hmrc" %% "webdriver-factory" % "0.+" % "test"
libraryDependencies += "uk.gov.hmrc" %% "zap-automation" % "2.+" % "test"

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-u", "target/test-reports")
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports/html-report")
testOptions in Test += Tests.Argument("-oD")

parallelExecution in Test := false

lazy val testSuite = (project in file("."))
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
