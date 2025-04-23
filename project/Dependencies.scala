import sbt.*

object Dependencies {

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"       %% "scalatest"              % "3.2.19"   % Test,
    "org.scalatestplus"   %% "selenium-4-12"          % "3.2.17.0" % Test,
    "com.vladsch.flexmark" % "flexmark-all"           % "0.64.8"   % Test,
    "uk.gov.hmrc"         %% "ui-test-runner"         % "0.45.0"   % Test,
    "com.typesafe.play"   %% "play-json"              % "2.10.6"   % Test,
    "com.typesafe.play"   %% "play-ahc-ws-standalone" % "2.2.11"   % Test,
    "com.adobe.testing"    % "s3mock"                 % "4.1.1"    % Test,
    "org.eclipse.jetty"    % "jetty-io"               % "12.0.19"  % Test,
    "org.eclipse.jetty"    % "jetty-client"           % "12.0.19"  % Test,
    "org.eclipse.jetty"    % "jetty-http"             % "12.0.19"  % Test,
    "org.eclipse.jetty"    % "jetty-util"             % "12.0.19"  % Test,
    "org.mock-server"      % "mockserver-netty"       % "5.15.0"   % Test,
    "org.assertj"          % "assertj-core"           % "3.27.3"   % Test
  )
}
