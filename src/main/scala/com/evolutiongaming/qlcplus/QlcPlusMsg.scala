package com.evolutiongaming.qlcplus

import com.evolutiongaming.util.Validation._

import scala.annotation.tailrec
import scala.util.Try

object QlcPlusMsg {

  sealed trait Out {
    def asText: String
  }

  object Out {
    val ApiPrefix: String = "QLC+API"

    case object GetWidgetsNumber extends Out {
      def asText = s"$ApiPrefix|getWidgetsNumber"
    }

    case object GetWidgetsList extends Out {
      def asText = s"$ApiPrefix|getWidgetsList"
    }

    final case class SetWidgetValue(id: Int, value: Int) extends Out {
      def asText = s"$id|$value"
    }
  }

  sealed trait In

  object In {
    val PrefixRegex = """QLC\+API\|([A-Za-z]+)(\|(.+))*""".r

    def parse(str: String): V[In] = {

      def header = str match {
        case PrefixRegex(name, _, args) => (name, Option(args)).ok
        case _                          => s"Unable to parse message: $str".ko
      }

      def num(str: String) = Try(Integer parseInt str) ?>> s"Can't parse: $str"

      def msg(name: String, args: Option[String]) = {
        name match {
          case "getWidgetsNumber" =>
            for {
              str <- args ?>> "Missing number"
              num <- num(str)
            } yield GetWidgetsNumber(num)

          case "getWidgetsList" =>
            @tailrec
            def loop(ls: List[String], res: Map[String, String] = Map()): Map[String, String] = {
              ls match {
                case a :: b :: rest => loop(rest, res + (a -> b))
                case _              => res
              }
            }
            for {
              widgets <- loop((args getOrElse "").split('|').toList).toList.allValid {
                case (id, name) =>
                  for (id <- num(id)) yield name -> id
              }
            } yield GetWidgetsList(widgets.toMap)

          case x =>
            s"Unknown message: $x".ko
        }
      }

      for {
        head         <- header
        (name, args) = head
        msg          <- msg(name, args)
      } yield msg
    }

    final case class GetWidgetsNumber(count: Int) extends In
    final case class GetWidgetsList(widgets: Map[String, Int]) extends In
  }

}
