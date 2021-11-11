package com.lukecreator.BonziBot.Gui;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiNewline;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

/**
 * The base menu, can redirect to all of the categories.
 */
public class GuiHelpMenu extends Gui {
	
	public static final int CPP = 8; // Commands per page.
	
	public boolean adminMode = false;
	
	public GuiHelpMenu(boolean adminMode) {
		super();
		this.adminMode = adminMode;
	}
	
	public CommandCategory fromButtonId(String id) {
		switch(id) {
		case "c0": return CommandCategory.FUN;
		case "c1": return CommandCategory.COINS;
		case "c2": return CommandCategory.MODERATION;
		case "c3": return CommandCategory.UTILITIES;
		case "c4": return CommandCategory.MUSIC;
		case "c5": return CommandCategory.UPGRADE;
		case "c6": return CommandCategory._HIDDEN;
		// should NEVER happen since it's
		// undefined to be anything else.
		default: return CommandCategory.FUN;
		}
	}
	
	@Override
	public void initialize(JDA jda) {
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("🎊"), "c0"));
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("🟡"), "c1"));
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("👮"), "c2"));
		this.elements.add(new GuiNewline());
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("🛠️"), "c3"));
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("🎵"), "c4"));
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("🚀"), "c5"));
		if(this.adminMode)
			this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("🛡️"), "c6"));
	}
	
	@Override
	public Object draw(JDA jda) {
		User u = jda.getUserById(this.parent.ownerId);
		
		EmbedBuilder eb;
		if(u != null)
			eb = BonziUtils.quickEmbed("Help Menu" + (this.adminMode ? " - ADMIN" : ""), "", u, Color.magenta);
		else eb = BonziUtils.quickEmbed("Help Menu" + (this.adminMode ? " - ADMIN" : ""), "", Color.magenta);
		
		eb.addField("🎊 Fun", "Fun commands that serve no useful purpose!", true);
		eb.addField("🟡 Coins", "Get rich and buy stuff with the coin commands!", true);
		eb.addField("👮 Moderator", "Moderate and enforce your server law.", true);
		eb.addField("🛠️ Utilities", "Other commands that you can spruce up your server with.", true);
		eb.addField("🎵 Music", "Blast music with your friends at 2AM or set the mood.", true);
		eb.addField("🚀 Upgrades", "Let your members upgrade the server to unlock extra-cool features!", true);
		eb.setFooter("Click the page you want to view.");
		if(this.adminMode) eb.addField("🛡 Admin (Hidden)", "Admin commands.", true);
		return eb.build();
	}
	
	@Override
	public void onButtonClick(String actionId, long executorId, JDA jda) {
		CommandCategory c = fromButtonId(actionId);
		GuiHelpMenuCategory menu = new GuiHelpMenuCategory(c, this.bonziReference);
		this.parent.setActiveGui(menu, jda);
	}
}
