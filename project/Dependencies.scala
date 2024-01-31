import sbt.*

object Dependencies {

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"       %% "scalatest"              % "3.2.17"   % Test,
    "org.scalatestplus"   %% "selenium-4-12"          % "3.2.17.0" % Test,
    "com.vladsch.flexmark" % "flexmark-all"           % "0.62.2"   % Test,
    "uk.gov.hmrc"         %% "ui-test-runner"         % "0.15.0"   % Test,
    "com.typesafe.play"   %% "play-json"              % "2.9.2"    % Test,
    "com.typesafe.play"   %% "play-ahc-ws-standalone" % "2.1.10"   % Test,
    "io.findify"          %% "s3mock"                 % "0.2.6"    % Test,
    "org.mock-server"      % "mockserver-netty"       % "5.12.0"   % Test,
    "org.assertj"          % "assertj-core"           % "3.23.1"   % Test
  )
}
