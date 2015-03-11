package com.autohome.kafka.core.periodic;

public interface PeriodicObject {
	
	public void periodic();
	public long sleep_ms();
}
