package com.lukecreator.BonziBot.Handling;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.Modifier;
import com.lukecreator.BonziBot.Logging.LogEntrySwearMessage;
import com.lukecreator.BonziBot.Managers.ScriptCache;
import com.lukecreator.BonziBot.NoUpload.Constants;
import com.lukecreator.BonziBot.Script.Model.InvocationMethod;
import com.lukecreator.BonziBot.Script.Model.InvocationMethod.Implementation;
import com.lukecreator.BonziBot.Script.Model.InvocationPhrase;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptPackage;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * The default message handler. Gets all events related to messages.
 * @see {@link BonziBot.DEFAULT_MESSAGE_HANDLER}
 * @author Lukec
 */
public class DefaultMessageHandler implements MessageHandler {
	
	// 100 custom level messages omg
	// {user} - their username
	public static final String[] LEVEL_UPS = {
		"and so it begins, {user}'s level 1!",
		"{user}'s level is now 2 times more than 1!",
		"{user} just hit level 3! wow!",
		"guess who's level 4? {user} is!!!",
		"level 5 club new member: {user}!",
		"{user} just hit level 6 let's go",
		"{user} is now level 7!",
		"level 8 now includes {user}!",
		"{user} is level 9 now! almost double digits...",
		"🎉 double digits level for {user}!! (level 10)",
		"{user} is now at level 11.",
		"{user} is slightly distraught at reaching level 12",
		"level 13 - {user}",
		"level 14 has its new member, {user}!",
		"{user} hit level 15 woo!",
		"16 levels and counting... {user}!",
		"{user} = level 17",
		"whoah {user} just hit level 18...",
		"{user} slapped level 19!",
		"{user}'s level is now 2 times more than 10",
		"{user} levelled up to 9 + 10 (use /calculator to learn more)",
		"level 22 club now includes: {user}!",
		"{user} left level 22 club for the 23 club!! (cya losers)",
		"{user}'s level is now 2x12 or something",
		"im no mathematician but {user} seems to be level 25",
		"level 26 hit {user}!",
		"{user} hit level 27!",
		"level 28 is now accepting {user}",
		"{user} is now level 29!",
		"{user}'s at the level 30 mark!",
		"level 31 incoming! oh it already hit {user}",
		"{user} jumped up to level 32",
		"{user} reached level 33! +20 max mana",
		"{user} is the master of level 34",
		"{user} must like sending messages cus they're now level 35",
		"level 36 just hit {user}!",
		"whoaah {user} is now level 37! cool",
		"they say {user} is now level 38 but i dont believe it",
		"i think {user} is level 39 now... wow!",
		"level 40 club warmly accepts {user}!",
		"{user} has climbed to level 41!",
		"{user} took it upon themselves to level to 42.",
		"{user} has reached level 43! +999999 nothing",
		"lol {user}'s only level 44? keep going",
		"{user} punched level 45 in the face.",
		"okay {user} we get it, you're level 46",
		"the newest level 47 is now {user}!",
		"level 48 club members: [wumpus, {user}]",
		"welcome to level 49, {user}!",
		"{user} is now halfway to level 100!",
		"{user} hit level 51, not as cool as 50!",
		"level 52 has located {user}",
		"i think {user} is now level 53, but idk",
		"study shows that {user} is level 54",
		"level 55 club new honorary member: {user}!!",
		"{user} is now a whopping level 56!",
		"{user} just hit level 7... no wait thats 57",
		"{user}'s now part of the level 58 committee",
		"level 59 is no problem for {user}!",
		"{user}'s now at the big level six-oh!",
		"{user} is now 61 more levels than they started.",
		"oh hey {user} just hit level 62",
		"63 level at now is {user}",
		"{user} happens to be level 64 now, huh!",
		"{user} just joined the level 65 club.",
		"{user} iz now levl 66",
		"okay okay {user} just scored level 67!",
		"{user} has now reached level 68!",
		"{user} is at level 69. no joke here.",
		"i wouldnt be lying if i said that {user} is at level 70",
		"71 levels and counting, it's {user}!",
		"{user} hops into level 72 head on!",
		"hello {user} i would like to inform you that you are now level 73!",
		"{user} is hitting level 74 head on!",
		"{user} now has 75 levels. 75% of the way to 100!",
		"{user} is now level 75 + 1",
		"lets go, {user} is level 77!",
		"level 78 gang now including {user}!",
		"{user} is jammin at level 79!",
		"the level 80 squad: {user} and {user}",
		"level 81 has been claimed by {user}",
		"82 levels that {user} has snagged!",
		"{user} is an entire 83 levels after 0!",
		"{user} is whabadabadooin whatever at level 84!",
		"{user} now level 85 skdjhgwfukywegfeliug",
		"hello {user} telegram: \"u lvel 86\"",
		"what is {user}'s level?? spoiler: 87",
		"{user} just hit level 88! thats 8 times more than 11... i think",
		"{user} just won a private island! no, but they are now level 89",
		"{user} has hit level 90! (+1 cat)", // 90
		"level 91 has now met {user}",
		"{user} just met level 92",
		"the level 93 squad took in {user} just now!",
		"i am serious {user} is now level 94...",
		"{user} is LEVEL 95???",
		"{user} just hit LEVEL 96 WHAaa",
		"LEVEL 97: now claimed by {user}!!!",
		"{user} is LEVEL 98!",
		"99 LEVELS BY {user}!",
		"👏👏 {user} HAS HIT LEVEL 100! 👏👏"
	};
	
