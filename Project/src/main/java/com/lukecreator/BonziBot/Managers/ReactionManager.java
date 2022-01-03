package com.lukecreator.BonziBot.Managers;

import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Commands.PollCommand;
import com.lukecreator.BonziBot.Commands.TimedPollCommand;
import com.lukecreator.BonziBot.Data.GenericReactionEvent;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionRemoveEvent;

/**
 * Just handles all things related to reactions.
 */
public class ReactionManager {
	
	public void reactionAddGuild(GuildMessageReactionAddEvent e, BonziBot bb) {
		this.onReaction(new GenericReactionEvent(e), bb);
	}
	public void reactionRemoveGuild(GuildMessageReactionRemoveEvent e, BonziBot bb) {
		this.onReaction(new GenericReactionEvent(e), bb);
	}
	public void reactionAddPrivate(PrivateMessageReactionAddEvent e, BonziBot bb) {
		this.onReaction(new GenericReactionEvent(e), bb);
	}
	public void reactionRemovePrivate(PrivateMessageReactionRemoveEvent e, BonziBot bb) {
		this.onReaction(new GenericReactionEvent(e), bb);
	}
	
	public void onReaction(GenericReactionEvent e, BonziBot bb) {
		long reactor = e.userIdLong;
		
		if(Constants.isBonziBot(reactor))
			return;
		
		this.checkPins(e, bb);
		this.checkStars(e, bb);
		this.checkPolls(e);
	}
	void checkPins(GenericReactionEvent e, BonziBot bb) {
		// cant pin in non-guilds
		if(e.guild == null)
			return;
		if(!e.reactionEmote.isEmoji())
			return;
		if(e.user.isBot())
			return;
		if(!e.added)
			return;
		
		if(e.reactionEmote.getEmoji().equals("📌")) {
			e.retrieveMessage().queue(msg -> {
				User author = msg.getAuthor();
				UserAccountManager uam = bb.accounts;
				UserAccount authorAcc = uam.getUserAccount(author);
				UserAccount account = uam.getUserAccount(e.userIdLong);
				boolean censorContent = false;
				if(authorAcc.optOutExpose)
					censorContent = true;
				account.addPersonalPin(msg, censorContent);
				uam.setUserAccount(e.userIdLong, account);
				msg.getChannel().sendMessage(e.user.getAsMention() + ", pinned! `📌`").queue(del -> {
					del.delete().queueAfter(3, TimeUnit.SECONDS);
				});
			});
		}
	}
	void checkStars(GenericReactionEvent e, BonziBot bb) {
		if(e.guild == null)
			return;
		if(!e.reactionEmote.isEmoji())
			return;
		if(e.user.isBot())
			return;
		if(!e.added)
			return;
		if(!e.reactionEmote.getEmoji().equals("⭐"))
			return;
		
		GuildSettings settings = bb.guildSettings.getSettings(e.guild.getIdLong());
		if(settings.starboard == 0l)
			return;
		
		e.retrieveMessage().queue(msg -> {
			msg.retrieveReactionUsers("⭐").queue(users -> {
				for(User testUser: users)
					if(testUser.isBot())
						return;
				TextChannel channel = e.guild.getTextChannelById(settings.starboard);
				if(channel == null) {
					e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("HELP!", "My starboard channel was deleted...")).queue();
					return;
				}
				
				int count = users.size();
				if(count >= settings.starboardLimit) {
					msg.addReaction("⭐").queue();
					MessageEmbed send = BonziUtils.createStarboardEntry(msg);
					channel.sendMessageEmbeds(send).queue();
				}
			});
		});
	}
	void checkPolls(GenericReactionEvent e) {
		
		// pre-check as much information as humanly possible
		boolean isPrivate = e.guild == null;
		boolean isEmoji = e.reactionEmote.isEmoji();
		String emoji = isEmoji?e.reactionEmote.getEmoji():"";
		boolean isUpEmoji = emoji.equals("👍");
		boolean isDownEmoji = emoji.equals("👎");
		boolean pollEmoji = isUpEmoji | isDownEmoji;
		
		if(!isPrivate && pollEmoji) {
			// Fetch the message so the poll can be updated.
			e.retrieveMessage().queue(pollMsg -> {
				if(pollMsg.getEmbeds().isEmpty())
					return;
				if(!Constants.isBonziBot(pollMsg.getAuthor().getIdLong()))
					return;
				List<MessageReaction> reactions
					= pollMsg.getReactions();
				int up = 0, down = 0;
				for(MessageReaction me: reactions) {
					ReactionEmote emote = me.getReactionEmote();
					boolean mIsEmoji = emote.isEmoji();
					String mEmoji = emote.getEmoji();
					if(mIsEmoji && mEmoji.equals("👍")) {
						up = me.getCount() - 1;
						continue;
					}
					if(mIsEmoji && mEmoji.equals("👎")) {
						down = me.getCount() - 1;
						continue;
					}
				}
				
				MessageEmbed embed = pollMsg.getEmbeds().get(0);
				
				String oldTitle = embed.getTitle();
				if(!oldTitle.equals(PollCommand.G_EMBED_TITLE) &&
					!oldTitle.equals(PollCommand.P_EMBED_TITLE))
					return;
				
				// Cancel if TimedPoll and time is expired.
				if(embed.getTimestamp() != null) {
					OffsetDateTime dt = embed.getTimestamp();
					OffsetDateTime now = Instant.now().atOffset(ZoneOffset.UTC);
					if(now.isAfter(dt)) {
						String desc = embed.getDescription();
						if(!desc.startsWith(TimedPollCommand.POLL_ENDED)) {
							desc = TimedPollCommand.POLL_ENDED + "\n" + desc;
							EmbedBuilder eb = new EmbedBuilder(embed);
							eb.setDescription(desc);
							pollMsg.editMessageEmbeds(eb.build()).queue();
						}
						return;
					}
				}
				
				EmbedBuilder eb = new EmbedBuilder(embed);
				eb.setColor(up > down ? Color.green :
					down > up ? Color.red : Color.gray);
				eb.setFooter(PollCommand.generateFooter(up, down));
				pollMsg.editMessageEmbeds(eb.build()).queue();
			});
		}
	}
}
