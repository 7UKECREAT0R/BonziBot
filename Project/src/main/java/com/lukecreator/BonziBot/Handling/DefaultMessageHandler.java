package com.lukecreator.BonziBot.Handling;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.Modifier;
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
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 * The default message handler. Gets all events related to messages.
 * @author Lukec
 */
public class DefaultMessageHandler implements MessageHandler {
	
	@Override
	public void handleGuildMessage(BonziBot bb, GuildMessageReceivedEvent e, Modifier[] modifiers) {
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
						TextChannel tc = e.getChannel();
						int member = executor.memory.createObjectReference(e.getMember());
						int channel = executor.memory.createObjectReference((GuildChannel)tc);
						executor.memory.writeExistingObjRef("member", member);
						executor.memory.writeExistingObjRef("channel", channel);
						
						ScriptContextInfo context = new ScriptContextInfo(content, null, null, null, msg, tc,
							e.getJDA(), bb, e.getAuthor(), e.getMember(), e.getGuild(), settings);
						
						executor.run(context);
						return;
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
			e.getMessage().delete().queue(null, fail -> {});
			e.getChannel().sendMessageEmbeds(BonziUtils.failureEmbed(
				"You can't say that, " + e.getAuthor().getName() + "!")).queue();
			return;
		}
		
		bb.logging.addMessageToHistory(e.getMessage(), e.getGuild());
		CommandExecutionInfo info = new CommandExecutionInfo(e);
		
		if(bb.commands.onTextInput(info.setBonziBot(bb)))
			return;
		else {
			boolean xp = true;
			for(Modifier mod1: modifiers)
				xp &= (mod1 != Modifier.NO_XP);
			if(xp)
				bb.accounts.incrementXp(info);
		}
		
		// These are intentionally guild-only.
		BonziUtils.sendMentionMessage(e, bb);
		BonziUtils.disableAfk(e.getAuthor(), e.getChannel(), bb);
		bb.reputation.checkMessage(e.getMessage(), bb);
		bb.quickDraw.messageReceived(e.getAuthor(), e.getMessage(), bb);
	}
	@Override
	public void handlePrivateMessage(BonziBot bb, PrivateMessageReceivedEvent e) {
		if(e.getAuthor().isBot()) return;
		if(bb.tags.receiveMessage(e)) return;
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
	public boolean appliesInChannel(MessageChannel channel) {
		return true;
	}
	@Override
	public boolean appliesInModifiers(Modifier[] modifiers) {
		return true;
	}

}
