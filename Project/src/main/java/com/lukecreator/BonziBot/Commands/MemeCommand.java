package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.github.jreddit.entity.Submission;
import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Wrappers.RedditClient;

import net.dv8tion.jda.api.EmbedBuilder;

public class MemeCommand extends Command {
	
	public static final String[] MEME_SUBS = new String[] {
			"me_irl", "dankmemes", "okbuddyretard", "memes"
	};
	
	public MemeCommand() {
		this.subCategory = 0;
		this.name = "Meme";
		this.description = "I'll post a spicy (or cringe) meme!";
		this.category = CommandCategory.FUN;
		this.icon = GenericEmoji.fromEmoji("😹");
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		BonziBot bb = e.bonzi;
		RedditClient client = bb.reddit;
		
		if(e.isSlashCommand) {
			e.slashCommand.deferReply().queue(waiting -> {
				Submission meme = client.getRandomSubmission(MEME_SUBS);
				EmbedBuilder eb = new EmbedBuilder()
					.setTitle(meme.getTitle())
					.setColor(Color.magenta)
					.setImage(meme.getURL())
					.setFooter("From r/" + meme.getSubreddit());
				waiting.editOriginalEmbeds(eb.build()).queue();
			});
		} else {
			Submission meme = client.getRandomSubmission(MEME_SUBS);
			EmbedBuilder eb = new EmbedBuilder()
				.setTitle(meme.getTitle())
				.setColor(Color.magenta)
				.setImage(meme.getURL())
				.setFooter("From r/" + meme.getSubreddit());
			e.channel.sendMessageEmbeds(eb.build()).queue();
		}
	}
	
}