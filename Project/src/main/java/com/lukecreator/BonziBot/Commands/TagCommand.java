package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class TagCommand extends Command {
	
	public TagCommand() {
		this.subCategory = 1;
		this.name = "Tag";
		this.icon = GenericEmoji.fromEmoji("📜");
		this.description = "Access a huge library of \"tags\" made by other bonzibot users!";
		this.args = CommandArgCollection.single("tag name");
		this.category = CommandCategory.FUN;
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		
		Guild g = e.guild;
		
		if(e.settings != null && !e.settings.enableTags) {
			MessageEmbed msg = BonziUtils.failureEmbed("Tags are disabled in this server.");
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(msg).queue();
			else
				e.channel.sendMessageEmbeds(msg).queue();
			return;
		}
		
		boolean isPrivate = (e.settings == null) ? false : e.settings.privateTags;
		
		String tagName = e.args.getString("tag name");
		String resp;
		if(isPrivate)
			resp = e.bonzi.tags.usePrivateTagByName(tagName, g);
		else
			resp = e.bonzi.tags.useTagByName(tagName);
		
		// If this is null the
		//  tag doesn't exist.
		if(resp == null) {
			if(tagName.length() > Constants.MAX_TAG_LENGTH) {
				String m = "Tag names can't be longer than "
					+ Constants.MAX_TAG_LENGTH + " characters!";
				MessageEmbed msg = BonziUtils.failureEmbed(m);
				if(e.isSlashCommand)
					e.slashCommand.replyEmbeds(msg).queue();
				else
					e.channel.sendMessageEmbeds(msg).queue();
				return;
			} else {
				String m1 = "This tag doesn't exist yet!";
				String m2 = "The next message you send will become the response!";
				MessageEmbed msg = BonziUtils.quickEmbed(m1, m2, Color.yellow).build();
				if(isPrivate)
					e.bonzi.tags.addToPrivateQueue(e.member, tagName);
				else
					e.bonzi.tags.addToPublicQueue(e.executor, tagName);
				
				if(e.isSlashCommand)
					e.slashCommand.replyEmbeds(msg).queue();
				else
					e.channel.sendMessageEmbeds(msg).queue();
				return;
			}
		} else {
			if(e.isSlashCommand)
				e.slashCommand.reply(BonziUtils.sanitizeGuildInput(resp)).queue();
			else
				e.channel.sendMessage(BonziUtils.sanitizeGuildInput(resp)).queue();
		}
	}
}