package com.lukecreator.BonziBot.Script.Model.Limiting;

import java.awt.Color;
import java.util.List;

import com.lukecreator.BonziBot.GuiAPI.DropdownItem;
import com.lukecreator.BonziBot.GuiAPI.GuiDropdown;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryChoice;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntrySwitch;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class StatementLimitRole implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;

	long roleId;
	boolean orHigher;
	
	@Override
	public String getKeyword() {
		return "require_role";
	}

	@Override
	public String getAsCode() {
		return "require_role " + this.roleId + " " + this.orHigher;
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		GuiDropdown dropdown = new GuiDropdown("Required role...", "role", false);
		for(Role role: server.getRoles())
			dropdown.addItem(new DropdownItem(role.getIdLong(), role.getName(), role.getId()));
		
		return new GuiEditEntry[] {
			new GuiEditEntryChoice(dropdown, null, "Required Role", "The role required to continue beyond this point."),
			new GuiEditEntrySwitch("higher", false, null, "Include Higher Roles", "Whether roles which are higher then this one also are allowed.")
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
		this.roleId = ((Long)inputs[0]).longValue();
	}

	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		if(!info.hasMember) {
			ScriptExecutor.raiseError(new ScriptError("There's no member to check the role of...", this));
			return;
		}
		
		Member member = info.member;
		
		// Allow continuing
		if(member.isOwner())
			return;
		if(member.hasPermission(Permission.ADMINISTRATOR))
			return;
		
		List<Role> roles = member.getRoles();
		
		if(roles.isEmpty()) {
			context.cancelExecution("Missing Role! You need to have <@&" + this.roleId + "> to run this script.", Color.red);
			return;
		}
		
		if(this.orHigher) {
			int position = info.guild.getRoleById(this.roleId).getPosition();
			if(roles.get(0).getPosition() < position) {
				context.cancelExecution("Missing Role! You need to have <@&" + this.roleId + "> or higher to run this script.", Color.red);
				return;
			}
		} else {
			if(!roles.stream().anyMatch(r -> r.getIdLong() == this.roleId)) {
				context.cancelExecution("Missing Role! You need to have <@&" + this.roleId + "> to run this script.", Color.red);
				return;
			}
		}
		
		// Allow continuing
		return;
	}

}
