package com.lukecreator.BonziBot;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.security.auth.login.LoginException;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.reflections.Reflections;

import com.lukecreator.BonziBot.InternalLogger.Severity;
import com.lukecreator.BonziBot.Async.AutoRepeat;
import com.lukecreator.BonziBot.CommandAPI.ChoiceArg;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArg;
import com.lukecreator.BonziBot.CommandAPI.CommandArg.ArgType;
import com.lukecreator.BonziBot.CommandAPI.CommandSystem;
import com.lukecreator.BonziBot.CommandAPI.EnumArg;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.EmoteCache;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GenericReactionEvent;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.Modifier;
import com.lukecreator.BonziBot.Data.SteamCache;
import com.lukecreator.BonziBot.Data.StringProvider;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Data.UsernameGenerator;
import com.lukecreator.BonziBot.Graphics.FontLoader;
import com.lukecreator.BonziBot.Handling.AnonymousMessageHandler;
import com.lukecreator.BonziBot.Handling.CountingMessageHandler;
import com.lukecreator.BonziBot.Handling.DefaultMessageHandler;
import com.lukecreator.BonziBot.Handling.MessageHandler;
import com.lukecreator.BonziBot.Handling.PicturesOnlyMessageHandler;
import com.lukecreator.BonziBot.Handling.PremiumOnlyMessageHandler;
import com.lukecreator.BonziBot.Handling.RPGMessageHandler;
import com.lukecreator.BonziBot.Managers.AppealsManager;
import com.lukecreator.BonziBot.Managers.BanManager;
import com.lukecreator.BonziBot.Managers.CooldownManager;
import com.lukecreator.BonziBot.Managers.CountingManager;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.FunnyChannelManager;
import com.lukecreator.BonziBot.Managers.GridManager;
import com.lukecreator.BonziBot.Managers.GuiManager;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;
import com.lukecreator.BonziBot.Managers.LoggingManager;
import com.lukecreator.BonziBot.Managers.LotteryManager;
import com.lukecreator.BonziBot.Managers.MuteManager;
import com.lukecreator.BonziBot.Managers.QuickDrawManager;
import com.lukecreator.BonziBot.Managers.ReactionManager;
import com.lukecreator.BonziBot.Managers.RepManager;
import com.lukecreator.BonziBot.Managers.RewardManager;
import com.lukecreator.BonziBot.Managers.ScriptCache;
import com.lukecreator.BonziBot.Managers.ScriptManager;
import com.lukecreator.BonziBot.Managers.SpecialPeopleManager;
import com.lukecreator.BonziBot.Managers.TagManager;
import com.lukecreator.BonziBot.Managers.TodoListManager;
import com.lukecreator.BonziBot.Managers.UpgradeManager;
import com.lukecreator.BonziBot.Managers.UserAccountManager;
import com.lukecreator.BonziBot.NoUpload.Constants;
import com.lukecreator.BonziBot.Script.Model.InvocationMethod;
import com.lukecreator.BonziBot.Script.Model.InvocationMethod.Implementation;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptPackage;
import com.lukecreator.BonziBot.Wrappers.RedditClient;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.audit.AuditLogKey;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
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
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
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
	public boolean test = false;
	
	/** This is the default handler for messages. It does text commands, XP, etc. */
	public static MessageHandler DEFAULT_MESSAGE_HANDLER = new DefaultMessageHandler();
	
	MessageHandler[] messageHandlers = new MessageHandler[0];
	List<IStorableData> storableData = new ArrayList<IStorableData>();
	ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(0);
	public GuildSettingsManager guildSettings = new GuildSettingsManager();
	public EventWaiterManager eventWaiter = new EventWaiterManager();
	public SpecialPeopleManager special = new SpecialPeopleManager();
	public UserAccountManager accounts = new UserAccountManager();
	public QuickDrawManager quickDraw = new QuickDrawManager();
	public ReactionManager reactions = new ReactionManager();
	public CooldownManager cooldowns = new CooldownManager();
	public TodoListManager todolists = new TodoListManager();
	public CountingManager counting = new CountingManager();
	public UpgradeManager upgrades = new UpgradeManager();
	public AppealsManager appeals = new AppealsManager();
	public StringProvider strings = new StringProvider();
	public LotteryManager lottery = new LotteryManager();
	public LoggingManager logging = new LoggingManager();
	public RewardManager rewards = new RewardManager();
	public ScriptManager scripts = new ScriptManager();
	public RedditClient reddit = new RedditClient();
	public RepManager reputation = new RepManager();
	public MuteManager mutes = new MuteManager();
	public GridManager grid = new GridManager();
	public BanManager bans = new BanManager();
	public GuiManager guis = new GuiManager();
	public TagManager tags = new TagManager();
	
	Reflections reflectionsInstance = new Reflections("com.lukecreator.BonziBot");
	public CommandSystem commands = new CommandSystem(reflectionsInstance);
	public GitHub github;
	public SteamCache steam;
	
	public BonziBot(boolean test) throws LoginException {
		builder = JDABuilder.create(
			GatewayIntent.GUILD_BANS,
			GatewayIntent.GUILD_VOICE_STATES,
			GatewayIntent.GUILD_MEMBERS,
			GatewayIntent.GUILD_MESSAGES,
			GatewayIntent.GUILD_MESSAGE_REACTIONS,
			GatewayIntent.GUILD_EMOJIS,
			GatewayIntent.DIRECT_MESSAGES,
			GatewayIntent.DIRECT_MESSAGE_REACTIONS
			).disableCache(
			CacheFlag.ACTIVITY,
			CacheFlag.ONLINE_STATUS,
			CacheFlag.CLIENT_STATUS);
		this.test = test;
		String token = test ?
			Constants.BOT_TOKEN_TEST :
			Constants.BOT_TOKEN;
		builder.setToken(token).addEventListeners(this);
	}
	
	public void start() throws InterruptedException, LoginException {
		setupMessageHandlers();
		setupStorableData();
		loadData();
		JDA bot = setupBot();
		setupExecutors(bot);
		loadFonts();
		
		if(this.test)
			slashCommands(bot);
		
		postSetup(bot);
	}
	public void slashCommands(JDA jda) {
		CommandListUpdateAction action = jda.updateCommands();
		List<Command> allCommands = commands.getRegisteredCommands();
		
		for(Command command: allCommands) {
			if(!command.isRegisterable())
				continue;
			CommandData data = new CommandData(command
				.getSlashCommandName(), command.description);
			List<OptionData> allArgs = new ArrayList<OptionData>(allCommands.size());
			
			if(command.args != null && command.args.getArgs() != null) {
				for(CommandArg arg: command.args.getArgs()) {
					OptionData option = new OptionData(arg.type.nativeOption,
						arg.argName.toLowerCase().replace(' ', '-'), arg.isOptional() ?
						"optional" : "required",
						!arg.isOptional());
					
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
						option.addChoices(choices);
					} else if(arg.type == ArgType.Choice) {
						String[] strs = ((ChoiceArg)arg).getValues();
						Choice[] choices = new Choice[strs.length];
						for(int i = 0; i < strs.length; i++)
							choices[i] = new Choice(strs[i].toLowerCase(), i);
						option.addChoices(choices);
					}
					allArgs.add(option);
				}
			}
			data.addOptions(allArgs);
			InternalLogger.print("Register command: " + data.toData().toString());
			action = action.addCommands(data);
		}
		action.queue();
	}
	
	void setupMessageHandlers() {
		this.messageHandlers = new MessageHandler[] {
				// auxilliary handlers
			new CountingMessageHandler(),		// Counting Game Modifier
			new RPGMessageHandler(),			// RPG Modifier
			new PicturesOnlyMessageHandler(),	// Pictures Only Modifier
			new PremiumOnlyMessageHandler(),	// Premium Only Modifier
			new AnonymousMessageHandler(),		// Anonymous Modifier
			
				// default
			DEFAULT_MESSAGE_HANDLER				// Fallback Handler (99% of all messages go here)
		};
	}
	void setupStorableData() {
		this.storableData.clear();
		this.storableData.add(accounts);
		this.storableData.add(guildSettings);
		this.storableData.add(tags);
		this.storableData.add(upgrades);
		this.storableData.add(lottery);
		this.storableData.add(rewards);
		this.storableData.add(logging);
		this.storableData.add(reputation);
		this.storableData.add(quickDraw);
		this.storableData.add(appeals);
		this.storableData.add(mutes);
		this.storableData.add(bans);
		this.storableData.add(todolists);
		this.storableData.add(grid);
		this.storableData.add(scripts);
		this.storableData.add(counting);
		
		int len = this.storableData.size();
		InternalLogger.print("Populated storable data with " + len + " elements.");
	}
	JDA setupBot() throws InterruptedException, LoginException {
		JDA bot = builder.build();
		InternalLogger.print("Starting bot...");
		bot.awaitReady();
		InternalLogger.print("Bot is ready!");
		return bot;
	}
	void setupExecutors(JDA jda) {
		
		Set<Class<? extends AutoRepeat>> toBeExecuted =
			reflectionsInstance.getSubTypesOf(AutoRepeat.class);
		
		int count = toBeExecuted.size(), i = 0;
		InternalLogger.print("Located " + count + " executors. Starting...");
		
		boolean error = false;
		try {
			for(Class<? extends AutoRepeat> execute: toBeExecuted) {
				String name = execute.getName();
				String status = "[" + (++i) + "/" + count +"]";
				InternalLogger.print("Starting thread for " + name + " " + status);
				
				AutoRepeat info = execute.newInstance();
				BonziBot singleton = this;
				Runnable run = new Runnable() {
					@Override
					public void run() {
						info.run(singleton, jda);
					}
				};
				
				this.threadPool.scheduleAtFixedRate(run,
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
				InternalLogger.print("Could not start all executors.");
			else
				InternalLogger.print("All executors started.");
		}
	}
	void loadFonts() {
		FontLoader.registerFont(FontLoader.THE_BOLD_FONT_FILE);
		FontLoader.registerFont(FontLoader.BEBAS_FONT_FILE);
		FontLoader.registerFont(FontLoader.EMOJI_FONT_FILE);
		FontLoader.registerFont(FontLoader.SEGOE_FONT_FILE);
	}
	void postSetup(JDA jda) {
		
		// anything else that needs to be done
		
		try {
			// compile regex patterns
			Constants.compileRegex();
			
			// initialize command cooldowns system
			cooldowns.initialize(commands);
			
			// download the stuff for the username generator
			UsernameGenerator.init();
			
			// prefetch all guild emotes
			for(Guild guild: jda.getGuilds())
				EmoteCache.appendGuildEmotes(guild);
			
			// register all scripts into the cache
			ScriptCache.registerAll(this.scripts, jda);
			
			// download and parse emoji shortcodes
			GenericEmoji.initializeShortcode();
			
			// download and parse steam titles
			steam = new SteamCache();
			steam.fetchTitles();
			
			InternalLogger.print("Connecting to GitHub...");
			github = new GitHubBuilder()
				.withOAuthToken(Constants.GITHUB_TOKEN)
				.build();
			InternalLogger.print("Connected");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void saveData() {
		InternalLogger.print("Saving data...");
		if(DataSerializer.backup) {
			InternalLogger.print("Cancelled due to backup in progress...");
			return;
		}
		for(IStorableData data: storableData) {
			data.saveData();
		}
		InternalLogger.print("Saved data!");
	}
	public void saveDataBackup() {
		InternalLogger.print("Saving data (BACKUP)...");
		if(DataSerializer.backup) {
			InternalLogger.print("Cancelled due to backup in progress...");
			return;
		}
		DataSerializer.backup = true;
		for(IStorableData data: storableData) {
			data.saveData();
		}
		InternalLogger.print("Saved data (BACKUP)!");
		DataSerializer.backup = false;
	}
	public void loadData() {
		int progress = 0;
		int total = storableData.size();
		
		for(IStorableData data: storableData) {
			InternalLogger.print("Loading " + data.getClass().getName() + "... [" + ++progress + "/" + total + "]");
			try {
				data.loadData();
			} catch(EOFException exc) {
				InternalLogger.printError("Could not load file into: " + data.getClass().getName(), Severity.FATAL);
			}
		}
		InternalLogger.print("Loaded all data!");
	}
	public void loadDataBackup() {
		int progress = 0;
		int total = storableData.size();
		
		DataSerializer.backup = true;
		for(IStorableData data: storableData) {
			InternalLogger.print("Loading backup For " + data.getClass().getName() + "... [" + ++progress + "/" + total + "]");
			try {
				data.loadData();
			} catch(EOFException exc) {
				InternalLogger.printError("Could not load backup file into: " + data.getClass().getName(), Severity.FATAL);
			}
		}
		InternalLogger.print("Loaded all backup data!");
		DataSerializer.backup = false;
	}
	
	// Events
	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		if(ScriptCache.shouldCheckScripts(event)) {
			if(this.scripts.onScriptTrigger(event, this))
				return;
		}
		this.commands.onInput(event, this);
	}
	@Override
	public void onButtonClick(ButtonClickEvent event) {
		String id = event.getComponentId();
		
		// Script call
		if(id.startsWith("::")) {
			this.scripts.onScriptTrigger(event, this);
			return;
		}
		
		// Check for hardcoded protocols
		String[] parts = id.split(":");
		if(parts[0].equals("_appeal")) {
			this.appeals.processButtonEvent(parts, this, event);
			return;
		}
		
		if(this.eventWaiter.onClick(event))
			return; // acknowledged already
		this.guis.onButtonClick(event);
	}
	@Override
	public void onSelectionMenu(SelectionMenuEvent event) {
		this.guis.onSelectionMenu(event);
	}
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		MessageChannel channel = e.getChannel();
		Modifier[] modifiers = BonziUtils.getChannelModifiers(channel);
		
		for(MessageHandler handler: this.messageHandlers) {
			if(!handler.appliesInChannel(channel))
				continue;
			if(!handler.appliesInModifiers(modifiers))
				continue;
			handler.handleGuildMessage(this, e, modifiers);
			return;
		}
	}
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
		MessageChannel channel = e.getChannel();
		
		for(MessageHandler handler: this.messageHandlers) {
			if(!handler.appliesInChannel(channel))
				continue;
			handler.handlePrivateMessage(this, e);
		}
	}
	public void onGuildJoin(GuildJoinEvent e) {
		// ahhhHH
	}
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		
		long guildId = e.getGuild().getIdLong();
		if(ScriptCache.shouldCheckJoinScripts(guildId)) {
			List<ScriptPackage> packages = this.scripts.getPackages(guildId);
			for(ScriptPackage pkg: packages) {
				if(!pkg.isEnabled())
					continue;
				for(Script script: pkg.getScripts()) {
					InvocationMethod _method = script.method;
					if(_method.getImplementation() != Implementation.JOIN)
						continue;
					
					ScriptExecutor executor = script.code.createExecutor();
					int member = executor.memory.createObjectReference(e.getMember());
					executor.memory.writeExistingObjRef("member", member);
					
					GuildSettings settings = this.guildSettings.getSettings(guildId);
					ScriptContextInfo context = new ScriptContextInfo(null, null, null, null, null, null,
						e.getJDA(), this, e.getUser(), e.getMember(), e.getGuild(), settings);
					
					executor.run(context);
					return;
				}
			}
		}
		
		// Give join role and join message (if any).
		this.guildSettings.memberJoined(this, e);
	}
	public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
		
		long guildId = e.getGuild().getIdLong();
		if(ScriptCache.shouldCheckJoinScripts(guildId)) {
			List<ScriptPackage> packages = this.scripts.getPackages(guildId);
			for(ScriptPackage pkg: packages) {
				if(!pkg.isEnabled())
					continue;
				for(Script script: pkg.getScripts()) {
					InvocationMethod _method = script.method;
					if(_method.getImplementation() != Implementation.LEAVE)
						continue;
					
					ScriptExecutor executor = script.code.createExecutor();
					int member = executor.memory.createObjectReference(e.getMember());
					executor.memory.writeExistingObjRef("member", member);
					
					GuildSettings settings = this.guildSettings.getSettings(guildId);
					ScriptContextInfo context = new ScriptContextInfo(null, null, null, null, null, null,
						e.getJDA(), this, e.getUser(), e.getMember(), e.getGuild(), settings);
					
					executor.run(context);
					return;
				}
			}
		}
		
		// Send a leave message.
		this.guildSettings.memberLeft(this, e);
	}
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
		// Send a message to and update GUIs. (outdated)
		//guis.onReactionAdd(e);
		
		// General reaction manager. (polls, pins, etc...)
		reactions.reactionAddGuild(e, this);
		
		// Continue any reaction event waiters.
		eventWaiter.onReaction(new GenericReactionEvent(e));
		
		// Quick draw minigame
		quickDraw.reactionReceived(e.getUser(), e, this);
	}
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
		// Send a message to and update GUIs. (UNUSED)
		guis.onReactionRemove(e);
		
		// General reaction manager. (polls, etc...)
		reactions.reactionRemoveGuild(e, this);
	}
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent e) {
		// Send a message to and update GUIs. (outdated)
		//guis.onReactionAdd(e);
		
		// General reaction manager.
		reactions.reactionAddPrivate(e, this);
		
		// Continue any reaction event waiters.
		eventWaiter.onReaction(new GenericReactionEvent(e));
	}
	public void onPrivateMessageReactionRemove(PrivateMessageReactionRemoveEvent e) {
		// Send a message to and update GUIs. (UNUSED)
		guis.onReactionRemove(e);
		
		// General reaction manager.
		reactions.reactionRemovePrivate(e, this);
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
				}, fail -> {/* no permission */});
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
			edit.editMessageEmbeds(newRules).queue();
		}, null); // deletion is already handled
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
		if(this.bans.isBanned(e.getGuild(), e.getUser()))
			this.bans.unban(e.getGuild(), e.getUser());
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
		if(nick == null)
			return;
		GuildSettings gc = this.guildSettings
			.getSettings(e.getGuild());
		if(!gc.testMessageInFilter(nick)) {
			String old = e.getOldNickname();
			e.getMember().modifyNickname(old).queue(null, fail -> { /* no permission */ });
		}
	}
	public void onTextChannelCreate(TextChannelCreateEvent e) {
		TextChannel tc = e.getChannel();
		Guild guild = e.getGuild();
		GuildSettings settings = this
			.guildSettings.getSettings(guild);
		long mutedRole = settings.mutedRole;
		
		if(mutedRole != 0) {
			Role muted = guild.getRoleById(mutedRole);
			
			if(muted == null) {
				// Silently remove the muted role because it's been deleted.
				settings.mutedRole = 0l;
				this.guildSettings.setSettings(guild, settings);
			} else {
				tc.getManager().putPermissionOverride(muted, 0l,
					Permission.MESSAGE_WRITE.getRawValue() |
					Permission.MESSAGE_ADD_REACTION.getRawValue() |
					Permission.MESSAGE_ATTACH_FILES.getRawValue()).queue();
			}
		}
	}
	public void onTextChannelDelete(TextChannelDeleteEvent e) {
		
	}
	public void onVoiceChannelCreate(VoiceChannelCreateEvent e) {
		
	}
	public void onVoiceChannelDelete(VoiceChannelDeleteEvent e) {
		
	}
}