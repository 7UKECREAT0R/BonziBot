package com.lukecreator.BonziBot.CommandAPI;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.Modifier;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Everything you need to execute a command.
 */
public class CommandExecutionInfo {
	
	public CommandExecutionInfo(GuildMessageReceivedEvent e) {
		bot = e.getJDA();
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
		bot = e.getJDA();
		isDirectMessage = true;
		executor = e.getAuthor();
		channel = e.getChannel();
		pChannel = e.getChannel();
		message = e.getMessage();
		fullText = message.getContentRaw();
	}
	public CommandExecutionInfo setCommandData(String commandName, String[] inputArgs, CommandParsedArgs args) {
		this.commandName = commandName;
		this.inputArgs = inputArgs;
		this.args = args;
		return this;
	}
	public CommandExecutionInfo setBonziBot(BonziBot in) {
		this.bonzi = in;
		return this;
	}
	public CommandExecutionInfo setModifiers(Modifier...modifiers) {
		this.modifiers = modifiers;
		return this;
	}
	
	public boolean isGuildMessage = false;
	public boolean isDirectMessage = false;
	public String fullText = null;
	public String commandName = null;
	public String[] inputArgs = null;
	public CommandParsedArgs args = null;
	public Modifier[] modifiers;
	
	// Never null.
	public JDA bot = null;
	public BonziBot bonzi = null;
	public User executor = null;
	public Message message = null;
	public MessageChannel channel = null;
	
	// Null if isDirectMessage
	public Guild guild = null;
	public Member member = null;
	
	// Sometimes null.
	public TextChannel tChannel = null;
	public PrivateChannel pChannel = null;
}