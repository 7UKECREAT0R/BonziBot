package com.lukecreator.BonziBot.CommandAPI;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Everything you need to execute a command.
 */
public class CommandExecutionInfo {
	
	public CommandExecutionInfo(MessageReceivedEvent e) {
		if(e.isFromType(ChannelType.TEXT)) {
			this.bot = e.getJDA();
			this.isGuildMessage = true;
			this.executor = e.getAuthor();
			this.channel = e.getChannel();
			this.tChannel = (TextChannel)e.getTextChannel();
			this.guild = e.getGuild();
			this.member = e.getMember();
			this.message = e.getMessage();
			this.fullText = this.message.getContentRaw();
		} else if(e.isFromType(ChannelType.PRIVATE)) {
			this.bot = e.getJDA();
			this.isDirectMessage = true;
			this.executor = e.getAuthor();
			this.channel = e.getChannel();
			this.pChannel = e.getPrivateChannel();
			this.message = e.getMessage();
			this.fullText = this.message.getContentRaw();
		}

	}
	public CommandExecutionInfo(SlashCommandInteractionEvent e) {
		this.isSlashCommand = true;
		this.slashCommand = e;
		this.bot = e.getJDA();
		this.isGuildMessage = e.isFromGuild();
		this.executor = e.getUser();
		this.channel = e.getChannel();
		if(e.getChannelType() == ChannelType.TEXT)
			this.tChannel = e.getTextChannel();
		else this.pChannel = e.getPrivateChannel();
		this.guild = e.getGuild();
		this.member = e.getMember();
		this.message = null; // beware
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
	public SlashCommandInteractionEvent slashCommand = null;
	
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
	public void reply(String content) {
		if(this.isSlashCommand && !this.slashCommand.isAcknowledged())
			this.slashCommand.reply(content).queue();
		else
			this.channel.sendMessage(content).queue();
	}
	/**
	 * Generic method to send a temporary message depending on general context.
	 * @param content
	 */
	public void reply(int seconds, String content) {
		if(this.isSlashCommand && !this.slashCommand.isAcknowledged())
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
	public void reply(MessageEmbed embed) {
		if(this.isSlashCommand && !this.slashCommand.isAcknowledged())
			this.slashCommand.replyEmbeds(embed).queue();
		else
			this.channel.sendMessageEmbeds(embed).queue();
	}
	/**
	 * Generic method to send a temporary message depending on general context.
	 * @param embed
	 */
	public void reply(int seconds, MessageEmbed embed) {
		if(this.isSlashCommand && !this.slashCommand.isAcknowledged())
			this.slashCommand.replyEmbeds(embed).queue(interaction -> {
				interaction.deleteOriginal().queueAfter(seconds, TimeUnit.SECONDS);
			});
		else
			this.channel.sendMessageEmbeds(embed).queue(msg -> {
				msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
			});
	}
}