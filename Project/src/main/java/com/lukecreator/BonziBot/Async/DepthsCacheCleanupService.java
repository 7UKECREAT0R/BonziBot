package com.lukecreator.BonziBot.Async;

import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Commands.Admin.GetDepthsVideoCommand;

import net.dv8tion.jda.api.JDA;

/**
 * Cycles the Credible class's tokens every couple of seconds to ensure they stay legitimate.
 * @author Lukec
 *
 */
public class DepthsCacheCleanupService extends AutoRepeat {
	
	public DepthsCacheCleanupService() {
		this.initialDelay = 6;
		this.delay = 6;
		this.unit = TimeUnit.HOURS;
	}
	
	@Override
	public void run(BonziBot bb, JDA jda) {
		// clear sublists
		GetDepthsVideoCommand.CACHED_RESULTS.values().forEach(l -> {
			l.clear();
		});
		
		// clear main hashmap
		GetDepthsVideoCommand.CACHED_RESULTS.clear();
	}
}
