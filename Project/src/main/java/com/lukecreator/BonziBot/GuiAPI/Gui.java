package com.lukecreator.BonziBot.GuiAPI;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;

public abstract class Gui {
	
	// never override or modify these
	protected String prefixOfLocation;
	protected BonziBot bonziReference;
	boolean initialized = false;
	public boolean wasInitialized() {
		return initialized;
	}
	public void hiddenInit(JDA jda, Guild g, BonziBot b) {
		if(buttons == null) {
			buttons = new ArrayList<GuiButton>();
		}
		this.initialize(jda);
		this.prefixOfLocation = b.prefixes.getPrefix(g);
		this.bonziReference = b;
		this.initialized = true;
	}
	public void hiddenInit(JDA jda, User u, BonziBot b) {
		if(buttons == null) {
			buttons = new ArrayList<GuiButton>();
		}
		this.initialize(jda);
		this.prefixOfLocation = Constants.DEFAULT_PREFIX;
		this.bonziReference = b;
		this.initialized = true;
	}
	
	public static final MessageEmbed EMPTY = new EmbedBuilder()
			.setTitle("EMPTY")
			.setDescription("For debugging purposes.")
			.setColor(Color.magenta)
			.build();
	
	public Gui(GuiContainer parent, JDA jda, Guild g, BonziBot b) {
		buttons = new ArrayList<GuiButton>();
		this.hiddenInit(jda, g, b);
		this.parent = parent;
	}
	public Gui(GuiContainer parent, JDA jda, User u, BonziBot b) {
		buttons = new ArrayList<GuiButton>();
		this.hiddenInit(jda, u, b);
		this.parent = parent;
	}
	public Gui() {}
	
	protected GuiContainer parent;
	public List<GuiButton> buttons;
	
	public void receiveReaction(ReactionEmote react) {
		for(GuiButton button: buttons) {
			if(!button.wasClicked(react)) continue;
			int action = button.actionId;
			this.onAction(action, react.getJDA());
			this.postAction(action, react.getJDA());
		}
	}
	
	// Overridable
	public void initialize(JDA jda) {}
	public void onAction(int buttonId, JDA jda) {}
	public void postAction(int buttonId, JDA jda) {}
	
	public MessageEmbed draw(JDA jda) { return EMPTY; }
	
}
