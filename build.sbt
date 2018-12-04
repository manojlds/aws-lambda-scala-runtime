import Dependencies._

val appVersion = sys.env.get("TRAVIS_TAG") orElse sys.env.get("BUILD_LABEL") getOrElse s"1.0.0-${System.currentTimeMillis / 1000}-SNAPSHOT"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.stacktoheap",
      scalaVersion := "2.12.7",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "aws-lambda-scala-runtime",
    libraryDependencies ++= circe ++ Seq(lambdaCore, lambdaEvents, scalaTest)
  )
