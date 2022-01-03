package com.lukecreator.BonziBot.Commands;

import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.ScriptManager;
import com.lukecreator.BonziBot.Script.Model.InvocationButton;
import com.lukecreator.BonziBot.Script.Model.InvocationMethod.Implementation;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptPackage;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class ScriptButtonCommand extends Command {

	public ScriptButtonCommand() {
		this.subCategory = 1;
		this.name = "Script Button";
		this.unicodeIcon = "🖱️";
		this.description = "Create a message that will trigger a script that is invoked using a button.";
		this.args = new CommandArgCollection(new StringArg("script"), new StringArg("body text"));
		this.worksInDms = false;
		this.userRequiredPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.category = CommandCategory.UTILITIES;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		ScriptManager manager = e.bonzi.scripts;
		List<ScriptPackage> packages = manager.getPackages(e.guild);
		
		String scriptName = e.args.getString("script");
		String bodyText = e.args.getString("body text");
		
		if(packages.isEmpty()) {
			e.reply(BonziUtils.failureEmbed("There are no packages in this server."));
			return;
		}
		
		ScriptPackage findPackage = null;
		Script find = null;
		
		found:
		for(ScriptPackage pkg: packages) {
			List<Script> scripts = pkg.getScripts();
			for(Script script: scripts) {
				if(script.method.getImplementation() != Implementation.BUTTON)
					continue;
				if(script.name.equalsIgnoreCase(scriptName)) {
					findPackage = pkg;
					find = script;
					break found;
				}
			}
		}
		
		if(find == null) {
			e.reply(BonziUtils.failureEmbed("No button-triggered script found with that name."));
			return;
		}
		
		String actionId = ScriptManager.createActionId(findPackage.getName(), find.name);
		InvocationButton method = (InvocationButton)find.method;
		GuiButton button = new GuiButton(method.buttonText, method.buttonColor, actionId);
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(BonziUtils.COLOR_BONZI_PURPLE);
		eb.setDescription(bodyText);
		
		if(e.isSlashCommand) {
			e.slashCommand.replyEmbeds(BonziUtils.successEmbed("All right, created script button.")).setEphemeral(true).queue();
		}
		
		e.channel.sendMessageEmbeds(eb.build()).setActionRow(button.toDiscord()).queue();
		
		return;
	}
}