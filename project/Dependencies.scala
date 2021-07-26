import sbt._

object Dependencies {

  val test = Seq(
    "org.scalatest"           %% "scalatest"          % "3.2.8"       %   Test,
    "org.scalatestplus"       %% "selenium-3-141"     % "3.2.8.0"     %   Test,
    "com.vladsch.flexmark"    % "flexmark-all"        % "0.36.8"      %   Test,
    "uk.gov.hmrc"             %% "webdriver-factory"  % "0.+"         %   Test,
    "com.typesafe.play"       %% "play-json"          % "2.9.2"       %   Test,
    "io.findify"              %% "s3mock"             % "0.2.6"       %   Test,
    "org.mock-server"         % "mockserver-netty"    % "5.11.2"      %   Test,
    "org.assertj"             % "assertj-core"        % "3.20.2"      %   Test,
    "com.google.guava"        %  "guava"              % "30.1.1-jre"  %   Test
  )
}
