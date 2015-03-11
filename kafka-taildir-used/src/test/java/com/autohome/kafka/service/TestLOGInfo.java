package com.autohome.kafka.service;




import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.autohome.kafka.ServerBootstrap;

public class TestLOGInfo {
	 private static final Logger LOG = LoggerFactory.getLogger(ServerBootstrap.class);
	@Before
	public void setUp() throws Exception {
		LOG.info("log write");
	}

	@Test
	public void test() {
//		fail("Not yet implemented");
		LOG.info("log test");
	}
	@Test
	public void testLogWrite(){
		LOG.info("file sis");
	}

}
