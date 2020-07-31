import Dependencies._


lazy val commonSettings = Seq(
  organization := "com.evolutiongaming",
  homepage := Some(new URL("http://github.com/evolution-gaming/qlc-plus")),
  startYear := Some(2020),
  organizationName := "Evolution Gaming",
  organizationHomepage := Some(url("http://evolutiongaming.com")),
  bintrayOrganization := Some("evolutiongaming"),
  scalaVersion := crossScalaVersions.value.head,
  crossScalaVersions := Seq("2.13.3", "2.12.12"),
  scalacOptions in(Compile, doc) ++= Seq("-groups", "-implicits", "-no-link-warnings"),
  resolvers += Resolver.bintrayRepo("evolutiongaming", "maven"),
  licenses := Seq(("MIT", url("https://opensource.org/licenses/MIT"))),
  releaseCrossBuild := true,
  scalacOptsFailOnWarn := Some(false),
  /*testOptions in Test ++= Seq(Tests.Argument(TestFrameworks.ScalaTest, "-oUDNCXEHLOPQRM"))*/
  libraryDependencies += compilerPlugin(`kind-projector` cross CrossVersion.full))


lazy val root = (project in file(".")
  settings (name := "qlc-plus")
  settings commonSettings
  settings (
    libraryDependencies ++= Seq(
      `scala-tools`,
      Akka.actor,
      Akka.stream,
      Akka.testkit      % Test,
      Akka.slf4j        % Test,
      AkkaHttp.core,
      Logback.core      % Test,
      Logback.classic   % Test,
      scalatest         % Test,
      `akka-tools-test` % Test)))