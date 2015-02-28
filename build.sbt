name := """akka-stream-scala"""

version := "1.1"

scalaVersion := "2.11.5"

// set the main class for 'sbt run'
mainClass in (Compile, run) := Some("sample.stream.MainStreamingExample")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-M3",
  "org.twitter4j" % "twitter4j-core" % "4.0.2",
  "org.twitter4j" % "twitter4j-stream" % "4.0.2"
)

scalariformSettings

