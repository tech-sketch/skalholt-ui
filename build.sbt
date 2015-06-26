name := """skalholt-ui"""

version := "0.1.3"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
  .aggregate(skalholtCore)
  .dependsOn(skalholtCore)

lazy val skalholtCore = (project in file("skalholt-core"))

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "com.typesafe.slick" %% "slick-codegen" % "3.0.0",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "com.typesafe.play" %% "play-slick" % "1.0.0",
  "com.h2database" % "h2" % "1.3.176",
  cache,
  ws,
 specs2 % Test
)
scalacOptions += "-deprecation"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

