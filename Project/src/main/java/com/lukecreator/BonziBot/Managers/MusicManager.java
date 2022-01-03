package com.lukecreator.BonziBot.Managers;

import java.util.Collection;
import java.util.HashMap;

import com.lukecreator.BonziBot.Music.MusicQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;


/**
 * Manages MusicQueues per guild.
 * @author Lukec
 */
public class MusicManager {
	
	private HashMap<Long, MusicQueue> queues = new HashMap<Long, MusicQueue>();
	
	private static MusicQueue createQueue(Guild guild, AudioPlayerManager apm) {
		AudioPlayer player = apm.createPlayer();
		return new MusicQueue(guild, player);
	}
	
	public MusicQueue getQueue(long guildId, JDA jda, AudioPlayerManager apm) {
		if(!queues.containsKey(guildId))
			this.queues.put(guildId, MusicManager.createQueue(jda.getGuildById(guildId), apm));
		
		return this.queues.get(guildId);
	}
	public MusicQueue getQueue(Guild guild, AudioPlayerManager apm) {
		long guildId = guild.getIdLong();
		if(!queues.containsKey(guildId))
			this.queues.put(guildId, MusicManager.createQueue(guild, apm));
		
		return this.queues.get(guildId);
	}
	
	public Collection<MusicQueue> getAllQueues() {
		return this.queues.values();
	}
}
