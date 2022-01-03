package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.HashMap;

import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Legacy.CountingLegacyLoader;

import net.dv8tion.jda.api.entities.Guild;

public class CountingManager implements IStorableData {
	
	public static final String FILE = "nextCountingNumbers";
	HashMap<Long, Integer> nextNumbers = new HashMap<Long, Integer>();
	
	public int getNextNumber(long guildId) {
		if(nextNumbers.containsKey(guildId))
			return nextNumbers.get(guildId);
		else {
			nextNumbers.put(guildId, 1);
			return 1;
		}
	}
	public int getNextNumber(Guild guild) {
		return this.getNextNumber(guild.getIdLong());
	}
	public void setNextNumber(long guildId, int number) {
		this.nextNumbers.put(guildId, number);
	}
	public void setNextNumber(Guild guild, int number) {
		this.setNextNumber(guild.getIdLong(), number);
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(nextNumbers, FILE);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject(FILE);
		if(o != null)
			this.nextNumbers = (HashMap<Long, Integer>)o;
	}
	
	public void loadLegacy() {
		CountingLegacyLoader cll = new CountingLegacyLoader();
		cll.execute();
		
		this.nextNumbers = cll.result;
		
		InternalLogger.print("Loaded legacy custom commands.");
	}
}
