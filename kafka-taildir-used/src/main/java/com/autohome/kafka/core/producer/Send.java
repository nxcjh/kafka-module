package com.autohome.kafka.core.producer;

/**
 * 
 * @author nxcjh
 *
 * @param <T>
 */
public interface Send<T> {
	public void init();
	public void send(T o);
//	public void sendList(List<T> o);
	public void rebuilt();
	
}
