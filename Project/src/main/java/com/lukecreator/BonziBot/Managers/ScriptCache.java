package com.lukecreator.BonziBot.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.InternalLogger.Severity;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.LongCountedBST;
import com.lukecreator.BonziBot.Script.Model.InvocationCommand;
import com.lukecreator.BonziBot.Script.Model.InvocationMethod;
import com.lukecreator.BonziBot.Script.Model.InvocationMethod.Implementation;
import com.lukecreator.BonziBot.Script.Model.InvocationTimed;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptPackage;
import com.lukecreator.BonziBot.Script.Model.ScriptTimer;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.commands.Command;

/**
 * Acts as a fast, cached way of evaluating script invocation data.
 * 
 * @author Lukec
 */
public class ScriptCache {

	public static final boolean DEBUG_MESSAGES = true;
	public static final int DELAY_MINIMUM = (int) BonziUtils.getMsForMinutes(60);
	public static final int POLL_INTERVAL = (int) BonziUtils.getMsForMinutes(1);

	/**
	 * All script command IDs.
	 */
	static final ArrayList<Long> commandIds = new ArrayList<Long>();
	/**
	 * All tokens formed by hash(packageName) + hash(scriptName)
	 */
	static final ArrayList<Long> buttonScripts = new ArrayList<Long>();
	/**
	 * List of guild IDs which have phrase scripts that need to be checked.
	 */
	static final LongCountedBST guildsWithPhraseScripts = new LongCountedBST();
	/**
	 * List of guild IDs which have a join script(s).
	 */
	static final LongCountedBST guildsWithJoinScripts = new LongCountedBST();
	/**
	 * List of guild IDs which have a leave script(s).
	 */
	static final LongCountedBST guildsWithLeaveScripts = new LongCountedBST();

	static final ArrayList<ScriptTimer> timers = new ArrayList<ScriptTimer>();

	public static long hashScriptButton(String packageName, String scriptName) {
		return ((long) packageName.hashCode()) + ((long) scriptName.hashCode());
	}

	public static void registerButton(ScriptPackage pkg, Script script) {
		if (DEBUG_MESSAGES)
			InternalLogger.print("Scripting - register button: " + script.name);
		String a = pkg.getName();
		String b = script.name;
		long hash = hashScriptButton(a, b);
		buttonScripts.add(hash);
	}

	public static void registerCommand(InvocationCommand command) {
		if (DEBUG_MESSAGES)
			InternalLogger.print("Scripting - register command: " + command.commandName + ", id: " + command._commandId);
		commandIds.add(command._commandId);
	}

	public static void registerPhraseGuild(long guildId) {
		if (DEBUG_MESSAGES)
			InternalLogger.print("Scripting - register phrase for: " + guildId);
		guildsWithPhraseScripts.put(guildId);
	}

	public static void registerJoinGuild(long guildId) {
		if (DEBUG_MESSAGES)
			InternalLogger.print("Scripting - register join for: " + guildId);
		guildsWithJoinScripts.put(guildId);
	}

	public static void registerLeaveGuild(long guildId) {
		if (DEBUG_MESSAGES)
			InternalLogger.print("Scripting - register leave for: " + guildId);
		guildsWithLeaveScripts.put(guildId);
	}

	public static void registerTimer(long guildId, InvocationTimed invoke, ScriptPackage pkg, Script script) {
		if (DEBUG_MESSAGES)
			InternalLogger
					.print("Scripting - register timer: " + script.name + ", delay: " + invoke.time.toShortString());
		timers.add(new ScriptTimer(invoke.time.ms, guildId, pkg.getName(), script.name));
	}

	public static void unregisterButton(ScriptPackage pkg, Script script) {
		if (DEBUG_MESSAGES)
			InternalLogger.print("Scripting - unregister button: " + script.name);
		String a = pkg.getName();
		String b = script.name;
		long hash = hashScriptButton(a, b);
		buttonScripts.remove(hash);
	}

	public static void unregisterCommand(InvocationCommand command) {
		if (DEBUG_MESSAGES)
			InternalLogger.print("Scripting - unregister command: " + command.commandName + ", id: " + command._commandId);
		commandIds.remove(command._commandId);
	}

	public static void unregisterPhraseGuild(long guildId) {
		if (DEBUG_MESSAGES)
			InternalLogger.print("Scripting - unregister phrase for: " + guildId);
		guildsWithPhraseScripts.remove(guildId);
	}

	public static void unregisterJoinGuild(long guildId) {
		if (DEBUG_MESSAGES)
			InternalLogger.print("Scripting - unregister join for: " + guildId);
		guildsWithJoinScripts.remove(guildId);
	}

	public static void unregisterLeaveGuild(long guildId) {
		if (DEBUG_MESSAGES)
			InternalLogger.print("Scripting - unregister leave for: " + guildId);
		guildsWithLeaveScripts.remove(guildId);
	}

	public static void unregisterTimer(ScriptPackage pkg, Script script) {
		if (DEBUG_MESSAGES)
			InternalLogger.print("Scripting - unregister timer: " + script.name);
		for (int i = 0; i < timers.size(); i++) {
			ScriptTimer timer = timers.get(i);
			if (!timer.packageName.equals(pkg.getName()))
				continue;
			if (!timer.scriptName.equals(script.name))
				continue;
			timers.remove(i);
			return;
		}
	}

	public static boolean shouldCheckScripts(SlashCommandInteractionEvent event) {
		if (!event.isFromGuild())
			return false;
		long eventId = event.getCommandIdLong();
		return commandIds.contains(eventId);
	}

	public static boolean shouldCheckScripts(ButtonInteractionEvent event) {
		return true; // Not called, already efficient enough.
	}

