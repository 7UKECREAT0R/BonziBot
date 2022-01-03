package com.lukecreator.BonziBot.Async;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.Music.MusicQueue;

import net.dv8tion.jda.api.JDA;

/**
 * Cleans up unused or bugged music players.
 * @author Lukec
 */
public class MusicCleanupService extends AutoRepeat {
	
	public MusicCleanupService() {
		this.initialDelay = 30;
		this.delay = 30;
		this.unit = TimeUnit.MINUTES;
	}
	
	@Override
	public void run(BonziBot bb, JDA jda) {
		Collection<MusicQueue> checks = bb.music.getAllQueues();
		long ms = System.currentTimeMillis();
		for(MusicQueue queue: checks) {
			if(queue.shouldKill(ms)) {
				InternalLogger.print("Had to kill AudioPlayer for guild: " + queue.guildId);
				queue.stop();
			}
		}
	}

}
