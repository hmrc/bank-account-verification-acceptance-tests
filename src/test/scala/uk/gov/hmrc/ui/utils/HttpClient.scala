/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ui.utils

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.{Materializer, SystemMaterializer}
import play.api.libs.ws.DefaultBodyWritables._
import play.api.libs.ws.StandaloneWSRequest
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.shaded.ahc.org.asynchttpclient._

import scala.concurrent.{ExecutionContext, Future}

trait HttpClient {

  implicit lazy val actorSystem: ActorSystem   = ActorSystem("wsClient")
  implicit lazy val materializer: Materializer = SystemMaterializer(actorSystem).materializer
  implicit lazy val ec: ExecutionContext       = ExecutionContext.global

  lazy val asyncHttpClientConfig: DefaultAsyncHttpClientConfig = new DefaultAsyncHttpClientConfig.Builder().build
  lazy val asyncHttpClient                                     = new DefaultAsyncHttpClient(asyncHttpClientConfig)
  lazy val wsClient                                            = new StandaloneAhcWSClient(asyncHttpClient)

  def get(url: String, headers: (String, String)*): Future[StandaloneWSRequest#Self#Response] =
    wsClient
      .url(url)
      .withHttpHeaders(headers: _*)
      .get()

  def post(url: String, bodyAsJson: String, headers: (String, String)*): Future[StandaloneWSRequest#Self#Response] =
    wsClient
      .url(url)
      .withHttpHeaders(headers: _*)
      .post(bodyAsJson)

  def delete(url: String, headers: (String, String)*): Future[StandaloneWSRequest#Self#Response] =
    wsClient
      .url(url)
      .withHttpHeaders(headers: _*)
      .delete()
}
