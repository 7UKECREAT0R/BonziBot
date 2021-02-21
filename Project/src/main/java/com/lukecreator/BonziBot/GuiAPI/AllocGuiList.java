package com.lukecreator.BonziBot.GuiAPI;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;

/**
 * Limit one per Entity, represents a imited
 *  size list which removes the element at
 *    index 0 when the capacity is hit.
 */
public class AllocGuiList {
	
	static final int GLOBAL_LIMIT = 10;
	
	List<GuiContainer> guis;
	
	public AllocGuiList() {
		guis = new ArrayList<GuiContainer>(GLOBAL_LIMIT);
	}
	
	public int size() {
		return guis.size();
	}
	public void addNew(GuiContainer gui) {
		if(guis.size() >= GLOBAL_LIMIT)
			guis.remove(0);
		guis.add(gui);
	}
	public void clear() {
		guis.clear();
	}
	
	public void onReactionAdd(ReactionEmote react, long messageId, User executor) {
		for(GuiContainer guiContainer: guis) {
			if(!guiContainer.hasSentMessage)
				continue;
			if(guiContainer.messageId == -1)
				continue;
			if(guiContainer.messageId != messageId)
				continue;
			guiContainer.onReaction(react, executor);
		}
	}
}
