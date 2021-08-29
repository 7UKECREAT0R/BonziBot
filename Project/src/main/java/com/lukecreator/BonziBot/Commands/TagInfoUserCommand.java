package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Gui.GuiUserTags;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class TagInfoUserCommand extends Command {

	public TagInfoUserCommand() {
		this.subCategory = 1;
		this.name = "User Tag Info";
		this.unicodeIcon = "🥸";
		this.description = "See a user's most popular tags.";
		this.args = new CommandArgCollection(new UserArg("user"));
		this.category = CommandCategory.FUN;
		this.setCooldown(5000);
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		Guild g = e.guild;
		boolean isPrivate = (e.settings == null) ? false : e.settings.privateTags;
		
		if(e.settings != null && !e.settings.enableTags) {
			MessageEmbed msg = BonziUtils.failureEmbed("Tags are disabled in this server.");
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(msg).queue();
			else
				e.channel.sendMessageEmbeds(msg).queue();
			return;
		}
		
		User target = e.args.getUser("user");
		long gId = isPrivate ? g.getIdLong() : -1l;
		GuiUserTags tags = new GuiUserTags(e.bonzi, target, gId);
		BonziUtils.sendGui(e, tags);
	}
}