	public static boolean shouldCheckPhraseScripts(long guildId) {
		return guildsWithPhraseScripts.has(guildId) > 0;
	}

	public static boolean shouldCheckJoinScripts(long guildId) {
		return guildsWithJoinScripts.has(guildId) > 0;
	}

	public static boolean shouldCheckLeaveScripts(long guildId) {
		return guildsWithLeaveScripts.has(guildId) > 0;
	}

	/**
	 * Poll for and run timed scripts. Should be called every minute or so.
	 * 
	 * @param bb
	 * @param jda
	 */
	public static void pollTimedScripts(BonziBot bb, JDA jda) {
		long ms = System.currentTimeMillis();
		for (ScriptTimer timer : timers) {
			if (!timer.poll(ms))
				continue;

			// Run script.
			List<ScriptPackage> packages = bb.scripts.getPackages(timer.guildId);
			for (ScriptPackage pkg : packages) {
				if (!pkg.isEnabled())
					continue;
				if (!pkg.getName().equals(timer.packageName))
					continue;
				Script script = pkg.getByName(timer.scriptName);
				ScriptExecutor executor = script.code.createExecutor();
				InvocationTimed invoke = (InvocationTimed) script.method;
				executor.memory.writeVariable("total_runs", ++invoke.timesRun);

				Guild guild = jda.getGuildById(timer.guildId);
				GuildSettings settings = bb.guildSettings.getSettings(timer.guildId);

				ScriptContextInfo context = new ScriptContextInfo(null, null, null, null, null, null, jda, bb, null,
						null, guild, settings);

				executor.run(context);
				return;
			}
		}
	}

	/**
	 * Register all scripts in BonziBot to the caching system
     */
	public static void registerAll(ScriptManager manager, JDA jda) {

		HashMap<Long, List<Command>> existingCommands = new HashMap<>();
		Set<Entry<Long, List<ScriptPackage>>> entries = manager.packages.entrySet();

		synchronized (entries) {
			for (Entry<Long, List<ScriptPackage>> entry : entries) {
				long guildId = entry.getKey();

				if (!existingCommands.containsKey(guildId)) {
					Guild guild = jda.getGuildById(guildId);
					if (guild == null)
						continue;
					try {
						List<Command> fetch = guild.retrieveCommands().complete();
						existingCommands.put(guildId, fetch);
					} catch (ErrorResponseException e) {
						InternalLogger.printError(
								"Ignoring script command register because guild " + guild.getName() + " ("
										+ guild.getIdLong() + ") is not authorized with applications.commands",
								Severity.WARN);
						continue;
					}
				}

				List<Command> existing = existingCommands.get(guildId);

				List<ScriptPackage> scriptPackages = entry.getValue();
				for (ScriptPackage pkg : scriptPackages) {
					List<Script> scripts = pkg.getScripts();
					for (Script script : scripts) {
						// Evaluate script.
						script.owningPackage = pkg;
						InvocationMethod method = script.method;
						Implementation impl = method.getImplementation();

						if (impl == Implementation.COMMAND) {
							InvocationCommand invokeCmd = (InvocationCommand) method;
							registerCommand((InvocationCommand) method);
							if (existing.stream().anyMatch(cmd -> cmd.getIdLong() == invokeCmd._commandId)) {
								if (DEBUG_MESSAGES)
									InternalLogger.print("Scripting - NOT registering command: " + invokeCmd.commandName
											+ ", id: " + invokeCmd._commandId + "; already exists");
								continue;
							}
							continue;
						}

						switch (impl) {
							case BUTTON:
								registerButton(pkg, script);
								break;
							case JOIN:
								registerJoinGuild(guildId);
								break;
							case LEAVE:
								registerLeaveGuild(guildId);
								break;
							case PHRASE:
								registerPhraseGuild(guildId);
								break;
							case TIMED:
								registerTimer(guildId, (InvocationTimed) method, pkg, script);
								break;
							default:
								break;
						}
					}
				}
			}
		}
	}

	/**
	 * Register a specific script.
	 * 
	 * @param guildId The guild that owns this script.
	 * @param pkg     The package the script is in.
	 * @param script  The script to be registered.
	 */
	public static void register(long guildId, ScriptPackage pkg, Script script) {
		InvocationMethod method = script.method;
		Implementation impl = method.getImplementation();

		switch (impl) {
		case BUTTON:
			registerButton(pkg, script);
			break;
		case COMMAND:
			registerCommand((InvocationCommand) method);
			break;
		case JOIN:
			registerJoinGuild(guildId);
			break;
		case LEAVE:
			registerLeaveGuild(guildId);
			break;
		case PHRASE:
			registerPhraseGuild(guildId);
			break;
		case TIMED:
			registerTimer(guildId, (InvocationTimed) method, pkg, script);
			break;
		default:
			break;
		}
	}

	/**
	 * Unregister a specific script.
	 * 
	 * @param guildId The guild that owns this script.
	 * @param pkg     The package the script is in.
	 * @param script  The script to be unregistered.
	 */
	public static void unregister(long guildId, ScriptPackage pkg, Script script) {
		InvocationMethod method = script.method;
		Implementation impl = method.getImplementation();

		switch (impl) {
		case BUTTON:
			unregisterButton(pkg, script);
			break;
		case COMMAND:
			unregisterCommand((InvocationCommand) method);
			break;
		case JOIN:
			unregisterJoinGuild(guildId);
			break;
		case LEAVE:
			unregisterLeaveGuild(guildId);
			break;
		case PHRASE:
			unregisterPhraseGuild(guildId);
			break;
		case TIMED:
			unregisterTimer(pkg, script);
			break;
		default:
			break;
		}
	}
}
