import sbt._

object Dependencies {

  val test = Seq(
    "org.scalatest"           %% "scalatest"          % "3.2.2"     %   Test,
    "org.scalatestplus"       %% "selenium-3-141"     % "3.2.2.0"   %   Test,
    "com.google.guava"        %  "guava"              % "30.0-jre"  %   Test,
    "com.vladsch.flexmark"    % "flexmark-all"        % "0.35.10"   %   Test,
    "com.github.nscala-time"  %% "nscala-time"        % "2.24.0"    %   Test,
    "uk.gov.hmrc"             %% "webdriver-factory"  % "0.+"       %   Test,
    "com.typesafe.play"       %% "play-json"          % "2.6.13"    %   Test,
    "io.findify"              %% "s3mock"             % "0.2.6"     %   Test,
    "org.mock-server"         % "mockserver-netty"    % "5.11.1"    %   Test,
    "org.assertj"             % "assertj-core"        % "3.18.0"    %   Test
  )
}
