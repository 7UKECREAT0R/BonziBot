package com.lukecreator.BonziBot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArg;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.Achievement;
import com.lukecreator.BonziBot.Data.AfkData;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.Modifier;
import com.lukecreator.BonziBot.Data.Rules;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.Managers.CooldownManager;
import com.lukecreator.BonziBot.Managers.UserAccountManager;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 * The all heavenly class which does everything lmao
 */
public class BonziUtils {
	
	// Tracks opened private channels and their ids.
	// Format: <User ID, Private Channel ID>
	public static HashMap<Long, Long> userPrivateChannels = new HashMap<Long, Long>();
	public static final char[] FILTER_CHARS_STD = "qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM1234567890$@!".toCharArray(); // for filtering
	public static final char[] STANDARD_CHARS_ALL = "qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM1234567890!@#$%^&*()_+:\"',./<>?`~".toCharArray();
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36 Edg/86.0.622.69";
	public static DateTimeFormatter MMddyy = DateTimeFormatter.ofPattern("MM/dd/yy");
	public static DateTimeFormatter MMdd = DateTimeFormatter.ofPattern("MM/dd");
	private static Random randomInstance = new Random(System.currentTimeMillis());
	
	private static final String[] birthdayMessages = new String[] {
		"oh yeah! it's {user}'s birthday today!",
		"by the way, make sure to wish {user} a happy birthday!",
		"{user} is also eating cake! just kidding, but it is their birthday",
		"{user} is one year older today!!",
		"oh yeah i almost forgot, it's {user}'s birthday today!",
		"hey, it's {user}'s birthday today!"
	};
	public static String getBirthdayMessage() {
		return birthdayMessages[randomInstance.nextInt(birthdayMessages.length)];
	}
	
	private static final String[] welcomeBackMessages = new String[] {
		"welcome back, {user}!",
		"hey, welcome back {user}!",
		"{user} is back!",
		"{user} has returned!",
		"{user} is back and ready to game",
		"whoa look {user} came back!",
		"alright guys {user} is back!"
	};
	public static String getWelcomeBackMessage() {
		return welcomeBackMessages[randomInstance.nextInt(welcomeBackMessages.length)];
	}
	
	// Colors
	public static final Color COLOR_BONZI_PURPLE = new Color(161, 86, 184);
	
