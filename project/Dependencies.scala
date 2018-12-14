import sbt._


object Dependencies {
  val circeVersion = "0.10.0"
  val sttpVersion = "1.5.1"

  lazy val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

  lazy val lambdaCore = "com.amazonaws" % "aws-lambda-java-core" % "1.2.0"

  lazy val lambdaEvents = "com.amazonaws" % "aws-lambda-java-events" % "2.2.4"

  lazy val httpClient = Seq(
    "com.softwaremill.sttp" %% "core",
    "com.softwaremill.sttp" %% "circe"
  ).map(_ % sttpVersion)


  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % Test
}
