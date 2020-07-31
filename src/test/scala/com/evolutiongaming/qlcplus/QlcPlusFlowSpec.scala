package com.evolutiongaming.qlcplus

import com.evolutiongaming.qlcplus.QlcPlusFlow.Scenario
import com.evolutiongaming.util.Validation._
import org.scalatest.{FunSuite, Matchers}

class QlcPlusFlowSpec extends FunSuite with Matchers {

  test("load successfully") {
    val flow = QlcPlusFlow
      .load(
        """scenario.1.foo=FOO
        |scenario.1.bar=BAR
        |scenario.2.qux=QUX
        |""".stripMargin,
      )
      .orError()

    flow.scenarios.toSet shouldBe Set(
      Scenario(
        name = "1",
        widgets = Map(
          "foo" -> List("FOO"),
          "bar" -> List("BAR"),
        ),
      ),
      Scenario(
        name = "2",
        widgets = Map(
          "qux" -> List("QUX"),
        ),
      ),
    )
  }

  test("load with multiple widgets per key") {
    val flow = QlcPlusFlow
      .load(
        """scenario.1.foo=FOO,BAR
        |scenario.1.bar= BAR , QUX
        |scenario.2.qux=QUX
        |""".stripMargin,
      )
      .orError()

    flow.scenarios.toSet shouldBe Set(
      Scenario(
        name = "1",
        widgets = Map(
          "foo" -> List("FOO", "BAR"),
          "bar" -> List("BAR", "QUX"),
        ),
      ),
      Scenario(
        name = "2",
        widgets = Map(
          "qux" -> List("QUX"),
        ),
      ),
    )
  }

  test("load with empty widgets") {
    val flow = QlcPlusFlow
      .load(
        """scenario.1.foo=FOO,BAR
        |scenario.1.bar=
        |scenario.2.qux=QUX
        |""".stripMargin,
      )
      .orError()

    flow.scenarios.toSet shouldBe Set(
      Scenario(
        name    = "1",
        widgets = Map("foo" -> List("FOO", "BAR")),
      ),
      Scenario(
        name    = "2",
        widgets = Map("qux" -> List("QUX")),
      ),
    )
  }

  test("load empty") {
    QlcPlusFlow.load("".stripMargin) shouldBe "No scenarios are defined".ko
  }

  test("load almost empty") {
    QlcPlusFlow.load("scenario.1.betting=".stripMargin) shouldBe "No scenarios are defined".ko
  }

  test("random") {
    val flow = QlcPlusFlow
      .load(
        """scenario.1.foo=FOO
        |scenario.1.bar=BAR
        |scenario.2.qux=QUX
        |""".stripMargin,
      )
      .orError()

    flow.random()
  }
}
