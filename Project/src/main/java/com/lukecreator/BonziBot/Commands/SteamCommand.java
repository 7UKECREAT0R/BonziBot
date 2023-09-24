package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.SteamCache;
import com.lukecreator.BonziBot.Data.SteamCache.Release;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class SteamCommand extends Command {
	
	public SteamCommand() {
		this.subCategory = 0;
		this.name = "Steam";
		this.icon = GenericEmoji.fromEmote(889252340123713576L, false);
		this.description = "Look up a game on steam. Supports fuzzy matching.";
		this.args = CommandArgCollection.single("game title");
		this.category = CommandCategory.UTILITIES;
		this.setCooldown(10000);
	}

	@Override
	public void run(CommandExecutionInfo e) {
		String title = e.args.getString("game title");
		SteamCache steam = e.bonzi.steam;
		Release release = steam.searchForTitle(title);
		
		if(release == null) {
			MessageEmbed noneFound = BonziUtils.failureEmbed("No steam games found.");
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(noneFound).queue();
			else
				e.channel.sendMessageEmbeds(noneFound).queue();
			return;
		}
		
		String url = SteamCache.STORE_URL + release.appID;
		
		if(e.isSlashCommand) {
			e.slashCommand.reply(url).queue();
		} else {
			e.channel.sendMessage(url).queue();
		}
	}
}