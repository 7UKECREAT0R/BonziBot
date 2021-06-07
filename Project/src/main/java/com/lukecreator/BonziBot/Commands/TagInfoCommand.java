package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.TagData;
import com.lukecreator.BonziBot.Gui.GuiTagEditing;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class TagInfoCommand extends Command {
	
	public TagInfoCommand() {
		this.subCategory = 1;
		this.name = "Tag Info";
		this.unicodeIcon = "📜❓";
		this.description = "View information about a tag, or modify it if it's your own.";
		this.args = CommandArgCollection.single("tag_name");
		this.category = CommandCategory.FUN;
		
		this.setCooldown(3000);
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		
		Guild g = e.guild;
		GuildSettings gs = e.bonzi
			.guildSettings.getSettings(g);
		
		if(gs != null && !gs.enableTags) {
			MessageEmbed msg = BonziUtils.failureEmbed("Tags are disabled in this server.");
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(msg).queue();
			else
				e.channel.sendMessage(msg).queue();
			return;
		}
		
		boolean isPrivate = (gs == null) ? false : gs.privateTags;
		
		String tagName = e.args.getString("tag_name");
		TagData tag;
		if(isPrivate)
			tag = e.bonzi.tags.getPrivateTagByName(tagName, g);
		else
			tag = e.bonzi.tags.getTagByName(tagName);
		
		if(tag == null) {
			MessageEmbed msg = BonziUtils.failureEmbed("That tag doesn't exist!");
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(msg).queue();
			else
				e.channel.sendMessage(msg).queue();
			return;
		}
		
		// Now realistically this will never do anything because
		// the system is built to only accept x characters. Some
		// tags were made before this system was made so they might
		// overflow the length. It's just a safeguard.
		String shortTagName = BonziUtils.cutOffString(tagName, MessageEmbed.TEXT_MAX_LENGTH - 48);
		String prefix = BonziUtils.getPrefixOrDefault(e);
		boolean isEditing = tag.creatorId == e.executor.getIdLong();
		
		EmbedBuilder eb = new EmbedBuilder();
		
		if(isEditing) {
			String s1 = e.executor.getName();
			String s2 = e.executor.getEffectiveAvatarUrl();
			eb.setAuthor(s1, null, s2);
		}
		eb.setColor(Color.magenta);
		eb.setTitle(isEditing ? "Tag Info and Editing" : "Tag Info");
		eb.setDescription(prefix + "tag " + shortTagName);
		eb.addField("Creator:", tag.creator, true);
		eb.addField("Created on:", tag.created.toString(), true);
		eb.addField("Place:", tag.guild, true);
		eb.addField("Total Uses:", tag.uses + " times.", true);
		eb.setFooter(isPrivate ?
			"This tag is special for this server only.":
			"This tag is available everywhere.");
		if(isEditing) {
			GuiTagEditing edit = new GuiTagEditing
				(eb, tagName, isPrivate, g.getIdLong());
			BonziUtils.sendGui(e, edit);
		} else {
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(eb.build()).queue();
			else
				e.channel.sendMessage(eb.build()).queue();
		}
	}
}
