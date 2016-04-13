package scala_lab

import akka.actor._

// Log base classes
case class LogMessage (
  timestamp: Long,
  headers: Map[String, Any],
  body: Map[String, Any]
)

class LogsBatch (messages:List[LogMessage]){
  def foreach(f: LogMessage => Unit):Unit = messages.foreach _
}

// Handlers/Pipeline
trait LogInput extends Actor {
  var destinations: List[ActorRef]

  def emit(message: String):Unit = {
    val msg = LogMessage(System.currentTimeMillis, Map(), Map("body" -> message))
    emit(msg)
  }

  def emit(headers: Map[String, Any], message:String):Unit = {
    val msg = LogMessage(System.currentTimeMillis, headers, Map("body" -> message))
    emit(msg)
  }

  def emit(message: LogMessage):Unit = {
    destinations.foreach { _ ! message }
  }

}

trait LogOutput extends Actor {

  def receive = {
    case msg:LogMessage => emit(msg)
    case _ => {}
  }

  def batchEmit(batch:LogsBatch){
    batch.foreach(this.emit(_))
  }

  // To be defined
  def emit(logMessage: LogMessage)

  def flush():Unit
}

abstract class LogHandler extends Actor {
  // To be extended with LogInput and/or LogOutput
  var destinations: List[ActorRef]

  def setNext(next:ActorRef) = {
    destinations = next :: destinations
  }
}

abstract class Pipeline {
  // Inputs + Filters/Forwarders/ + Outputs
  val chain:List[ActorRef]

  def start(){
    chain.fold(chain.head)((prev:ActorRef, curr:ActorRef) => {
      if (prev != curr){
        prev ! ("setNext", curr)
      }
      curr
    })
  }
}

// Storage access
abstract class LogStorage {
  def store(logMessage:LogMessage):Unit
  def store(logsBatch:LogsBatch):Unit

  def fetch_one():Option[LogMessage]
  def fetch_all():Array[LogMessage]
}

// HTTP traits
trait AccessLogs {
  def getLogs(options:Map[_, _]):LogsBatch

}

trait StreamLogs {
  def streamLogs(options:Map[_, _]):Stream[LogMessage]
}
