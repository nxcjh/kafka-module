package com.autohome.kafka.core.downstream.imp;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.autohome.kafka.conf.KafkaConfiguration;
import com.autohome.kafka.core.downstream.SendAbstract;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;


public class KafkaProduceSendImpbackup<KeyedMessage> extends SendAbstract<KeyedMessage> {
	
	public Logger LOG = Logger.getLogger(KafkaProduceSendImpbackup.class);
	public Properties props = null;
	public ProducerConfig config =null;
	public Producer<String, String> producer=null;
	public KeyedMessage data = null; 
	
	
	 public KafkaProduceSendImpbackup(Properties prop){
		 super();
		 this.props = prop;
	 }
	 public void init(){
		    //192.168.7.5-7
		    props.put("metadata.broker.list", this.props.get(KafkaConfiguration.BROKER_LIST_KEY));
		    props.put("serializer.class", "kafka.serializer.StringEncoder");
		    props.put("partitioner.class", "com.autohome.kafka.comm.SimplePartitioner");
		    props.put("request.required.acks", "1");
		    props.put(KafkaConfiguration.PRODUCER_TYPE_KEY, KafkaConfiguration.PRODUCER_TYPE_VALUE);
		    props.put(KafkaConfiguration.BATCH_NUM_KEY, KafkaConfiguration.BATCH_NUM_VALUE);
		    config = new ProducerConfig(props);
		    producer = new Producer<String, String>(config);
	 }
	

	public void send(KeyedMessage o) {
		// TODO Auto-generated method stub
		producer.send((kafka.producer.KeyedMessage<String, String>) o);
	}
	public void rebuilt(){
		  
		  this.init();
	}
	public void close(final Producer producer){
		producer.close();
	}

}
