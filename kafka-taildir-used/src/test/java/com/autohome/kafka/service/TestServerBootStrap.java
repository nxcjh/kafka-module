package com.autohome.kafka.service;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.autohome.kafka.ServerBootstrap;
import com.autohome.kafka.conf.KafkaConfiguration;

public class TestServerBootStrap {
	Properties prop = new Properties();
	
	@Before
	public void setUp() throws Exception {
		ServerBootstrap sb =new ServerBootstrap();
	}

	@Test
	public void test() {}
	
	

}
