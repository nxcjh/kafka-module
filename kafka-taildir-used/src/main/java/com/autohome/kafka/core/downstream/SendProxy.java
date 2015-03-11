package com.autohome.kafka.core.downstream;

import org.apache.log4j.Logger;


/**
 * 发送代理
 * @author yaogang 2014-09-19
 *
 */
public class SendProxy<T> implements Send<T> {
	
	final Logger LOG = Logger.getLogger(SendProxy.class);
	private Send<T> _realSend = null;
	private static SendFactoryImp sfi = new SendFactoryImp();
	
	public SendProxy(){
		
	}
	public SendProxy(Class c){
		try {
			_realSend = sfi.getSendByClassName(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static SendProxy getSendProxyByClass(Class c) throws Exception{
		
		return new SendProxy(c);
	}
	public void send(T o) {
		// 加入统计信息
		_realSend.send(o);
	}
	

	public void init() {
		_realSend.init();
	}
	public void rebuilt() {
		LOG.info("Send proxy init ");
		_realSend.rebuilt();
	}
	public void setRealSend(Send _realSend){
		this._realSend = _realSend;
	}
	

}
