import sbt._

object Dependencies {
  
  val scalatest         = "org.scalatest"       %% "scalatest"       % "3.0.8"
  val `kind-projector`  = "org.typelevel"        % "kind-projector"  % "0.11.0"
  val `scala-tools`     = "com.evolutiongaming" %% "scala-tools"     % "3.0.5"
  val `akka-tools-test` = "com.evolutiongaming" %% "akka-tools-test" % "3.0.10"

  object Akka {
    private val version = "2.6.5"
    val actor   = "com.typesafe.akka" %% "akka-actor"   % version
    val testkit = "com.typesafe.akka" %% "akka-testkit" % version
    val stream  = "com.typesafe.akka" %% "akka-stream"  % version
    val slf4j   = "com.typesafe.akka" %% "akka-slf4j"   % version
  }

  object AkkaHttp {
    private val version = "10.1.11"
    val core            = "com.typesafe.akka" %% "akka-http-core" % version
  }

  object Logback {
    private val version = "1.2.3"
    val core    = "ch.qos.logback" % "logback-core"    % version
    val classic = "ch.qos.logback" % "logback-classic" % version
  }
}