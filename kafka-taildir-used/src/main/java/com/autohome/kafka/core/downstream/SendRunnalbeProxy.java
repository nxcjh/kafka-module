package com.autohome.kafka.core.downstream;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;




public class SendRunnalbeProxy<T> implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(SendRunnalbeProxy.class);
	private Send<T> s = null;
	private volatile boolean running = false;
	final BlockingQueue<T> sync;
	private int count = 0;
	
	public SendRunnalbeProxy(Send<T> send,BlockingQueue<T> sync){
		this.s = send;
		this.sync = sync;
		this.running = true;
	}
	public void run() {
		T tmpks = null;
		while(running){
			try {
				if(tmpks==null){// 表明上次读取成功，从sync中读取新的内容
					tmpks = this.sync.poll(100, TimeUnit.MILLISECONDS);
				} // 如果不为空，证明上次循环发送失败，这次继续发送上次没有发送成功的数据。
				send(tmpks);// 如果在这个地方出现异常，发送数据失败，那么tmpks就不会设置成空，系统就不会从sync中提取数据，会重复发送失败的数据
				tmpks = null;//发送成功时设置为空，从sync中读取后面的数据
				count=0;
			} catch (Throwable e) {
				e.printStackTrace();
				LOG.error("kafka send Exception");
				this.retuile();
				count++;
				if(count>50){
					this.running = false;
					LOG.error("Kafka Send Thread is down");
				}
				try {
					Thread.sleep(2000);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		LOG.error("kafka send agent exit");
		System.exit(-1);
	}
	
	/**
	 * 从 producer向broker发送数据
	 * @param tmpks
	 */
	private void send(final T tmpks){
		
			if(tmpks!=null){
					this.s.send(tmpks);
			}
		
	}
	
	private void retuile(){
		
		this.s.rebuilt();
	}
	
}
