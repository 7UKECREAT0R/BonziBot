package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Gui.GuiReactionRoles;

import net.dv8tion.jda.api.Permission;

public class ReactionRolesCommand extends Command {

	public ReactionRolesCommand() {
		this.subCategory = 0;
		this.name = "Reaction Roles";
		this.unicodeIcon = "📋";
		this.description = "Create a custom menu that lets users give themselves roles.";
		this.args = CommandArgCollection.single("text");
		this.category = CommandCategory.UTILITIES;
		this.worksInDms = false;
		this.setCooldown(10000);
		
		this.userRequiredPermissions = new Permission[] { Permission.MANAGE_ROLES };
		this.neededPermissions = new Permission[] { Permission.MANAGE_ROLES };
	}

	@Override
	public void run(CommandExecutionInfo e) {
		String text = e.args.getString("text");
		GuiReactionRoles gui = new GuiReactionRoles(text);
		BonziUtils.sendGui(e, gui);
	}
}