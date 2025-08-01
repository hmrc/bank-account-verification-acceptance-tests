import sbt.*

object Dependencies {

  val scalatestVersion = "3.2.19"

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"         %% "scalatest"              % scalatestVersion,
    "org.scalatestplus"     %% "selenium-4-21"          % s"$scalatestVersion.0",
    "com.vladsch.flexmark"   % "flexmark-all"           % "0.64.8",
    "uk.gov.hmrc"           %% "ui-test-runner"         % "0.48.0",
    "org.playframework"     %% "play-json"              % "3.0.5",
    "org.playframework"     %% "play-ahc-ws-standalone" % "3.0.7",
    "org.mock-server"        % "mockserver-netty"       % "5.15.0",
    "software.amazon.awssdk" % "s3"                     % "2.32.10",
    "org.assertj"            % "assertj-core"           % "3.27.3"
  ).map(_ % Test)
}
