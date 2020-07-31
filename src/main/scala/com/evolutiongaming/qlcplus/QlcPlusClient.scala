package com.evolutiongaming.qlcplus

import akka.NotUsed
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props, Stash, Status, Terminated}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.ws._
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{CompletionStrategy, OverflowStrategy}

import scala.util.{Failure, Success}

class QlcPlusClient(uri: Uri, listener: ActorRef) extends Actor with Stash with ActorLogging {
  import QlcPlusClient._
  import QlcPlusMsg._

  override def preStart() = {
    log.info(s"Starting QLC+ client for $uri")
    connect()
  }

  override def postStop() = {
    log.info(s"Stopped QLC+ client for $uri")
  }

  def receive: Receive = connecting

  def connecting: Receive = {
    case Connected(outgoing) =>
      log.info(s"QLC+ client for $uri connected")
      context watch outgoing
      context become connected(outgoing)
      unstashAll()

    case _: Out => stash()

    case _: Terminated =>
      log.warning(s"QLC+ client for $uri observed actor termination")
  }

  def connected(outgoing: ActorRef): Receive = {
    case TextMessage.Strict(str) =>
      In.parse(str) match {
        case Right(cmd) =>
          log.info(s"QLC+ client for $uri received command $cmd")
          listener ! cmd
        case Left(err)  => log.warning(s"Parse failed with $err for input $str")
      }

    case msg: Out =>
      log.info(s"QLC+ client for $uri received message ${msg.asText}")
      outgoing ! TextMessage(msg.asText)

    case Status.Failure(t) =>
      log.error(s"Connection to $uri closed: $t")
      context stop self

    case Terminated(`outgoing`) =>
      log.error(s"Connection actor for $uri was terminated")
      context stop self

    case _: Terminated =>
      log.warning(s"QLC+ client for $uri observed actor termination")
  }

  def connect(): Unit = {
    import context.{dispatcher, system}

    log.info(s"Connecting to $uri")

    val incoming: Sink[Message, NotUsed] = Sink.actorRef(self, PoisonPill, _ => PoisonPill)

    val outgoing: Source[Message, NotUsed] = {
      val bufferSize = system.settings.config.getInt("evolutiongaming.qlcplus.client.bufferSize")
      val completionMatcher: PartialFunction[Any, CompletionStrategy] = {
        case Status.Success(s: CompletionStrategy) => s
        case Status.Success(_)                     => CompletionStrategy.draining
        case Status.Success                        => CompletionStrategy.draining
      }
      val failureMatcher: PartialFunction[Any, Throwable] = {
        case Status.Failure(cause) =>
          log.warning(s"QLC+ stream failed due to $cause")
          cause
      }

      Source
        .actorRef(completionMatcher, failureMatcher, bufferSize, OverflowStrategy.fail)
        .mapMaterializedValue { out =>
          self ! Connected(out)
          NotUsed
        }
    }

    val wsFlow = Http().webSocketClientFlow(WebSocketRequest(uri))

    val (upgraded, _) = outgoing
      .viaMat(wsFlow)(Keep.right)
      .toMat(incoming)(Keep.both)
      .run()

    upgraded onComplete {
      case Success(x: ValidUpgrade) =>
        log.info(s"Connected to $uri ($x)")

      case Success(x: InvalidUpgradeResponse) =>
        log.error(s"Connection to $uri failed: ${x.cause}")
        self ! PoisonPill

      case Failure(t) =>
        log.error(s"Connection to $uri failed: $t")
        self ! PoisonPill
    }
  }

}

object QlcPlusClient {
  private[qlcplus] final case class Connected(outgoing: ActorRef)

  def props(uri: Uri, listener: ActorRef): Props = Props(new QlcPlusClient(uri, listener))
}
