package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;

public abstract class Gui {
	
	public static final MessageEmbed EMPTY = new EmbedBuilder()
			.setTitle("EMPTY")
			.setDescription("For debugging purposes.")
			.setColor(Color.magenta)
			.build();
	
	public Gui(GuiContainer parent) {
		buttons = new ArrayList<GuiButton>();
		this.parent = parent;
	}
	
	GuiContainer parent;
	List<GuiButton> buttons;
	
	public void receiveReaction(ReactionEmote react) {
		for(GuiButton button: buttons) {
			if(!button.wasClicked(react)) continue;
			int action = button.actionId;
			this.onAction(action);
			this.postAction(action);
		}
	}
	
	// Overridable
	public void onAction(int buttonId) {}
	public void postAction(int buttonId) {}
	
	public MessageEmbed draw() { return EMPTY; }
	
}
