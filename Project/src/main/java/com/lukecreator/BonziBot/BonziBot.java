package com.lukecreator.BonziBot;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.security.auth.login.LoginException;

import org.reflections.Reflections;

import com.lukecreator.BonziBot.InternalLogger.Severity;
import com.lukecreator.BonziBot.Async.AutoRepeat;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArg;
import com.lukecreator.BonziBot.CommandAPI.CommandArg.ArgType;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.CommandSystem;
import com.lukecreator.BonziBot.CommandAPI.EnumArg;
import com.lukecreator.BonziBot.Data.EmojiCache;
import com.lukecreator.BonziBot.Data.GenericReactionEvent;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.JokeProvider;
import com.lukecreator.BonziBot.Data.Modifier;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Data.UsernameGenerator;
import com.lukecreator.BonziBot.Graphics.FontLoader;
import com.lukecreator.BonziBot.Managers.CooldownManager;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.FunnyChannelManager;
import com.lukecreator.BonziBot.Managers.GuiManager;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;
import com.lukecreator.BonziBot.Managers.LoggingManager;
import com.lukecreator.BonziBot.Managers.LotteryManager;
import com.lukecreator.BonziBot.Managers.ReactionManager;
import com.lukecreator.BonziBot.Managers.RepManager;
import com.lukecreator.BonziBot.Managers.RewardManager;
import com.lukecreator.BonziBot.Managers.SpecialPeopleManager;
import com.lukecreator.BonziBot.Managers.TagManager;
import com.lukecreator.BonziBot.Managers.UpgradeManager;
import com.lukecreator.BonziBot.Managers.UserAccountManager;
import com.lukecreator.BonziBot.NoUpload.Constants;
import com.lukecreator.BonziBot.Wrappers.RedditClient;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.audit.AuditLogKey;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateTopicEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

/**
 * The main bot class. Handles just about everything.
 */
public class BonziBot extends ListenerAdapter {
	
	JDABuilder builder = null;
	public boolean adminBypassing = false;
	
