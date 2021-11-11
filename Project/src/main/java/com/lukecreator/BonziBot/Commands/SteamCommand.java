package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.SteamCache;
import com.lukecreator.BonziBot.Data.SteamCache.Release;

public class SteamCommand extends Command {
	
	public static final String EMOJI = "<:steam:889252340123713576>";
	
	public SteamCommand() {
		this.subCategory = 0;
		this.name = "Steam";
		this.unicodeIcon = EMOJI;
		this.description = "Look up a game on steam.";
		this.args = CommandArgCollection.single("game title");
		this.category = CommandCategory.UTILITIES;
		this.setCooldown(10000);
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		
		// How release names are stored.
		String title = e.args.getString("game title").toUpperCase();
		SteamCache steam = e.bonzi.steam;
		
		Release closestMatch = null;
		Release[] releases = steam.searchForTitles(title);
		
		if(releases.length < 1) {
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("No steam games found.")).queue();
			else e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("No steam games found.")).queue();
			return;
		}
		
		for(Release release: releases) {
			if(release.appName.equals(title)) {
				closestMatch = release;
				break;
			}
		}
		
		if(closestMatch == null)
			closestMatch = releases[0];
		
		String url = SteamCache.STORE_URL + closestMatch.appID;
		if(e.isSlashCommand)
			e.slashCommand.reply(url).queue();
		else e.channel.sendMessage(url).queue();
	}
}