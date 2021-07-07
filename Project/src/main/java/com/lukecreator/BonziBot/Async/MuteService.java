package com.lukecreator.BonziBot.Async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Tuple;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Managers.MuteManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

/**
 * Iterate over all muted users and check if they can be unmuted.
 * @author Lukec
 *
 */
public class MuteService extends AutoRepeat {
	
	public MuteService() {
		this.unit = TimeUnit.MINUTES;
		this.initialDelay = 1;
		this.delay = 2;
	}

	@Override
	public void run(BonziBot bb, JDA jda) {
		long now = System.currentTimeMillis();
		
		// Queue up items to remove after the iterations.
		List<Tuple<Long, Long>> toRemove = new ArrayList<Tuple<Long, Long>>();
		
		// All muted entries in BonziBot.
		HashMap<Long, HashMap<Long, Long>> entries = bb.mutes.data;
		
		for(Map.Entry<Long, HashMap<Long, Long>> guildEntry: entries.entrySet()) {
			
			Guild guild = jda.getGuildById(guildEntry.getKey().longValue());
			if(guild == null) {
				toRemove.add(new Tuple<Long, Long>(guildEntry.getKey(), null));
				continue;
			}
			
			GuildSettings settings = bb.guildSettings.getSettings(guild);
			
			Role mutedRole = guild.getRoleById(settings.mutedRole);
			if(mutedRole == null) {
				toRemove.add(new Tuple<Long, Long>(guild.getIdLong(), null));
				continue;
			}
			
			for(Map.Entry<Long, Long> userEntry: guildEntry.getValue().entrySet()) {
				long userId = userEntry.getKey().longValue();
				long timeEnds = userEntry.getValue().longValue();
				
				if(timeEnds == MuteManager.PERM)
					continue;
				if(timeEnds > now)
					continue;
				
				guild.removeRoleFromMember(userId, mutedRole).queue(null, fail -> {});
				toRemove.add(new Tuple<Long, Long>(guild.getIdLong(), userId));
			}
		}
		
		if(!toRemove.isEmpty()) {
			for(Tuple<Long, Long> remove: toRemove) {
				long g = remove.getA().longValue();
				Long u = remove.getB();
				if(u == null) {
					entries.remove(g);
					continue;
				}
				HashMap<Long, Long> e2 = entries.get(g);
				e2.remove(u.longValue());
				entries.put(g, e2);
			}
			bb.mutes.data = entries;
		}
	}
}
