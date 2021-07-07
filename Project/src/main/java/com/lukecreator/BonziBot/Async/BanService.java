package com.lukecreator.BonziBot.Async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Tuple;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Iterate over all temp-banned users and check if they can be unbanned.
 * @author Lukec
 *
 */
public class BanService extends AutoRepeat {
	
	public BanService() {
		this.unit = TimeUnit.MINUTES;
		this.initialDelay = 1;
		this.delay = 2;
	}

	@Override
	public void run(BonziBot bb, JDA jda) {
		long now = System.currentTimeMillis();
		
		// Queue up items to remove after the iterations.
		List<Tuple<Long, Long>> toRemove = new ArrayList<Tuple<Long, Long>>();
		// Queue up users to be bulk unbanned.
		List<Tuple<Guild, String>> toUnban = new ArrayList<Tuple<Guild, String>>();
		
		// All ban entries in BonziBot.
		HashMap<Long, HashMap<Long, Long>> entries = bb.bans.data;
		
		for(Map.Entry<Long, HashMap<Long, Long>> guildEntry: entries.entrySet()) {
			
			Guild guild = jda.getGuildById(guildEntry.getKey().longValue());
			if(guild == null) {
				toRemove.add(new Tuple<Long, Long>(guildEntry.getKey(), null));
				continue;
			}
			
			for(Map.Entry<Long, Long> userEntry: guildEntry.getValue().entrySet()) {
				long userId = userEntry.getKey().longValue();
				long timeEnds = userEntry.getValue().longValue();
				
				if(timeEnds > now)
					continue;
				
				toUnban.add(new Tuple<Guild, String>(guild, String.valueOf(userId)));
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
			bb.bans.data = entries;
		}
		
		if(!toUnban.isEmpty()) {
			for(Tuple<Guild, String> unban: toUnban) {
				unban.getA().unban(unban.getB()).reason
					("User's temporary ban expired.").queue();
			}
		}
	}
}
