package com.lukecreator.BonziBot.Managers;

import java.util.HashMap;

import com.lukecreator.BonziBot.Constants;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;

import net.dv8tion.jda.api.entities.Guild;

/*
 * Middleman for interacting with server prefixes.
 */
public class PrefixManager implements IStorableData {
	
	public static final String FILE = "prefixes";
	
	HashMap<Long, String> prefixData;
	
	public PrefixManager() {
		prefixData = new HashMap<Long, String>();
	}
	public String getPrefix(Guild guild) {
		return getPrefix(guild.getIdLong());
	}
	public String getPrefix(long guild) {
		if(prefixData.containsKey(guild))
			return prefixData.get(guild);
		else {
			String p = Constants.DEFAULT_PREFIX;
			prefixData.put(guild, p);
			return p;
		}
	}
	public void setPrefix(long guild, String prefix) {
		prefixData.put(guild, prefix);
	}
	public void setPrefix(Guild guild, String prefix) {
		setPrefix(guild.getIdLong(), prefix);
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(prefixData, FILE);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() {
		Object data = DataSerializer.retrieveObject(FILE);
		if(data == null) return;
		prefixData = (HashMap<Long, String>)data;
	}
}