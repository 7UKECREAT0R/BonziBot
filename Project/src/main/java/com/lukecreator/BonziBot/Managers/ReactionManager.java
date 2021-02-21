package com.lukecreator.BonziBot.Managers;

import java.awt.Color;
import java.util.List;

import com.lukecreator.BonziBot.Commands.PollCommand;
import com.lukecreator.BonziBot.Data.GenericReactionEvent;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionRemoveEvent;

/**
 * Just handles all things related to reactions.
 */
public class ReactionManager {
	
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
