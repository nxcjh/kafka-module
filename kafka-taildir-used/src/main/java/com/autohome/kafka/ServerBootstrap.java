package com.autohome.kafka;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
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
	public ServerBootstrap(){
		
	}
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		
		PropertyConfigurator.configure(System.getProperty("log4j.configuration")); 
		Properties prop = new Properties();
		InputStream in = new BufferedInputStream(new FileInputStream(args[0]));
		prop.load(in);
		Enumeration enum1 = prop.propertyNames();//得到配置文件的名字
	    while(enum1.hasMoreElements()) {
	          String strKey = (String) enum1.nextElement();
	         LOG.info("server.properties "+strKey + "=" + prop.getProperty(strKey));
	    }
		//设置 配置 属性
		param.setTopic(prop.getProperty(KafkaConfiguration.TOPIC_KEY));
		param.setDir(prop.getProperty(KafkaConfiguration.DIR_KEY));
		param.setPattfile(prop.getProperty(KafkaConfiguration.PATTERN_KEY));
		//创建 文件夹 监控 , 初始化producer实例
		KafkaAgentServer kas = new KafkaAgentServer(param,prop);
		//进行 producer, watecherdir, tailfile, hafile的代理 设置初始化
		if(Boolean.parseBoolean(prop.getProperty(KafkaConfiguration.RE_CONSTRUTOR_KEY))){
			kas.reinit();
		}else{
			kas.init();
		}
		LOG.info("##############################################");
		LOG.info("########   ServerBootstrap start  ############");
		LOG.info("##############################################");
		//开启 线程
		kas.start();	
	}
	
}
