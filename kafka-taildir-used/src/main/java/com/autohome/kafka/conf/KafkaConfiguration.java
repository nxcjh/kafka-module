package com.autohome.kafka.conf;

public class KafkaConfiguration extends Configuration {
	public static final String BROKER_LIST_KEY = "metadata.broker.list";
//	public static final String BROKER_LIST_VALUE = "192.168.100.160:9092,192.168.100.142:9092,192.168.100.137:9092";
	public static final String SERIALIZER_CLASS_KEY = "serializer.class";
	public static final String SERIALIZER_CLASS_VALUE="kafka.serializer.StringEncoder";
	public static final String PARTITIONER_CLASS_KEY  = "partitioner.class";
	public static final String PARTITIONER_CLASS_VALUE  = "com.autohome.kafka.comm.SimplePartitioner";
	public static final String PRODUCER_TYPE_KEY = "producer.type";
	public static final String PRODUCER_TYPE_VALUE = "async";
	public static final String BATCH_NUM_KEY = "batch.num.messages";
	public static final String BATCH_NUM_VALUE="100";
	public static final String SERVER_PATH="../conf/server.properties";
	public static final String TOPIC_KEY="topic";
	public static final String DIR_KEY="dir";
	public static final String PATTERN_KEY="pattern";
	public static final String RE_CONSTRUTOR_KEY="isconstruct";
//	public static final String SERVER_PATH="server.properties";
}
