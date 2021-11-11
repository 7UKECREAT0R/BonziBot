package com.lukecreator.BonziBot.CommandAPI;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.Modifier;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
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
	public CommandExecutionInfo(SlashCommandEvent e) {
		isSlashCommand = true;
		slashCommand = e;
		bot = e.getJDA();
		isGuildMessage = e.isFromGuild();
		executor = e.getUser();
		channel = e.getChannel();
		if(e.getChannelType() == ChannelType.TEXT)
			tChannel = e.getTextChannel();
		else pChannel = e.getPrivateChannel();
		guild = e.getGuild();
		member = e.getMember();
		message = null; // beware
	}
	public CommandExecutionInfo setCommandData(String commandName, String[] inputArgs, CommandParsedArgs args) {
		this.commandName = commandName;
		this.inputArgs = inputArgs;
		this.args = args;
		return this;
	}
	public CommandExecutionInfo setBonziBot(BonziBot in) {
		this.bonzi = in;
		
		if(this.isGuildMessage)
			this.settings = in.guildSettings.getSettings(this.guild);
		
		return this;
	}
	public CommandExecutionInfo setModifiers(Modifier...modifiers) {
		this.modifiers = modifiers;
		return this;
	}
	public CommandExecutionInfo setSettings(GuildSettings settings) {
		this.settings = settings;
		return this;
	}
	/**
	 * Get the color of the executor from role color.
	 * @return
	 */
	public Color getExecutorColor() {
		if(this.isDirectMessage)
			return Color.white.darker();
		
		if(this.member.getColor() == null)
			return new Color(Role.DEFAULT_COLOR_RAW);
		else return this.member.getColor();
	}
	
	public boolean isSlashCommand = false;
	public SlashCommandEvent slashCommand = null;
	
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
	public Message message = null; // if isSlashCommand, this is null. be careful.
	public MessageChannel channel = null;
	
	// Null if isDirectMessage
	public Guild guild = null;
	public Member member = null;
	public GuildSettings settings = null;
	
	// Sometimes null.
	public TextChannel tChannel = null;
	public PrivateChannel pChannel = null;
	
	/**
	 * Generic method to send a message depending on general context.
	 * @param content
	 */
	public void sendMessage(String content) {
		if(this.isSlashCommand)
			this.slashCommand.reply(content).queue();
		else
			this.channel.sendMessage(content).queue();
	}
	/**
	 * Generic method to send a message depending on general context.
	 * @param embed
	 */
	public void sendMessageEmbeds(MessageEmbed embed) {
		if(this.isSlashCommand)
			this.slashCommand.replyEmbeds(embed).queue();
		else
			this.channel.sendMessageEmbeds(embed).queue();
	}
}