	List<IStorableData> storableData = new ArrayList<IStorableData>();
	ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(0);
	public GuildSettingsManager guildSettings = new GuildSettingsManager();
	public EventWaiterManager eventWaiter = new EventWaiterManager();
	public SpecialPeopleManager special = new SpecialPeopleManager();
	public UserAccountManager accounts = new UserAccountManager();
	public ReactionManager reactions = new ReactionManager();
	public CooldownManager cooldowns = new CooldownManager();
	public UpgradeManager upgrades = new UpgradeManager();
	public LotteryManager lottery = new LotteryManager();
	public LoggingManager logging = new LoggingManager();
	public RewardManager rewards = new RewardManager();
	public RedditClient reddit = new RedditClient();
	public RepManager reputation = new RepManager();
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
		loadFonts();
		slashCommands(bot);
		postSetup(bot);
	}
	
	void setupStorableData() {
		storableData.clear();
		storableData.add(accounts);
		storableData.add(guildSettings);
		storableData.add(tags);
		storableData.add(upgrades);
		storableData.add(lottery);
		storableData.add(rewards);
		storableData.add(logging);
		storableData.add(reputation);
		
		int len = storableData.size();
		InternalLogger.print("[SD] Populated storable data with " + len + " elements.");
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
	void loadFonts() {
		FontLoader.registerFont(FontLoader.THE_BOLD_FONT_FILE);
		FontLoader.registerFont(FontLoader.BEBAS_FONT_FILE);
		FontLoader.registerFont(FontLoader.EMOJI_FONT_FILE);
		FontLoader.registerFont(FontLoader.SEGOE_FONT_FILE);
	}
	void slashCommands(JDA jda) {
		CommandListUpdateAction action = jda.updateCommands();
		List<Command> allCommands = commands.getRegisteredCommands();
		for(Command command: allCommands) {
			if(!command.isRegisterable())
				continue;
			CommandData data = new CommandData(command
				.getFilteredCommandName(), command.description);
			List<OptionData> allArgs = new ArrayList<OptionData>(allCommands.size());
			for(CommandArg arg: command.args.getArgs()) {
				OptionData option = new OptionData(arg.type.nativeOption,
					arg.argName.toLowerCase(), arg.isOptional() ?
					"Optional argument." : "Required argument.");
				if(arg.type == ArgType.Enum) {
					EnumArg e = (EnumArg)arg;
					@SuppressWarnings("rawtypes")
					Enum[] values = e.getValues();
					Choice[] choices = new Choice[e.validTypes];
					int accessor = 0;
					for(int i = 0; i < values.length; i++) {
						@SuppressWarnings("rawtypes")
						Enum value = values[i];
						if(value.name().startsWith("_"))
							continue;
						choices[accessor++] = new Choice
							(value.name().toLowerCase(), i);
					}
				}
				
				allArgs.add(option);
			}
			action = action.addCommands(data.addOptions(allArgs));
		}
		action.queue();
	}
	void postSetup(JDA jda) {
		
		// Anything else that needs to be done.
		Constants.compileRegex();
		cooldowns.initialize(commands);
		UsernameGenerator.init();
		
		Guild bonziGuild = BonziUtils.getBonziGuild(jda);
		EmojiCache.appendGuildEmotes(bonziGuild);
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
	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		// TODO implement slash commands.
	}
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		// Execute commands and chat-related things.
		if(e.getAuthor().isBot()) return;
		if(tags.receiveMessage(e, this)) return;
		if(eventWaiter.onMessage(e.getMessage())) return;
		GuildSettings settings = this.guildSettings.getSettings(e.getGuild());
		if(!settings.testMessageInFilter(e.getMessage())) {
			logging.addMessageToHistory(e.getMessage(), e.getGuild());
			e.getMessage().delete().queue(null, fail -> {});
			e.getChannel().sendMessage(BonziUtils.failureEmbed(
				"You can't say that, " + e.getAuthor().getName() + "!")).queue();
			return;
		}
		
		logging.addMessageToHistory(e.getMessage(), e.getGuild());
		CommandExecutionInfo info = new CommandExecutionInfo(e);
		
		if(commands.onInput(info.setBonziBot(this)))
			return;
		
		// Everything from here on is not a command.
		
		// These are intentionally guild-only.
		BonziUtils.sendMentionMessage(e, this);
		BonziUtils.disableAfk(e.getAuthor(), e.getChannel(), this);
		reputation.checkMessage(e.getMessage(), this); // ok this is kinda a command
	}
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
		if(e.getAuthor().isBot()) return;
		if(tags.receiveMessage(e)) return;
		if(eventWaiter.onMessage(e.getMessage())) return;
		CommandExecutionInfo info = new CommandExecutionInfo(e);
		commands.onInput(info.setBonziBot(this)); // dont return
		
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
		// Give join role and join message (if any).
		this.guildSettings.memberJoined(this, e);
	}
	public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
		// Send a leave message.
		this.guildSettings.memberLeft(this, e);
	}
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
		// Send a message to and update GUIs.
		guis.onReactionAdd(e);
		
		// General reaction manager. (polls, etc...)
		reactions.reactionAddGuild(e);
		
		// Continue any reaction event waiters.
		eventWaiter.onReaction(new GenericReactionEvent(e));
	}
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
		// Send a message to and update GUIs. (UNUSED)
		guis.onReactionRemove(e);
		
		// General reaction manager. (polls, etc...)
		reactions.reactionRemoveGuild(e);
	}
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent e) {
		// Send a message to and update GUIs.
		guis.onReactionAdd(e);
		
		// General reaction manager.
		reactions.reactionAddPrivate(e);
		
		// Continue any reaction event waiters.
		eventWaiter.onReaction(new GenericReactionEvent(e));
	}
	public void onPrivateMessageReactionRemove(PrivateMessageReactionRemoveEvent e) {
		// Send a message to and update GUIs. (UNUSED)
		guis.onReactionRemove(e);
		
		// General reaction manager.
		reactions.reactionRemovePrivate(e);
	}
	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent e) {
		FunnyChannelManager.funnyChannels(this, e);
		
		// Leave the music channel if no more users are in it.
		
	}
	public void onTextChannelUpdateTopic(TextChannelUpdateTopicEvent e) {
		String topic = e.getNewTopic();
		Modifier[] mods = BonziUtils.getModifiers(topic);
		
		for(Modifier mod: mods) {
			if(mod == Modifier.LOGGING) {
				TextChannel tc = e.getChannel();
				
				// retrieve the audit for the change
				//   to get the user in question.
				tc.getGuild().retrieveAuditLogs().type(ActionType.CHANNEL_UPDATE).limit(1).queue(logs -> {
					if(logs.isEmpty()) {
						InternalLogger.printError("No audit logs fetched.", Severity.ERROR);
						return;
					}
					AuditLogEntry ale = logs.get(0);
					AuditLogChange alc = ale.getChangeByKey(AuditLogKey.CHANNEL_TOPIC);
					if(alc == null) {
						InternalLogger.printError("Updated text channel topic, but couldn't fetch the change.", Severity.ERROR);
						return;
					}
					User user = ale.getUser();
					this.logging.changeLogChannel(tc, this, user);
				});
			}
		}
	}
	public void onGuildUpdateName(GuildUpdateNameEvent e) {
		// Update the rule message, if any.
		String name = e.getNewName();
		Guild guild = e.getGuild();
		
		GuildSettings settings =
			this.guildSettings
			.getSettings(guild);
		settings.getRules().retrieveRulesMessage(guild, edit -> {
			MessageEmbed newRules = new EmbedBuilder(edit.getEmbeds().get(0))
				.setTitle(name + " Rules").build();
			edit.editMessage(newRules).queue();
		}, null);
	}
	
	// Logging Events
	public static HashMap<Long, List<Long>> banCache = new HashMap<Long, List<Long>>();
	public void onGuildBan(GuildBanEvent e) {
		long id = e.getGuild().getIdLong();
		if(banCache.containsKey(id)) {
			List<Long> bans = banCache.get(id);
			bans.add(e.getUser().getIdLong());
			banCache.put(id, bans);
		} else {
			List<Long> bans = new ArrayList<Long>();
			bans.add(e.getUser().getIdLong());
			banCache.put(id, bans);
		}
	}
	public void onGuildUnban(GuildUnbanEvent e) {
		long id = e.getGuild().getIdLong();
		if(banCache.containsKey(id)) {
			List<Long> bans = banCache.get(id);
			bans.remove(e.getUser().getIdLong());
			banCache.put(id, bans);
		}
	}
	public void onGuildMessageDelete(GuildMessageDeleteEvent e) {
		
		Message fetched = this.logging.getMessageById
			(e.getMessageIdLong(), e.getGuild());
		
		if(fetched != null && !fetched.getAuthor().isBot()) {
			String topic = e.getChannel().getTopic();
			Modifier[] modifiers = BonziUtils.getModifiers(topic);
			boolean noExpose = false;
			for(Modifier m: modifiers) {
				if(m == Modifier.NO_EXPOSE) {
					noExpose = true;
					break;
				}
			}
			if(!noExpose) {
				long author = fetched.getAuthor().getIdLong();
				UserAccount account = this.accounts.getUserAccount(author);
				if(!account.optOutExpose)
					this.logging.setExposeData(fetched, e.getGuild());
			}
		}
	}
	public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent e) {
		String nick = e.getNewNickname();
		GuildSettings gc = this.guildSettings
			.getSettings(e.getGuild());
		if(!gc.testMessageInFilter(nick)) {
			String old = e.getOldNickname();
			e.getMember().modifyNickname(old).queue();
		}
	}
	public void onTextChannelCreate(TextChannelCreateEvent e) {
		
	}
	public void onTextChannelDelete(TextChannelDeleteEvent e) {
		
	}
	public void onVoiceChannelCreate(VoiceChannelCreateEvent e) {
		
	}
	public void onVoiceChannelDelete(VoiceChannelDeleteEvent e) {
		
	}
}