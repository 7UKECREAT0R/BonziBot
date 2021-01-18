package com.lukecreator.BonziBot.Async;

import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;

/*
 * Any classes which extend this will automatically
 * 		be scheduled for repeated execution.
 */
public abstract class AutoRepeat implements Runnable {
	
	public BonziBot botInstance;
	
	TimeUnit unit;
	int initialDelay, delay;
	
	@Override
	public void run() {}
	
	public int getInitial() {
		return initialDelay;
	}
	public int getDelay() {
		return delay;
	}
	public TimeUnit getUnit() {
		return unit;
	}
}
