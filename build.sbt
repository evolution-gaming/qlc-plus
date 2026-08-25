import Dependencies.*
import com.typesafe.tools.mima.core.*

lazy val commonSettings = Seq(
  organization := "com.evolutiongaming",
  homepage := Some(new URL("http://github.com/evolution-gaming/qlc-plus")),
  startYear := Some(2020),
  organizationName := "Evolution Gaming",
  organizationHomepage := Some(url("http://evolutiongaming.com")),
  scalaVersion := crossScalaVersions.value.head,
  crossScalaVersions := Seq("2.13.18", "3.3.8"),
  Compile / doc / scalacOptions ++= Seq("-groups", "-implicits", "-no-link-warnings"),
  licenses := Seq(("MIT", url("https://opensource.org/licenses/MIT"))),
  scalacOptsFailOnWarn := Some(false),
  /*testOptions in Test ++= Seq(Tests.Argument(TestFrameworks.ScalaTest, "-oUDNCXEHLOPQRM"))*/
  libraryDependencies ++= {
    scalaBinaryVersion.value match {
      case "2.13" => Seq(compilerPlugin(`kind-projector` cross CrossVersion.full))
      case _ => Nil
    }
  },
  versionPolicyIntention := Compatibility.BinaryCompatible,
)

val alias: Seq[sbt.Def.Setting[?]] =
  addCommandAlias("check", "+all scalafmtCheckRepo versionPolicyCheck Compile/doc") ++
    addCommandAlias("fmt", "scalafmtRepo") ++
    addCommandAlias("build", "+all compile test")

lazy val root = project
  .in(file("."))
  .settings(name := "qlc-plus")
  .settings(commonSettings)
  .settings(alias)
  .settings(
    libraryDependencies ++= Seq(
      `scala-tools`,
      Akka.actor,
      Akka.stream,
      Akka.testkit % Test,
      Akka.slf4j % Test,
      AkkaHttp.core.cross(CrossVersion.for3Use2_13),
      Logback.core % Test,
      Logback.classic % Test,
      scalatest % Test,
      `akka-tools-test` % Test,
    ),
  )
