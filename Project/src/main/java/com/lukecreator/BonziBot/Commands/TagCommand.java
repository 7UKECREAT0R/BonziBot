package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class TagCommand extends Command {

	public TagCommand() {
		this.subCategory = 1;
		this.name = "Tag";
		this.unicodeIcon = "📜";
		this.description = "Access a huge library of \"tags\" made by other bonzibot users! If it doesn't already exist, then you get to make your own!";
		this.args = CommandArgCollection.single("tag_name");
		this.category = CommandCategory.FUN;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		
		Guild g = e.guild;
		GuildSettings gs = e.bonzi
			.guildSettings.getSettings(g);
		
		if(gs != null && !gs.enableTags) {
			MessageEmbed msg = BonziUtils.failureEmbed("Tags are disabled in this server.");
			e.channel.sendMessage(msg).queue();
			return;
		}
		
		boolean isPrivate = (gs == null) ? false : gs.privateTags;
		
		String tagName = e.args.getString("tag_name");
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
				e.channel.sendMessage(msg).queue();
				return;
			} else {
				String m1 = "This tag doesn't exist yet!";
				String m2 = "The next message you send will become the response!";
				MessageEmbed msg = BonziUtils.quickEmbed(m1, m2, Color.yellow).build();
				if(isPrivate)
					e.bonzi.tags.addToPrivateQueue(e.member, tagName);
				else
					e.bonzi.tags.addToPublicQueue(e.executor, tagName);
				e.channel.sendMessage(msg).queue();
				return;
			}
		} else
			e.channel.sendMessage(resp).queue();
	}
}