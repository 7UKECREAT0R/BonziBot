package com.lukecreator.BonziBot.Managers;

import java.util.HashMap;

import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.IStorableData;

/*
 * Manages GuildSettings objects.
 */
public class GuildSettingsManager implements IStorableData {
	
	public HashMap<Long, GuildSettings> settings = new HashMap<Long, GuildSettings>();
	
	public GuildSettings getSettings(long gId) {
		if(settings.containsKey(gId))
			return settings.get(gId);
		GuildSettings gs = new GuildSettings();
		settings.put(gId, gs);
		return gs;
	}
	public void setSettings(long gId, GuildSettings gs) {
		settings.put(gId, gs);
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(settings, "guildSettings");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() {
		Object o = DataSerializer.retrieveObject("guildSettings");
		if(o == null) return;
		settings = (HashMap<Long, GuildSettings>)o;
	}
}
