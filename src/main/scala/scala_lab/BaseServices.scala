package scala_lab

case class LogMessage(timestamp: Any, headers: Any, body: Any)

trait LogInput {
  def handle[M](message: M)
}

trait LogOutput {
  def emit[L](logMessage: L)
  def flush():Unit
}

abstract class LogHandler {
  // To be extended with LogInput and/or LogOutput
  val config:Map[String, Any]
  def start():Unit
  def stop():Unit
}

abstract class Pipeline {
  // Inputs + Filters/Forwarders/ + Outputs
  val chain:Array[Any]
}

abstract class LogStorage {
  def fetch_one():Option[LogMessage]
  def fetch_all():Array[LogMessage]
}
