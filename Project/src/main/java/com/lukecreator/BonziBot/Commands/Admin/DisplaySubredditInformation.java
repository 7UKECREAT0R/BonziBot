package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.CommandAPI.ArgsComparison;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Wrappers.RedditClient;
import com.lukecreator.BonziBot.Wrappers.SubredditInfo;

import net.dv8tion.jda.api.EmbedBuilder;

public class DisplaySubredditInformation extends Command {
	
	public DisplaySubredditInformation() {
		this.name = "subredditinfo";
		this.description = "";
		this.usage = "subredditinfo <subreddit>";
		this.category = CommandCategory._HIDDEN;
		
		this.usesArgs = true;
		this.goalArgs = 1;
		this.argsCheck = ArgsComparison.EQUAL;
		
		this.adminOnly = true;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		String sub = e.args[0];
		BonziBot bonzi = e.bonzi;
		RedditClient reddit = bonzi.reddit;
		SubredditInfo subreddit = reddit
			.getSubredditInfo(sub);
		
		EmbedBuilder eb = new EmbedBuilder()
				.setColor(subreddit.color)
				.setTitle(subreddit.name)
				.setDescription(subreddit.description_trimmed);
		
		eb.addField("Visibility", subreddit.visibility, true);
		eb.addField("Is NSFW?", String.valueOf(subreddit.nsfw), true);
		eb.addField("Created", subreddit.created.toString(), true);
		eb.addField("Color Hex", subreddit.colorHex, true);
		e.channel.sendMessage(eb.build()).queue();
	}
	
}
