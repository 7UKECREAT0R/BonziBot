package com.lukecreator.BonziBot.Gui;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.TextChannelArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiNewline;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

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
		this.keyword = this.leave ? "Leave" : "Join";
		this.keywordl = this.keyword.toLowerCase();
	}
	
	GuiButton bOn = new GuiButton(GenericEmoji.fromEmoji("🖱️"), "Enable", GuiButton.ButtonColor.GREEN, "toggle");
	GuiButton bOff = new GuiButton(GenericEmoji.fromEmoji("🖱️"), "Disable", GuiButton.ButtonColor.RED, "toggle");
	GuiButton eOn = new GuiButton(GenericEmoji.fromEmoji("📰"), "Enable Embed", GuiButton.ButtonColor.GREEN, "embed");
	GuiButton eOff = new GuiButton(GenericEmoji.fromEmoji("📰"), "Disable Embed", GuiButton.ButtonColor.RED, "embed");
	@Override
	public void initialize(JDA jda) {
		
		GuildSettingsManager mgr = this.bonziReference.guildSettings;
		GuildSettings settings = mgr.getSettings(this.guildId);
		this.reinitialize(settings);
	}
	public void reinitialize(GuildSettings settings) {
		boolean enabled = this.leave ? settings.leaveMessages : settings.joinMessages;
		boolean embeds = this.leave ? settings.leaveMessageIsEmbed : settings.joinMessageIsEmbed;
		this.elements.clear();
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬅️"), "return"));
		this.elements.add(enabled ? this.bOff : this.bOn);
		this.elements.add(new GuiButton("Test", GuiButton.ButtonColor.BLUE, "test").asEnabled(enabled));
		this.elements.add(new GuiNewline());
		this.elements.add(new GuiButton(GenericEmoji.fromEmoji("🗨️"), "Set Message...", GuiButton.ButtonColor.BLUE, "message").asEnabled(enabled));
		this.elements.add(new GuiButton(GenericEmoji.fromEmoji("#️⃣"), "Set Channel...", GuiButton.ButtonColor.BLUE,  "channel").asEnabled(enabled));
		this.elements.add((embeds ? this.eOff : this.eOn).asEnabled(enabled));
		this.elements.add(new GuiNewline());
	}
	
	@Override
	public Object draw(JDA jda) {
		
		EmbedBuilder menu = BonziUtils.quickEmbed
			(this.guildName, "Server Settings - " + this.keyword
			+ " Messages", BonziUtils.COLOR_BONZI_PURPLE);
		
		GuildSettingsManager mgr = this.bonziReference.guildSettings;
		GuildSettings settings = mgr.getSettings(this.guildId);
		boolean enabled = this.leave ? settings.leaveMessages : settings.joinMessages;
		boolean isEmbed = this.leave ? settings.leaveMessageIsEmbed : settings.joinMessageIsEmbed;
		String msg = enabled ? this.leave ? settings.leaveMessage : settings.joinMessage : null;
		long channelId = enabled ? this.leave ? settings.leaveMessageChannel : settings.joinMessageChannel : 0l;
		TextChannel tc = jda.getTextChannelById(channelId);
		String channelMnt = tc != null ? tc.getAsMention() : null;
		
		if(msg == null)
			msg = "Not set.";
		
		if(channelMnt == null)
			channelMnt = "Not set.";
		
		String statusDesc = enabled ? 
			  "Click to disable " + this.keywordl + " messages in this server."
			: "Click to enable " + this.keywordl + " messages in this server.";
		
		menu.addField("🖱️ Status: `" + (enabled?"✅ ENABLED`":"🔳 DISABLED`"), statusDesc, false);
		
		if(enabled) {
			menu.addField("🗨️ Message", msg, false);
			menu.addField("#️⃣ Channel", channelMnt, false);
			menu.addField("📰 Use Embed? `" + (isEmbed?"🟩 YES`":"🟥 NO`"),
				"Whether the " + this.keywordl + " messages should be placed in an embed or not.", false);
		}
		
		return menu.build();
	}
	
	@Override
	public void onButtonClick(String actionId, long executorId, JDA jda) {
		
		GuildSettingsManager mgr = this.bonziReference.guildSettings;
		GuildSettings settings = mgr.getSettings(this.guildId);
		
		if(actionId.equals("return")) {
			// Back button.
			
			if(this.didEnable && (!this.setChannel || !this.setMessage)) {
				MessageChannelUnion channel = this.parent.getChannel(jda);
				BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed(
					"Warning: You haven't set this up completely yet!",
					this.keyword + " messages won't work until you set both the message and the channel they go into!"), 5);
				return;
			}
			
			this.parent.setActiveGui(new GuiGuildSettingsPage1(this.guildId, this.guildName), jda);
			return;
		}
		
		if(actionId.equals("toggle")) {
			// Enable/Disable
			if(this.leave) {
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
			
			this.reinitialize(settings);
			mgr.setSettings(this.guildId, settings);
			this.parent.redrawMessage(jda);
			return;
		}
		
		if(actionId.equals("message")) {
			
			MessageChannelUnion channel = this.parent.getChannel(jda);
			
			if(!(this.leave ? settings.leaveMessages : settings.joinMessages)) {
				BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("Enable " + this.keywordl + " messages first!"), 3);
				return;
			}
			
			// Message
			EventWaiterManager waiter = 
				this.bonziReference.eventWaiter;
			long owner = this.parent.ownerId;
			EmbedBuilder eb = BonziUtils.quickEmbed(
					"What should the " + this.keywordl + " message be?",
					"Feel free to attach an image too!", Color.orange);
			eb.addField("Use these variables to spruce it up!",
				  "`(user)` - The user's name.\n"
				+ "`(server)` - The server's name.\n"
				+ "`(members)` - The member count.\n"
				+ "`(date)` - The current date.\n"
				+ "`(created)` - When this user created their account.", false);
			channel.sendMessageEmbeds(eb.build()).queue(sent -> {
				waiter.waitForResponse(owner, message -> {
					String text = message.getContentRaw();
					if(!message.getAttachments().isEmpty()) {
						Attachment a = message.getAttachments().get(0);
						String aUrl = a.getUrl();
						if(text.length() + aUrl.length() < Message.MAX_CONTENT_LENGTH)
							text = text + "\n" + aUrl;
					}
					if(this.leave)
						settings.leaveMessage = text;
					else settings.joinMessage = text;
					this.setMessage = true;
					
					sent.delete().queue();
					message.delete().queue();
					mgr.setSettings(this.guildId, settings);
					this.parent.redrawMessage(message.getJDA());
					return;
				});
			});
			
			return;
		}
		
		if(actionId.equals("channel")) {
			
			MessageChannelUnion channel = this.parent.getChannel(jda);
			
			if(!(this.leave ? settings.leaveMessages : settings.joinMessages)) {
				BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("Enable " + this.keywordl + " messages first!"), 3);
				return;
			}
			
			EventWaiterManager waiter = this.bonziReference.eventWaiter;
			long owner = this.parent.ownerId;
			EmbedBuilder eb = BonziUtils.quickEmbed("Mention a channel...",
					"This will be where " + this.keywordl + " messages will be sent to!", Color.orange);
			channel.sendMessageEmbeds(eb.build()).queue(sent -> {
				waiter.waitForArgument(owner, new TextChannelArg(""), _textChannel -> {
					TextChannel tc = (TextChannel)_textChannel;
					long tcId = tc.getIdLong();
					if(this.leave)
						settings.leaveMessageChannel = tcId;
					else settings.joinMessageChannel = tcId;
					this.setChannel = true;
					
					sent.delete().queue();
					mgr.setSettings(this.guildId, settings);
					this.parent.redrawMessage(tc.getJDA());
					return;
				});
			});
			return;
		}
		
		if(actionId.equals("embed")) {
			
			MessageChannelUnion channel = this.parent.getChannel(jda);
			
			if(!(this.leave ? settings.leaveMessages : settings.joinMessages)) {
				BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("Enable " + this.keywordl + " messages first!"), 3);
				return;
			}
			
			if(this.leave)
				settings.leaveMessageIsEmbed = !settings.leaveMessageIsEmbed;
			else
				settings.joinMessageIsEmbed = !settings.joinMessageIsEmbed;
			
			this.reinitialize(settings);
			mgr.setSettings(this.guildId, settings);
			this.parent.redrawMessage(jda);
			return;
		}
		
		if(actionId.equals("test")) {
			
			BonziBot bb = this.bonziReference;
			Guild guild = jda.getGuildById(this.guildId);
			
			if(guild == null)
				return;
			
			Member executor = guild.getMemberById(executorId);
			
			if(executor == null)
				return;
			
			if(this.leave)
				bb.guildSettings.simulateMemberLeave(bb, executor);
			else
				bb.guildSettings.simulateMemberJoin(bb, executor);
			
		}
	}
}
