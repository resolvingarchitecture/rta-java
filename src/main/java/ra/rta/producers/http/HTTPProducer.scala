package ra.rta.producers.http

import java.io.BufferedReader

import javax.servlet.ServletConfig
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import org.slf4j.LoggerFactory
import ra.rta.producers.MessageManager

/**
 *
 */
class HTTPProducer extends HttpServlet {

  val LOG = LoggerFactory.getLogger(classOf[HTTPProducer])

  var messageManager: MessageManager = _

  override def init(servletConfig: ServletConfig): Unit = {
    super.init(servletConfig)
    messageManager = new MessageManager(servletConfig.getServletContext().)
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
        try {
          messageManager.send(topic, body, !topic.eq("transaction"))
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
