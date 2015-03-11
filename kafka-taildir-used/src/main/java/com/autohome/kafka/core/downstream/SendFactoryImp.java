package com.autohome.kafka.core.downstream;


public class SendFactoryImp {
	public static Send _real = null;
	public Send getSendByClassName(Class c) throws Exception{
		if(_real==null){
			_real = (Send) c.newInstance();
		} 
		if(!(_real instanceof Send)){
			throw new Exception("not send.class type");
		}
		
		return _real;
		
	}
}
