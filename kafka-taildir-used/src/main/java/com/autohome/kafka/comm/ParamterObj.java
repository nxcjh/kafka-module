package com.autohome.kafka.comm;

public class ParamterObj {
	private String topic;
	private String dir;
	private String pattfile;
	
	private String broker_list;
	private boolean reconstruct;
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public String getPattfile() {
		return pattfile;
	}
	public void setPattfile(String pattfile) {
		this.pattfile = pattfile;
	}
	public String getBroker_list() {
		return broker_list;
	}
	public void setBroker_list(String broker_list) {
		this.broker_list = broker_list;
	}
	public boolean isReconstruct() {
		return reconstruct;
	}
	public void setReconstruct(boolean reconstruct) {
		this.reconstruct = reconstruct;
	}
	
	
	
	
	
}
