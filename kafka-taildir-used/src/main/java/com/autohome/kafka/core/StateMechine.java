package com.autohome.kafka.core;
/**
 * 状态类，传递更新标志位的，当程序读取了数据时，需要通知到dump类进行计数更新
 * 计数更新之后 设置成false
 * @author root
 *
 */
public class StateMechine {
	private  boolean madeProgress = false;

	public synchronized boolean isMadeProgress() {
		return madeProgress;
	}

	public synchronized void setMadeProgress(boolean madeProgress) {
		this.madeProgress = madeProgress;
	}
	
}
