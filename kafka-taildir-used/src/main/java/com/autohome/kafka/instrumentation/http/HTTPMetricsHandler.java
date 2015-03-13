package com.autohome.kafka.instrumentation.http;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

import com.autohome.kafka.instrumentation.utils.JMXPollUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HTTPMetricsHandler extends AbstractHandler {

	Type mapType =new TypeToken<Map<String, Map<String, String>>>() {}.getType();
	Gson gson = new Gson();
	
	
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, int dispatch) throws IOException,
			ServletException {
		 if(request.getMethod().equalsIgnoreCase("TRACE") || request.getMethod()
			        .equalsIgnoreCase("OPTIONS")) {
			        response.sendError(HttpServletResponse.SC_FORBIDDEN);
			        response.flushBuffer();
			        ((Request) request).setHandled(true);
			        return;
			      }
			      if (target.equals("/")) {
			        response.setContentType("text/html;charset=utf-8");
			        response.setStatus(HttpServletResponse.SC_OK);
			        response.getWriter().write("For KafkaAgent metrics please click"
			                + " <a href = \"./metrics\"> here</a>.");
			        response.flushBuffer();
			        ((Request) request).setHandled(true);
			        return;
			      } else if (target.equalsIgnoreCase("/metrics")) {
			        response.setContentType("application/json;charset=utf-8");
			        response.setStatus(HttpServletResponse.SC_OK);
			        Map<String, Map<String, String>> metricsMap = JMXPollUtil.getAllMBeans();
			        String json = gson.toJson(metricsMap, mapType);
			       // System.out.println(json);
			        response.getWriter().write(json);
			        response.flushBuffer();
			        ((Request) request).setHandled(true);
			        return;
			      }
			      response.sendError(HttpServletResponse.SC_NOT_FOUND);
			      response.flushBuffer();
			      //Not handling the request returns a Not found error page.
		
	}

	
}
