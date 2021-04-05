package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.CommandArg;
import com.lukecreator.BonziBot.CommandAPI.RoleArg;
import com.lukecreator.BonziBot.CommandAPI.TextChannelArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.GuildSettings.FilterLevel;
import com.lukecreator.BonziBot.Data.Rules;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class GuiGuildSettingsPage1 extends Gui {
	
	long guildId;
	String guildName;
	
	public GuiGuildSettingsPage1(long guildId, String guildName) {
		this.guildId = guildId;
		this.guildName = guildName;
	}
	public GuiGuildSettingsPage1(Guild guild) {
		this.guildId = guild.getIdLong();
		this.guildName = guild.getName();
	}
	
	@Override
	public void initialize(JDA jda) {
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🤬"), 0));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🗒️"), 1));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("📜"), 2));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🕵️"), 3));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("📝"), 4));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🤖"), 5));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("👋"), 6));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🚪"), 7));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("💥"), 8));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("➡️"), 9));
	}
	
	@Override
	public Object draw(JDA jda) {
		EmbedBuilder menu = BonziUtils.quickEmbed
			(this.guildName, "Server Settings - Press "
			+ "a button to toggle/enter an option.",
			BonziUtils.COLOR_BONZI_PURPLE);
		
		Guild guild = jda.getGuildById(guildId);
		GuildSettings settings = this.bonziReference
			.guildSettings.getSettings(guildId);
		FilterLevel filter = settings.filter;
		
		String emoji = this.emojiForFilter(filter);
		String filterTitle = "🤬 Filter Level: `" + emoji + " " + filter.name() + "`";
		String filterDesc = filter.desc;
		menu.addField(filterTitle, filterDesc, false);
		
		List<String> cFilter = settings.customFilter;
		int cFS = cFilter.size();
		String cFDesc = "`Filtering " + cFS + BonziUtils.plural(" word", cFS) + "`";
		menu.addField("🗒️ Custom Filter", cFDesc, false);
		
		boolean tags = settings.enableTags;
		boolean ptags = settings.privateTags;
		menu.addField("📜 Tags: `" + (tags?"✅ ENABLED`":"🔳 DISABLED`"), "Tags use user generated content from around the world so they are off by default.", false);
		menu.addField("🕵️ Tag Privacy: `" + (ptags?"PRIVATE`":"PUBLIC`"), "Enabling private tags will use your server's own tags rather than the public ones.", false);
		
		boolean logs = settings.loggingEnabled;
		boolean cmdsEnabled = settings.botCommandsEnabled;
		long logChannelId = logs ? settings.loggingChannelCached : 0;
		TextChannel logChannel = logs ? guild.getTextChannelById(logChannelId) : null;
		String logName = (logChannel != null) ? "Channel: " + logChannel.getAsMention() : "No log channel set.";
		menu.addField("📝 Logging: `" + (logs?"✅ ENABLED`":"🔳 DISABLED`"), "" + logName + "\nPut detailed log UIs into a channel for easy moderation.", false);
		menu.addField("🤖 Bot Commands: `" + (cmdsEnabled?"✅ ENABLED`":"🔳 DISABLED`"), "If this is off, I will only "
			+ "work in channels with the `Bot Commands` modifier. (check `" + this.prefixOfLocation + "modifiers`)", false);
		
		boolean joinMsg = settings.joinMessages,
			leaveMsg = settings.leaveMessages;
		String joinMessage = joinMsg ? settings.joinMessage : null;
		String leaveMessage = leaveMsg ? settings.leaveMessage : null;
		long joinCh = joinMsg ? settings.joinMessageChannel : 0l;
		long leaveCh = leaveMsg ? settings.leaveMessageChannel : 0l;
		String joinDesc, leaveDesc;
		
		if(!joinMsg || joinMessage == null || joinCh == 0l)
			joinDesc = joinMsg?"❗ Enabled, but hasn't been completely set up yet. ❗":"Send a customized message in a channel whenever a member joins the server.";
		else
			joinDesc = "In the channel <#" + joinCh + ">. Click to change settings/disable.";
			
		if(!leaveMsg || leaveMessage == null || leaveCh == 0l)
			leaveDesc = leaveMsg?"❗ Enabled, but hasn't been completely set up yet. ❗":"Send a customized message in a channel whenever a member leaves.";
		else
			leaveDesc = "In the channel <#" + leaveCh + ">. Click to change settings/disable.";
		
		menu.addField("👋 Join Messages: `" + (joinMsg?"✅ ENABLED`":"🔳 DISABLED`"), joinDesc, false);
		menu.addField("🚪 Leave Messages: `" + (leaveMsg?"✅ ENABLED`":"🔳 DISABLED`"), leaveDesc, false);
		
		boolean joinRole = settings.joinRole;
		long joinRoleId = settings.joinRoleId;
		String roleDesc = joinRole ? 
			"Giving <@&" + joinRoleId + "> to new members." :
			"Give new members a role when they join!";
		menu.addField("💥 Join Role: `" + (joinRole?"✅ ENABLED`":"🔳 DISABLED`"), roleDesc, false);
		
		menu.setFooter("Page 1/2 - Press ➡️");
		return menu.build();
	}
	
	@Override
	public void onAction(int buttonId, JDA jda) {
		GuildSettingsManager gsm = this
			.bonziReference.guildSettings;
		GuildSettings settings = gsm.getSettings(guildId);
		
		if(buttonId == 0) {
			// Filtering setting
			settings.cycleFilter();
			gsm.setSettings(guildId, settings);
			Rules rules = settings.getRules();
			Guild guild = jda.getGuildById(guildId);
			rules.retrieveRulesMessage(jda, guildId, edit -> {
				MessageEmbed newRules = BonziUtils.generateRules
					(guild, this.bonziReference).build();
				edit.editMessage(newRules).queue();
			}, fail -> {
				settings.setRules(rules);
				gsm.setSettings(guildId, settings);
			});
			this.parent.redrawMessage(jda);
			return;
		}
		if(buttonId == 1) {
			// Custom filter
			Gui next = new GuiCustomFilter(guildId, guildName);
			this.parent.setActiveGui(next, jda);
		}
		if(buttonId == 2) {
			// Tags enabled
			settings.enableTags = !settings.enableTags;
			gsm.setSettings(guildId, settings);
			this.parent.redrawMessage(jda);
			return;
		}
		if(buttonId == 3) {
			// Tag privacy
			settings.privateTags = !settings.privateTags;
			gsm.setSettings(guildId, settings);
			this.parent.redrawMessage(jda);
			return;
		}
		if(buttonId == 4) {
			// Logging
			if(settings.loggingEnabled) {
				settings.loggingEnabled = false;
				gsm.setSettings(guildId, settings);
				this.parent.redrawMessage(jda);
			} else {
				CommandArg tca = new TextChannelArg("");
				EventWaiterManager ewm = this.bonziReference.eventWaiter;
				MessageChannel mc = this.parent.getChannel(jda);
				mc.sendMessage(BonziUtils.quickEmbed("Turning on Logging...",
					"Mention the channel you want logs to go into!", Color.gray).build()).queue(sent -> {
						long sentId = sent.getIdLong();
						ewm.waitForArgument(this.parent.ownerId, tca, object -> {
							mc.deleteMessageById(sentId).queue();
							TextChannel tc = (TextChannel)object;
							settings.loggingEnabled = true;
							settings.loggingChannelCached = tc.getIdLong();
							gsm.setSettings(guildId, settings);
							this.parent.redrawMessage(jda);
							BonziUtils.sendTempMessage(mc, BonziUtils.successEmbed("Logging is now enabled in #" + tc.getName() + "!"), 3);
						});
					});
			}
			return;
		}
		if(buttonId == 5) {
			// Bot commands
			settings.botCommandsEnabled = !settings.botCommandsEnabled;
			gsm.setSettings(guildId, settings);
			this.parent.redrawMessage(jda);
			return;
		}
		if(buttonId == 6) {
			// Join messages
			GuiJoinLeaveMessages gui = new GuiJoinLeaveMessages(guildId, guildName, false);
			this.parent.setActiveGui(gui, jda);
			return;
		}
		if(buttonId == 7) {
			// Leave messages
			GuiJoinLeaveMessages gui = new GuiJoinLeaveMessages(guildId, guildName, true);
			this.parent.setActiveGui(gui, jda);
			return;
		}
		if(buttonId == 8) {
			// Join role
			if(settings.joinRole) {
				settings.joinRole = false;
				gsm.setSettings(guildId, settings);
				this.parent.redrawMessage(jda);
			} else {
				CommandArg tca = new RoleArg("");
				EventWaiterManager ewm = this.bonziReference.eventWaiter;
				MessageChannel mc = this.parent.getChannel(jda);
				mc.sendMessage(BonziUtils.quickEmbed("Turning on Join Role...",
					"Mention or send the ID of the role you want to be given to new members.", Color.gray).build()).queue(sent -> {
					long sentId = sent.getIdLong();
					ewm.waitForArgument(this.parent.ownerId, tca, object -> {
						mc.deleteMessageById(sentId).queue();
						Role role = (Role)object;
						settings.joinRole = true;
						settings.joinRoleId = role.getIdLong();
						gsm.setSettings(guildId, settings);
						
						// concern message
						boolean concernH = false; // hierarchy
						boolean concernP = false; // permission
						Member self = sent.getGuild().getSelfMember();
						if(self != null) {
							if(!self.hasPermission(Permission.MANAGE_ROLES))
								concernP = true;
							if(self.getRoles().get(0).getPosition() <= role.getPosition())
								concernH = true;
						}
						
						this.parent.redrawMessage(jda);
						if(concernH | concernP) {
							String concerns =
								(concernH ? "⚠️ I'm not high up enough on the hierarchy to give people this role!\n" : "") +
								(concernP ? "⚠️ I don't have the \"Manage Roles\" permission, so I can't assign any roles.\n" : "");
							concerns = concerns.substring(0, concerns.length() - 1);
							mc.sendMessage(BonziUtils.quickEmbed("Successfully set the join role, but...", concerns, Color.orange).build()).queue();
						} else
							BonziUtils.sendTempMessage(mc, BonziUtils.successEmbed("Users will now get \"" + role.getName() + "\" when they join!"), 4);
					});
				});
			}
			return;
		}
		if(buttonId == 9) {
			// Next Page
			Gui next = new GuiGuildSettingsPage2(this.guildId, this.guildName);
			this.parent.setActiveGui(next, jda);
			return;
		}
	}
	
	public String emojiForFilter(FilterLevel level) {
		switch(level) {
		case NONE:
			return "❌";
		case SENSITIVE:
			return "👀";
		case SLURS:
			return "😤";
		case SWEARS:
			return "🤬";
		default:
			return "❔";
		}
	}
}
