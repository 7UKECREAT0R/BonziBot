package com.lukecreator.BonziBot.Async;

import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;

import net.dv8tion.jda.api.JDA;

/**
 * Any classes which extend this will automatically
 * 		be scheduled for repeated execution.
 */
public abstract class AutoRepeat {
	
	TimeUnit unit;
	int initialDelay, delay;
	
	public abstract void run(BonziBot bb, JDA jda);
	
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
