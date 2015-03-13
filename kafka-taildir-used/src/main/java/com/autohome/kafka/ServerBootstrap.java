package com.autohome.kafka;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;









import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.autohome.kafka.comm.ParamterObj;
import com.autohome.kafka.conf.KafkaConfiguration;


/**
 * 项目入口启动类
 * 
 * @author yaogang 2014-09-19
 *
 */
public class ServerBootstrap {
	private static final Logger LOG = Logger.getLogger(ServerBootstrap.class);
	private static ParamterObj param = new ParamterObj();
	private static KafkaAgentServer kas;

	
	public ServerBootstrap(){
		
	}
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		
		PropertyConfigurator.configure(System.getProperty("log4j.configuration")); 
		
		//load配置文件
		configure(args);
		//初始化
		setup();
		LOG.info("##############################################");
		LOG.info("########   ServerBootstrap start  ############");
		LOG.info("##############################################");
		//开启 线程
		kas.start();	
	}
	
	
	
	public static void configure(String[] args) throws IOException{
		//加载配置文件
		Properties prop = new Properties();
		InputStream in = new BufferedInputStream(new FileInputStream(args[0]));
		prop.load(in);
		Enumeration enum1 = prop.propertyNames();//得到配置文件的名字
	    while(enum1.hasMoreElements()) {
	          String strKey = (String) enum1.nextElement();
	         LOG.info("server.properties "+strKey + "=" + prop.getProperty(strKey));
	    }
		//设置 配置 属性
		param.setTopic(prop.getProperty(KafkaConfiguration.TOPIC_KEY));	//topic
		param.setDir(prop.getProperty(KafkaConfiguration.DIR_KEY));		//dir
		param.setPattfile(prop.getProperty(KafkaConfiguration.PATTERN_KEY));//pattern	
		param.setBroker_list(prop.getProperty(KafkaConfiguration.BROKER_LIST));//metadata.broker.list
		param.setReconstruct(Boolean.parseBoolean(prop.getProperty(KafkaConfiguration.RE_CONSTRUTOR_KEY)));
	}
	
	//开始前的初始化
	public static void setup() throws ClassNotFoundException, IOException{
		kas = new KafkaAgentServer(param);
		if(param.isReconstruct()){
			kas.reinit();
		}else{
			kas.init();
		}
	}
	
}
