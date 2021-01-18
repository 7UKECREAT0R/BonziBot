package com.lukecreator.BonziBot.Async;

import java.util.concurrent.TimeUnit;

public class AutosaveService extends AutoRepeat {
	
	public AutosaveService() {
		this.initialDelay = 1;
		this.delay = 5;
		this.unit = TimeUnit.MINUTES;
	}
	
	@Override
	public void run() {
		this.botInstance.saveData();
	}
}
