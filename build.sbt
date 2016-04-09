
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.3",
  "com.typesafe.akka" %% "akka-stream" % "2.4.3",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.3",
  "io.spray" %% "spray-can" % "1.3.3",
  "io.spray" %% "spray-routing" % "1.3.3",
  "io.spray" %% "spray-testkit" % "1.3.3",
  "org.scalatest" % "scalatest_2.11" % "2.2.6" % Test
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

val commonSettings = Seq(
  organization := "scala_lab",
  version := "0.1",
  scalaVersion := "2.11.8",
  fork in test := true
)

lazy val root = Project("root", file("."))
  .settings(commonSettings: _*)