	@Override
	public void handleGuildMessage(BonziBot bb, MessageReceivedEvent e, Modifier[] modifiers) {
		// Execute commands and chat-related things.
		if(e.getAuthor().isBot())
			return;
		if(bb.tags.receiveMessage(e, bb))
			return;
		Message msg = e.getMessage();
		if(bb.eventWaiter.onMessage(e.getMessage()))
			return;
		
		long guildId = e.getGuild().getIdLong();
		
		GuildSettings settings = bb.guildSettings.getSettings(guildId);
		
		// Run 'phrase' scripts.
		if(ScriptCache.shouldCheckPhraseScripts(guildId)) {
			List<ScriptPackage> packages = bb.scripts.getPackages(guildId);
			for(ScriptPackage pkg: packages) {
				if(!pkg.isEnabled())
					continue;
				for(Script script: pkg.getScripts()) {
					InvocationMethod _method = script.method;
					if(_method.getImplementation() != Implementation.PHRASE)
						continue;
					InvocationPhrase method = (InvocationPhrase)_method;
					String content = msg.getContentRaw();
					if(method.isInText(content)) {
						ScriptExecutor executor = script.code.createExecutor();
						MessageChannelUnion tc = e.getChannel();
						
						if(tc.getType() == ChannelType.TEXT) {
							int member = executor.memory.createObjectReference(e.getMember());
							int channel = executor.memory.createObjectReference((GuildChannel)tc);
							executor.memory.writeExistingObjRef("member", member);
							executor.memory.writeExistingObjRef("channel", channel);
							
							ScriptContextInfo context = new ScriptContextInfo(content, null, null, null, msg, tc.asTextChannel(),
								e.getJDA(), bb, e.getAuthor(), e.getMember(), e.getGuild(), settings);
							
							executor.run(context);
							return;
						}
					}
				}
			}
		}
		
		// Scan for tokens.
		if(settings.tokenScanning) {
			String content = msg.getContentRaw();
			Matcher matcher = Constants.TOKEN_REGEX_COMPILED.matcher(content);
			if(matcher.find()) {
				String token = matcher.group();
				try {
					bb.github.createGist().file("invalidate.txt", "Token was accidentally leaked: " + token).public_(true).create();
					EmbedBuilder eb = BonziUtils.quickEmbed("⚠️ BOT TOKEN DETECTED!",
						"I reset your bot token for you. Be more careful next time!", Color.red);
					e.getChannel().sendMessageEmbeds(eb.build()).queue();
				} catch (IOException e1) {
					e1.printStackTrace();
					msg.delete().queue();
					e.getChannel().sendMessage("Whoa, hold on! Your bot token was in that message.\nI couldn't invalidate it, but I deleted the message for you.\n\nDeveloper portal: https://discord.com/developers/applications").queue();
				}
				return;
			}
			matcher = Constants.PASTEBIN_REGEX_COMPILED.matcher(content);
			if(matcher.find()) {
				try {
					String full = matcher.group(0);
					String paste = matcher.group(2);
					boolean haste = full.contains("hastebin");
					paste = (haste ? "https://hastebin.com/raw/" : "https://pastebin.com/raw/") + paste;
					String download = BonziUtils.getStringFrom(paste);
					matcher = Constants.TOKEN_REGEX_COMPILED.matcher(download);
					if(matcher.find()) {
						String token = matcher.group();
						bb.github.createGist().file("invalidate.txt", "Token was accidentally leaked VIA " +
							(haste ? "hastebin: " : "pastebin: ") + token).public_(true).create();
						EmbedBuilder eb = BonziUtils.quickEmbed("⚠️ BOT TOKEN DETECTED!",
							"I reset your bot token for you. Be more careful next time!", Color.red);
						e.getChannel().sendMessageEmbeds(eb.build()).queue();
					}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
					msg.delete().queue();
					e.getChannel().sendMessage(e.getAuthor().getAsMention() + ", **Reset your token AS SOON AS POSSIBLE!**\nYour bot token was in that pastebin.\n"
						+ "I couldn't invalidate it, but I deleted the message for you.\n\nDeveloper portal: https://discord.com/developers/applications").queue();
				}
				return;
			}
		}
		
		// Scan for BAD WORd
		boolean filter = true;
		for(Modifier mod0: modifiers)
			filter &= (mod0 != Modifier.NO_FILTER);
		if(filter && !settings.testMessageInFilter(e.getMessage())) {
			bb.logging.addMessageToHistory(e.getMessage(), e.getGuild());
			msg.delete().queue(null, fail -> {});
			e.getChannel().sendMessageEmbeds(BonziUtils.failureEmbed(
				"You can't say that, " + e.getAuthor().getName() + "!")).queue(del -> {
					del.delete().queueAfter(5, TimeUnit.SECONDS);
				});
			
			LogEntrySwearMessage log = new LogEntrySwearMessage();
			log.loadData(msg, bb, entry -> {
				bb.logging.tryLog(e.getGuild(), bb, entry);
			}, null);
			
			return;
		}
		
		bb.logging.addMessageToHistory(e.getMessage(), e.getGuild());
		CommandExecutionInfo info = new CommandExecutionInfo(e);
		
		if(bb.commands.onTextInput(info.setBonziBot(bb)))
			return;
		else if(settings.levellingEnabled) {
			boolean xp = true;
			for(Modifier mod1: modifiers)
				xp &= (mod1 != Modifier.NO_XP);
			if(xp)
				bb.accounts.incrementXp(info);
		}
		
		// These are intentionally guild-only.
		BonziUtils.sendMentionMessage(e, bb);
		BonziUtils.disableAfk(e.getAuthor(), (TextChannel)e.getChannel(), bb);
		bb.reputation.checkMessage(e.getMessage(), bb);
		bb.quickDraw.messageReceived(e.getAuthor(), e.getMessage(), bb);
	}
	@Override
	public void handlePrivateMessage(BonziBot bb, MessageReceivedEvent e) {
		if(e.getAuthor().isBot()) return;
		if(bb.tags.receiveMessage(e, bb)) return;
		if(bb.eventWaiter.onMessage(e.getMessage())) return;
		
		// cache the channel ID for future use
		// (used in BonziUtils.messageUser(...))
		long pcid = e.getChannel().getIdLong();
		long uid = e.getAuthor().getIdLong();
		BonziUtils.userPrivateChannels.put(uid, pcid);
		
		// run a command, if any
		CommandExecutionInfo info = new CommandExecutionInfo(e);
		if(bb.commands.onTextInput(info.setBonziBot(bb)))
			return;
	}
	
	@Override
	public boolean appliesInChannel(MessageChannelUnion channel) {
		return true;
	}
	@Override
	public boolean appliesInModifiers(Modifier[] modifiers) {
		return true;
	}

}
