import Dependencies._

val appVersion = sys.env.get("TRAVIS_TAG") orElse sys.env.get("BUILD_LABEL") getOrElse s"1.0.0-${System.currentTimeMillis / 1000}-SNAPSHOT"

lazy val commonSettings = Seq(
  organization := "com.stacktoheap",
  version := appVersion,
  scalaVersion := "2.11.11",
  libraryDependencies ++= circe ++ Seq(
    lambdaCore, lambdaEvents, scalaTest
  ),
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  crossPaths := false,
  scalafmtOnCompile := true,
  assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
)

lazy val client = (project in file("lambda-runtime-client")).
  settings(commonSettings: _*).
  settings(
    name := "aws-lambda-scala-runtime-client",
    libraryDependencies ++= httpClient,
  )

lazy val runtime = (project in file("lambda-runtime")).
  dependsOn(client).
  settings(commonSettings: _*).
  settings(
    name := "aws-lambda-scala-runtime",
  )

lazy val root = Project(
  id = "aws-lambda-scala",
  base = file(".")
) aggregate(client, runtime)
