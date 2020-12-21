package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Gui.GuiTagLeaderboard;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class TagLeaderboard extends Command {

	public TagLeaderboard() {
		this.subCategory = 1;
		this.name = "Tag Leaderboard";
		this.unicodeIcon = "📜🏆";
		this.description = "Check out the most popular tags!";
		this.category = CommandCategory.FUN;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		Guild g = e.guild;
		GuildSettings gs = e.bonzi.guildSettings.getSettings(g);
		
		if(!gs.enableTags) {
			MessageEmbed msg = BonziUtils.failureEmbed("Tags are disabled in this server.");
			e.channel.sendMessage(msg).queue();
			return;
		}
		
		long gId = gs.privateTags ? g.getIdLong() : -1l;
		GuiTagLeaderboard lb = new GuiTagLeaderboard(e.bonzi, gId);
		BonziUtils.sendGuiFromExecutionInfo(e, lb);
	}
}
