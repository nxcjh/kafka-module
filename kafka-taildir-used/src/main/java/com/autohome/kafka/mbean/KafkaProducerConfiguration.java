package com.autohome.kafka.mbean;

public class KafkaProducerConfiguration {

	private String broker_list;
	private String producer_type;
	private int batch_num_message;
	private int retry_backoff_ms;
	private int message_send_max_retries;
	public String getBroker_list() {
		return broker_list;
	}
	public void setBroker_list(String broker_list) {
		this.broker_list = broker_list;
	}
	public String getProducer_type() {
		return producer_type;
	}
	public void setProducer_type(String producer_type) {
		this.producer_type = producer_type;
	}
	public int getBatch_num_message() {
		return batch_num_message;
	}
	public void setBatch_num_message(int batch_num_message) {
		this.batch_num_message = batch_num_message;
	}
	public int getRetry_backoff_ms() {
		return retry_backoff_ms;
	}
	public void setRetry_backoff_ms(int retry_backoff_ms) {
		this.retry_backoff_ms = retry_backoff_ms;
	}
	public int getMessage_send_max_retries() {
		return message_send_max_retries;
	}
	public void setMessage_send_max_retries(int message_send_max_retries) {
		this.message_send_max_retries = message_send_max_retries;
	}
	
	
	
	
}
