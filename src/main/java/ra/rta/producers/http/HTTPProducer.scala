package ra.rta.producers.http

import java.io.BufferedReader
import java.util.Properties

import javax.servlet.ServletConfig
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerRecord}
import org.slf4j.LoggerFactory

/**
 *
 */
class HTTPProducer extends HttpServlet {

  val LOG = LoggerFactory.getLogger(classOf[HTTPProducer])

  var transactionProducerProps: Properties = _
  var transactionProducer: Producer[String, String] = _

  var referenceProducerProps: Properties = _
  var referenceProducer: Producer[String, String] = _

  override def init(servletConfig: ServletConfig): Unit = {
    super.init(servletConfig)
    // Kafka Producers Setup
    transactionProducerProps = new Properties
    transactionProducerProps.put("metadata.broker.list", servletConfig.getInitParameter("metadata.broker.list"))
    transactionProducerProps.put("serializer.class", "kafka.serializer.StringEncoder")
    transactionProducerProps.put("key.serializer.class", "kafka.serializer.StringEncoder")
    transactionProducerProps.put("request.required.acks", "1")
    transactionProducerProps.put("partitioner.class", "com.segmint.rt.producers.utilities.partitioners.KafkaRoundRobinPartitioner")
    transactionProducer = new KafkaProducer[String, String](transactionProducerProps)

    referenceProducerProps = new Properties
    referenceProducerProps.put("metadata.broker.list", servletConfig.getInitParameter("metadata.broker.list"))
    referenceProducerProps.put("serializer.class", "kafka.serializer.StringEncoder")
    referenceProducerProps.put("key.serializer.class", "kafka.serializer.StringEncoder")
    referenceProducerProps.put("request.required.acks", "-1")
    referenceProducerProps.put("partitioner.class", "com.segmint.rt.producers.utilities.partitioners.KafkaRoundRobinPartitioner")
    referenceProducer = new KafkaProducer[String, String](referenceProducerProps)
  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    super.doPost(req, resp)
    val command: String = req.getRequestURI
    var topic: String = "transaction"
    if (!command.contains("transaction")) topic = "reference"
    if (req.getContentType.contains("application/json")) {
      val sb: StringBuffer = new StringBuffer
      var line: String = null
      val reader: BufferedReader = req.getReader
      while ({line = reader.readLine; line} != null) sb.append(line)
      val body = sb.toString
      if(body!=null && !"".equals(body)) {
        val message: ProducerRecord[String, String] = new ProducerRecord[String, String](topic, ""+System.currentTimeMillis(), body)
        val producer: Producer[String, String] = topic match {
          case "transaction" => transactionProducer
          case "reference" => referenceProducer
        }
        try {// TODO: Getting HTTP 405 Error code but message gets sent and received by Kafka
          producer.send(message)
          resp.setStatus(200)
          resp.setContentType("application/json")
          resp.getOutputStream.print("{test sat}")
          resp.flushBuffer()
        }
        catch {
          case exception: Exception => exception.printStackTrace(System.out)
            resp.setStatus(512)
            resp.setContentType("application/json")
            resp.getOutputStream.print("{test failed}")
            resp.flushBuffer()
        }
      }
    } else {
      resp.setStatus(200)
      resp.setContentType("application/json")
      resp.getOutputStream.print("{error: \"json required\"}")
      resp.flushBuffer()
    }

  }

}
