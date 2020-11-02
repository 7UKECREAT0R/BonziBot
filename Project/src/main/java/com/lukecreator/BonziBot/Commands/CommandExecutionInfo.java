package com.lukecreator.BonziBot.Commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/*
 * Everything you need to execute a command.
 */
public class CommandExecutionInfo {
	
	public CommandExecutionInfo(GuildMessageReceivedEvent e) {
		isGuildMessage = true;
		executor = e.getAuthor();
		channel = e.getChannel();
		tChannel = e.getChannel();
		guild = e.getGuild();
		member = e.getMember();
		message = e.getMessage();
		fullText = message.getContentRaw();
	}
	public CommandExecutionInfo(PrivateMessageReceivedEvent e) {
		isDirectMessage = true;
		executor = e.getAuthor();
		channel = e.getChannel();
		pChannel = e.getChannel();
		message = e.getMessage();
	}
	public CommandExecutionInfo setCommandData(String commandName, String...args) {
		this.commandName = commandName;
		this.args = args;
		return this;
	}
	
	public boolean isGuildMessage = false;
	public boolean isDirectMessage = false;
	public String fullText = null;
	public String commandName = null;
	public String[] args = null;
	
	// Never null.
	public User executor = null;
	public MessageChannel channel = null;
	
	// Null if isDirectMessage
	public Guild guild = null;
	public Member member = null;
	public Message message = null;
	
	// Sometimes null.
	public TextChannel tChannel = null;
	public PrivateChannel pChannel = null;
}