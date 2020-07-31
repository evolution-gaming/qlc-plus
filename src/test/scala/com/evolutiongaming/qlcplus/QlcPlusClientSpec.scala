package com.evolutiongaming.qlcplus

import akka.actor.{PoisonPill, Status}
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.ws._
import akka.testkit.{TestActorRef, TestProbe}
import com.evolutiongaming.test.ActorSpec
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

class QlcPlusClientSpec extends WordSpec with ActorSpec with Matchers {
  import QlcPlusMsg._

  "QLC+ client" should {

    "forward incoming messages to listener" in new TestScope {
      ref ! QlcPlusClient.Connected(outgoing.ref)
      ref ! TextMessage.Strict("QLC+API|getWidgetsList|0|foo|1|bar")
      listener.expectMsgPF() {
        case in: In.GetWidgetsList =>
          in.widgets shouldBe Map("foo" -> 0, "bar" -> 1)
      }
    }

    "send outgoing messages to socket" in new TestScope {
      ref ! QlcPlusClient.Connected(outgoing.ref)
      ref ! Out.GetWidgetsList
      outgoing.expectMsgPF() {
        case TextMessage.Strict("QLC+API|getWidgetsList") =>
      }
    }

    "stash incoming messages until connected" in new TestScope {
      ref ! Out.GetWidgetsNumber
      ref ! Out.GetWidgetsList

      outgoing.expectNoMessage(500.millis)

      ref ! QlcPlusClient.Connected(outgoing.ref)
      outgoing.expectMsg(TextMessage("QLC+API|getWidgetsNumber"))
      outgoing.expectMsg(TextMessage("QLC+API|getWidgetsList"))
    }

    "stop on receiving Status.Failure after connecting" in new TestScope {
      watch(ref)
      ref ! QlcPlusClient.Connected(outgoing.ref)
      ref ! Status.Failure(new Exception("error"))
      expectTerminated(ref)
    }

    "stop if connection actor got terminated" in new TestScope {
      watch(ref)
      ref ! QlcPlusClient.Connected(outgoing.ref)
      ref ! PoisonPill
      expectTerminated(ref)
    }
  }

  trait TestScope extends ActorScope {
    val uri = Uri("ws://localhost:9999/qlcplusWS")

    val listener = TestProbe()
    val outgoing = TestProbe()

    lazy val client = new QlcPlusClient(uri, listener.ref) {
      override def connect(): Unit = {
        // Suppressing auto-connection on start
      }
    }

    val ref = TestActorRef(client)
  }
}
