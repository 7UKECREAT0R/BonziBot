package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.HashMap;

import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.IStorableData;

import net.dv8tion.jda.api.entities.Guild;

/*
 * Manages GuildSettings objects.
 */
public class GuildSettingsManager implements IStorableData {
	
	public HashMap<Long, GuildSettings> settings = new HashMap<Long, GuildSettings>();
	
	public GuildSettings getSettings(Guild g) {
		if(g == null) return null;
		return getSettings(g.getIdLong());
	}
	public void setSettings(Guild g, GuildSettings gs) {
		setSettings(g.getIdLong(), gs);
	}
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
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject("guildSettings");
		if(o == null) return;
		settings = (HashMap<Long, GuildSettings>)o;
	}
}
