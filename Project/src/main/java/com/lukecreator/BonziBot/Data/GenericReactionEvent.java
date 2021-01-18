package com.lukecreator.BonziBot.Data;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionRemoveEvent;
import net.dv8tion.jda.api.requests.RestAction;

public class GenericReactionEvent {
	public final JDA jda;
	public final boolean added; // added/removed
	public final MessageChannel channel;
	public final Guild guild;
	public final User user;
	public final long userIdLong;
	public final long messageIdLong;
	public final MessageReaction reaction;
	public final ReactionEmote reactionEmote;
	public final long responseNumber;
	
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