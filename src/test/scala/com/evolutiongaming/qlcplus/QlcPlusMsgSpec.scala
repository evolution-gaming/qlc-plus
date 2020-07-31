package com.evolutiongaming.qlcplus

import com.evolutiongaming.qlcplus.QlcPlusMsg._
import com.evolutiongaming.util.Validation._
import org.scalatest.{FunSuite, Matchers}

class QlcPlusMsgSpec extends FunSuite with Matchers {

  test("render GetWidgetsNumber") {
    Out.GetWidgetsNumber.asText shouldBe "QLC+API|getWidgetsNumber"
  }

  test("render GetWidgetsList") {
    Out.GetWidgetsList.asText shouldBe "QLC+API|getWidgetsList"
  }

  test("render SetWidgetValue") {
    Out.SetWidgetValue(0, 255).asText shouldBe "0|255"
  }

  test("parse GetWidgetsNumber") {
    In.parse(s"QLC+API|getWidgetsNumber|5") shouldBe In.GetWidgetsNumber(5).ok
  }

  test("parse non-empty GetWidgetsList") {
    In.parse(s"QLC+API|getWidgetsList|0|foo|1|bar|2|baz") shouldBe In
      .GetWidgetsList(
        Map(
          "foo" -> 0,
          "bar" -> 1,
          "baz" -> 2,
        ),
      )
      .ok
  }

  test("parse empty GetWidgetsList") {
    In.parse(s"QLC+API|getWidgetsList") shouldBe In.GetWidgetsList(Map()).ok
  }

}
