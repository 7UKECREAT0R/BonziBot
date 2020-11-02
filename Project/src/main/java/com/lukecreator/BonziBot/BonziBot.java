package com.lukecreator.BonziBot;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.security.auth.login.LoginException;

import com.lukecreator.BonziBot.Commands.CommandExecutionInfo;
import com.lukecreator.BonziBot.Commands.CommandSystem;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

/*
 * The main bot class. Handles just about everything.
 */
public class BonziBot extends ListenerAdapter {
	
	JDABuilder builder = null;
	
	ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(0);
	CommandSystem commands = new CommandSystem();
	
	public BonziBot(boolean test) {
		builder = JDABuilder.create(
			GatewayIntent.GUILD_BANS,
			GatewayIntent.GUILD_VOICE_STATES,
			GatewayIntent.GUILD_MEMBERS,
			GatewayIntent.GUILD_MESSAGES,
			GatewayIntent.GUILD_MESSAGE_REACTIONS,
			GatewayIntent.GUILD_EMOJIS
			).disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS);
		
		String token = test ? Constants.BOT_TOKEN_TEST : Constants.BOT_TOKEN;
		builder.setToken(token);
		builder.addEventListeners(this);
	}
	public void start() throws InterruptedException, LoginException {
		loadData();
		setupBot();
		setupExecutors();
	}
	
	void setupBot() throws InterruptedException, LoginException {
		JDA bot = builder.build();
		InternalLogger.print("Starting bot...");
		bot.awaitReady();
		InternalLogger.print("Bot is ready!");
	}
	void setupExecutors() {
		// Executor stuff.
		InternalLogger.print("Executors set up.");
	}
	void saveData() {
		InternalLogger.print("Saving data...");
		
		// Save data.
		
		
		InternalLogger.print("Saved data!");
	}
	void loadData() {
		InternalLogger.print("Loading data...");
		
		// Load data.
		
		
		InternalLogger.print("Loaded data!");
	}
	
	// Events
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		// Execute commands and chat-related things.
		if(e.getAuthor().isBot()) return;
		CommandExecutionInfo info = new CommandExecutionInfo(e);
		commands.onInput(info.setBonziBot(this));
	}
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
		if(e.getAuthor().isBot()) return;
		CommandExecutionInfo info = new CommandExecutionInfo(e);
		commands.onInput(info.setBonziBot(this));
	}
	public void onGuildJoin(GuildJoinEvent e) {
		// Give off the opening speech.
	}
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		// Send a join message and give roles.
	}
	public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
		// Send a leave message.
	}
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
		// Mainly going to be used for pagination
	}
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
		// Mainly going to be used for pagination
	}
	public void onGenericGuildVoice(GenericGuildVoiceEvent e) {
		// Used to leave the voice channel if nobody is left.
	}
	public void onGuildMessageDelete(GuildMessageDeleteEvent e) {
		// For logging and the expose command.
	}
	public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent e) {
		// For logging.
	}
	public void onTextChannelCreate(TextChannelCreateEvent e) {
		// For logging.
	}
}