name := "cameoLoadTest"

version := "1.0"

scalaVersion := "2.10.3"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)


libraryDependencies ++= Seq (
  "com.typesafe.akka" %% "akka-actor" % "2.3.1",
  "com.typesafe.play" %% "play-json" % "2.2.2",
  "com.typesafe.play" %% "play" % "2.2.2",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0"
)

//com.typesafe.sbt.SbtAtmos.atmosSettings