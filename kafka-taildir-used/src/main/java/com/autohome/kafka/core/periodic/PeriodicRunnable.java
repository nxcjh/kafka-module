package com.autohome.kafka.core.periodic;

public class PeriodicRunnable implements Runnable{
	
	private PeriodicObject po = null;
	 private volatile boolean done = false;
	 private volatile boolean running = false;
	 private long sleep_ms;
	 
	 
	public PeriodicRunnable(PeriodicObject po){
		this.po = po;
		this.running = true;
		
	}
	public void run() {
		while(running){
			po.periodic();
			try {
				if(po.sleep_ms()>0){
					Thread.sleep(po.sleep_ms());
				}
			} catch (InterruptedException e) {
				//catch block 中断异常需要特别处理
				e.printStackTrace();
			}
		}
	}
	public void stop(){
		 this.running  = false;
	 }
	
}
