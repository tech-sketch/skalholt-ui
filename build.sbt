name := """skalholt-ui"""

version := "0.1.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
  .aggregate(skalholtCore)
  .dependsOn(skalholtCore)

lazy val skalholtCore = (project in file("skalholt-core"))

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  cache,
  ws
)