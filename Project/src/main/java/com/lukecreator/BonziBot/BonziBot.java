package com.lukecreator.BonziBot;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.security.auth.login.LoginException;

import com.lukecreator.BonziBot.CommandAPI.*;
import com.lukecreator.BonziBot.Managers.*;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.reflections.Reflections;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.lukecreator.BonziBot.InternalLogger.Severity;
import com.lukecreator.BonziBot.Async.AutoRepeat;
import com.lukecreator.BonziBot.CommandAPI.CommandArg.ArgType;
import com.lukecreator.BonziBot.Data.AllocationList;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.EmoteCache;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GenericReactionEvent;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.Modifier;
import com.lukecreator.BonziBot.Data.QuickDraw;
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
import com.lukecreator.BonziBot.Logging.LogButtons;
import com.lukecreator.BonziBot.Logging.LogEntry;
import com.lukecreator.BonziBot.Logging.LogEntryBan;
import com.lukecreator.BonziBot.Logging.LogEntryDeletedMessage;
import com.lukecreator.BonziBot.Logging.LogEntryNicknameChange;
import com.lukecreator.BonziBot.Logging.LogEntryTextChannelCreate;
import com.lukecreator.BonziBot.Logging.LogEntryTextChannelRemove;
import com.lukecreator.BonziBot.Logging.LogEntryUnban;
import com.lukecreator.BonziBot.Logging.LogEntryUserJoined;
import com.lukecreator.BonziBot.Logging.LogEntryUserLeft;
import com.lukecreator.BonziBot.Logging.LogEntryVoiceChannelCreate;
import com.lukecreator.BonziBot.Logging.LogEntryVoiceChannelRemove;
import com.lukecreator.BonziBot.Music.MusicQueue;
import com.lukecreator.BonziBot.NoUpload.Constants;
import com.lukecreator.BonziBot.Script.Model.InvocationMethod;
import com.lukecreator.BonziBot.Script.Model.InvocationMethod.Implementation;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptPackage;
import com.lukecreator.BonziBot.Ticketing.TicketProtocol;
import com.lukecreator.BonziBot.Wrappers.RedditClient;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.audit.AuditLogKey;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateTopicEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

/**
 * The main bot class. Handles just about everything.
 */
public class BonziBot extends ListenerAdapter {
	
	JDABuilder builder = null;
	public boolean adminBypassing = false;
	public boolean test = true; // set App::DEBUG, not this
	
	/** This is the default handler for messages. It does text commands, XP, etc. */
	public static final MessageHandler DEFAULT_MESSAGE_HANDLER = new DefaultMessageHandler();
	
	MessageHandler[] messageHandlers = new MessageHandler[0];
	List<IStorableData> storableData = new ArrayList<>();
	ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
	public GuildSettingsManager guildSettings = new GuildSettingsManager();
	public ReactionRoleManager reactionRoles = new ReactionRoleManager();
	public AutocompleteManager autocomplete = new AutocompleteManager(this);
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
	public MusicManager music = new MusicManager();
	public MuteManager mutes = new MuteManager();
	public GridManager grid = new GridManager();
	public BanManager bans = new BanManager();
	public GuiManager guis = new GuiManager();
	public TagManager tags = new TagManager();
	
	public JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
	public AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
	Reflections reflectionsInstance = new Reflections("com.lukecreator.BonziBot");
	public CommandSystem commands = new CommandSystem(this.reflectionsInstance);
	public GitHub github;
	public SteamCache steam;
	public YouTube youtube;
	public YouTube youtubeDeepHarvester;
	
	public BonziBot(boolean test) throws LoginException {
		this.builder = JDABuilder.create(
				GatewayIntent.GUILD_VOICE_STATES,
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_MESSAGE_REACTIONS,
				GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.DIRECT_MESSAGE_REACTIONS,
				GatewayIntent.MESSAGE_CONTENT,
				GatewayIntent.GUILD_EMOJIS_AND_STICKERS
		).disableCache(
				CacheFlag.ACTIVITY,
				CacheFlag.ONLINE_STATUS,
				CacheFlag.CLIENT_STATUS,
				CacheFlag.SCHEDULED_EVENTS
		);
		
		this.test = test;
		String token = test ?
			Constants.BOT_TOKEN_TEST :
			Constants.BOT_TOKEN;
		this.builder.setToken(token);
	}
	
