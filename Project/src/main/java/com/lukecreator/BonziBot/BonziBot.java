package com.lukecreator.BonziBot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.CommandSystem;
import com.lukecreator.BonziBot.Data.EmojiCache;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Managers.AdminManager;
import com.lukecreator.BonziBot.Managers.CooldownManager;
import com.lukecreator.BonziBot.Managers.GuiManager;
import com.lukecreator.BonziBot.Managers.PrefixManager;
import com.lukecreator.BonziBot.Managers.UserAccountManager;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
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
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

/*
 * The main bot class. Handles just about everything.
 */
public class BonziBot extends ListenerAdapter {
	
	JDABuilder builder = null;
	
	List<IStorableData> toSaveAndLoad = new ArrayList<IStorableData>();
	ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(0);
	public CommandSystem commands = new CommandSystem();
	public AdminManager admins = new AdminManager();
	public PrefixManager prefixes = new PrefixManager();
	public UserAccountManager accounts = new UserAccountManager();
	public CooldownManager cooldowns = new CooldownManager();
	public GuiManager guis = new GuiManager();
	
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
		setupStorableData();
		loadData();
		JDA bot = setupBot();
		setupExecutors();
		postSetup(bot);
	}
	public void saveBackup() {
		
	}
	public void loadBackup() {
		
	}
	
	void setupStorableData() {
		toSaveAndLoad.clear();
		toSaveAndLoad.add(prefixes);
		toSaveAndLoad.add(accounts);
		
		int len = toSaveAndLoad.size();
		InternalLogger.print("Populated storable data with " + len + " element(s)");
	}
	JDA setupBot() throws InterruptedException, LoginException {
		JDA bot = builder.build();
		InternalLogger.print("Starting bot...");
		bot.awaitReady();
		InternalLogger.print("Bot is ready!");
		return bot;
	}
	void setupExecutors() {
		// Autosaving.
		threadPool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				saveData();
			}
		}, 1, 5, TimeUnit.MINUTES);
		
		InternalLogger.print("Executors set up.");
	}
	void postSetup(JDA jda) {
		// Anything else that needs to be done.
		cooldowns.initialize(commands);
		
		Guild bonziGuild = BonziUtils.getBonziGuild(jda);
		EmojiCache.appendGuildEmotes(bonziGuild);
	}
	void saveData() {
		InternalLogger.print("Saving data...");
		for(IStorableData data: toSaveAndLoad) {
			data.saveData();
		}
		InternalLogger.print("Saved data!");
	}
	void loadData() {
		InternalLogger.print("Loading data...");
		for(IStorableData data: toSaveAndLoad) {
			data.loadData();
		}
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
		guis.onReactionAdd(e);
	}
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
		guis.onReactionRemove(e);
	}
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent e) {
		guis.onReactionAdd(e);
	}
	public void onPrivateMessageReactionRemove(PrivateMessageReactionRemoveEvent e) {
		guis.onReactionRemove(e);
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