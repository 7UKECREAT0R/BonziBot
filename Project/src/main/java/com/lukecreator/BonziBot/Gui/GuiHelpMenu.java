package com.lukecreator.BonziBot.Gui;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

/*
 * The base menu, can redirect to all of the categories.
 */
public class GuiHelpMenu extends Gui {
	
	public static final int CPP = 5; // Commands per page.
	
	public CommandCategory fromButtonId(int id) {
		switch(id) {
		case 0: return CommandCategory.FUN;
		case 1: return CommandCategory.COINS;
		case 2: return CommandCategory.MODERATION;
		case 3: return CommandCategory.UTILITIES;
		case 4: return CommandCategory.MUSIC;
		case 5: return CommandCategory.UPGRADE;
		
		// should NEVER happen since it's
		// undefined to be anything else.
		default: return CommandCategory.FUN;
		}
	}
	
	@Override
	public void initialize(JDA jda) {
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🎊"), 0));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🟡"), 1));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("👮"), 2));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🛠️"), 3));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🎵"), 4));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🚀"), 5));
	}
	
	@Override
	public MessageEmbed draw(JDA jda) {
		User u = jda.getUserById(this.parent.ownerId);
		
		EmbedBuilder eb;
		if(u != null)
			eb = BonziUtils.quickEmbed("Help Menu", "", u, Color.magenta);
		else eb = BonziUtils.quickEmbed("Help Menu", "", Color.magenta);
		
		eb.addField("🎊 Fun", "Fun commands that serve no useful purpose!", true);
		eb.addField("🟡 Coins", "Get rich and buy stuff with the coin commands!", true);
		eb.addField("👮 Moderator", "Moderate and enforce your server law.", true);
		eb.addField("🛠️ Utilities", "Other commands that you can spruce up your server with.", true);
		eb.addField("🎵 Music", "Blast music with your friends at 2AM or set the mood.", true);
		eb.addField("🚀 Upgrades", "Let your members upgrade the server to unlock extra-cool features!", true);
		eb.setFooter("React with the page you want to view.");
		return eb.build();
	}
	
	@Override
	public void onAction(int buttonId, JDA jda) {
		CommandCategory c = fromButtonId(buttonId);
		GuiHelpMenuCategory menu = new GuiHelpMenuCategory(c, this.bonziReference);
		this.parent.setActiveGui(menu, jda);
	}
}
