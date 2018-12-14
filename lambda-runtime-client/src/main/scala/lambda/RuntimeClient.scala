package lambda

import io.circe.parser.decode
import io.circe.generic.auto._
import com.softwaremill.sttp._
import com.softwaremill.sttp.circe._
import io.circe

object Headers extends Enumeration {
  protected case class Header(name: String) extends super.Val

  val RequestId       = Header("Lambda-Runtime-Aws-Request-Id")
  val FunctionArn     = Header("Lambda-Runtime-Invoked-Function-Arn")
  val TraceId         = Header("Lambda-Runtime-Trace-Id")
  val Deadline        = Header("Lambda-Runtime-Deadline-Ms")
  val ClientContext   = Header("Lambda-Runtime-Client-Context")
  val CognitoIdentity = Header("Lambda-Runtime-Cognito-Identity")
}

case class ClientApplication(
    installationId: String,
    appTitle: String,
    appVersionName: String,
    appVersionCode: String,
    appPackageName: String
)

case class ClientContext(
    client: ClientApplication,
    custom: Map[String, String],
    environment: Map[String, String]
)

case class CognitoIdentity(identity_id: String, identity_pool_id: String)

case class EventContext(
    awsRequestId: String,
    invokedFunctionArn: String,
    xrayTraceId: String,
    deadline: Long,
    clientContext: Option[ClientContext],
    identity: Option[CognitoIdentity]
)

object EventContext {

  def apply(headers: Map[String, String]): EventContext =
    EventContext(
      headers(Headers.RequestId.name),
      headers(Headers.FunctionArn.name),
      headers(Headers.TraceId.name),
      headers(Headers.Deadline.name).toLong,
      headers
        .get(Headers.ClientContext.name)
        .flatMap(x => decode[ClientContext](x).fold(_ => None, (context: ClientContext) => Some(context))),
      headers
        .get(Headers.CognitoIdentity.name)
        .flatMap(x => decode[CognitoIdentity](x).fold(_ => None, (identity: CognitoIdentity) => Some(identity)))
    )
}

class RuntimeClient(val endpoint: String) {
  val RUNTIME_API_VERSION    = "2018-06-01"
  val API_CONTENT_TYPE       = "application/json"
  val API_ERROR_CONTENT_TYPE = "application/vnd.aws.lambda.error+json"
  val RUNTIME_ERROR_HEADER   = "Lambda-Runtime-Function-Error-Type"

  implicit lazy val backend = HttpURLConnectionBackend()
  lazy val httpClient       = sttp

  def nextEvent() {
    val request = httpClient
      .get(uri"http://$endpoint/$RUNTIME_API_VERSION/runtime/invocation/next")

    val response = request.send()

    val eventContext = EventContext(Map(response.headers: _*))
  }
}
