package com.lukecreator.BonziBot.Gui;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.TextChannelArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class GuiJoinLeaveMessages extends Gui {
	
	long guildId;
	String guildName;
	String keyword; // Join / Leave
	String keywordl; // join / leave
	boolean leave; // whether for join or leave messages.
	boolean didEnable = false, setMessage = false, setChannel = false;
	
	public GuiJoinLeaveMessages(long guildId, String guildName, boolean leaveMessages) {
		this.guildId = guildId;
		this.guildName = guildName;
		this.leave = leaveMessages;
		keyword = leave ? "Leave" : "Join";
		keywordl = keyword.toLowerCase();
	}
	
	@Override
	public void initialize(JDA jda) {
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("⬅️"), 0));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🖱️"), 1));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🗨️"), 2));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("#️⃣"), 3));
	}
	
	@Override
	public MessageEmbed draw(JDA jda) {
		
		EmbedBuilder menu = BonziUtils.quickEmbed
			(this.guildName, "Server Settings - " + keyword
			+ " Messages", BonziUtils.COLOR_BONZI_PURPLE);
		
		GuildSettingsManager mgr = this.bonziReference.guildSettings;
		GuildSettings settings = mgr.getSettings(guildId);
		boolean enabled = leave ? settings.leaveMessages : settings.joinMessages;
		String msg = enabled ? leave ? settings.leaveMessage : settings.joinMessage : null;
		long channelId = enabled ? leave ? settings.leaveMessageChannel : settings.joinMessageChannel : 0l;
		TextChannel tc = jda.getTextChannelById(channelId);
		String channelMnt = tc != null ? tc.getAsMention() : null;
		
		if(msg == null)
			msg = "Not set.";
		else {
			User owner = jda.getUserById(this.parent.ownerId);
			Guild guild = jda.getGuildById(guildId);
			msg = BonziUtils.joinLeaveVariables(msg, owner, guild);
		}
		if(channelMnt == null) channelMnt = "Not set.";
		
		String statusDesc = enabled ? 
			  "Click to disable " + keywordl + " messages in this server."
			: "Click to enable " + keywordl + " messages in this server.";
		
		menu.addField("🖱️ Status: `" + (enabled?"✅ ENABLED`":"🔳 DISABLED`"), statusDesc, false);
		
		if(enabled) {
			menu.addField("🗨️ Message", msg, false);
			menu.addField("#️⃣ Channel", channelMnt, false);
		}
		
		return menu.build();
	}
	
	@Override
	public void onAction(int buttonId, JDA jda) {
		
		GuildSettingsManager mgr = this.bonziReference.guildSettings;
		GuildSettings settings = mgr.getSettings(guildId);
		
		if(buttonId == 0) {
			// Back button.
			
			if(this.didEnable && (!this.setChannel || !this.setMessage)) {
				MessageChannel channel = this.parent.getChannel(jda);
				BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed(
					"Warning: You haven't set this up completely yet!",
					keyword + " messages won't work until you set both the message and the channel they go into!"), 5);
				return;
			}
			
			this.parent.setActiveGui(new GuiGuildSettings(guildId, guildName), jda);
			return;
		}
		
		if(buttonId == 1) {
			// Enable/Disable
			if(leave) {
				settings.leaveMessages = !settings.leaveMessages;
				if(settings.leaveMessages)
					this.didEnable = true;
				else {
					this.didEnable = false;
					settings.leaveMessageChannel = 0l;
					settings.leaveMessage = null;
				}
			} else {
				settings.joinMessages = !settings.joinMessages;
				if(settings.joinMessages)
					this.didEnable = true;
				else {
					this.didEnable = false;
					settings.joinMessageChannel = 0l;
					settings.joinMessage = null;
				}
			}
			
			mgr.setSettings(guildId, settings);
			this.parent.redrawMessage(jda);
			return;
		}
		
		if(buttonId == 2) {
			
			MessageChannel channel = this.parent.getChannel(jda);
			
			if(!(leave ? settings.leaveMessages : settings.joinMessages)) {
				BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("Enable " + keywordl + " messages first!"), 3);
				return;
			}
			
			// Message
			EventWaiterManager waiter = 
				this.bonziReference.eventWaiter;
			long owner = this.parent.ownerId;
			EmbedBuilder eb = BonziUtils.quickEmbed(
					"What should the " + keywordl + " message be?",
					"Feel free to attach an image too!", Color.orange);
			eb.addField("Use these variables to spruce it up!",
				  "`(user)` - *The user's name without numbers.\n"
				+ "`(tag)` - *The user's name and numbers.\n"
				+ "`(server)` - The server's name.\n"
				+ "`(members)` - The member count.\n"
				+ "`(date)` - The current date.\n"
				+ "`(created)` - When this user created their account.", false);
			channel.sendMessage(eb.build()).queue(sent -> {
				waiter.waitForResponse(owner, message -> {
					String text = message.getContentRaw();
					if(!message.getAttachments().isEmpty()) {
						Attachment a = message.getAttachments().get(0);
						String aUrl = a.getUrl();
						if(text.length() + aUrl.length() < Message.MAX_CONTENT_LENGTH)
							text = text + "\n" + aUrl;
					}
					if(leave)
						settings.leaveMessage = text;
					else settings.joinMessage = text;
					this.setMessage = true;
					
					sent.delete().queue();
					message.delete().queue();
					mgr.setSettings(guildId, settings);
					this.parent.redrawMessage(message.getJDA());
					return;
				});
			});
			
			return;
		}
		
		if(buttonId == 3) {
			
			MessageChannel channel = this.parent.getChannel(jda);
			
			if(!(leave ? settings.leaveMessages : settings.joinMessages)) {
				BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("Enable " + keywordl + " messages first!"), 3);
				return;
			}
			
			EventWaiterManager waiter = this.bonziReference.eventWaiter;
			long owner = this.parent.ownerId;
			EmbedBuilder eb = BonziUtils.quickEmbed("Mention a channel...",
					"This will be where " + keywordl + " messages will be sent to!", Color.orange);
			channel.sendMessage(eb.build()).queue(sent -> {
				waiter.waitForArgument(owner, new TextChannelArg(""), _textChannel -> {
					TextChannel tc = (TextChannel)_textChannel;
					long tcId = tc.getIdLong();
					if(leave)
						settings.leaveMessageChannel = tcId;
					else settings.joinMessageChannel = tcId;
					this.setChannel = true;
					
					sent.delete().queue();
					mgr.setSettings(guildId, settings);
					this.parent.redrawMessage(tc.getJDA());
					return;
				});
			});
			
			return;
		}
	}
}
