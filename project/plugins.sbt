resolvers += "HMRC-open-artefacts-maven" at "https://open.artefacts.tax.service.gov.uk/maven2"
resolvers += Resolver.url("HMRC-open-artefacts-ivy-local", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(
  Resolver.ivyStylePatterns
)

addDependencyTreePlugin
addSbtPlugin("uk.gov.hmrc"   % "sbt-auto-build" % "3.6.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt"   % "2.4.6")

logLevel := Level.Warn
