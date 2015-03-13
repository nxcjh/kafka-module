package com.autohome.kafka.core.producer.imp;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.autohome.kafka.comm.ParamterObj;
import com.autohome.kafka.conf.KafkaConfiguration;
import com.autohome.kafka.core.producer.SendAbstract;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;


public class KafkaProduceSendListImp extends SendAbstract<List<kafka.producer.KeyedMessage<String, String>>>{

	public Logger LOG = Logger.getLogger(KafkaProduceSendListImp.class);
	public ParamterObj param = null;
	public ProducerConfig config =null;
	public Producer producer=null;
	
	 public KafkaProduceSendListImp(ParamterObj param){
		 super();
		 this.param = param;
		
		 
	 }
	 public void init(){
		    //192.168.7.5-7
		 	Properties props = new Properties();  
		 	
		    props.put("metadata.broker.list", param.getBroker_list());
		    props.put("serializer.class", "kafka.serializer.StringEncoder");
		    props.put("partitioner.class", "com.autohome.kafka.comm.SimplePartitioner");
		    props.put("request.required.acks", "1");
		    props.put("message.send.max.retries", "100");
		    
		    props.put(KafkaConfiguration.PRODUCER_TYPE_KEY, KafkaConfiguration.PRODUCER_TYPE_VALUE);
		    props.put(KafkaConfiguration.BATCH_NUM_KEY, KafkaConfiguration.BATCH_NUM_VALUE);
		    config = new ProducerConfig(props);
		    producer = new Producer(config);
		
		   
	 }
	 
	public void rebuilt(){
		  this.close(producer);
		  this.init();
	}
	public void close(final Producer producer){
		if(producer!=null){
			producer.close();
		}
	}
	public void send(List<kafka.producer.KeyedMessage<String, String>> o) {
		producer.send(o);
	}
	

}
