package com.lukecreator.BonziBot.GuiAPI;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;

/**
 * Limit one per Entity, represents a imited
 *  size list which removes the element at
 *    index 0 when the capacity is hit.
 */
public class AllocGuiList {
	
	static final int GLOBAL_LIMIT = 10;
	
	List<GuiContainer> guis;
	
	public AllocGuiList() {
		this.guis = new ArrayList<GuiContainer>(GLOBAL_LIMIT);
	}
	
	/**
	 * Move the object at the specified index to the front of the list.
	 * Used to prevent a GUI from expiring as long as it's being interacted with.
	 * @param index
	 */
	public void front(int index) {
		guis.add(guis.remove(index));
	}
	public boolean hasMessageId(long mid) {
		for(GuiContainer gui: this.guis) {
			if(gui.hasSentMessage && gui.messageId == mid)
				return true;
		}
		return false;
	}
	public int size() {
		return this.guis.size();
	}
	public void addNew(GuiContainer gui) {
		if(this.guis.size() >= GLOBAL_LIMIT) {
			this.guis.remove(0).disableSilent(); // disable when it falls out of scope
		}
		this.guis.add(gui);
	}
	public void clear() {
		for(GuiContainer gc: this.guis)
			gc.disableSilent(); // disable these when they fall out of scope too so that they can be removed from the animationservice
		this.guis.clear();
	}
	
	/*public void onReactionAdd(ReactionEmote react, long messageId, User executor) {
		for(GuiContainer guiContainer: guis) {
			if(!guiContainer.hasSentMessage)
				continue;
			if(guiContainer.messageId == -1)
				continue;
			if(guiContainer.messageId != messageId)
				continue;
			guiContainer.onReaction(react, executor);
		}
	}*/
	public void handleInteraction(ButtonClickEvent event, long messageId, User executor) {
		
		for(int i = 0; i < this.guis.size(); i++) {
			GuiContainer gui = this.guis.get(i);
			if(!gui.hasSentMessage)
				continue;
			if(gui.messageId == -1)
				continue;
			if(gui.messageId != messageId)
				continue;
			this.front(i);
			gui.onAction(event);
			return;
		}
	}
	public void handleInteraction(SelectionMenuEvent event, long messageId, User executor) {
		for(int i = 0; i < this.guis.size(); i++) {
			GuiContainer gui = this.guis.get(i);
			if(!gui.hasSentMessage)
				continue;
			if(gui.messageId == -1)
				continue;
			if(gui.messageId != messageId)
				continue;
			this.front(i);
			gui.onAction(event);
			return;
		}
	}
}
