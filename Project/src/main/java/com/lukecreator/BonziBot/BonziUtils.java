package com.lukecreator.BonziBot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.Managers.CooldownManager;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

/*
 * The all heavenly class which does everything lmao
 */
public class BonziUtils {
	
	// Tracks opened private channels and their ids.
	// Format: <User ID, Private Channel ID>
	public static HashMap<Long, Long> userPrivateChannels = new HashMap<Long, Long>();
	public static final char[] STANDARD_CHARS = "qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM".toCharArray();
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36 Edg/86.0.622.69";
	
	/*
	 * Checks if a string is complete whitespace.
	 */
	public static boolean isWhitespace(String s) {
		if(s.isEmpty()) return true;
		return s.chars().allMatch(Character::isWhitespace);
	}
	/*
	 * Strip text of all non-standard characters.
	 * "Hi, boys! Im stupid?" -> "Hi boys Im stupid"
	 */
	public static String stripText(String s) {
		StringBuilder sb = new StringBuilder();
		for(char c: s.toCharArray())
			for(char std: STANDARD_CHARS)
				if(c == std) {
					sb.append(c);
					break;
				}
		return sb.toString();
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
	public static String getPrefixOrDefault(CommandExecutionInfo info) {
		if(info.isGuildMessage) {
			return info.bonzi.prefixes.getPrefix(info.guild);
		} else {
			return Constants.DEFAULT_PREFIX;
		}
	}
	public static String getShortTimeStringMs(long ms) {
		if(ms < 1000) return "0s";
		
		int secs = (int)Math.round
			(((double)ms)/1000.0);
		
		// Less than one minute.
		if(ms < 60000) {
			return secs + "s";
		}
		
		int mins = (int)Math.floor
			(((double)secs)/60.0);
		
		// Less than one hour.
		if(ms < 3600000) {
			int rem_secs = secs % 60;
			return mins + "m, " + rem_secs + "s";
		}
		
		int hours = (int)Math.floor
			(((double)mins)/60.0);
		
		// Less than one day (24h).
		if(ms < 86400000) {
			int rem_secs = secs % 60;
			int rem_mins = mins % 60;
			return hours + "h, " + rem_mins + "m, " + rem_secs + "s";
		}
		
		// Over a day, simply round down.
		int days = (int)Math.floor
			(((double)hours)/24.0);
		int rem_secs = secs % 60;
		int rem_mins = mins % 60;
		int rem_hours = hours % 24;
		return days + "d, " + rem_hours + "h, " + rem_mins + "m, " + rem_secs + "s";
	}
	public static long getMsForSeconds(int secs) {
		return secs * 1000l;
	}
	public static long getMsForMinutes(int mins) {
		return mins * 60000l;
	}
	public static long getMsForHours(int hours) {
		return hours * 3600000l;
	}
	public static long getMsForDays(int days) {
		return days * 86400000l;
	}
	public static int calculateLevel(int xp) {
		return (int)Math.floor(Math.sqrt(((double)xp)*0.1));
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
	
	public static void sendGuiFromExecutionInfo(CommandExecutionInfo info, Gui gui) {
		if(info.isGuildMessage)
			info.bonzi.guis.sendAndCreateGui(info.tChannel, info.executor, gui, info.bonzi);
		else info.bonzi.guis.sendAndCreateGui(info.pChannel, gui, info.bonzi);
	}
	public static void sendTempMessage(MessageChannel c, CharSequence cs, int seconds) {
		c.sendMessage(cs).queue(msg -> {
			msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
		});
	}
	public static void sendTempMessage(MessageChannel c, MessageEmbed embed, int seconds) {
		c.sendMessage(embed).queue(msg -> {
			msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
		});
	}
	public static void sendTempMessage(MessageChannel c, Message m, int seconds) {
		c.sendMessage(m).queue(msg -> {
			msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
		});
	}
	public static void sendUsage(Command cmd, CommandExecutionInfo info) {
		String msg = "Wrong Command Usage!";
		MessageChannel channel = info.channel;
		
		String prefix = BonziUtils.getPrefixOrDefault(info);
		String desc = prefix + cmd.usage;
		
		EmbedBuilder usage = failureEmbed(msg, desc);
		channel.sendMessage(usage.build()).queue();
	}
	public static void sendNeededPerms(Command cmd, CommandExecutionInfo info) {
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
	public static void sendAdminOnly(Command cmd, CommandExecutionInfo info) {
		EmbedBuilder eb = quickEmbed
			("This command is reserved for admins.", "Admins are usually developers"
					+ "of BonziBot or very well known contributors.",
			Color.orange);
		info.channel.sendMessage(eb.build());
				
	}
	public static void sendOnCooldown(Command cmd, CommandExecutionInfo info, CooldownManager cdm) {
		long userId = info.executor.getIdLong();
		long timeLeftMs = cdm.getUserCooldown(cmd, userId);
		String msg;
		if(timeLeftMs < 100) { // or -1
			msg = "Command is no longer on cooldown.";
		} else {
			msg = "Command is on cooldown!";
		}
		String ts = getShortTimeStringMs(timeLeftMs);
		String desc = ts + " Remaining";
		EmbedBuilder embed = quickEmbed(msg, desc, Color.yellow);
		sendTempMessage(info.channel, embed.build(), 5);
		return;
	}
	
	public static Guild getBonziGuild(JDA jda) {
		return jda.getGuildById(Constants.BONZI_GUILD_ID);
	}
	public static void messageUser(User user, CharSequence text) {
		long id = user.getIdLong();
		if(user.hasPrivateChannel() && userPrivateChannels.containsKey(id)) {
			long cId = userPrivateChannels.get(id);
			PrivateChannel pc = user.getJDA().getPrivateChannelById(cId);
			pc.sendMessage(text).queue();
		} else {
			user.openPrivateChannel().queue(p -> {
				long privateChannelId = p.getIdLong();
				userPrivateChannels.put(id, privateChannelId);
				p.sendMessage(text).queue();
			});
		}
	}
	public static void messageUser(User user, MessageEmbed me) {
		long id = user.getIdLong();
		if(user.hasPrivateChannel() && userPrivateChannels.containsKey(id)) {
			long cId = userPrivateChannels.get(id);
			PrivateChannel pc = user.getJDA().getPrivateChannelById(cId);
			pc.sendMessage(me).queue();
		} else {
			user.openPrivateChannel().queue(p -> {
				long privateChannelId = p.getIdLong();
				userPrivateChannels.put(id, privateChannelId);
				p.sendMessage(me).queue();
			});
		}
	}
	public static void messageUser(User user, Message msg) {
		long id = user.getIdLong();
		if(user.hasPrivateChannel() && userPrivateChannels.containsKey(id)) {
			long cId = userPrivateChannels.get(id);
			PrivateChannel pc = user.getJDA().getPrivateChannelById(cId);
			pc.sendMessage(msg).queue();
		} else {
			user.openPrivateChannel().queue(p -> {
				long privateChannelId = p.getIdLong();
				userPrivateChannels.put(id, privateChannelId);
				p.sendMessage(msg).queue();
			});
		}
	}
	public static PrivateChannel getCachedPrivateChannel(User user) {
		long userId = user.getIdLong();
		if(user.hasPrivateChannel() && userPrivateChannels.containsKey(userId)) {
			return user.getJDA().getPrivateChannelById(userPrivateChannels.get(userId));
		}
		// Not cached.
		return null;
	}
	
	// Networking
	public static String getStringFrom(String url) {
		StringBuilder output = new StringBuilder();
		InputStream urlc = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			URL urlObject = new URL(url);
			URLConnection con = urlObject.openConnection();
			con.addRequestProperty("user-agent", USER_AGENT);
			urlc = con.getInputStream();
			isr = new InputStreamReader(urlc);
			br = new BufferedReader(isr);
			
			String disc;
			while((disc = br.readLine()) != null)
				output.append(disc + "\n");
		} catch(IOException exc) {
			exc.printStackTrace();
		} finally {
			try {
				if(br != null)
					br.close();
			} catch(IOException e) { 
				e.printStackTrace();
			}
		}
		// Remove trailing newline character.
		return output.deleteCharAt(output.length() - 1).toString();
	}
}