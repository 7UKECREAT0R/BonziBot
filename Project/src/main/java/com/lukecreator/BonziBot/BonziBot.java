package com.lukecreator.BonziBot;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.security.auth.login.LoginException;

import org.reflections.Reflections;

import com.lukecreator.BonziBot.InternalLogger.Severity;
import com.lukecreator.BonziBot.Async.AutoRepeat;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.CommandSystem;
import com.lukecreator.BonziBot.Data.EmojiCache;
import com.lukecreator.BonziBot.Data.GenericReactionEvent;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.JokeProvider;
import com.lukecreator.BonziBot.Managers.CooldownManager;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.GuiManager;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;
import com.lukecreator.BonziBot.Managers.LotteryManager;
import com.lukecreator.BonziBot.Managers.ModeratorManager;
import com.lukecreator.BonziBot.Managers.PrefixManager;
import com.lukecreator.BonziBot.Managers.ReactionManager;
import com.lukecreator.BonziBot.Managers.RewardManager;
import com.lukecreator.BonziBot.Managers.SpecialPeopleManager;
import com.lukecreator.BonziBot.Managers.TagManager;
import com.lukecreator.BonziBot.Managers.UpgradeManager;
import com.lukecreator.BonziBot.Managers.UserAccountManager;
import com.lukecreator.BonziBot.NoUpload.Constants;
import com.lukecreator.BonziBot.Wrappers.RedditClient;

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
	
	List<IStorableData> storableData = new ArrayList<IStorableData>();
	ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(0);
	public GuildSettingsManager guildSettings = new GuildSettingsManager();
	public EventWaiterManager eventWaiter = new EventWaiterManager();
	public SpecialPeopleManager special = new SpecialPeopleManager();
	public UserAccountManager accounts = new UserAccountManager();
	public ModeratorManager moderators = new ModeratorManager();
	public ReactionManager reactions = new ReactionManager();
	public CooldownManager cooldowns = new CooldownManager();
	public UpgradeManager upgrades = new UpgradeManager();
	public LotteryManager lottery = new LotteryManager();
	public PrefixManager prefixes = new PrefixManager();
	public RewardManager rewards = new RewardManager();
	public RedditClient reddit = new RedditClient();
	public JokeProvider jokes = new JokeProvider();
	public GuiManager guis = new GuiManager();
	public TagManager tags = new TagManager();
	
	Reflections reflectionsInstance = new Reflections("com.lukecreator.BonziBot");
	public CommandSystem commands = new CommandSystem(reflectionsInstance);
	
	public BonziBot(boolean test) {
		builder = JDABuilder.create(
			GatewayIntent.GUILD_BANS,
			GatewayIntent.GUILD_VOICE_STATES,
			GatewayIntent.GUILD_MEMBERS,
			GatewayIntent.GUILD_MESSAGES,
			GatewayIntent.GUILD_MESSAGE_REACTIONS,
			GatewayIntent.GUILD_EMOJIS,
			GatewayIntent.DIRECT_MESSAGES,
			GatewayIntent.DIRECT_MESSAGE_REACTIONS
			).disableCache(CacheFlag.ACTIVITY,
				CacheFlag.CLIENT_STATUS);
		
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
	
	void setupStorableData() {
		storableData.clear();
		storableData.add(prefixes);
		storableData.add(accounts);
		storableData.add(guildSettings);
		storableData.add(tags);
		storableData.add(moderators);
		storableData.add(upgrades);
		storableData.add(lottery);
		storableData.add(rewards);
		
		int len = storableData.size();
		InternalLogger.print("[SD] Populated storable data with " + len + " element(s)");
	}
	JDA setupBot() throws InterruptedException, LoginException {
		JDA bot = builder.build();
		InternalLogger.print("[STATUS] Starting bot...");
		bot.awaitReady();
		InternalLogger.print("[STATUS] Bot is ready!");
		return bot;
	}
	void setupExecutors() {
		
		Set<Class<? extends AutoRepeat>> toBeExecuted =
			reflectionsInstance.getSubTypesOf(AutoRepeat.class);
		
		int count = toBeExecuted.size(), i = 0;
		InternalLogger.print("[EXE] Located " + count + " executors. Starting...");
		
		boolean error = false;
		try {
			for(Class<? extends AutoRepeat> execute: toBeExecuted) {
				String name = execute.getName();
				String status = "[" + (++i) + "/" + count +"]";
				InternalLogger.print("[EXE] Starting " + name + " " + status);
				
				AutoRepeat info = execute.newInstance();
				info.botInstance = this;
				this.threadPool.scheduleAtFixedRate(info,
					info.getInitial(), info.getDelay(), info.getUnit());
			}
		} catch(InstantiationException ie) {
			ie.printStackTrace();
			error = true;
		} catch(IllegalAccessException iae) {
			iae.printStackTrace();
			error = true;
		} finally {
			if(error)
				InternalLogger.print("[EXE/ERROR] Could not start all executors.");
			else
				InternalLogger.print("[EXE] All executors started.");
		}
	}
	void postSetup(JDA jda) {
		// Anything else that needs to be done.
		cooldowns.initialize(commands);
		
		Guild bonziGuild = BonziUtils.getBonziGuild(jda);
		EmojiCache.appendGuildEmotes(bonziGuild);
		
		// for debug purposes
		this.prefixes.setPrefix(674436740446158879l, "btemp:");
		this.prefixes.setPrefix(529089349762023436l, "btemp:");
		this.prefixes.setPrefix(740775774528733236l, "btemp:");
		InternalLogger.print("[TEST] Set Test Prefixes");
	}
	public void saveData() {
		InternalLogger.print("[IO] Saving data...");
		for(IStorableData data: storableData) {
			data.saveData();
		}
		InternalLogger.print("[IO] Saved data!");
	}
	public void loadData() {
		int progress = 0;
		int total = storableData.size();
		for(IStorableData data: storableData) {
			progress++;
			InternalLogger.print("[IO] Loading " + data.getClass().getName() + "... [" + progress + "/" + total + "]");
			try {
				data.loadData();
			} catch(EOFException exc) {
				InternalLogger.printError("[IO/ERROR] Could not load file into: " + data.getClass().getName(), Severity.FATAL);
			}
		}
		InternalLogger.print("[IO] Loaded all data!");
	}
	
	// Events
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		// Execute commands and chat-related things.
		if(e.getAuthor().isBot()) return;
		if(tags.receiveMessage(e, this)) return;
		eventWaiter.onMessage(e.getMessage());
		CommandExecutionInfo info = new CommandExecutionInfo(e);
		commands.onInput(info.setBonziBot(this));
	}
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
		if(e.getAuthor().isBot()) return;
		if(tags.receiveMessage(e)) return;
		eventWaiter.onMessage(e.getMessage());
		CommandExecutionInfo info = new CommandExecutionInfo(e);
		commands.onInput(info.setBonziBot(this));
		
		// Cache the channel for future use.
		// (used in BonziUtils.messageUser)
		long pcid = e.getChannel().getIdLong();
		long uid = e.getAuthor().getIdLong();
		BonziUtils.userPrivateChannels.put(uid, pcid);
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
		reactions.reactionAddGuild(e);
		eventWaiter.onReaction(new GenericReactionEvent(e));
	}
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
		guis.onReactionRemove(e);
		reactions.reactionRemoveGuild(e);
	}
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent e) {
		guis.onReactionAdd(e);
		reactions.reactionAddPrivate(e);
		eventWaiter.onReaction(new GenericReactionEvent(e));
	}
	public void onPrivateMessageReactionRemove(PrivateMessageReactionRemoveEvent e) {
		guis.onReactionRemove(e);
		reactions.reactionRemovePrivate(e);
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