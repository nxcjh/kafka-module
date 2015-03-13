package com.autohome.kafka.mbean;

public interface KafkaProducerConfigurationMBean {

	public String getBroker_list();
	public void setBroker_list(String broker_list);
	public String getProducer_type();
	public void setProducer_type(String producer_type);
	public int getBatch_num_message();
	public void setBatch_num_message(int batch_num_message);
	public int getRetry_backoff_ms();
	public void setRetry_backoff_ms(int retry_backoff_ms);
	public int getMessage_send_max_retries();
	public void setMessage_send_max_retries(int message_send_max_retries);
}