	/**
	 * Append an S to the end of a word if the
	 * count is higher than 1. English is weird.
	 */
	public static String plural(String s, int count) {
		if(count>1||count==0||count<-1)
			s += "s";
		return s;
	}
	/**
	 * Similar to String.valueOf(int) but
	 *    places commas where needed.
	 */
	public static String comma(int num) {
		return NumberFormat.getInstance().format(num);
	}
	/**
	 * Checks if a string is complete whitespace.
	 */
	public static boolean isWhitespace(String s) {
		if(s.isEmpty()) return true;
		return s.chars().allMatch(Character::isWhitespace);
	}
	/**
	 *  Strip text of all non-standard characters.
	 * "Hi, boys! Im stupid?" -> "Hi boys Im stupid"
	 */
	public static String stripText(String s) {
		StringBuilder sb = new StringBuilder();
		for(char c: s.toCharArray())
			for(char std: FILTER_CHARS_STD)
				if(c == std) {
					sb.append(c);
					break;
				}
		return sb.toString();
	}
	/**
	 * Return if a string contains un-type-able
	 * characters making the name unmentionable.
	 */
	public static boolean isUnpingable(String s) {
		for(char c: s.toCharArray()) {
			boolean any = false;
			for(char std: STANDARD_CHARS_ALL) {
				if(c == std) {
					any = true;
					break;
				}
			}
			if(any == false)
				return true;
		}
		return false;
	}
	/**
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
	/**
	 * Cuts off string if it gets too l...
	 */
	public static String cutOffString(String s, int maxLength) {
		if(s.length() <= maxLength) return s;
		String part = s.substring(0, maxLength - 3);
		return part + "...";
	}
	/**
	 * Joins strings together and appends "or" to the delimiter for the last element
	 * (a, b, c, or d)
	 */
	public static String stringJoinOr(CharSequence delimiter, Iterable<? extends CharSequence> collection) {
		StringJoiner joiner = new StringJoiner(delimiter);
		Iterator<? extends CharSequence> iter = collection.iterator();
		while(iter.hasNext()) {
			CharSequence chars = iter.next();
			boolean last = !iter.hasNext();
				joiner.add(last ? "or " + chars : chars);
		}
		return joiner.toString();
	}
	/**
	 * Joins strings together and appends "and" to the delimiter for the last element
	 * (a, b, c, and d)
	 */
	public static String stringJoinAnd(CharSequence delimiter, Iterable<? extends CharSequence> collection) {
		StringJoiner joiner = new StringJoiner(delimiter);
		Iterator<? extends CharSequence> iter = collection.iterator();
		while(iter.hasNext()) {
			CharSequence chars = iter.next();
			boolean last = !iter.hasNext();
				joiner.add((last?"and ":"")+chars);
		}
		return joiner.toString();
	}
	/**
	 * String-join a set of some arbitrary object that isn't a CharSequence.
	 * @param delimiter
	 * @param items
	 * @return
	 */
	public static String stringJoinArbitrary(CharSequence delimiter, Object[] items) {
		String[] convert = new String[items.length];
		for(int i = 0; i < items.length; i++)
			convert[i] = items[i].toString();
		return String.join(delimiter, convert);
	}
	/**
	 * Returns the user's name and discriminator formatted.
	 */
	public static String fullName(User u) {
		return u.getName() + "#" + u.getDiscriminator();
	}
	/**
	 * Returns the roman numeral representation of a number.
	 */
	public static String numeral(int number) {
		
		if(number == 0) return "0";
		if(number < 0) return "-" + numeral(-number);
		
		// Define the table of roman numerals to use.
		String[] M = new String[]{"", "M", "MM", "MMM"};
		String[] C = new String[]{"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
		String[] X = new String[]{"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
		String[] I = new String[]{"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
		
		// Use division and modulus to select from the table above.
		String onPlace = I[number % 10];
		String tePlace = X[(number % 100) / 10];
		String huPlace = C[(number % 1000) / 100];
		String thPlace = M[number / 1000];
		
		return thPlace + huPlace + tePlace + onPlace;
	}
	/**
	 * does this really need an explanation 
	 */
	public static String uwu(String in) {
		return in.replace('l', 'w').replace('r', 'w');
	}
	/**
	 * Set the variables for join/leave messages.
	 */
	public static String joinLeaveVariables(String in, User u, Guild g) {
		String user = u.getName();
		String tag = u.getAsTag();
		String server = g.getName();
		String members = BonziUtils.comma(g.getMemberCount());
		String date = LocalDateTime.now().format(MMddyy);
		String created = u.getTimeCreated().format(MMddyy);
		
		return in
			.replaceAll("(?i)\\(user\\)", user)
			.replaceAll("(?i)\\(tag\\)", tag)
			.replaceAll("(?i)\\(server\\)", server)
			.replaceAll("(?i)\\(members\\)", members)
			.replaceAll("(?i)\\(date\\)", date)
			.replaceAll("(?i)\\(created\\)", created);
			
	}
	/**
	 * Generate a pseudo-random long that represents a unique ID.
	 */
	public static long generateId() {
		return randomInstance.nextLong();
	}
	/**
	 * Get the generic emoji for a specific integer.
	 * Guaranteed that <code>isGeneric == true</code>
	 * @param number
	 * @return
	 */
	public static GenericEmoji getEmojiForNumberG(int number) {
		String emoji = getEmojiForNumber(number);
		return GenericEmoji.fromEmoji(emoji);
	}
	/**
	 * Get the generic emoji for a specific character.
	 * Guaranteed that <code>isGeneric == true</code>
	 * @param number
	 * @return
	 */
	public static GenericEmoji getEmojiForCharacterG(char c) {
		String emoji = getEmojiForCharacter(c);
		return GenericEmoji.fromEmoji(emoji);
	}
	/**
	 * Get the emoji for a specific integer.
	 * @param number
	 * @return
	 */
	public static String getEmojiForNumber(int number) {
		switch(number) {
		case 0:
			return "0️⃣";
		case 1:
			return "1️⃣";
		case 2:
			return "2️⃣";
		case 3:
			return "3️⃣";
		case 4:
			return "4️⃣";
		case 5:
			return "5️⃣";
		case 6:
			return "6️⃣";
		case 7:
			return "7️⃣";
		case 8:
			return "8️⃣";
		case 9:
			return "9️⃣";
		case 10:
			return "🔟";
		}
		return "❓";
	}
	/**
	 * Get the emoji for a specific character. Returns '❓' if invalid.
	 * @param c
	 * @return
	 */
	public static String getEmojiForCharacter(char c) {
		c = Character.toLowerCase(c);
		switch(c) {
		case 'a':
			return "🇦";
		case 'b':
			return "🇧";
		case 'c':
			return "🇨";
		case 'd':
			return "🇩";
		case 'e':
			return "🇪";
		case 'f':
			return "🇫";
		case 'g':
			return "🇬";
		case 'h':
			return "🇭";
		case 'i':
			return "🇮";
		case 'j':
			return "🇯";
		case 'k':
			return "🇰";
		case 'l':
			return "🇱";
		case 'm':
			return "🇲";
		case 'n':
			return "🇳";
		case 'o':
			return "🇴";
		case 'p':
			return "🇵";
		case 'q':
			return "🇶";
		case 'r':
			return "🇷";
		case 's':
			return "🇸";
		case 't':
			return "🇹";
		case 'u':
			return "🇺";
		case 'v':
			return "🇻";
		case 'w':
			return "🇼";
		case 'x':
			return "🇽";
		case 'y':
			return "🇾";
		case 'z':
			return "🇿";
		case '!':
			return "❗";
		case '?':
			return "❓";
		case '#':
			return "#️⃣";
		case '*':
			return "*️⃣";
		case ' ':
			return "🟦";
		}
		return "❓";
	}
	/**
	 * Count the amount of a certain character in a string.
	 * @param search
	 * @param c
	 * @return The amount of times <b>c</b> occurs in <b>search</b>.
	 */
	public static int countChars(String search, char c) {
		char[] chars = search.toCharArray();
		int i = 0;
		for(char _c: chars)
			if(_c == c) i++;
		return i;
	}
	public static String getPrefixOrDefault(CommandExecutionInfo info) {
		if(info.isGuildMessage)
			return info.bonzi.guildSettings.getSettings(info.guild).getPrefix();
		else return Constants.DEFAULT_PREFIX;
	}
	public static String getPrefixOrDefault(Guild guild, BonziBot bb) {
		return bb.guildSettings.getSettings(guild).getPrefix();
	}
	public static String getPrefixOrDefault(GuildMessageReceivedEvent info, BonziBot bonzi) {
		return bonzi.guildSettings.getSettings(info.getGuild()).getPrefix();
	}
	public static String getPrefixOrDefault(PrivateMessageReceivedEvent info, BonziBot bonzi) {
		return Constants.DEFAULT_PREFIX;
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
	public static String getLongTimeStringMs(long ms) {
		if(ms < 1000) return "0 seconds";
		
		int secs = (int)Math.round
			(((double)ms)/1000.0);
		
		// Less than one minute.
		if(ms < 60000) {
			return secs + plural(" second", secs);
		}
		
		int mins = (int)Math.floor
			(((double)secs)/60.0);
		
		// Less than one hour.
		if(ms < 3600000) {
			int rem_secs = secs % 60;
			return mins + plural(" minute", mins) +
				", " + rem_secs + plural(" second", secs);
		}
		
		int hours = (int)Math.floor
			(((double)mins)/60.0);
		
		// Less than one day (24h).
		if(ms < 86400000) {
			int rem_secs = secs % 60;
			int rem_mins = mins % 60;
			return hours + plural(" hour", hours) +
				", " + rem_mins + plural(" minute", rem_mins) +
				", " + rem_secs + plural(" second", secs);
		}
		
		// Over a day, simply round down.
		int days = (int)Math.floor
			(((double)hours)/24.0);
		int rem_secs = secs % 60;
		int rem_mins = mins % 60;
		int rem_hours = hours % 24;
		return days + plural(" day", days) +
			", " + rem_hours + plural(" hour", rem_hours) +
			", " + rem_mins + plural(" minute", rem_mins) +
			", " + rem_secs + plural(" second", secs);
	}
	public static String[] splitByLength(String s, int length) {
		if(s.length() <= length)
			return new String[] {s};
		
		int stringLength = s.length();
		int truncated = stringLength / length;
		int roundedUp = truncated + 1;
		
		String[] result = new String[roundedUp];
		for(int i = 0; i < roundedUp; i++) {
			int startIndex = i * length;
			int endIndex = startIndex + length - 1;
			result[i] = s.substring(startIndex, endIndex);
		}
		
		return result;
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
		// (floor) sqrt(xp * 0.1)
		return (int)Math.floor(Math.sqrt(((double)xp)*0.1));
	}
	public static int calculateXpForLevel(int level) {
		// finally, algebra has a use for once!!
		// (floor) xp^2 / (1/10) = xp^2 * 10
		return (int)Math.floor(10 * (double)(level*level));
	}
	public static int clamp(int i, int min, int max) {
		return Math.min(max, Math.max(i, min));
	}
	
	public static EmbedBuilder successEmbedIncomplete(String message) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setTitle(message);
		return eb;
	}
	public static EmbedBuilder failureEmbedIncomplete(String message) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setTitle(message);
		return eb;
	}
	public static EmbedBuilder successEmbedIncomplete(String title, String description) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setTitle(title);
		eb.setDescription(description);
		return eb;
	}
	public static EmbedBuilder failureEmbedIncomplete(String title, String description) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setTitle(title);
		eb.setDescription(description);
		return eb;
	}
	public static MessageEmbed successEmbed(String message) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setTitle(message);
		return eb.build();
	}
	public static MessageEmbed failureEmbed(String message) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setTitle(message);
		return eb.build();
	}
	public static MessageEmbed successEmbed(String title, String description) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setTitle(title);
		eb.setDescription(description);
		return eb.build();
	}
	public static MessageEmbed failureEmbed(String title, String description) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setTitle(title);
		eb.setDescription(description);
		return eb.build();
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
	public static EmbedBuilder generateRules(Guild g, BonziBot bb) {
		GuildSettings settings = bb.guildSettings.getSettings(g);
		Rules rules = settings.getRules();
		Rules.Formatting format = rules.getFormatting();
		String[] lines = rules.getRules();
		
		EmbedBuilder eb = new EmbedBuilder()
			.setTitle(g.getName() + " Rules")
			.setColor(Color.LIGHT_GRAY)
			.setFooter(settings.filter.footer);
		
		final int max = MessageEmbed.VALUE_MAX_LENGTH;
		StringBuilder buffer = new StringBuilder();
		int count = 0;
		for(String line: lines) {
			count++;
			int len = line.length();
			String prepend;
			switch(format) {
			case NUMBERED:
				prepend = count + ". ";
				break;
			case DOTTED:
				prepend = "• ";
				break;
			case PLAIN:
				prepend = "";
				break;
			default:
				prepend = "";
				break;
			}
			len += prepend.length() + 1;
			if(len <= max) {
				buffer.append(prepend);
				buffer.append(line);
				buffer.append('\n');
			} else {
				String str = buffer.toString();
				str = str.substring(0, str.length() - 1);
				eb.addField(EmbedBuilder.ZERO_WIDTH_SPACE, str, false);
				buffer = new StringBuilder();
				buffer.append(prepend);
				buffer.append(line);
				buffer.append('\n');
			}
		}
		String str = buffer.toString();
		str = str.substring(0, str.length() - 1);
		eb.addField(EmbedBuilder.ZERO_WIDTH_SPACE, str, false);
		return eb;
	}
	
	public static void sendGui(CommandExecutionInfo info, Gui gui) {
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
	public static void sendUsage(Command cmd, CommandExecutionInfo info, boolean tooFew, CommandArg arg) {
		String msg = "";
		if(arg != null) {
			msg = "Incorrect argument type for " + BonziUtils.titleString(arg.argName);
		} else if(tooFew) {
			msg = "Too few arguments!";
		}
		MessageChannel channel = info.channel;
		
		String prefix = BonziUtils.getPrefixOrDefault(info);
		String desc = (arg != null) ? arg.getErrorDescription() + "\n" : "";
		EmbedBuilder usage = failureEmbedIncomplete(msg, desc);
		String[] usages = cmd.args.buildUsage(prefix, cmd.getFilteredCommandName());
		String fieldTitle = BonziUtils.plural("Correct Usage", usages.length) + ": ";
		usage.addField(fieldTitle, String.join("\n", usages), false);
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
	public static void sendUserNeedsPerms(Command cmd, CommandExecutionInfo info) {
		Permission[] perms = cmd.userRequiredPermissions;
		String msg = perms.length > 1 ?
			"You need the following permissions to execute this command:":
			"You need the following permission to execute this command:";
		StringBuilder sb = new StringBuilder();
		for(Permission perm: cmd.userRequiredPermissions) {
			String ps = perm.getName();
			sb.append(ps + "\n");
		}
		sb = sb.deleteCharAt(sb.length() - 1);
		String desc = sb.toString();
		
		EmbedBuilder send = quickEmbed(msg, desc, Color.orange);
		info.channel.sendMessage(send.build()).queue();
	}
	public static void sendModOnly(Command cmd, CommandExecutionInfo info, String prefix) {
		EmbedBuilder eb = quickEmbed("This command is reserved for moderators/admins.",
				"Use `" + prefix + "modrole` to see the current moderator role.", Color.orange);
		info.channel.sendMessage(eb.build()).queue();
	}
	public static void sendNotPurchased(Command cmd, CommandExecutionInfo info, String prefix) {
		EmbedBuilder eb = quickEmbed("You don't own this command yet!",
				"Check out the `" + prefix + "shop` to buy it.", Color.red);
		info.channel.sendMessage(eb.build()).queue();
	}
	public static void sendAdminOnly(Command cmd, CommandExecutionInfo info) {
		EmbedBuilder eb = quickEmbed(
			"This command is reserved for admins.",
			"Admins are usually developers of BonziBot or very well known contributors.",
			Color.orange);
		info.channel.sendMessage(eb.build()).queue();
				
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
		info.channel.sendMessage(embed.build()).queue();
		return;
	}
	public static void sendDoesntWorkDms(Command cmd, CommandExecutionInfo info) {
		EmbedBuilder eb = quickEmbed("This command doesn't work in DMs!",
				"Try running the command in a server instead!", Color.orange);
		info.channel.sendMessage(eb.build()).queue();
	}
	public static void sendAwaitingConfirmation(CommandExecutionInfo info) {
		EmbedBuilder eb = quickEmbed(
			"A command is still waiting for your confirmation!",
			"React with any reaction to cancel it, or react with the appropriate reaction to continue the command.",
			Color.orange);
		info.channel.sendMessage(eb.build()).queue();
	}
	public static void sendMentionMessage(GuildMessageReceivedEvent e, BonziBot bb) {
		if(!e.getMessage().getContentRaw().contains("<@"))
			return;
		
		List<User> mentioned = e.getMessage().getMentionedUsers();
		long executor = e.getAuthor().getIdLong();
		UserAccountManager uam = bb.accounts;
		
		List<String> content = new ArrayList<String>();
		for(User mention: mentioned) {
			if(mention.getIdLong() == executor)
				continue;
			content.clear();
			String name = mention.getName();
			UserAccount account = uam.getUserAccount(mention);
			if(account.afkData != null) {
				AfkData afk = account.afkData;
				if(account.afkData.isAfk()) {
					String reason = afk.getReasonAfk();
					TimeSpan backAt = afk.timeUntilBack();
					content.add(name + " is AFK right now. `" + reason.replace("`", "") +
						"`\nThey will probably be back in " + backAt.toLongString());
				}
			} else {
				account.afkData = new AfkData();
			}
			
			if(account.isTodayBirthday()) {
				content.add(getBirthdayMessage().replace("{user}", name));
			}
			
			if(content.isEmpty())
				continue;
			String msg = String.join("\n\n", content);
			e.getChannel().sendMessage(msg).queue();
		}
	}
	public static void disableAfk(User user, TextChannel tc, BonziBot bb) {
		UserAccountManager uam = bb.accounts;
		UserAccount account = uam.getUserAccount(user);
		if(account.afkData == null)
			account.afkData = new AfkData();
		else if(account.afkData.isAfk()) {
			account.afkData.noLongerAfk();
			uam.setUserAccount(user, account);
			String name = user.getName();
			tc.sendMessage(BonziUtils.successEmbed(getWelcomeBackMessage().replace("{user}", name))).queue();
			return;
		}
	}
	
	public static void tryAwardAchievement(MessageChannel channel, BonziBot bb, User u, Achievement a) {
		UserAccountManager uam = bb.accounts;
		UserAccount account = uam.getUserAccount(u);
		boolean hasAlready = account.hasAchievement(a);
		
		if(!hasAlready) {
			// wtf
			String prefix = (channel.getType() == ChannelType.TEXT)?
				bb.guildSettings.getSettings(((TextChannel)channel).getGuild()).getPrefix() :
				Constants.DEFAULT_PREFIX;
				
			account.awardAchievement(a);
			uam.setUserAccount(u, account);
			EmbedBuilder builder = new EmbedBuilder();
			builder.setColor(Color.yellow);
			builder.setAuthor("Achievement Unlocked!", null, u.getEffectiveAvatarUrl());
			builder.setTitle(a.icon.toString() + " " + a.name);
			builder.setDescription(a.desc);
			builder.setFooter("You can see your achievements with `" + prefix + "achievements`");
			channel.sendMessage(builder.build()).queue();
		}
	}
	public static void tryAwardAchievement(MessageChannel channel, BonziBot bb, long uid, Achievement a) {
		JDA jda = channel.getJDA();
		User user = jda.getUserById(uid);
		tryAwardAchievement(channel, bb, user, a);
	}
	public static Modifier[] getModifiers(String _topic) {
		if(_topic == null) return new Modifier[0];
		if(_topic.length() < 2) return new Modifier[0];
		String topic = _topic
			.replaceAll(Constants.WHITESPACE_REGEX, "")
			.toUpperCase();
		List<Modifier> list = new ArrayList<Modifier>();
		for(Modifier mod: Modifier.values()) {
			if(topic.contains(mod.getCompareName())) {
				list.add(mod);
			}
		}
		return ((Modifier[])list.toArray
			(new Modifier[list.size()]));
	}
	public static Modifier[] getChannelModifiers(TextChannel tc) {
		return getModifiers(tc.getTopic());
	}
	public static Modifier[] getChannelModifiers(MessageChannel mc) {
		if(mc.getType() == ChannelType.TEXT)
			return getChannelModifiers((TextChannel)mc);
		else return new Modifier[0];
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
	public static String getStringFrom(String url) throws FileNotFoundException {
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
	public static long getFileSizeBytes(String url) throws MalformedURLException {
		return getFileSizeBytes(new URL(url));
	}
	public static long getFileSizeBytes(URL url) {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection)url.openConnection();
			connection.addRequestProperty("user-agent", BonziUtils.USER_AGENT);
			connection.setRequestMethod("HEAD");
			long bytes = connection.getContentLengthLong();
			return bytes;
		} catch(IOException exc) {
			exc.printStackTrace();
			return -1;
		} finally {
			if(connection != null)
				connection.disconnect();
		}
	}
	public static double getFileSizeMb(String url) throws MalformedURLException {
		return ((double)getFileSizeBytes(url)) / 1000000.0;
	}
	public static double getFileSizeMb(URL url) {
		return ((double)getFileSizeBytes(url)) / 1000000.0;
	}
}