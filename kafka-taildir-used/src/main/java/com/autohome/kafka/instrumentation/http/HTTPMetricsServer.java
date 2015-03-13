package com.autohome.kafka.instrumentation.http;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;



public class HTTPMetricsServer {
	private static final Logger LOG = Logger.getLogger(HTTPMetricsServer.class);
	private static Server jettyServer;
	private static int port = 50026;
	public static int DEFAULT_PORT = 41414;
	public static String CONFIG_PORT = "port";
	
	public static void start(){
		jettyServer = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setReuseAddress(true);
		connector.setPort(port);
		jettyServer.setConnectors(new Connector[]{connector});
		jettyServer.setHandler(new HTTPMetricsHandler());
		
		try {
			jettyServer.start();
			while(!jettyServer.isStarted()){
				Thread.sleep(500);
			}
		} catch (Exception e) {
			 LOG.error("Error starting Jetty. JSON Metrics may not be available.", e);
		}
	}
	
	
	public static void stop(){
		try{
			jettyServer.stop();
			jettyServer.join();
		}catch(Exception e){
			 LOG.error("Error stopping Jetty. JSON Metrics may not be available.", e);
		}
	}
	
	public static void main(String[] args) {
		start();
	}
}
