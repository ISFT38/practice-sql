// Comment to get more information during initialization
logLevel := Level.Warn

// Resolvers
resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

// Sbt plugins

addSbtPlugin("com.vmunier"               % "sbt-web-scalajs"           % "1.1.0")
addSbtPlugin("org.scala-js"              % "sbt-scalajs"               % "1.3.1")

addSbtPlugin("com.typesafe.play"         % "sbt-plugin"                % "2.8.7")
addSbtPlugin("org.portable-scala"        % "sbt-scalajs-crossproject"  % "0.6.1")
addSbtPlugin("com.typesafe.sbt"          % "sbt-gzip"                  % "1.0.2")
addSbtPlugin("com.typesafe.sbt"          % "sbt-digest"                % "1.1.4")