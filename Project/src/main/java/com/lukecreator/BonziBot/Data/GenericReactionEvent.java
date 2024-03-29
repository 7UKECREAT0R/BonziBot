package com.lukecreator.BonziBot.Data;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.requests.RestAction;

public class GenericReactionEvent {
	public final JDA jda;
	public final boolean added; // added/removed
	public final MessageChannelUnion channel;
	public final Guild guild;
	public final User user;
	public final long userIdLong;
	public final long messageIdLong;
	public final MessageReaction reaction;
	public final EmojiUnion reactionEmote;
	public final long responseNumber;
	
	public GenericReactionEvent(MessageReactionAddEvent e) {
		if(e.isFromGuild())
			this.guild = e.getGuild();
        else
			this.guild = null;

		this.jda = e.getJDA();
		this.added = true;
		this.channel = e.getChannel();
		this.user = e.getUser();
		this.userIdLong = this.user.getIdLong();
		this.messageIdLong = e.getMessageIdLong();
		this.reaction = e.getReaction();
		this.reactionEmote = e.getEmoji();
		this.responseNumber = e.getResponseNumber();
	}
	public GenericReactionEvent(MessageReactionRemoveEvent e) {
		if(e.isFromGuild())
			this.guild = e.getGuild();
		else
			this.guild = null;

		this.jda = e.getJDA();
		this.added = false;
		this.channel = e.getChannel();
		this.user = e.getUser();
		this.userIdLong = this.user.getIdLong();
		this.messageIdLong = e.getMessageIdLong();
		this.reaction = e.getReaction();
		this.reactionEmote = e.getEmoji();
		this.responseNumber = e.getResponseNumber();
	}
	
    public RestAction<Message> retrieveMessage()
    {
    	if(this.messageIdLong == 0L)
    		return null;
        return this.channel.retrieveMessageById(this.messageIdLong);
    }
}