import sbtcrossproject.{crossProject, CrossType}

enablePlugins(JavaAppPackaging)

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val server = (project in file("server")).settings(commonSettings).settings(
	name := "practice-sql-server",
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= Seq(
    "com.vmunier" %% "scalajs-scripts" % "1.1.4",
    guice,
    "net.codingwell" %% "scala-guice" % "4.2.7",
		"org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
    "org.postgresql" % "postgresql" % "42.2.18",
    "io.getquill" %% "quill-async-postgres" % "3.7.1",
    "com.lihaoyi" %% "upickle" % "1.1.0",
    "org.mindrot" % "jbcrypt" % "0.4",
    specs2 % Test
  )
).enablePlugins(PlayScala).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(commonSettings).settings(
	name := "practice-sql-client",
  scalacOptions += "-Ymacro-annotations",
  scalaJSUseMainModuleInitializer := true,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "1.1.0",
		"me.shadaj" %%% "slinky-core" % "0.6.6",
		"me.shadaj" %%% "slinky-web" % "0.6.6",
    "com.lihaoyi" %%% "upickle" % "1.1.0",
    //"io.scalajs.npm" %%% "async" % "0.5.0"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(sharedJs)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(commonSettings)
	.settings(
		name := "practice-sql-shared",
    libraryDependencies += "com.lihaoyi" %% "upickle" % "1.1.0"
	)
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  scalaVersion := "2.13.4",
  organization := "ar.edu.isft38"
)

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen {s: State => "project server" :: s}
