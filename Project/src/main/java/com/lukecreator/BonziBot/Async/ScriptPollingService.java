package com.lukecreator.BonziBot.Async;

import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Managers.ScriptCache;

import net.dv8tion.jda.api.JDA;

/**
 * Polls for scripts that need to be run every minute.
 * @author Lukec
 */
public class ScriptPollingService extends AutoRepeat {

	public ScriptPollingService() {
		this.unit = TimeUnit.MILLISECONDS;
		this.initialDelay = ScriptCache.POLL_INTERVAL;
		this.delay = ScriptCache.POLL_INTERVAL;
	}
	
	@Override
	public void run(BonziBot bb, JDA jda) {
		ScriptCache.pollTimedScripts(bb, jda);
	}
	
}
