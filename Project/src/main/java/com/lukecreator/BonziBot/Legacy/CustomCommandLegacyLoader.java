package com.lukecreator.BonziBot.Legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lukecreator.BonziBot.CustomCommand;
import com.lukecreator.BonziBot.CustomCommandManager;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.TagData;

public class CustomCommandLegacyLoader {
	
	public CustomCommandLegacyLoader() {}
	
	public List<TagData> resultPublic = null;
	public HashMap<Long, List<TagData>> resultPrivate = null;
	
	public void execute() {
		resultPublic = new ArrayList<TagData>();
		resultPrivate = new HashMap<Long, List<TagData>>();
		CustomCommandManager ccm = (CustomCommandManager)DataSerializer.retrieveObject("cc");
		
		for(CustomCommand cc: ccm.global) {
			TagData tag = new TagData(cc);
			resultPublic.add(tag);
		}
		for(Map.Entry<Long, List<CustomCommand>> mlcc: ccm.specific.entrySet()) {
			List<CustomCommand> lcc = mlcc.getValue();
			long guildId = mlcc.getKey();
			
			List<TagData> guildResult = new ArrayList<TagData>();
			for(CustomCommand cc: lcc) {
				TagData tag = new TagData(cc);
				guildResult.add(tag);
			}
			resultPrivate.put(guildId, guildResult);
		}
		
		return;
	}
}
