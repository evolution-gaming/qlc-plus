import sbt._

object Dependencies {

  val scalatest = "org.scalatest" %% "scalatest" % "3.2.20"
  val `kind-projector` = "org.typelevel" % "kind-projector" % "0.13.4"
  val `scala-tools` = "com.evolutiongaming" %% "scala-tools" % "3.0.6"
  val `akka-tools-test` = "com.evolutiongaming" %% "akka-tools-test" % "3.0.13"

  object Akka {
    private val version = "2.6.21" // `2.6.21` is last open source version before switch to BSL
    val actor = "com.typesafe.akka" %% "akka-actor" % version
    val testkit = "com.typesafe.akka" %% "akka-testkit" % version
    val stream = "com.typesafe.akka" %% "akka-stream" % version
    val slf4j = "com.typesafe.akka" %% "akka-slf4j" % version
  }

  object AkkaHttp {
    private val version = "10.2.10" // `10.2.10` is last open source version before switch to BSL
    val core = "com.typesafe.akka" %% "akka-http-core" % version
  }

  object Logback {
    private val version = "1.2.3"
    val core = "ch.qos.logback" % "logback-core" % version
    val classic = "ch.qos.logback" % "logback-classic" % version
  }
}
