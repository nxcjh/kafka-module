package com.autohome.kafka.ha;

import java.io.Serializable;

public class FileOffsetObj implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String path = null;	//路径
	private String filename = null; //文件名
	private String fullfileName;//文件全路径名
	private long offset;//offset偏移量
	private boolean on;// 是否再读
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public FileOffsetObj(){
		
	}
	public FileOffsetObj(long offset){
		this.offset = offset;
	}
	
	public String getFullfileName() {
		return fullfileName;
	}
	public void setFullfileName(String fullfileName) {
		this.fullfileName = fullfileName;
	}
	public long getOffset() {
		return offset;
	}
	public void setOffset(long offset) {
		this.offset = offset;
	}
	public boolean isOn() {
		return on;
	}
	public void setOn(boolean on) {
		this.on = on;
	}
	

}
