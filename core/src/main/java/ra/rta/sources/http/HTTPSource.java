package ra.rta.sources.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.connectors.kafka.KafkaMgr;
import ra.rta.Event;
import ra.rta.transform.JSONTransformer;
import ra.rta.utilities.JSONUtil;
import ra.rta.utilities.RandomUtil;

/**
 *
 */
class HTTPSource extends HttpServlet {

  private static final Logger LOG = LoggerFactory.getLogger(HTTPSource.class);

  private long sourceId;
  private String topic;
  private boolean durable = false;
  private KafkaMgr kafkaMgr;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    Map<String,Object> params = new HashMap<>();
    Enumeration initParams = config.getInitParameterNames();
    while(initParams.hasMoreElements()) {
      String param = (String)initParams.nextElement();
      params.put(param, config.getInitParameter(param));
    }
    kafkaMgr = new KafkaMgr(params);
    topic = (String)params.get("topic");
    durable = (Boolean)params.get("durable");
    sourceId = (Long)params.get("sourceId");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    super.doPost(req, resp);
    Event event = new Event();
    event.id = RandomUtil.nextRandomLong();
    event.sourceId = sourceId;
    event.commandId = Integer.parseInt(req.getRequestURI());
    if (req.getContentType().contains("application/json")) {
      event.payloadTransformerClass = JSONTransformer.class.getName();
      StringBuilder sb = new StringBuilder();
      String line;
      BufferedReader reader = req.getReader();
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      if(sb.length()==0) {
        resp.setStatus(200);
        resp.setContentType(req.getContentType());
        resp.getOutputStream().print("error: body required");
        resp.flushBuffer();
        return;
      }
      event.rawPayload = sb.toString().getBytes();
      try {
        LOG.info(".");
        kafkaMgr.send(topic, JSONUtil.MAPPER.writeValueAsBytes(event), durable);
        resp.setStatus(200);
        resp.setContentType("application/json");
        resp.getOutputStream().print("{status: Success}");
        resp.flushBuffer();
      } catch (Exception ex) {
          resp.setStatus(512);
          resp.setContentType("application/json");
          resp.getOutputStream().print("{error: }");
          resp.flushBuffer();
      }
    } else {
      resp.setStatus(200);
      resp.setContentType(req.getContentType());
      resp.getOutputStream().print("error: json required");
      resp.flushBuffer();
    }
  }

}
