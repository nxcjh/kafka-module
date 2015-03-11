package com.autohome.kafka.core;

import static org.junit.Assert.*;
import kafka.producer.KeyedMessage;

import org.junit.Before;
import org.junit.Test;

import com.autohome.kafka.core.downstream.Send;
import com.autohome.kafka.core.downstream.SendFactoryImp;
import com.autohome.kafka.core.downstream.SendProxy;
import com.autohome.kafka.core.downstream.imp.KafkaProduceSendImp;

public class TestSendProxy {
//	private SendProxy sendp = new SendProxy<KeyedMessage<String,String>>(KafkaProduceSendImp.class);
//	private static SendFactoryImp sfi = new SendFactoryImp();
//	
	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void test() {
		try {
//			KafkaProduceSendImp send = (KafkaProduceSendImp) sfi.getSendByClassName(KafkaProduceSendImp.class);
//			send.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		fail("Not yet implemented");
	}

}
