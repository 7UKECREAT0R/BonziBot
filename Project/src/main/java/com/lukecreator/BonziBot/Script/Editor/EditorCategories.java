package com.lukecreator.BonziBot.Script.Editor;

import com.lukecreator.BonziBot.Script.Model.InvocationButton;
import com.lukecreator.BonziBot.Script.Model.InvocationCommand;
import com.lukecreator.BonziBot.Script.Model.InvocationJoin;
import com.lukecreator.BonziBot.Script.Model.InvocationLeave;
import com.lukecreator.BonziBot.Script.Model.InvocationPhrase;
import com.lukecreator.BonziBot.Script.Model.InvocationTimed;
import com.lukecreator.BonziBot.Script.Model.Data.StatementChannelGet;
import com.lukecreator.BonziBot.Script.Model.Data.StatementChannelGetFromID;
import com.lukecreator.BonziBot.Script.Model.Data.StatementChannelGetFromName;
import com.lukecreator.BonziBot.Script.Model.Data.StatementMemberGet;
import com.lukecreator.BonziBot.Script.Model.Data.StatementMemberGetFromID;
import com.lukecreator.BonziBot.Script.Model.Data.StatementMemberGetFromName;
import com.lukecreator.BonziBot.Script.Model.Data.StatementRoleGet;
import com.lukecreator.BonziBot.Script.Model.Data.StatementRoleGetFromID;
import com.lukecreator.BonziBot.Script.Model.Data.StatementRoleGetFromName;
import com.lukecreator.BonziBot.Script.Model.System.StatementAddVariable;
import com.lukecreator.BonziBot.Script.Model.System.StatementDivVariable;
import com.lukecreator.BonziBot.Script.Model.System.StatementModVariable;
import com.lukecreator.BonziBot.Script.Model.System.StatementMulVariable;
import com.lukecreator.BonziBot.Script.Model.System.StatementSetVariable;
import com.lukecreator.BonziBot.Script.Model.System.StatementStop;
import com.lukecreator.BonziBot.Script.Model.System.StatementSubVariable;
import com.lukecreator.BonziBot.Script.Model.System.StatementToBoolean;
import com.lukecreator.BonziBot.Script.Model.System.StatementToDecimal;
import com.lukecreator.BonziBot.Script.Model.System.StatementToInteger;
import com.lukecreator.BonziBot.Script.Model.System.StatementToString;

/**
 * Statically assigned lists for categories and their associated sub-categories.
 * @author Lukec
 */
public class EditorCategories {
	
	// Invocation
	static final InvocationDescriptor[] INVOCATIONS = {
		new InvocationDescriptor(InvocationCommand.class, "Command", "Lets the user trigger this script through a command."),
		new InvocationDescriptor(InvocationButton.class, "Button", "Lets you create a message with a button that will trigger this script."),
		new InvocationDescriptor(InvocationPhrase.class, "Phrase", "Runs this script when a word or phrase is said in chat."),
		new InvocationDescriptor(InvocationTimed.class, "Timed", "Automatically runs script on a timed interval."),
		new InvocationDescriptor(InvocationJoin.class, "Member Join", "Runs when a member joins, in the join/leave channel if present."),
		new InvocationDescriptor(InvocationLeave.class, "Member Leave", "Runs when a member leaves, in the join/leave channel if present."),
	};
	
	// Statements
	
	static final StatementDescriptor[] SYSTEM = {
		new StatementDescriptor(StatementStop.class, "Stop", "Stops running of the script."),
		new StatementDescriptor(StatementSetVariable.class, "Set", "Set a variable."),
		
		new StatementDescriptor(StatementToString.class, "To Text", "Converts a variable to text."),
		new StatementDescriptor(StatementToInteger.class, "To Integer", "Converts a variable to an integer."),
		new StatementDescriptor(StatementToDecimal.class, "To Decimal", "Converts a variable to a decimal."),
		new StatementDescriptor(StatementToBoolean.class, "To True/False", "Converts a variable to a true/false."),
		
		new StatementDescriptor(StatementAddVariable.class, "Add", "Add a value to a variable."),
		new StatementDescriptor(StatementSubVariable.class, "Subtract", "Subtract a value from a variable."),
		new StatementDescriptor(StatementMulVariable.class, "Multiply", "Muliply a variable with a value."),
		new StatementDescriptor(StatementDivVariable.class, "Divide", "Divide a variable by a value."),
		new StatementDescriptor(StatementModVariable.class, "Modulo", "Divide a variable by a value and get its remainder.")
	};
	static final StatementDescriptor[] DATA = {
		new StatementDescriptor(StatementMemberGet.class, "Get Field from Member", "Get a field from a member."),
		new StatementDescriptor(StatementChannelGet.class, "Get Field from Channel", "Get a field from a channel."),
		new StatementDescriptor(StatementRoleGet.class, "Get Field from Role", "Get a field from a role."),
		new StatementDescriptor(StatementMemberGetFromName.class, "Get Member", "Gets a member by name and puts it in a variable."),
		new StatementDescriptor(StatementChannelGetFromName.class, "Get Channel", "Gets a channel by name and puts it in a variable."),
		new StatementDescriptor(StatementRoleGetFromName.class, "Get Role", "Gets a role by name and puts it in a variable."),
		new StatementDescriptor(StatementMemberGetFromID.class, "Get Member From ID", "Gets a member by ID and puts it in a variable."),
		new StatementDescriptor(StatementChannelGetFromID.class, "Get Channel From ID", "Gets a channel by ID and puts it in a variable."),
		new StatementDescriptor(StatementRoleGetFromID.class, "Get Role From ID", "Gets a role by ID and puts it in a variable."),
	};
	static final StatementDescriptor[] STORAGE = {
			
	};
	static final StatementDescriptor[] LIMITING = {
			
	};
	static final StatementDescriptor[] MESSAGING = {
			
	};
	static final StatementDescriptor[] ROLES = {
			
	};
	static final StatementDescriptor[] ACTIONS = {
			
	};
	
	public static StatementDescriptor[] getStatementsForCategory(StatementCategory category) {
		switch(category) {
		case ACTIONS: 	return ACTIONS;
		case DATA: 		return DATA;
		case STORAGE: 	return STORAGE;
		case LIMITING: 	return LIMITING;
		case MESSAGING:	return MESSAGING;
		case ROLES: 	return ROLES;
		case SYSTEM: 	return SYSTEM;
		default: 		return null;
		}
	}
	public static InvocationDescriptor[] getInvocationDescriptors() {
		return INVOCATIONS;
	}
}
