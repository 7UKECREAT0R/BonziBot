package com.lukecreator.BonziBot.Managers;

import java.awt.Color;
import java.util.List;

import com.lukecreator.BonziBot.Commands.PollCommand;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionRemoveEvent;
import net.dv8tion.jda.api.requests.RestAction;

/*
 * Just handles all things related to reactions.
 */
public class ReactionManager {
	
	class GenericReactionEvent {
		final JDA jda;
		final boolean added; // added/removed
		final MessageChannel channel;
		final Guild guild;
		final User user;
		final long userIdLong;
		final long messageIdLong;
		final MessageReaction reaction;
		final ReactionEmote reactionEmote;
		final long responseNumber;
		
		public GenericReactionEvent(GuildMessageReactionAddEvent e) {
			jda = e.getJDA();
			added = true;
			channel = e.getChannel();
			guild = e.getGuild();
			user = e.getUser();
			userIdLong = user.getIdLong();
			messageIdLong = e.getMessageIdLong();
			reaction = e.getReaction();
			reactionEmote = e.getReactionEmote();
			responseNumber = e.getResponseNumber();
		}
		public GenericReactionEvent(GuildMessageReactionRemoveEvent e) {
			jda = e.getJDA();
			added = false;
			channel = e.getChannel();
			guild = e.getGuild();
			user = e.getUser();
			userIdLong = user.getIdLong();
			messageIdLong = e.getMessageIdLong();
			reaction = e.getReaction();
			reactionEmote = e.getReactionEmote();
			responseNumber = e.getResponseNumber();
		}
		public GenericReactionEvent(PrivateMessageReactionAddEvent e) {
			jda = e.getJDA();
			added = true;
			channel = e.getChannel();
			guild = null;
			user = e.getUser();
			userIdLong = user.getIdLong();
			messageIdLong = e.getMessageIdLong();
			reaction = e.getReaction();
			reactionEmote = e.getReactionEmote();
			responseNumber = e.getResponseNumber();
		}
		public GenericReactionEvent(PrivateMessageReactionRemoveEvent e) {
			jda = e.getJDA();
			added = false;
			channel = e.getChannel();
			guild = null;
			user = e.getUser();
			userIdLong = user.getIdLong();
			messageIdLong = e.getMessageIdLong();
			reaction = e.getReaction();
			reactionEmote = e.getReactionEmote();
			responseNumber = e.getResponseNumber();
		}
		
	    public RestAction<Message> retrieveMessage()
	    {
	        return channel.retrieveMessageById(messageIdLong);
	    }
	}
	
	public void reactionAddGuild(GuildMessageReactionAddEvent e) {
		this.onReaction(new GenericReactionEvent(e));
	}
	public void reactionRemoveGuild(GuildMessageReactionRemoveEvent e) {
		this.onReaction(new GenericReactionEvent(e));
	}
	public void reactionAddPrivate(PrivateMessageReactionAddEvent e) {
		this.onReaction(new GenericReactionEvent(e));
	}
	public void reactionRemovePrivate(PrivateMessageReactionRemoveEvent e) {
		this.onReaction(new GenericReactionEvent(e));
	}
	
	public void onReaction(GenericReactionEvent e) {
		long reactor = e.userIdLong;
		if(Constants.isBonziBot(reactor)) return;
		
		// Polls
		checkPolls(e);
	}
	void checkPolls(GenericReactionEvent e) {
		boolean isPrivate = e.guild == null;
		boolean isEmoji = e.reactionEmote.isEmoji();
		String emoji = isEmoji?e.reactionEmote.getEmoji():"";
		boolean isUpEmoji = emoji.equals("👍");
		boolean isDownEmoji = emoji.equals("👎");
		boolean pollEmoji = isUpEmoji | isDownEmoji;
		
		if(!isPrivate && isEmoji && pollEmoji) {
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
				if(!embed.getTitle().equals(PollCommand.EMBED_TITLE))
					return;
				EmbedBuilder eb = new EmbedBuilder(embed);
				eb.setColor(up>down?Color.green : down > up ? Color.red : Color.gray);
				eb.setFooter(PollCommand.generateFooter(up, down));
				pollMsg.editMessage(eb.build()).queue();
			});
		}
	}
}
