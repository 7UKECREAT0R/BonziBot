package com.lukecreator.BonziBot.Script.Model.Storage;

import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.DynamicValue;
import com.lukecreator.BonziBot.Script.Model.PackageStorage;
import com.lukecreator.BonziBot.Script.Model.PackageStorage.StorageEntry;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public class StatementGetStorage implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;

	String key;
	String dst;
	
	@Override
	public String getKeyword() {
		return "s_get";
	}
	@Override
	public String getAsCode() {
		return "s_get " + Script.asArgument(this.key) + " " + Script.asArgument(this.dst);
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			caller.createVariableChoice(null, "Key", "The key to get the data from."),
			new GuiEditEntryText(new StringArg("dst"), null, "Destination", "The variable to store the retrieved data in.")
		};
	}
	
	@Override
	public String getNewVariable() {
		return this.dst;
	}

	@Override
	public StatementCategory getCategory() {
		return StatementCategory.STORAGE;
	}

	@Override
	public void parse(Object[] inputs) {
		this.key = (String)inputs[0];
		this.dst = (String)inputs[1];
	}
	
	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		
		DynamicValue keyVar = context.memory.readVariable(this.key);
		if(keyVar == null) {
			ScriptExecutor.raiseError(new ScriptError("Non-existent variable as key.", this));
			return;
		}
		
		Object object = keyVar.getAsObject(context.memory);
		long key = PackageStorage.toKey(object);
		StorageEntry entry = context._script.owningPackage.storage.getData(key);
		
		if(entry == null) {
			ScriptExecutor.raiseError(new ScriptError("The key \"" + object.toString() + "\" doesn't have any data.", this));
			return;
		}
		
		switch(entry.type) {
		case BOOLEAN:
			context.memory.writeVariable(this.dst, (Boolean)entry.data);
			break;
		case DOUBLE:
			context.memory.writeVariable(this.dst, (Double)entry.data);
			break;
		case LONG:
			context.memory.writeVariable(this.dst, (Long)entry.data);
			break;
		case STRING:
			context.memory.writeVariable(this.dst, (String)entry.data);
			break;
		case ENTITYCHANNEL:
			long channelId = ((Long)entry.data).longValue();
			GuildChannel channel = info.guild.getGuildChannelById(channelId);
			int channelIndex = context.memory.createObjectReference(channel);
			context.memory.writeExistingObjRef(this.dst, channelIndex);
			break;
		case ENTITYMEMBER:
			long memberId = ((Long)entry.data).longValue();
			Member member = info.guild.getMemberById(memberId);
			int memberIndex = context.memory.createObjectReference(member);
			context.memory.writeExistingObjRef(this.dst, memberIndex);
			break;
		case ENTITYROLE:
			long roleId = ((Long)entry.data).longValue();
			Role role = info.guild.getRoleById(roleId);
			int roleIndex = context.memory.createObjectReference(role);
			context.memory.writeExistingObjRef(this.dst, roleIndex);
			break;
		default:
			break;
		}
	}
}
