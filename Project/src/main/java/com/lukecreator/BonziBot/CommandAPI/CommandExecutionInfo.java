package com.lukecreator.BonziBot.CommandAPI;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.Modifier;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Everything you need to execute a command.
 */
public class CommandExecutionInfo {
	
	public CommandExecutionInfo(MessageReceivedEvent e) {
		this.executor = e.getAuthor();
		this.channel = e.getChannel();
		this.guild = e.getGuild();
		this.member = e.getMember();
		this.message = e.getMessage();
		this.fullText = this.message.getContentRaw();
		this.bot = e.getJDA();
		
		if(this.channel.getType() == ChannelType.TEXT)
			this.tChannel = this.channel.asTextChannel();
		if(this.channel.getType() == ChannelType.PRIVATE)
			this.pChannel = this.channel.asPrivateChannel();
		
		if(e.isFromType(ChannelType.TEXT) || e.isFromType(ChannelType.VOICE) || e.isFromType(ChannelType.GUILD_PUBLIC_THREAD)) {
			this.isDirectMessage = false;
			this.isGuildMessage = true;
		} else if(e.isFromType(ChannelType.PRIVATE)) {
			this.isDirectMessage = true;
			this.isGuildMessage = false;
		}
	}
	public CommandExecutionInfo(SlashCommandInteractionEvent e) {
		this.isSlashCommand = true;
		this.slashCommand = e;
		this.bot = e.getJDA();
		this.isGuildMessage = e.isFromGuild();
		this.executor = e.getUser();
		this.channel = e.getChannel();
		this.guild = e.getGuild();
		this.member = e.getMember();
		this.message = null; // beware
		
		if(this.channel.getType() == ChannelType.TEXT)
			this.tChannel = this.channel.asTextChannel();
		if(this.channel.getType() == ChannelType.PRIVATE)
			this.pChannel = this.channel.asPrivateChannel();
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
	public MessageChannelUnion channel = null;
	
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
		if(this.isSlashCommand) {
			if(this.slashCommand.isAcknowledged()) {
				this.channel.sendMessage(content).queue();
			} else {
				this.slashCommand.reply(content).queue();
			}
		} else {
			this.message.reply(content).queue(null, fail -> {
				// message might have been deleted
				this.channel.sendMessage(content).queue();
			});
		}
	}
	/**
	 * Generic method to send a temporary message depending on general context.
	 * @param content
	 */
	public void reply(int seconds, String content) {
		if(this.isSlashCommand) {
			if(this.slashCommand.isAcknowledged()) {
				this.channel.sendMessage(content).queue(msg -> {
					msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
				});
			} else {
				this.slashCommand.reply(content).queue(interaction -> {
					interaction.deleteOriginal().queueAfter(seconds, TimeUnit.SECONDS);
				});
			}
		} else {
			this.message.reply(content).queue(msg -> {
				msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
			}, fail -> {
				this.channel.sendMessage(content).queue(msg -> {
					msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
				});
			});
		}
	}
	/**
	 * Generic method to send a message depending on general context.
	 * @param embed
	 */
	public void reply(MessageEmbed embed) {
		if(this.isSlashCommand) {
			if(this.slashCommand.isAcknowledged()) {
				this.channel.sendMessageEmbeds(embed).queue();
			} else {
				this.slashCommand.replyEmbeds(embed).queue();
			}
		} else {
			this.message.replyEmbeds(embed).queue(null, fail -> {
				// message might have been deleted
				this.channel.sendMessageEmbeds(embed).queue();
			});
		}
	}
	/**
	 * Generic method to send a temporary message depending on general context.
	 * @param embed
	 */
	public void reply(int seconds, MessageEmbed embed) {
		if(this.isSlashCommand) {
			if(this.slashCommand.isAcknowledged()) {
				this.channel.sendMessageEmbeds(embed).queue(msg -> {
					msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
				});
			} else {
				this.slashCommand.replyEmbeds(embed).queue(interaction -> {
					interaction.deleteOriginal().queueAfter(seconds, TimeUnit.SECONDS);
				});
			}
		} else {
			this.message.replyEmbeds(embed).queue(msg -> {
				msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
			}, fail -> {
				this.channel.sendMessageEmbeds(embed).queue(msg -> {
					msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
				});
			});
		}
	}
}