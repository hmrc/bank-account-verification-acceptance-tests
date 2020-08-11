package uk.gov.hmrc.acceptance.config

import com.typesafe.config.{Config, ConfigFactory}

object TestConfig {
  val config: Config = ConfigFactory.load()
  val env: String = config.getString("env")
  val defaultConfig: Config = config.getConfig("local")
  val envConfig: Config = config.getConfig(env).withFallback(defaultConfig)


  def getHost(service: String): String = {
    env match {
      case "local" => s"$environmentHost:${servicePort(service)}"
      case _ => s"${envConfig.getString(s"services.host")}"
    }
  }

  def url(service: String): String = {
    s"${getHost(service)}${serviceRoute(service)}"
  }

  def apiUrl(service: String): String = {
    s"${getHost(service)}${serviceAPIRoute(service)}"
  }

  def environmentHost: String = envConfig.getString("services.host")

  def servicePort(serviceName: String): String = envConfig.getString(s"services.$serviceName.port")

  def serviceRoute(serviceName: String): String = envConfig.getString(s"services.$serviceName.productionRoute")

  def serviceAPIRoute(serviceName: String): String = envConfig.getString(s"services.$serviceName.api")

  def mockServerPort(): Int = envConfig.getInt(s"mock.server.port")

  def s3MockPort(): Int = envConfig.getInt(s"mock.server.port")
}
