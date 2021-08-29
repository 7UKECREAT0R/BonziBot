package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Gui.GuiTagLeaderboard;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class TagLeaderboardCommand extends Command {

	public TagLeaderboardCommand() {
		this.subCategory = 1;
		this.name = "Tag Leaderboard";
		this.unicodeIcon = "📜🏆";
		this.description = "Check out the most popular tags!";
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
		
		long gId = isPrivate ? g.getIdLong() : -1l;
		GuiTagLeaderboard lb = new GuiTagLeaderboard(e.bonzi, gId);
		BonziUtils.sendGui(e, lb);
	}
}
