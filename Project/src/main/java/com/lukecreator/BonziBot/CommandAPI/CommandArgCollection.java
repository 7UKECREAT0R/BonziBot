package com.lukecreator.BonziBot.CommandAPI;

import java.util.List;

import com.lukecreator.BonziBot.CommandAPI.CommandArg.ArgType;

import net.dv8tion.jda.api.JDA;

/*
 * Wraps a CommandArg[]. Does all the parsing via #parse(String[], JDA)
 */
public class CommandArgCollection {
	
	CommandArg[] args;
	
	public CommandArgCollection(CommandArg...args) {
		this.args = args;
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
	/*
	 * Construct a CommandArgCollection which only takes a remainder.
	 */
	public static CommandArgCollection single(String argName) {
		return new CommandArgCollection(new StringRemainderArg(argName));
	}
	
	public int size() {
		return args.length;
	}
	
	/*
	 * Builds a usage string based off of this CommandArgCollection.
	 */
	public String buildUsage(String prefix, String commandName) {
		String[] strings = new String[args.length];
		for(int i = 0; i < args.length; i++)
			strings[i] = args[i].getUsageTerm().replace('_', ' ');
		String start = prefix + commandName + " ";
		return start + String.join(" ", strings);
	}
	/*
	 * Parses an array of words. Ensure the words array does
	 *  not contain the prefix part of the command message.
	 */
	public CommandParsedArgs parse(String[] words, JDA jda) {
		
		if(words.length < args.length) {
			return new CommandParsedArgs(null, true);
		}
		
		CommandArg[] clone = args.clone();
		for(int i = 0; i < clone.length; i++) {
			String word = words[i];
			CommandArg cmd = clone[i];
			
			if(cmd.type == ArgType.StringRem) {
				String s = buildRemainder(words, i);
				cmd.object = s;
				clone[i] = cmd;
				break;
			} else if(cmd.isWordParsable(word)) {
				cmd.parseWord(word, jda);
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
