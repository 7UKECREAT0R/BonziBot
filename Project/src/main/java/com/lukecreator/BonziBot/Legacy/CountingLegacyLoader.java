package com.lukecreator.BonziBot.Legacy;

import java.util.HashMap;
import java.util.Map.Entry;

import com.lukecreator.BonziBot.Data.DataSerializer;

public class CountingLegacyLoader {
	
	public HashMap<Long, Integer> result = new HashMap<Long, Integer>();
	
	public void execute() {
		@SuppressWarnings("unchecked")
		HashMap<Long, Integer> cg = (HashMap<Long, Integer>)DataSerializer.retrieveObject("countingGames");
		
		// bound to 1 minimum
		for(Entry<Long, Integer> entry: cg.entrySet()) {
			if(entry.getValue() < 1)
				entry.setValue(1);
		}
		
		this.result = cg;
		return;
	}
	
}
