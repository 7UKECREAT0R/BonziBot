package com.lukecreator.BonziBot.CommandAPI;

import java.util.List;

import com.lukecreator.BonziBot.InternalLogger;
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
		for(CommandArg arg: this.args) {
			if(arg.optional && !foundOptional) {
				foundOptional = true;
				continue;
			}
			if(!arg.optional && foundOptional)
				throw new IllegalArgumentException("All arguments after an optional need to be also optional.");
		}
	}
	public int size() {
		return this.args.length;
	}
	
	/**
	 * Builds a usage string[] based off of this CommandArgCollection.
	 *       The array is in case multiple combinations exist.
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
			int argLength = this.args.length - x;
			String localStart = start;
			if(argLength > 0)
				localStart += ' ';
			String[] buffer = new String[argLength];
			for(int y = 0; y < argLength; y++)
				buffer[y] = this.args[y].getUsageTerm().replace('_', ' ');
			allUsages[x] = localStart + String.join(" ", buffer) + '`';
		}
		
		int baseIndex = combinations;
		if(hasUO) {
			for(int ex = 0; ex < extraComb; ex++) {
				int fIndex = baseIndex + ex;
				String override = this.usageOverride[ex];
				allUsages[fIndex] = start + ' ' + override + '`';
			}
		}
		
		return allUsages;
	}
	/**
	 * Parses slash-command options input into a CommandArg[]
	 */
	@SuppressWarnings("rawtypes")
	public CommandParsedArgs parse(OptionMapping[] options, JDA jda, User exec, Guild g) {
		
		CommandArg[] clone = new CommandArg[this.args.length];
		for(int i = 0; i < clone.length; i++)
			clone[i] = this.args[i].createNew();
		
		for(OptionMapping mapping: options) {
			String optionName = mapping.getName().replace('-', ' ');
			
			int search = -1;
			for(int i = 0; i < clone.length; i++) {
				CommandArg arg = clone[i];
				if(arg.argName.equalsIgnoreCase(optionName)) {
					search = i;
					break;
				}
			}
			
			if(search == -1) {
				InternalLogger.print("[ERROR] Got invalid argument: " + optionName);
				continue;
			}
			
			CommandArg cmd = clone[search];
			ArgType baseType = cmd.type;
			
			if(cmd.type == ArgType.Enum) {
				EnumArg eArg = (EnumArg)cmd;
				Enum[] tests = eArg.enumType;
				cmd.object = tests[(int)mapping.getAsLong()];
				clone[search] = cmd;
				continue;
			}if(cmd.type == ArgType.Choice) {
				ChoiceArg cArg = (ChoiceArg)cmd;
				cmd.object = cArg.getValues()[(int)mapping.getAsLong()];
				clone[search] = cmd;
				continue;
			} else if(cmd.type == ArgType.TimeSpan) {
				TimeSpanArg tArg = (TimeSpanArg)cmd;
				tArg.parseWord(mapping.getAsString(), jda, exec, g);
				clone[search] = tArg;
				continue;
			} else if(cmd.type == ArgType.Color) {
				ColorArg cArg = (ColorArg)cmd;
				cArg.parseWord(mapping.getAsString(), jda, exec, g);
				clone[search] = cArg;
				continue;
			}
			
			OptionType goalType = baseType.nativeOption;
			switch(goalType) {
			case BOOLEAN:
				cmd.object = mapping.getAsBoolean();
				break;
			case CHANNEL:
				cmd.object = mapping.getAsChannel();
				break;
			/*
			case INTEGER:
				cmd.object = mapping.getAsLong();
				break;
			*/ // now uses string input type for handling 'all' and cases of suffixes
			case ROLE:
				cmd.object = mapping.getAsRole();
				break;
			case USER:
				cmd.object = mapping.getAsUser();
				break;
			default:
				if(baseType == ArgType.Int) {
					IntArg intArg = (IntArg)cmd;
					intArg.parseWord(mapping.getAsString(), jda, exec, g);
					clone[search] = intArg;
					continue;
				} else {
					cmd.object = mapping.getAsString();
				}
			}
			clone[search] = cmd;
		}
		
		return new CommandParsedArgs(clone, false);
	}
	/**
	 * Parses the given array of words into a CommandParsedArgs object.
	 *
	 * @param words The array of words to parse.
	 * @param jda The JDA instance.
	 * @param exec The User who executed the command.
	 * @param g The Guild where the command was executed.
	 * @return The CommandParsedArgs object containing the parsed arguments.
	 */
	public CommandParsedArgs parse(String[] words, JDA jda, User exec, Guild g) {
		
		int argCount = this.args.length;
		for(CommandArg a: this.args)
			if(a.optional) argCount--;
		if(words.length < argCount) {
			return new CommandParsedArgs(null, true);
		}
		
		CommandArg[] clone = this.args.clone();
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
				String s = this.buildRemainder(words, i);
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
