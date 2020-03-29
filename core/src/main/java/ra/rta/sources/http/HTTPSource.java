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
import ra.rta.MessageManager;
import ra.rta.models.Event;
import ra.rta.utilities.JSONUtil;
import ra.rta.utilities.RandomUtil;

/**
 *
 */
class HTTPSource extends HttpServlet {

  private static final Logger LOG = LoggerFactory.getLogger(HTTPSource.class);

  private long groupId;
  private String topic;
  private boolean durable = false;
  private MessageManager messageManager;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    Map<String,Object> params = new HashMap<>();
    Enumeration initParams = config.getInitParameterNames();
    while(initParams.hasMoreElements()) {
      String param = (String)initParams.nextElement();
      params.put(param, config.getInitParameter(param));
    }
    messageManager = new MessageManager(params);
    topic = (String)params.get("topic");
    durable = (Boolean)params.get("durable");
    groupId = (Integer)params.get("groupId");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    super.doPost(req, resp);
    Event event = new Event();
    event.command = Integer.parseInt(req.getRequestURI());
    event.id = RandomUtil.nextRandomLong();
    event.groupId = groupId;
    event.payload.put("topic",topic);
    event.payload.put("durable",durable);
    if (req.getContentType().contains("application/json")) {
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
      event.payload.put("line",sb.toString());

      try {
        LOG.info(".");
        messageManager.send(topic, JSONUtil.MAPPER.writeValueAsBytes(event), durable);
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