	public void start() throws InterruptedException, GeneralSecurityException {
		this.setupMessageHandlers();
		this.setupStorableData();
		this.loadData();
		JDA bot = this.setupBot();
		this.setupExecutors(bot);
		this.loadFonts();
		
		if(this.test)
			this.slashCommands(bot);
		
		this.postSetup(bot);
	}
	public void slashCommands(JDA jda) {
		CommandListUpdateAction action = jda.updateCommands();
		List<Command> allCommands = this.commands.getRegisteredCommands();
		
		for(Command command: allCommands) {
			if(!command.isRegisterable())
				continue;
			
			SlashCommandData data = Commands.slash(command.getSlashCommandName(), command.description);
			List<OptionData> allArgs = new ArrayList<>(allCommands.size());
			
			if(command.userRequiredPermissions != null && command.userRequiredPermissions.length > 0)
				data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(command.userRequiredPermissions));
			
			if(command.args != null && command.args.getArgs() != null) {
				for(CommandArg arg: command.args.getArgs()) {

					OptionData option = getOptionData(arg);

					if(arg.type == ArgType.Enum) {
                        assert arg instanceof EnumArg;
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
                        assert arg instanceof ChoiceArg;
                        String[] choiceStrings = ((ChoiceArg)arg).getValues();
						Choice[] choices = new Choice[choiceStrings.length];
						for(int i = 0; i < choiceStrings.length; i++)
							choices[i] = new Choice(choiceStrings[i].toLowerCase(), i);
						option.addChoices(choices);
					}
					allArgs.add(option);
				}
			}
			data.addOptions(allArgs);
			InternalLogger.print("Register command: " + data.toData());
			action = action.addCommands(data);
		}
		action.queue();
	}

	@NotNull
	private static OptionData getOptionData(CommandArg arg) {
		OptionType optionType = arg.type.nativeOption;

		// if it's an IntArg, there's a chance the input is 'all'
		if(arg instanceof IntArg) {
			if(((IntArg) arg).getSupportsAll())
				optionType = OptionType.STRING;
		}

		OptionData option = new OptionData(optionType,
			arg.argName.toLowerCase().replace(' ', '-'), arg.isOptional() ?
			"optional" : "required",
			!arg.isOptional());

		if(arg.type == ArgType.StringAutocomplete)
			option.setAutoComplete(true);
		return option;
	}

	void setupMessageHandlers() {
		this.messageHandlers = new MessageHandler[] {
				// auxiliary handlers
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
		this.storableData.add(this.accounts);
		this.storableData.add(this.guildSettings);
		this.storableData.add(this.tags);
		this.storableData.add(this.upgrades);
		this.storableData.add(this.lottery);
		this.storableData.add(this.rewards);
		this.storableData.add(this.logging);
		this.storableData.add(this.reputation);
		this.storableData.add(this.quickDraw);
		this.storableData.add(this.appeals);
		this.storableData.add(this.mutes);
		this.storableData.add(this.bans);
		this.storableData.add(this.todolists);
		this.storableData.add(this.grid);
		this.storableData.add(this.scripts);
		this.storableData.add(this.counting);
		this.storableData.add(this.reactionRoles);
		
		int len = this.storableData.size();
		InternalLogger.print("registered " + len + " elements of IStorableData");
	}
	JDA setupBot() throws InterruptedException, LoginException {
		JDA bot = this.builder.build();
		InternalLogger.print("Starting bot...");
		bot.awaitReady();
		bot.addEventListener(this);
		InternalLogger.print("Bot is ready! Events enabled.");
		return bot;
	}
	void setupExecutors(JDA jda) {
		
		Set<Class<? extends AutoRepeat>> toBeExecuted =
			this.reflectionsInstance.getSubTypesOf(AutoRepeat.class);
		
		int count = toBeExecuted.size(), i = 0;
		InternalLogger.print("Located " + count + " executors. Starting...");
		
		boolean error = false;
		try {
			for(Class<? extends AutoRepeat> execute: toBeExecuted) {
				String name = execute.getName();
				String status = "[" + (++i) + "/" + count +"]";
				InternalLogger.print("Starting thread for " + name + " " + status);
				
				AutoRepeat info = execute.getDeclaredConstructor().newInstance();
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
		} catch(InstantiationException | IllegalAccessException ie) {
			InternalLogger.printError(ie);
			error = true;
		} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			InternalLogger.printError(e);
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
	void postSetup(JDA jda) throws GeneralSecurityException {
		
		// anything else that needs to be done
		
		try {
			// setup audio
			// 2024 update: exclude deprecated built-in YouTube source manager and use v2 from lavalink-devs
			YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(false, true, true);
			this.audioPlayerManager.registerSourceManager(youtube);
			// noinspection deprecation
			AudioSourceManagers.registerRemoteSources(this.audioPlayerManager, com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);
			
			// compile regex patterns
			Constants.compileRegex();
			
			// initialize command cooldowns system
			this.cooldowns.initialize(this.commands);
			
			// download the stuff for the username generator
			UsernameGenerator.init();
			
			// prefetch all guild emotes
			for(Guild guild: jda.getGuilds())
				EmoteCache.appendGuildEmotes(guild);
			InternalLogger.print("Cached " + EmoteCache.getAmount() + " emoji from guilds.");
			
			// register all scripts into the cache
			ScriptCache.registerAll(this.scripts, jda);
			
			// download and parse emoji shortcodes
			GenericEmoji.initializeShortcode();
			
			// setup youtube api
			InternalLogger.print("Setting up YouTube API...");
			NetHttpTransport httpTransport = 
				GoogleNetHttpTransport.newTrustedTransport();
			this.youtube = new YouTube.Builder
				(httpTransport, this.jsonFactory, null)
				.setApplicationName(Constants.YTAPI_APP_NAME)
				.build();
			InternalLogger.print("Connected to YouTube: " + this.youtube.getApplicationName());
			
			// setup youtube-deep-harvester api
			InternalLogger.print("Setting up YouTube API (deep-harvester)...");
			this.youtubeDeepHarvester = new YouTube.Builder
				(httpTransport, this.jsonFactory, null)
				.setApplicationName(Constants.YTAPI_APP_NAME_DH)
				.build();
			InternalLogger.print("Connected to YouTube: " + this.youtubeDeepHarvester.getApplicationName());
			
			// connect to github
			InternalLogger.print("Connecting to GitHub...");
			this.github = new GitHubBuilder()
				.withOAuthToken(Constants.GITHUB_TOKEN)
				.build();
			InternalLogger.print("Connected to GitHub: " + this.github.getApiUrl());
			
			// download and parse steam titles
			this.steam = new SteamCache();
			this.steam.fetchTitles();
		} catch (IOException e) {
			InternalLogger.printError(e);
		}
	}
	public void saveData() {
		InternalLogger.print("Saving data...");
		if(DataSerializer.backup) {
			InternalLogger.print("Cancelled due to backup in progress...");
			return;
		}
		for(IStorableData data: this.storableData) {
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
		for(IStorableData data: this.storableData) {
			data.saveData();
		}
		InternalLogger.print("Saved data (BACKUP)!");
		DataSerializer.backup = false;
	}
	public void loadData() {
		int progress = 0;
		int total = this.storableData.size();
		
		for(IStorableData data: this.storableData) {
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
		int total = this.storableData.size();
		
		DataSerializer.backup = true;
		for(IStorableData data: this.storableData) {
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
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		if(ScriptCache.shouldCheckScripts(event)) {
			if(this.scripts.onScriptTrigger(event, this))
				return;
		}
		this.commands.onInput(event, this);
	}
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		String id = event.getComponentId();
		
		// Script call
		if(id.startsWith("::")) {
			this.scripts.onScriptTrigger(event, this);
			return;
		}
		
		// Check for hardcoded protocols
		String[] parts = id.split(":");
        switch (parts[0]) {
            case "_appeal":
                this.appeals.processButtonEvent(parts, this, event);
                return;
            case QuickDraw.BUTTON_PROTOCOL:
                String part2 = parts.length < 2 ? "" : parts[1];
                this.quickDraw.buttonReceived(event.getUser(), part2, event, this);
                return;
            case TicketProtocol.PROTOCOL:
                // TODO
                return;
        }

        MessageChannel channel = event.getChannel();
		
		if(channel.getType() == ChannelType.TEXT) {
			Guild guild = event.getGuild();
			assert guild != null;
			GuildSettings settings = this.guildSettings.getSettings(guild);
			if(settings.loggingEnabled && channel.getIdLong() == settings.loggingChannelCached) {
				// yes this is probably a logging button
				// unless someone decides the logging channel is the best place to use a gui lmao
				long msgId = event.getMessageIdLong();
				AllocationList<LogEntry> entries = this.logging.getAllocation(guild);
				
				LogEntry entry = entries.search(e -> {
					return e.messageId == msgId;
				});
				
				if(entry != null) {
					String actionId = event.getComponentId();
					
					boolean wasLoggingButton = true;
					if(actionId.equals(LogButtons.UNDO.protocol)) {
						event.deferEdit().queue();
						entry.performActionUndo(this, event);
					} else if(actionId.equals(LogButtons.WARN.protocol)) {
						event.deferEdit().queue();
						entry.performActionWarn(this, event);
					} else if(actionId.equals(LogButtons.MUTE.protocol)) {
						event.deferEdit().queue();
						entry.performActionMute(this, event);
					} else if(actionId.equals(LogButtons.KICK.protocol)) {
						event.deferEdit().queue();
						entry.performActionKick(this, event);
					} else if(actionId.equals(LogButtons.BAN.protocol)) {
						event.deferEdit().queue();
						entry.performActionBan(this, event);
					} else
						wasLoggingButton = false;
					
					if(wasLoggingButton) {
						event.editButton(event.getButton().asDisabled()).queue();
						return;
					}
				}
			}
		}
		
		if(this.eventWaiter.onClick(event))
			return; // acknowledged already
		
		this.guis.onButtonClick(event);
	}
	@Override
	public void onGenericSelectMenuInteraction(@NotNull GenericSelectMenuInteractionEvent event) {
		this.guis.onSelectionMenu(event);
	}
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if(e.isFromType(ChannelType.TEXT))
			this.onGuildMessageReceived(e);
		else if(e.isFromType(ChannelType.PRIVATE))
			this.onPrivateMessageReceived(e);
	}
	public void onGuildMessageReceived(MessageReceivedEvent e) {
		if(e.getAuthor().isBot())
			return;
		
		MessageChannelUnion channel = e.getChannel();
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
	public void onPrivateMessageReceived(MessageReceivedEvent e) {
		if(!e.isFromType(ChannelType.PRIVATE))
			return;
		
		MessageChannelUnion channel = e.getChannel();
		
		for(MessageHandler handler: this.messageHandlers) {
			if(!handler.appliesInChannel(channel))
				continue;
			handler.handlePrivateMessage(this, e);
		}
	}
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		
		long guildId = e.getGuild().getIdLong();
		if(ScriptCache.shouldCheckJoinScripts(guildId)) {
			List<ScriptPackage> packages = this.scripts.getPackages(guildId);
			for(ScriptPackage pkg: packages) {
				if(!pkg.isEnabled())
					continue;
				for(Script script: pkg.getScripts()) {
					InvocationMethod _method = script.method;
					Member member = e.getMember();
					if(_method.getImplementation() != Implementation.JOIN)
						continue;

					runScript(guildId, script, e.getJDA(), e.getUser(), member, e.getGuild());
				}
			}
		}
		
		LogEntryUserJoined ban = new LogEntryUserJoined();
		ban.loadData(e, this, entry -> {
			this.logging.tryLog(e.getGuild(), this, entry);
		}, null);
		
		// Give join role and join message (if any).
		this.guildSettings.memberJoined(this, e);
	}
	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
		long guildId = e.getGuild().getIdLong();
		if(ScriptCache.shouldCheckJoinScripts(guildId)) {
			List<ScriptPackage> packages = this.scripts.getPackages(guildId);
			for(ScriptPackage pkg: packages) {
				if(!pkg.isEnabled())
					continue;
				for(Script script: pkg.getScripts()) {
					InvocationMethod _method = script.method;
					Member member = e.getMember();
					if(_method.getImplementation() != Implementation.LEAVE)
						continue;

					runScript(guildId, script, e.getJDA(), e.getUser(), member, e.getGuild());
				}
			}
		}
		
		LogEntryUserLeft ban = new LogEntryUserLeft();
		ban.loadData(e, this, entry -> {
			this.logging.tryLog(e.getGuild(), this, entry);
		}, null);
		
		// Send a leave message.
		this.guildSettings.memberLeft(this, e);
	}
	@Override
	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
		Guild guild = event.getGuild(); // may be null
		AutoCompleteQuery query = event.getInteraction().getFocusedOption();
		String key = query.getName();
		String value = query.getValue();
		
		String[] results = this.autocomplete.provideResults(guild, key, value);
		if(results == null || results.length == 0) {
			event.replyChoiceStrings().queue();
			return;
		}

		if (results.length > 25) {
			// sort by the best match and prune
			Arrays.sort(results, Comparator.comparingInt(s -> LevenshteinDistance.getDefaultInstance().apply(value, s)));
			String[] prunedResults = Arrays.copyOfRange(results, 0, 25);
			event.replyChoiceStrings(prunedResults).queue();
		}
		
		event.replyChoiceStrings(results).queue();
	}

	private void runScript(long guildId, Script script, JDA jda, User user, Member actualMember, Guild guild) {
		ScriptExecutor executor = script.code.createExecutor();
		int member = executor.memory.createObjectReference(actualMember);
		executor.memory.writeExistingObjRef("member", member);

		GuildSettings settings = this.guildSettings.getSettings(guildId);
		ScriptContextInfo context = new ScriptContextInfo(null, null, null, null, null, null,
				jda, this, user, actualMember, guild, settings);

		executor.run(context);
    }

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if(e.isFromType(ChannelType.TEXT)) {
			// reaction roles
			this.reactionRoles.handleEvent(e, this);
			
			// general reaction manager (polls, pins, etc...)
			this.reactions.reactionAddGuild(e, this);
			
			// continue any reaction event waiters (changed to buttons)
			this.eventWaiter.onReaction(new GenericReactionEvent(e));
			
			// quick draw minigame
			this.quickDraw.reactionReceived(e.getUser(), e, this);
			
		}
		else if(e.isFromType(ChannelType.PRIVATE)) {
			// General reaction manager.
			this.reactions.reactionAddPrivate(e, this);
			
			// Continue any reaction event waiters.
			this.eventWaiter.onReaction(new GenericReactionEvent(e));
		}
	}
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
		if(e.isFromType(ChannelType.TEXT)) {
			// Send a message to and update GUIs. (UNUSED)
			this.guis.onReactionRemove(e);
			
			this.reactionRoles.handleEvent(e, this);
			
			// General reaction manager. (polls, etc...)
			this.reactions.reactionRemoveGuild(e, this);
		}
		else if(e.isFromType(ChannelType.PRIVATE)) {
			// Send a message to and update GUIs. (UNUSED)
			this.guis.onReactionRemove(e);
			
			// General reaction manager.
			this.reactions.reactionRemovePrivate(e, this);
		}
	}
	@Override
	public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent e) {
		FunnyChannelManager.funnyChannels(this, e);
	}
	@Override
	public void onChannelUpdateTopic(ChannelUpdateTopicEvent e) {
		if(!e.isFromGuild())
			return;
		if(!e.isFromType(ChannelType.TEXT))
			return;
		
		String topic = e.getNewValue();
		Modifier[] mods = BonziUtils.getModifiers(topic);
		
		for(Modifier mod: mods) {
			if(mod == Modifier.LOGGING) {
				TextChannel tc = (TextChannel)e.getChannel();
				
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
	@Override
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
	@Override
	public void onGenericGuildVoice(GenericGuildVoiceEvent e) {
    	Guild guild = e.getGuild();
    	Member self = guild.getSelfMember();
    	GuildVoiceState state = self.getVoiceState();

		if(state == null)
			return;
    	if(!state.inAudioChannel())
    		return;
    	
    	AudioChannel channel = state.getChannel();

		if(channel == null)
			return;

    	List<Member> session = channel.getMembers();
    	int usersInVoice = session.size();
    	
    	if(usersInVoice > 1)
    		return;
    	
    	// only bonzi is left
    	guild.getAudioManager().closeAudioConnection();
    	MusicQueue queue = this.music.getQueue(guild, this.audioPlayerManager);
    	queue.stop();
    }
	
	// Logging Events
	public static HashMap<Long, List<Long>> banCache = new HashMap<>();
	public static List<Long> dontLogDeletion = new ArrayList<>();
	@Override
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
		
		LogEntryBan ban = new LogEntryBan();
		ban.loadData(e, this, entry -> {
			this.logging.tryLog(e.getGuild(), this, entry);
		}, null);
	}
	@Override
	public void onGuildUnban(GuildUnbanEvent e) {
		long id = e.getGuild().getIdLong();
		if(this.bans.isBanned(e.getGuild(), e.getUser()))
			this.bans.unban(e.getGuild(), e.getUser());
		if(banCache.containsKey(id)) {
			List<Long> bans = banCache.get(id);
			bans.remove(e.getUser().getIdLong());
			banCache.put(id, bans);
		}
		
		LogEntryUnban ban = new LogEntryUnban();
		ban.loadData(e, this, entry -> {
			this.logging.tryLog(e.getGuild(), this, entry);
		}, null);
	}
	@Override
	public void onMessageDelete(MessageDeleteEvent e) {
		if(!e.isFromGuild())
			return;
		
		Message fetched = this.logging.getMessageById
			(e.getMessageIdLong(), e.getGuild());
		
		if(fetched == null)
			return;
		
		if(fetched.isFromGuild()) {
			Guild guild = fetched.getGuild();
			User author = fetched.getAuthor();
			if(banCache.containsKey(guild.getIdLong())) {
				List<Long> bans = banCache.get(guild.getIdLong());
				if(bans.contains(author.getIdLong()))
					return;
			}
		}
		
		if(dontLogDeletion.remove(fetched.getIdLong()))
			return;
		
		if(!fetched.getAuthor().isBot()) {
			LogEntryDeletedMessage log = new LogEntryDeletedMessage();
			log.loadData(fetched, this, entry -> {
				this.logging.tryLog(e.getGuild(), this, entry);
			}, null);
			
			String topic = ((TextChannel)e.getChannel()).getTopic();
			Modifier[] modifiers = BonziUtils.getModifiers(topic);
			boolean canExpose = true;
			for(Modifier m: modifiers)
				canExpose &= (m != Modifier.NO_EXPOSE);
			if(canExpose) {
				long author = fetched.getAuthor().getIdLong();
				UserAccount account = this.accounts.getUserAccount(author);
				if(!account.optOutExpose)
					this.logging.setExposeData(fetched, e.getGuild());
			}
		}
	}
	@Override
	public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent e) {
		String nick = e.getNewNickname();
		if(nick == null)
			return;
		GuildSettings gc = this.guildSettings
			.getSettings(e.getGuild());
		if(!gc.testMessageInFilter(nick)) {
			String old = e.getOldNickname();
			e.getMember().modifyNickname(old).queue(null, fail -> { /* no permission */ });
		} else {
			LogEntryNicknameChange log = new LogEntryNicknameChange();
			log.loadData(e, this, entry -> {
				this.logging.tryLog(e.getGuild(), this, entry);
			}, null);
		}
	}
	@Override
	public void onChannelCreate(ChannelCreateEvent e) {
		if(e.isFromType(ChannelType.TEXT)) {
			TextChannel tc = (TextChannel)e.getChannel();
			Guild guild = e.getGuild();
			GuildSettings settings = this
				.guildSettings.getSettings(guild);
			long mutedRole = settings.mutedRole;
			
			LogEntryTextChannelCreate ban = new LogEntryTextChannelCreate();
			ban.loadData(e, this, entry -> {
				this.logging.tryLog(e.getGuild(), this, entry);
			}, null);
			
			if(mutedRole != 0) {
				Role muted = guild.getRoleById(mutedRole);
				
				if(muted == null) {
					// Silently remove the muted role because it's been deleted.
					settings.mutedRole = 0L;
					this.guildSettings.setSettings(guild, settings);
				} else {
					tc.getManager().putPermissionOverride(muted, 0L,
						Permission.MESSAGE_SEND.getRawValue() |
						Permission.MESSAGE_ADD_REACTION.getRawValue() |
						Permission.MESSAGE_ATTACH_FILES.getRawValue()).queue();
				}
			}
		} else if(e.isFromType(ChannelType.VOICE)) {
			LogEntryVoiceChannelCreate ban = new LogEntryVoiceChannelCreate();
			ban.loadData(e, this, entry -> {
				this.logging.tryLog(e.getGuild(), this, entry);
			}, null);
		}
	}
	@Override
	public void onChannelDelete(ChannelDeleteEvent e) {
		if(e.isFromType(ChannelType.TEXT)) {
			LogEntryTextChannelRemove ban = new LogEntryTextChannelRemove();
			ban.loadData(e, this, entry -> {
				this.logging.tryLog(e.getGuild(), this, entry);
			}, null);
		} else if(e.isFromType(ChannelType.VOICE)) {
			LogEntryVoiceChannelRemove ban = new LogEntryVoiceChannelRemove();
			ban.loadData(e, this, entry -> {
				this.logging.tryLog(e.getGuild(), this, entry);
			}, null);
		}
	}
}