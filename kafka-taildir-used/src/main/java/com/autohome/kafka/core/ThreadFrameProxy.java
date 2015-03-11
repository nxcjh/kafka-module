package com.autohome.kafka.core;

public  class ThreadFrameProxy implements Runnable {
	private Runnable _real = null;
	ThreadFrameProxy(Runnable _real){
		this._real = _real;
	}
	public void run() {
		// TODO Auto-generated method stub
		this._real.run();
	}

}
