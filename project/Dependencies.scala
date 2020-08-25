import sbt._

object Dependencies {

  val test = Seq(
    "org.scalatest"           %% "scalatest"          % "3.2.0"   % Test,
    "org.scalatestplus"       %% "selenium-3-141"     % "3.2.0.0" % Test,
    "com.vladsch.flexmark"    %  "flexmark-all"       % "0.35.10" % Test,
    "com.github.nscala-time"  %% "nscala-time"        % "2.24.0"  % Test,
    "uk.gov.hmrc"             %% "webdriver-factory"  % "0.+"     % Test,
    "uk.gov.hmrc"             %% "zap-automation"     % "2.+"     % Test,
    "io.findify"              %% "s3mock"             % "0.2.5"   % Test
  )
}
