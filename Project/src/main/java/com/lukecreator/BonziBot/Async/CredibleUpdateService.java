package com.lukecreator.BonziBot.Async;

import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.Credible;

import net.dv8tion.jda.api.JDA;

/**
 * Cycles the Credible class's tokens every couple of seconds to ensure they stay legitimate.
 * @author Lukec
 *
 */
public class CredibleUpdateService extends AutoRepeat {
	
	public CredibleUpdateService() {
		this.initialDelay = 3;
		this.delay = 3;
		this.unit = TimeUnit.SECONDS;
	}
	
	@Override
	public void run(BonziBot bb, JDA jda) {
		Credible.cycle();
	}
}
