lazy val akkaVersion = "2.4.17"

fork in Test := true

javaOptions in Test := Seq("-ea")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "org.mockito" % "mockito-core" % "2.7.9" % Test,
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)

name := "akka-game-of-life"

scalacOptions := Seq("-deprecation", "-feature", "-Xfuture")

scalaVersion := "2.12.1"

version := "0.1-SNAPSHOT"
