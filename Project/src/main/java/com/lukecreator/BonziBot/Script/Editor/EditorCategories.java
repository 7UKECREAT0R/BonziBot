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
import com.lukecreator.BonziBot.Script.Model.Data.StatementServerGet;
import com.lukecreator.BonziBot.Script.Model.Limiting.StatementLimitCompare;
import com.lukecreator.BonziBot.Script.Model.Limiting.StatementLimitHasData;
import com.lukecreator.BonziBot.Script.Model.Limiting.StatementLimitOwner;
import com.lukecreator.BonziBot.Script.Model.Limiting.StatementLimitPermission;
import com.lukecreator.BonziBot.Script.Model.Limiting.StatementLimitRole;
import com.lukecreator.BonziBot.Script.Model.Messages.StatementAddReaction;
import com.lukecreator.BonziBot.Script.Model.Messages.StatementSendMessageEmbed;
import com.lukecreator.BonziBot.Script.Model.Messages.StatementSendMessageText;
import com.lukecreator.BonziBot.Script.Model.Messages.StatementSendTempMessageEmbed;
import com.lukecreator.BonziBot.Script.Model.Messages.StatementSendTempMessageText;
import com.lukecreator.BonziBot.Script.Model.Storage.StatementClearStorage;
import com.lukecreator.BonziBot.Script.Model.Storage.StatementGetStorage;
import com.lukecreator.BonziBot.Script.Model.Storage.StatementInitializeStorage;
import com.lukecreator.BonziBot.Script.Model.Storage.StatementPutStorage;
import com.lukecreator.BonziBot.Script.Model.Storage.StatementRemoveStorage;
import com.lukecreator.BonziBot.Script.Model.System.StatementAddVariable;
import com.lukecreator.BonziBot.Script.Model.System.StatementDivVariable;
import com.lukecreator.BonziBot.Script.Model.System.StatementModVariable;
import com.lukecreator.BonziBot.Script.Model.System.StatementMulVariable;
import com.lukecreator.BonziBot.Script.Model.System.StatementRandom;
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
		new InvocationDescriptor(InvocationButton.class, "Button", "Lets you create a message with a button that will trigger this script. /scriptbutton"),
		new InvocationDescriptor(InvocationPhrase.class, "Phrase", "Runs this script when a word or phrase is said in chat."),
		new InvocationDescriptor(InvocationTimed.class, "Timed", "Automatically runs script on a timed interval."),
		new InvocationDescriptor(InvocationJoin.class, "Member Join", "Runs when a member joins, in the join/leave channel if present."),
		new InvocationDescriptor(InvocationLeave.class, "Member Leave", "Runs when a member leaves, in the join/leave channel if present."),
	};
	
	// Statements
	
	static final StatementDescriptor[] SYSTEM = {
		new StatementDescriptor(StatementStop.class, "Stop", "Stops running of the script."),
		new StatementDescriptor(StatementSetVariable.class, "Set", "Set a variable."),
		new StatementDescriptor(StatementRandom.class, "Random", "Get a random number."),
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
		new StatementDescriptor(StatementServerGet.class, "Get Field from Server", "Get a field from this server."),
		new StatementDescriptor(StatementMemberGetFromName.class, "Get Member", "Gets a member by name and puts it in a variable."),
		new StatementDescriptor(StatementChannelGetFromName.class, "Get Channel", "Gets a channel by name and puts it in a variable."),
		new StatementDescriptor(StatementRoleGetFromName.class, "Get Role", "Gets a role by name and puts it in a variable."),
		new StatementDescriptor(StatementMemberGetFromID.class, "Get Member From ID", "Gets a member by ID and puts it in a variable."),
		new StatementDescriptor(StatementChannelGetFromID.class, "Get Channel From ID", "Gets a channel by ID and puts it in a variable."),
		new StatementDescriptor(StatementRoleGetFromID.class, "Get Role From ID", "Gets a role by ID and puts it in a variable."),
	};
	static final StatementDescriptor[] STORAGE = {
		new StatementDescriptor(StatementPutStorage.class, "Put Value", "Put a value into your server's storage with a key."),
		new StatementDescriptor(StatementGetStorage.class, "Get Value", "Get a value out of your server's storage by a key."),
		new StatementDescriptor(StatementInitializeStorage.class, "Initialize Value", "If a key doesn't have a value yet, initialize it to a value."),
		new StatementDescriptor(StatementRemoveStorage.class, "Remove Value", "Remove a value from your server's storage by a key."),
		new StatementDescriptor(StatementClearStorage.class, "Clear", "Clear all server storage.")
	};
	static final StatementDescriptor[] LIMITING = {
		new StatementDescriptor(StatementLimitPermission.class, "Require Permission", "Require user to have permission to continue past this statement."),
		new StatementDescriptor(StatementLimitRole.class, "Require Role", "Require user to have role to continue past this statement."),
		new StatementDescriptor(StatementLimitOwner.class, "Require Owner", "Require user to be owner of server to continue past this statement."),
		new StatementDescriptor(StatementLimitCompare.class, "Require Comparison", "Only continue the code if a comparison passes."),
		new StatementDescriptor(StatementLimitHasData.class, "Require Data", "Require a key of data in storage to be present to continue."),
	};
	static final StatementDescriptor[] MESSAGING = {
		new StatementDescriptor(StatementSendMessageText.class, "Send Message (text)", "Send a plain-text message."),
		new StatementDescriptor(StatementSendMessageEmbed.class, "Send Message (embed)", "Send an embed message."),
		new StatementDescriptor(StatementSendTempMessageText.class, "Send Temporary Message (text)", "Send a plain-text message but delete after a couple seconds."),
		new StatementDescriptor(StatementSendTempMessageEmbed.class, "Send Temporary Message (embed)", "Send an embed message but delete after a couple seconds."),
		new StatementDescriptor(StatementAddReaction.class, "Add Reaction", "Add a reaction to the sent message, if any."),
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
		case MESSAGES:	return MESSAGING;
		case ROLES: 	return ROLES;
		case SYSTEM: 	return SYSTEM;
		default: 		return null;
		}
	}
	public static InvocationDescriptor[] getInvocationDescriptors() {
		return INVOCATIONS;
	}
}
