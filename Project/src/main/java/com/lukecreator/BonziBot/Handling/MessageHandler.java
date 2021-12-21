package com.lukecreator.BonziBot.Handling;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.Modifier;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Handles incoming messages. Used to handle messages differently
 * depending on the properties of the channel that it's in.
 * @author Lukec
 */
public interface MessageHandler {
	
	/** Ignores errors based off collisions with Bonzi 1.0 during its uptime. */
	static final Consumer<? super Throwable> COLLISION_IGNORE = fail -> {};
	
	public void handleGuildMessage(BonziBot bb, GuildMessageReceivedEvent e, Modifier[] modifiers);
	public void handlePrivateMessage(BonziBot bb, PrivateMessageReceivedEvent e);
	
	public boolean appliesInChannel(MessageChannel channel);
	public boolean appliesInModifiers(Modifier[] modifiers);
}
