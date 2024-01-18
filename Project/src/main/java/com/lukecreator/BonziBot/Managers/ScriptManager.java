package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.InvocationCommand;
import com.lukecreator.BonziBot.Script.Model.InvocationMethod.Implementation;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptPackage;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Manages scripts and all of their packages.
 * @author Lukec
 */
public class ScriptManager implements IStorableData {
	
	HashMap<Long, List<ScriptPackage>> packages = new HashMap<Long, List<ScriptPackage>>();
	
	public boolean onScriptTrigger(SlashCommandInteractionEvent event, BonziBot bb) {
		long commandId = event.getCommandIdLong();
		List<ScriptPackage> localPackages = this.packages.get(event.getGuild().getIdLong());
		
		if(localPackages == null)
			return false;
		
		for(ScriptPackage pkg: localPackages) {
			if(!pkg.isEnabled())
				continue;
			List<Script> scripts = pkg.getScripts();
			if(scripts == null)
				return false;
			for(Script script: scripts) {
				Implementation method = script.method.getImplementation();
				if(method != Implementation.COMMAND)
					continue;
				InvocationCommand cmd = (InvocationCommand)script.method;
				if(cmd._commandId != commandId)
					continue;
				
				InvocationCommand invoke = (InvocationCommand)script.method;
				ScriptExecutor executor = script.code.createExecutor();
				List<OptionMapping> mappings = event.getOptions();
				
				// Set invocation variables
				int member = executor.memory.createObjectReference(event.getMember());
				int channel = executor.memory.createObjectReference(event.getGuildChannel());
				executor.memory.writeExistingObjRef("member", member);
				executor.memory.writeExistingObjRef("channel", channel);
				
				String[] inputArgs = new String[invoke.argNames.size()];
				
				// Map the args
				if(!invoke.argNames.isEmpty()) {
					int index = 0;
					for(String arg: invoke.argNames) {
						OptionMapping mapping = mappings.get(index);
						String string = mapping.getAsString();
						inputArgs[index] = string;
						DynamicValue value = DynamicValue.parse(string);
						executor.memory.writeVariable(arg, value);
						index++;
					}
				}
				
				GuildSettings settings = bb.guildSettings.getSettings(event.getGuild());
				ScriptContextInfo context = new ScriptContextInfo(event.getCommandString(), event.getName(), inputArgs, event,
					null, event.getChannel().asTextChannel(), event.getJDA(), bb, event.getUser(), event.getMember(), event.getGuild(), settings);
				
				// run the script
				executor.run(context);
				
				// if interaction hasn't been responded to make sure to do that
				if(!event.isAcknowledged()) {
					if(executor.wasCancelledSilently())
						event.replyEmbeds(BonziUtils.failureEmbed("Ran script successfully!")).setEphemeral(true).queue();
					else
						event.replyEmbeds(BonziUtils.successEmbed("Ran script successfully!")).setEphemeral(false).queue();
				}
				
				return true;
			}
		}
		return false;
	}
	public boolean onScriptTrigger(ButtonInteractionEvent event, BonziBot bb) {
		String[] extract = extractScript(event.getComponentId());
		String packageName = extract[0];
		String scriptName = extract[1];
		
		List<ScriptPackage> pkgs = this.packages.get(event.getGuild().getIdLong());
		
		if(pkgs == null)
			return false;
		
		Optional<ScriptPackage> _match = pkgs.stream().filter(pkg ->
			pkg.getName().equalsIgnoreCase(packageName)).findFirst();
		
		if(_match.isEmpty() || !_match.get().isEnabled())
			return false;	// no response
		
		ScriptPackage match = _match.get();
		Script script = match.getByName(scriptName);
		
		if(script == null)
			return false;	// no response
		
		ScriptExecutor executor = script.code.createExecutor();
		int member = executor.memory.createObjectReference(event.getMember());
		int channel = executor.memory.createObjectReference(event.getGuildChannel());
		executor.memory.writeExistingObjRef("member", member);
		executor.memory.writeExistingObjRef("channel", channel);
		
		GuildSettings settings = bb.guildSettings.getSettings(event.getGuild());
		ScriptContextInfo context = new ScriptContextInfo(null, null, new String[0], null, null,
			event.getChannel().asTextChannel(), event.getJDA(), bb, event.getUser(), event.getMember(), event.getGuild(), settings);
		
		// don't worry about replying
		event.deferEdit().queue();
		
		// Run script
		executor.run(context);
		return true;
	}
	
	public static String createActionId(String packageName, String scriptName) {
		if(packageName.length() > 48)
			packageName = packageName.substring(0, 48);
		if(scriptName.length() > 48)
			scriptName = scriptName.substring(0, 48);
		return "::" + packageName + "::" + scriptName;
		
	}
	public static String[] extractScript(String actionId) {
		if(actionId.startsWith("::"))
			return actionId.substring(2).split("::");
		return actionId.split("::");
	}
	
	public List<ScriptPackage> getPackages(Guild guild) {
		return this.getPackages(guild.getIdLong());
	}
	public List<ScriptPackage> getPackages(long guildId) {
		if(this.packages.containsKey(guildId))
			return this.packages.get(guildId);
		else {
			List<ScriptPackage> ret = new ArrayList<ScriptPackage>(ScriptPackage.MAX_SCRIPTS);
			this.packages.put(guildId, ret);
			return ret;
		}
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(this.packages, "scripts");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object obj = DataSerializer.retrieveObject("scripts");
		if(obj != null)
			this.packages = (HashMap<Long, List<ScriptPackage>>)obj;
	}
}
