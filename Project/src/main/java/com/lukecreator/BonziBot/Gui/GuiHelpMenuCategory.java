package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandSort;
import com.lukecreator.BonziBot.CommandAPI.CommandSystem;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiPaging;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
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
		targets.sort(new CommandSort());
		this.allCommands = (Command[]) targets
			.toArray(new Command[targets.size()]);
		
		int len = this.allCommands.length;
		int div = GuiHelpMenu.CPP;
		
		// Truncate + 1 = Round Up
		// (But only if a%b!=0)
		this.maxPage = len / div;
		if(len % div != 0)
			this.maxPage++;
	}
	
	@Override
	public void initialize(JDA jda) {
		super.initialize(jda);
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("📁"), "return").withColor(GuiButton.ButtonColor.GRAY));
	}
	
	@Override
	public Object draw(JDA jda) {
		
		String catName = this.category.name;
		User u = jda.getUserById(this.parent.ownerId);
		
		EmbedBuilder eb;
		if(u != null)
			eb = BonziUtils.quickEmbed("Help Menu - " + catName, "", u, Color.magenta);
		else eb = BonziUtils.quickEmbed("Help Menu - " + catName, "", Color.magenta);
		
		int index = this.currentPage - 1;
		int start = index * GuiHelpMenu.CPP;
		int end = start + GuiHelpMenu.CPP;
		if(end > this.allCommands.length)
			end = this.allCommands.length;
		
		String prefix = this.prefixOfLocation;
		if(prefix == null)
			prefix = Constants.DEFAULT_PREFIX;
		
		int subCat = -1;
		
		for(int i = start; i < end; i++) {
			Command current = this.allCommands[i];
			String icon = current.icon.toString();
			String name = current.name;
			String desc = current.description;
			String cmdName = current.getFilteredCommandName();
			String prefixFixed = current.isRegisterable() ? "/" : prefix;
			String[] usages = (current.args != null) ?
				current.args.buildUsage(prefixFixed, cmdName) :
				new String[] { '`' + prefixFixed + cmdName + '`' };
			String usage = String.join("\n", usages);
				
			int subCatOfCmd = current.subCategory;
			if(subCatOfCmd > subCat) {
				if(subCat != -1)
					eb.addBlankField(false);
				subCat = subCatOfCmd;
			}
			
			eb.addField(icon + " " + name,
				usage + "\n" + desc, false);
		}
		
		if((this.maxPage - this.minPage) > 0)
			eb.setFooter("Page " + this.getPageString());
		else eb.setFooter("This is all the available commands.");
		
		return eb.build();
	}
	
	@Override
	public void onButtonClick(String actionId, long executorId, JDA jda) {
		super.onButtonClick(actionId, executorId, jda);
		if(actionId.equals("return")) {
			GuiHelpMenu menu = new GuiHelpMenu(this.category == CommandCategory._HIDDEN);
			this.parent.setActiveGui(menu, jda);
		}
	}
}
