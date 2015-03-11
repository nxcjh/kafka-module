package com.autohome.kafka.core.downstream;



/**
 * 
 * @author yaogang 2014-09-19
 *
 * @param <T>
 */
public interface Send<T> {
	public void init();
	public void send(T o);
//	public void sendList(List<T> o);
	public void rebuilt();
	
}
