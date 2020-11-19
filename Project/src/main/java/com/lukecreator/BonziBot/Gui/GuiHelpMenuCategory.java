package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandSystem;
import com.lukecreator.BonziBot.GuiAPI.GuiPaging;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class GuiHelpMenuCategory extends GuiPaging {
	
	CommandCategory category;
	Command[] allCommands;
	
	public GuiHelpMenuCategory(CommandCategory category, BonziBot bonzi) {
		super();
		this.category = category;
		CommandSystem system = bonzi.commands;
		List<Command> targets = system
			.getCommandsWithCategory(category);
		this.allCommands = (Command[]) targets
			.toArray(new Command[targets.size()]);
		
		int len = this.allCommands.length;
		int div = GuiHelpMenu.CPP;
		
		// Truncate + 1 = Round Up
		this.maxPage = (len / div) + 1;
	}
	
	@Override
	public MessageEmbed draw(JDA jda) {
		
		String catName = category.name;
		User u = jda.getUserById(this.parent.ownerId);
		
		EmbedBuilder eb;
		if(u != null)
			eb = BonziUtils.quickEmbed("Help Menu - " + catName, "", u, Color.magenta);
		else eb = BonziUtils.quickEmbed("Help Menu" + catName, "", Color.magenta);
		
		int index = this.getCurrentIndex();
		int start = index * GuiHelpMenu.CPP;
		int end = start + GuiHelpMenu.CPP;
		if(end > allCommands.length)
			end = allCommands.length;
		
		String prefix = this.prefixOfLocation;
		if(prefix == null)
			prefix = Constants.DEFAULT_PREFIX;
		
		for(int i = start; i < end; i++) {
			Command current = allCommands[i];
			String icon = current.unicodeIcon;
			String name = current.name;
			String desc = current.description;
			String usage = prefix + current.usage;
			eb.addField(icon + " " + name,
				usage + "\n" + desc, false);
		}
		
		if((this.maxPage - this.minPage) > 0)
			eb.setFooter("Page " + this.getPageString() + " - React to see other pages.");
		else eb.setFooter("This is all the available commands.");
		
		
		return eb.build();
	}
}
