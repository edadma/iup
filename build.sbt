name := "iup"

version := "0.1.2"

scalaVersion := "3.2.1"

enablePlugins(ScalaNativePlugin)

nativeLinkStubs := true

nativeMode := "debug"

//nativeLinkingOptions += "-static"

//nativeLinkingOptions += s"-L${baseDirectory.value}/native-lib"

nativeConfig ~= { c => c.withLinkingOptions(c.linkingOptions :+ "-L${baseDirectory.value}/native-lib") }

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:existentials",
  "-language:dynamics",
)

organization := "io.github.edadma"

githubOwner := "edadma"

githubRepository := name.value

Global / onChangedBuildSource := ReloadOnSourceChanges

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.githubPackages("edadma")

Compile / mainClass := Some("Main")

licenses := Seq("ISC" -> url("https://opensource.org/licenses/ISC"))

homepage := Some(url("https://github.com/edadma/" + name.value))

publishMavenStyle := true

Test / publishArtifact := false

pomIncludeRepository := { _ =>
  false
}

pomExtra :=
  <scm>
    <url>git@github.com:edadma/{name.value}.git</url>
    <connection>scm:git:git@github.com:edadma/{name.value}.git</connection>
  </scm>
    <developers>
      <developer>
        <id>edadma</id>
        <name>Edward A. Maxedon, Sr.</name>
        <url>https://github.com/edadma</url>
      </developer>
    </developers>
