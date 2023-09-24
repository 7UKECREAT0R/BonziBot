package com.lukecreator.BonziBot.Script.Model.Limiting;

import java.awt.Color;

import com.lukecreator.BonziBot.GuiAPI.DropdownItem;
import com.lukecreator.BonziBot.GuiAPI.GuiDropdown;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryChoice;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class StatementLimitPermission implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;

	Permission required;
	
	@Override
	public String getKeyword() {
		return "require_permission";
	}

	@Override
	public String getAsCode() {
		return "require_permission " + this.required.name();
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		GuiDropdown dropdown = new GuiDropdown("Require permission...", "permission", false);
		for(Permission permission: Permission.values())
			dropdown.addItem(new DropdownItem(permission, permission.getName(), permission.name()));
		
		return new GuiEditEntry[] {
			new GuiEditEntryChoice(dropdown, null, "Permission", "The permission required to continue beyond this point.")
		};
	}

	@Override
	public String getNewVariable() {
		return null;
	}

	@Override
	public StatementCategory getCategory() {
		return StatementCategory.LIMITING;
	}

	@Override
	public void parse(Object[] inputs) {
		this.required = (Permission)inputs[0];
	}

	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		if(!info.hasMember) {
			ScriptExecutor.raiseError(new ScriptError("There's no member to check the permissions of...", this));
			return;
		}
		
		Member member = info.member;
		
		// Allow continuing
		if(member.isOwner())
			return; 
		if(member.hasPermission(Permission.ADMINISTRATOR))
			return;
		
		if(!member.hasPermission(this.required)) {
			context.cancelExecution("Missing Permission! You need to have \"" + this.required.getName() + "\" to run this script.", Color.red);
			return;
		}
		
		// Allow continuing
		return;
	}

}
