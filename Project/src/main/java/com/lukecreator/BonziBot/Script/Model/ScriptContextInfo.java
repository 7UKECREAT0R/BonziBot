package com.lukecreator.BonziBot.Script.Model;

import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GuildSettings;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

/**
 * Wraps a <code>CommandExecutionInfo</code> object but has better, more
 * concise information about what context is avaiable. Different script
 * execution methods might offer different avaiable information.
 * @author Lukec
 *
 */
public class ScriptContextInfo {
	
	public final boolean
		hasCommand,		// Has command arguments.
		hasMessage,		// Has message object and information.
		hasChannel,		// Has a specific channel.
		hasCore,		// Has a BonziBot/JDA instance included.
		hasExecutor,	// Has info on the executor User.
		hasMember,		// Has the Member of the executor.
		hasGuild;		// Has the guild.
	
	// hasCommand
	public final String fullText;
	public final String commandName;
	public final String[] inputArgs;
	//public final CommandParsedArgs args;
	public boolean hasSlashCommand;
	public SlashCommandEvent slashCommand;
	
	// hasMessage
	public final Message msg;
	
	// hasChannel
	public final TextChannel channel;
	
	// hasCore
	public final JDA jda;
	public final BonziBot bonzi;
	
	// hasExecutor
	public final User user;
	// hasMember
	public final Member member;
	
	// hasGuild
	public final Guild guild;
	public final GuildSettings settings;
	
	/**
	 * Harvest the information from command execution info.
	 * @param info
	 */
	public ScriptContextInfo(CommandExecutionInfo info) {
		this.hasCommand = info.commandName != null;
		this.hasSlashCommand = info.isSlashCommand;
		this.hasMessage = info.message != null;
		this.hasChannel = info.tChannel != null;
		this.hasCore = info.bonzi != null && info.bot != null;
		this.hasExecutor = info.executor != null;
		this.hasMember = info.member != null;
		this.hasGuild = info.isGuildMessage && info.guild != null;
		
		this.fullText = info.fullText;
		this.commandName = info.commandName;
		this.inputArgs = info.inputArgs;
		this.slashCommand = info.slashCommand;
		this.msg = info.message;
		this.channel = info.tChannel;
		this.jda = info.bot;
		this.bonzi = info.bonzi;
		this.user = info.executor;
		this.member = info.member;
		this.guild = info.guild;
		this.settings = info.settings;
	}
	public ScriptContextInfo(String fullText, String commandName, String[] inputArgs, SlashCommandEvent slashCommand, Message msg,
			TextChannel channel, JDA jda, BonziBot bonzi, User user, Member member, Guild guild, GuildSettings settings) {
		this.hasCommand = commandName != null;
		this.hasMessage = msg != null;
		this.hasSlashCommand = slashCommand != null;
		this.hasChannel = channel != null;
		this.hasCore = bonzi != null && jda != null;
		this.hasExecutor = user != null;
		this.hasMember = member != null;
		this.hasGuild = guild != null && settings != null;
		
		this.fullText = fullText;
		this.commandName = commandName;
		this.inputArgs = inputArgs;
		this.slashCommand = slashCommand;
		this.msg = msg;
		this.channel = channel;
		this.jda = jda;
		this.bonzi = bonzi;
		this.user = user;
		this.member = member;
		this.guild = guild;
		this.settings = settings;
	}
	
	/**
	 * Generic method to send a message depending on general context.
	 * @param content
	 */
	public void sendMessage(String content) {
		if(this.hasSlashCommand)
			this.slashCommand.reply(content).queue();
		else
			this.channel.sendMessage(content).queue();
	}
	/**
	 * Generic method to send a temporary message depending on general context.
	 * @param content
	 */
	public void sendMessage(int seconds, String content) {
		if(this.hasSlashCommand)
			this.slashCommand.reply(content).queue(interaction -> {
				interaction.deleteOriginal().queueAfter(seconds, TimeUnit.SECONDS);
			});
		else
			this.channel.sendMessage(content).queue(msg -> {
				msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
			});
	}
	/**
	 * Generic method to send a message depending on general context.
	 * @param embed
	 */
	public void sendMessageEmbeds(MessageEmbed embed) {
		if(this.hasSlashCommand)
			this.slashCommand.replyEmbeds(embed).queue();
		else
			this.channel.sendMessageEmbeds(embed).queue();
	}
	/**
	 * Generic method to send a temporary message depending on general context.
	 * @param embed
	 */
	public void sendMessageEmbeds(int seconds, MessageEmbed embed) {
		if(this.hasSlashCommand)
			this.slashCommand.replyEmbeds(embed).queue(interaction -> {
				interaction.deleteOriginal().queueAfter(seconds, TimeUnit.SECONDS);
			});
		else
			this.channel.sendMessageEmbeds(embed).queue(msg -> {
				msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
			});
	}
}