package com.lukecreator.BonziBot.CommandAPI;

import java.util.List;

import com.lukecreator.BonziBot.CommandAPI.CommandArg.ArgType;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

/**
 * Wraps a CommandArg[]. Does all the parsing via #parse(String[], JDA)
 */
public class CommandArgCollection {
	
	public String[] usageOverride = null; // Add extra usage line(s) onto the usage of this command.
	CommandArg[] args;
	
	public CommandArgCollection(CommandArg...args) {
		this.args = args;
	}
	public CommandArgCollection withUsageOverride(String...lines) {
		this.usageOverride = lines;
		return this;
	}
	public CommandArg[] getArgs() {
		return this.args;
	}
	public static CommandArgCollection fromList(List<CommandArg> list) {
		CommandArgCollection cac = new CommandArgCollection();
		cac.args = new CommandArg[list.size()];
		for(int i = 0; i < list.size(); i++) {
			cac.args[i] = list.get(i);
		}
		return cac;
	}
	public static CommandArgCollection fromArray(CommandArg[] args) {
		CommandArgCollection cac = new CommandArgCollection(args);
		return cac;
	}
	/**
	 * Construct a CommandArgCollection which only takes a remainder.
	 */
	public static CommandArgCollection single(String argName) {
		return new CommandArgCollection(new StringRemainderArg(argName));
	}
	
	/**
	 * Tests if optional arguments are fine.
	 */
	public void testValidity() throws IllegalArgumentException {
		boolean foundOptional = false; 
		for(CommandArg arg: args) {
			if(arg.optional && !foundOptional) {
				foundOptional = true;
				continue;
			}
			if(!arg.optional && foundOptional)
				throw new IllegalArgumentException("All arguments after an optional need to be also optional.");
		}
	}
	public int size() {
		return args.length;
	}
	
	/**
	 * Builds a usage string[] based off of this CommandArgCollection.
	 *       The array is incase multiple combinations exist.
	 */
	public String[] buildUsage(String prefix, String commandName) {
		
		// Count optional possibilities.
		int optionals = 0;
		for(CommandArg arg: this.args)
			if(arg.optional) optionals++;
		int combinations = optionals + 1;
		
		// If command has a usage override.
		boolean hasUO = this.usageOverride != null;
		int extraComb = hasUO ? this.usageOverride.length : 0;
		
		// Holds result.
		String[] allUsages = new String[combinations + extraComb];
		
		// Build possible results and store in allUsages.
		String start = '`' + prefix + commandName;
		for(int x = 0; x < combinations; x++) {
			int argLength = args.length - x;
			String localStart = start;
			if(argLength > 0)
				localStart += ' ';
			String[] buffer = new String[argLength];
			for(int y = 0; y < argLength; y++)
				buffer[y] = args[y].getUsageTerm().replace('_', ' ');
			allUsages[x] = localStart + String.join(" ", buffer) + '`';
		}
		
		int baseIndex = combinations;
		if(hasUO) {
			for(int ex = 0; ex < extraComb; ex++) {
				int fIndex = baseIndex + ex;
				String override = usageOverride[ex];
				allUsages[fIndex] = start + ' ' + override + '`';
			}
		}
		
		return allUsages;
	}
	/**
	 * Parses slash-command options input into a CommandArg[]
	 */
	public CommandParsedArgs parse(OptionMapping[] options, JDA jda, User exec, Guild g) {
		
		CommandArg[] clone = args.clone();
		for(int i = 0; i < clone.length; i++) {
			CommandArg cmd = clone[i];
			if(i >= options.length) {
				cmd.object = null;
				clone[i] = cmd;
				continue;
			}
			OptionType goalType = cmd.type.nativeOption;
			switch(goalType) {
			case BOOLEAN:
				cmd.object = options[i].getAsBoolean();
				break;
			case CHANNEL:
				cmd.object = options[i].getAsGuildChannel();
				break;
			case INTEGER:
				cmd.object = options[i].getAsLong();
				break;
			case ROLE:
				cmd.object = options[i].getAsRole();
				break;
			case USER:
				cmd.object = options[i].getAsUser();
				break;
			default:
				cmd.object = options[i].getAsString();
			}
			clone[i] = cmd;
		}
		
		return new CommandParsedArgs(clone, false);
	}
	/**
	 * Parses an array of words. Ensure the words array does
	 *  not contain the prefix part of the command message.
	 *  
	 *  g can be null sometimes if it's in a DM.
	 */
	public CommandParsedArgs parse(String[] words, JDA jda, User exec, Guild g) {
		
		int argCount = args.length;
		for(CommandArg a: args)
			if(a.optional) argCount--;
		if(words.length < argCount) {
			return new CommandParsedArgs(null, true);
		}
		
		CommandArg[] clone = args.clone();
		for(int i = 0; i < clone.length; i++) {
			CommandArg cmd = clone[i];
			if(cmd.optional) {
				// Be careful, word array
				// might not be big enough
				if(i >= words.length) {
					cmd.object = null;
					clone[i] = cmd;
					continue;
				}
			}
			String word = words[i];
			
			if(cmd.type == ArgType.StringRem) {
				String s = buildRemainder(words, i);
				cmd.object = s;
				clone[i] = cmd;
				break;
			} else if(cmd.isWordParsable(word, g)) {
				cmd.parseWord(word, jda, exec, g);
				clone[i] = cmd;
			} else clone[i] = null;
		}
		
		return new CommandParsedArgs(clone, false);
	}
	private String buildRemainder(String[] words, int index) {
		if(words.length <= index) return "";
		String[] rem = new String[words.length - index];
		for(int i = index; i < words.length; i++) {
			rem[i - index] = words[i];
		}
		return String.join(" ", rem);
	}
}
