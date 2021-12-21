package com.lukecreator.BonziBot.Commands;

import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Gui.GuiScriptPackages;
import com.lukecreator.BonziBot.Script.Model.ScriptPackage;

import net.dv8tion.jda.api.Permission;

public class ScriptingCommand extends Command {

	public ScriptingCommand() {
		this.subCategory = 1;
		this.name = "Scripting";
		this.unicodeIcon = "📜";
		this.description = "Manage the scripts in your server.";
		this.userRequiredPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.args = null;
		this.worksInDms = false;
		this.category = CommandCategory.UTILITIES;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		List<ScriptPackage> pkgs = e.bonzi.scripts.getPackages(e.guild);
		GuiScriptPackages gui = new GuiScriptPackages(pkgs);
		BonziUtils.sendGui(e, gui);
	}
}