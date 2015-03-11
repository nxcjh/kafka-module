package com.autohome.kafka.core.downstream;

import java.util.List;

public abstract class SendAbstract<T> implements Send<T> {
	public SendAbstract(){
//		init();
	}
	public void sendList(List<T> o){
		
	}
}
