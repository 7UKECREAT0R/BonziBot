package com.lukecreator.BonziBot.Commands;

import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.*;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.ScriptManager;
import com.lukecreator.BonziBot.Script.Model.InvocationButton;
import com.lukecreator.BonziBot.Script.Model.InvocationMethod.Implementation;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptPackage;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.components.ItemComponent;

public class ScriptButtonCommand extends Command {

	public ScriptButtonCommand() {
		this.subCategory = 1;
		this.name = "Script Button";
		this.icon = GenericEmoji.fromEmoji("🖱️");
		this.description = "Create a message with buttons that will trigger script(s).";
		this.args = new CommandArgCollection(new StringArg("body text"),
				new AutocompleteArg("scriptbutton1"),
				new AutocompleteArg("scriptbutton2").optional(),
				new AutocompleteArg("scriptbutton3").optional(),
				new AutocompleteArg("scriptbutton4").optional(),
				new AutocompleteArg("scriptbutton5").optional());
		this.worksInDms = false;
		this.userRequiredPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.category = CommandCategory.UTILITIES;
	}

	/**
	 * The input should be in the format "packageName:scriptName".
	 * @param guild The guild for which the validation is being done.
	 * @param bot   The BonziBot instance.
	 * @param input The input to validate.
	 * @return the located Script if the input is valid and the script button exists, null otherwise.
	 */
	public Script validateScriptButtonInput(Guild guild, BonziBot bot, String input) {
		if (!input.contains(":"))
			return null;
		String[] parts = input.split(":");
		if (parts.length != 2)
			return null;

		String packageName = parts[0];
		String scriptName = parts[1];
		List<ScriptPackage> packages = bot.scripts.getPackages(guild);

		for (ScriptPackage pkg : packages) {
			if (!pkg.packageName.equals(packageName))
				continue;
			Script script = pkg.getByName(scriptName);
			if (script != null && script.method.getImplementation() == Implementation.BUTTON) {
				return script;
			}
		}
		return null;
	}
	

	@Override
	public void run(CommandExecutionInfo e) {
		ScriptManager manager = e.bonzi.scripts;
		String bodyText = e.args.getString("body text");
		List<String> inputScripts = new ArrayList<>(5);
		List<ItemComponent> buttons = new ArrayList<>(5);
		inputScripts.add(e.args.getString("scriptbutton1"));
		
		if(e.args.argSpecified("scriptbutton2"))
			inputScripts.add(e.args.getString("scriptbutton2"));
		if(e.args.argSpecified("scriptbutton3"))
			inputScripts.add(e.args.getString("scriptbutton3"));
		if(e.args.argSpecified("scriptbutton4"))
			inputScripts.add(e.args.getString("scriptbutton4"));
		if(e.args.argSpecified("scriptbutton5"))
			inputScripts.add(e.args.getString("scriptbutton5"));
		
		for(String input : inputScripts) {
			Script find = validateScriptButtonInput(e.guild, e.bonzi, input);
			if(find == null) {
				e.reply(BonziUtils.failureEmbed("Couldn't find package.", "Query: `" + input + "`"));
				return;
			}
			
			String actionId = ScriptManager.createActionId(find.owningPackage.packageName, find.name);
			InvocationButton method = (InvocationButton)find.method;
			GuiButton button = new GuiButton(method.buttonText, method.buttonColor, actionId);
			buttons.add(button.toDiscord());
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(BonziUtils.COLOR_BONZI_PURPLE);
		eb.setDescription(bodyText);
		
		if(e.isSlashCommand) {
			e.slashCommand.replyEmbeds(BonziUtils.successEmbed("All right, created script button.")).setEphemeral(true).queue();
		}
		
		e.channel.sendMessageEmbeds(eb.build()).setActionRow(buttons).queue();
    }
}