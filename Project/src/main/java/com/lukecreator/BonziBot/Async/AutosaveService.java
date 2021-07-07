package com.lukecreator.BonziBot.Async;

import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;

import net.dv8tion.jda.api.JDA;

public class AutosaveService extends AutoRepeat {
	
	public AutosaveService() {
		this.initialDelay = 1;
		this.delay = 5;
		this.unit = TimeUnit.MINUTES;
	}
	
	@Override
	public void run(BonziBot bb, JDA jda) {
		bb.saveData();
	}
}
