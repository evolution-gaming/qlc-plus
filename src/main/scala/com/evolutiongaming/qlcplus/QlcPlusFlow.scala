package com.evolutiongaming.qlcplus

import java.io.StringReader
import java.util.Properties

import com.evolutiongaming.util.Validation._

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Random

final case class QlcPlusFlow(scenarios: Vector[QlcPlusFlow.Scenario]) {
  require(scenarios.nonEmpty, "Scenarios are empty")

  def random(): QlcPlusFlow.Scenario = {
    scenarios(Random.nextInt(scenarios.size))
  }
}

object QlcPlusFlow {
  type Key     = String
  type Widget  = String
  type Widgets = List[Widget]

  object Widgets {
    def unapply(str: String): Option[Widgets] = {
      val widgets = str.split(",").map(_.trim).filter(_.nonEmpty).toList
      if (widgets.nonEmpty) Some(widgets) else None
    }
  }

  private val ScenarioRegex = """scenario\.([a-zA-Z0-9_]+)\.(.+)""".r

  final case class Scenario(name: String, widgets: Map[Key, Widgets]) {
    import Scenario._

    def default: Option[Widgets] = widgets.get(DefaultKey)
  }

  object Scenario {
    val DefaultKey: Key = "default"
  }

  def load(config: String): V[QlcPlusFlow] = {
    val props = new Properties
    props.load(new StringReader(config))
    load(props.asScala.toMap)
  }

  def load(config: Map[String, String]): V[QlcPlusFlow] = {
    val scenarios = mutable.Map[String, Scenario]()
    config collect {
      case (ScenarioRegex(name, phase), Widgets(widgets)) =>
        val sc = scenarios.getOrElse(name, Scenario(name, Map.empty))
        scenarios(name) = sc.copy(widgets = sc.widgets + (phase -> widgets))
    }
    for {
      _ <- scenarios.nonEmpty trueOr "No scenarios are defined"
    } yield QlcPlusFlow(scenarios.values.toVector)
  }
}
