package scala_lab

case class LogMessage (
  timestamp: Long,
  headers: Map[String, Any],
  body: Map[String, Any]
)

class LogsBatch (messages:List[LogMessage]){
  // HOWTO: define with only '= messages.foreach'
  def foreach(f: LogMessage => Unit):Unit = messages.foreach _
}

trait LogInput {
  def handle(message: String){
    val msg = LogMessage(System.currentTimeMillis, Map(), Map("body" -> message))
    this.handle(msg)
  }
  def handle(headers: Map[String, Any], message:String){
    val msg = LogMessage(System.currentTimeMillis, headers, Map("body" -> message))
    this.handle(msg)
  }

  // Implement handler here
  def handle(message: LogMessage)
}

trait LogOutput {

  def emit(logMessage: LogMessage)

  def batchEmit(batch:LogsBatch){
    batch.foreach(this.emit(_))
  }

  def flush():Unit
}

abstract class LogHandler {
  // To be extended with LogInput and/or LogOutput
  val config:Map[String, Any]
  def setNext[H](next:H)

  def start():Unit
  def stop():Unit
}

abstract class Pipeline {
  // Inputs + Filters/Forwarders/ + Outputs
  val chain:List[LogHandler]
  def start(){
    chain.fold(chain.head)((prev:LogHandler, curr:LogHandler) => {
      if (prev != curr){
        prev.setNext(curr)
      }
      curr
    })
  }
}

abstract class LogStorage {
  def store(logMessage:LogMessage):Unit
  def store(logsBatch:LogsBatch):Unit

  def fetch_one():Option[LogMessage]
  def fetch_all():Array[LogMessage]
}
