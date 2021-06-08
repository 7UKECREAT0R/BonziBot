package com.lukecreator.BonziBot.Commands;

import com.github.jreddit.entity.Submission;
import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.Wrappers.RedditClient;
import com.lukecreator.BonziBot.Wrappers.SubredditInfo;
import com.lukecreator.BonziBot.Wrappers.SubredditPostVideoData;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SubredditCommand extends Command {
	
	public SubredditCommand() {
		this.subCategory = 0;
		
		this.name = "Subreddit";
		this.description = "Slap a random recent subreddit post into chat!";
		this.args = new CommandArgCollection(new StringArg("sub"));
		this.category = CommandCategory.FUN;
		this.unicodeIcon = "🇷";
		
		this.setCooldown(5000);
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		BonziBot bb = e.bonzi;
		RedditClient client = bb.reddit;
		String subName = e.args.getString("sub");
		SubredditInfo sub = client.getSubredditInfo(subName);
		
		if(sub == null || !sub.subredditExists) {
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("Subreddit '" + subName + "' doesn't exist!")).queue();
			else
				e.channel.sendMessage(BonziUtils.failureEmbed("Subreddit '" + subName + "' doesn't exist!")).queue();
			return;
		}
		
		if(e.isSlashCommand)
			e.slashCommand.reply(":small_red_triangle_down:` Sent message...`").setEphemeral(false).queue();
		
		e.channel.sendMessage("Downloading a fresh post...").queue(msg -> {
			Submission post = client.getRandomSubmission(subName);
			if(post == null) {
				if(e.isSlashCommand)
					e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("There's nothing on that subreddit!")).queue();
				else
					e.channel.sendMessage(BonziUtils.failureEmbed("There's nothing on that subreddit!")).queue();
				return;
			}
			if(e.isGuildMessage && !e.tChannel.isNSFW()) {
				if(sub.nsfw) {
					if(e.isSlashCommand)
						e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("This subreddit is NSFW.", "Please use an NSFW channel for this subreddit specifically!")).queue();
					else
						e.channel.sendMessage(BonziUtils.failureEmbed("This subreddit is NSFW.", "Please use an NSFW channel for this subreddit specifically!")).queue();
					return;
				}
				if(post.isNSFW()) {
					if(e.isSlashCommand)
						e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("The post that was gotten was NSFW. Can't post it here!")).queue();
					else
						e.channel.sendMessage(BonziUtils.failureEmbed("The post that was gotten was NSFW. Can't post it here!")).queue();
					return;
				}
			}
			EmbedBuilder eb = new EmbedBuilder()
				.setColor(sub.color)
				.setAuthor(sub.name, sub.url, sub.iconUrl)
				.setTitle(post.getTitle(), post.getUrl());
			
			String postUrl = client.getSubmissionAboutUrl(post);
			SubredditPostVideoData media
				= client.getSubredditPostInfo(postUrl);
			
			try {
				if(!media.hasVideoMedia)
					eb.setImage(post.getUrl());
			} catch(IllegalArgumentException exc) {}
			
			String text = post.getSelftext();
			if(text.length() <= MessageEmbed.TEXT_MAX_LENGTH) {
				eb.setDescription(text);
			} else if (text.length() <= (MessageEmbed.EMBED_MAX_LENGTH_BOT-sub.name.length())){
				int splitLen = MessageEmbed.VALUE_MAX_LENGTH;
				String[] strings = BonziUtils.splitByLength(text, splitLen);
				eb.setDescription(strings[0] + strings[1]);
				for(int i = 2; i < strings.length; i++) {
					eb.addField("", strings[i], false);
				}
			}
			msg.editMessage(eb.build()).queue();
			if(media.hasVideoMedia)
				e.channel.sendMessage(media.videoURL).queue();
		});
	}
}
