package com.lukecreator.BonziBot;

import java.awt.Color;

import com.lukecreator.BonziBot.Commands.ACommand;
import com.lukecreator.BonziBot.Commands.CommandExecutionInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

/*
 * The all heavenly class which does everything lmao
 */
public class BonziUtils {
	
	/*
	 * Checks if a string is complete whitespace.
	 */
	public static boolean isWhitespace(String s) {
		if(s.isEmpty()) return true;
		return s.chars().allMatch(Character::isWhitespace);
	}
	/*
	 * CONVERTS_CODE_NAMING -> Converts Code Naming
	 */
	public static String titleString(String input) {
		char[] original = input.toCharArray();
		char[] changed = new char[original.length];
		
		boolean capsNextChar = true;
		for(int i = 0; i < original.length; i++) {
			char c = original[i];
			if(c == '_' || Character.isWhitespace(c)) {
				changed[i] = ' ';
				capsNextChar = true;
				continue;
			}
			
			char nChar = capsNextChar?
				Character.toUpperCase(c):
				Character.toLowerCase(c);
			capsNextChar = false;
			changed[i] = nChar;
		}
		return new String(changed);
	}
	
	public static EmbedBuilder successEmbed(String message) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setTitle(message);
		return eb;
	}
	public static EmbedBuilder failureEmbed(String message) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setTitle(message);
		return eb;
	}
	public static EmbedBuilder successEmbed(String title, String description) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setTitle(title);
		eb.setDescription(description);
		return eb;
	}
	public static EmbedBuilder failureEmbed(String title, String description) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setTitle(title);
		eb.setDescription(description);
		return eb;
	}
	public static EmbedBuilder quickEmbed(String title, String description) {
		return new EmbedBuilder()
			.setTitle(title)
			.setDescription(description);
	}
	public static EmbedBuilder quickEmbed(String title, String description, Color color) {
		return new EmbedBuilder()
			.setTitle(title)
			.setDescription(description)
			.setColor(color);
	}
	public static EmbedBuilder quickEmbed(String title, String description, User author) {
		return new EmbedBuilder()
			.setTitle(title)
			.setDescription(description)
			.setAuthor(author.getName(), null,
				author.getEffectiveAvatarUrl());
	}
	public static EmbedBuilder quickEmbed(String title, String description, User author, Color color) {
		return new EmbedBuilder()
			.setTitle(title)
			.setDescription(description)
			.setColor(color)
			.setAuthor(author.getName(), null, 
				author.getEffectiveAvatarUrl());
	}
	public static EmbedBuilder quickEmbed(String title, String description, Member authorWithColor) {
		return new EmbedBuilder()
			.setTitle(title)
			.setDescription(description)
			.setAuthor(authorWithColor.getEffectiveName(), null,
				authorWithColor.getUser().getEffectiveAvatarUrl())
			.setColor(authorWithColor.getColor());
	}
	
	public static void sendUsage(ACommand cmd, CommandExecutionInfo info) {
		String msg = "Wrong Command Usage!";
		MessageChannel channel = info.channel;
		
		String prefix;
		if(info.isGuildMessage)
			prefix = info.bonzi.prefixes.getPrefix(info.guild);
		else prefix = Constants.DEFAULT_PREFIX;
		String desc = prefix + cmd.usage;
		
		EmbedBuilder usage = failureEmbed(msg, desc);
		channel.sendMessage(usage.build()).queue();
	}
	public static void sendNeededPerms(ACommand cmd, CommandExecutionInfo info) {
		Permission[] perms = cmd.neededPermissions;
		String msg = perms.length > 1?
			"I need the following permissions to execute this command:":
			"I need the following permission to execute this command:";
		StringBuilder sb = new StringBuilder();
		for(Permission perm: cmd.neededPermissions) {
			String ps = perm.getName();
			sb.append(ps + "\n");
		}
		sb = sb.deleteCharAt(sb.length() - 1);
		String desc = sb.toString();
		
		EmbedBuilder send = quickEmbed(msg, desc, Color.orange);
		info.channel.sendMessage(send.build()).queue();
	}
}
