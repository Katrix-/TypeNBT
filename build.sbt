import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val commonSettings = Seq(
  organization := "net.katsstuff",
  version := "0.5.0",
  scalaVersion := "2.12.8",
  crossScalaVersions := Seq("2.11.12", scalaVersion.value, "2.13.0")
)

lazy val publishSettigs = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/Katrix/TypeNBT"),
      "scm:git:github.com/Katrix/TypeNBT",
      Some("scm:git:github.com/Katrix/TypeNBT")
    )
  ),
  homepage := Some(url("https://github.com/Katrix/TypeNBT")),
  developers := List(
    Developer(
      id = "Katrix",
      name = "Nikolai Frid",
      email = "katrix97@hotmail.com",
      url = url("http://katsstuff.net/")
    )
  ),
  autoAPIMappings := true
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
)

lazy val typenbt = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(
    commonSettings,
    publishSettigs,
    name := "typenbt",
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.8" % Test,
    description := "TypeNBT is a NBT library that let's the user focus on the data, not how it's represented",
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided
  )

lazy val typenbtExtra = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(typenbt)
  .settings(
    commonSettings,
    publishSettigs,
    name := "typenbt-extra",
    description := "A module for TypeNBT which offers extra functionality using Shapeless",
    libraryDependencies += "com.chuusai" %%% "shapeless" % "2.3.3"
  )

lazy val typenbtMojangson = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(typenbt)
  .settings(
    commonSettings,
    publishSettigs,
    name := "typenbt-mojangson",
    libraryDependencies += "com.lihaoyi"    %%% "fastparse"  % "2.1.3",
    libraryDependencies += "org.scalatest"  %%% "scalatest"  % "3.0.8" % Test,
    libraryDependencies += "org.scalacheck" %%% "scalacheck" % "1.14.0" % Test,
    description := "The mojangson module for TypeNBT lets user parse and print mojangson",
    crossScalaVersions := Seq(scalaVersion.value, "2.13.0")
  )

lazy val typenbtJVM = typenbt.jvm
lazy val typenbtJS  = typenbt.js

lazy val typenbtExtraJVM = typenbtExtra.jvm
lazy val typenbtExtraJS  = typenbtExtra.js

lazy val typenbtMojangsonJVM = typenbtMojangson.jvm
lazy val typenbtMojangsonJS  = typenbtMojangson.js

lazy val example =
  project.dependsOn(typenbtJVM, typenbtExtraJVM, typenbtMojangsonJVM).settings(commonSettings, name := "examples")

lazy val rootTypeNBT = project
  .in(file("."))
  .aggregate(typenbtJVM, typenbtJS, typenbtExtraJVM, typenbtExtraJS, typenbtMojangsonJVM, typenbtMojangsonJS)
  .settings(
    commonSettings,
    noPublishSettings,
    //Fixes repository not specified error
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  )
