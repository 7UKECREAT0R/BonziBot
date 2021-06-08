package com.lukecreator.BonziBot.Gui;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.Rules;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;

public class GuiGuildSettingsPage2 extends Gui {
	
	long guildId;
	String guildName;
	
	public GuiGuildSettingsPage2(long guildId, String guildName) {
		this.guildId = guildId;
		this.guildName = guildName;
	}
	public GuiGuildSettingsPage2(Guild guild) {
		this.guildId = guild.getIdLong();
		this.guildName = guild.getName();
	}
	
	@Override
	public void initialize(JDA jda) {
		this.buttons.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬅️"), "lastpage"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("↪️"), "Prefix", GuiButton.Color.BLUE, "prefix"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("📖"), "Rules", GuiButton.Color.BLUE,  "rules"));
	}
	
	@Override
	public Object draw(JDA jda) {
		EmbedBuilder menu = BonziUtils.quickEmbed
			(this.guildName, "Server Settings - Press "
			+ "a button to toggle/enter an option.",
			BonziUtils.COLOR_BONZI_PURPLE);
		
		GuildSettings settings = this.bonziReference
			.guildSettings.getSettings(guildId);
		
		String prefix = settings.getPrefix();
		menu.addField("↪️ Prefix: `" + prefix + "`", "Set the prefix I use in this server.", false);
		
		Rules rules = settings.getRules();
		menu.addField("📖 Rules", rules.toString() + "\nEdit/Add rules to your server. Automatically updates your rules message!", false);
		
		menu.setFooter("Page 2/2");
		return menu.build();
	}
	
	@Override
	public void onAction(String actionId, JDA jda) {
		GuildSettingsManager gsm = this
			.bonziReference.guildSettings;
		GuildSettings settings = gsm.getSettings(guildId);
		
		if(actionId.equals("lastpage")) {
			// Next Page
			Gui next = new GuiGuildSettingsPage1(this.guildId, this.guildName);
			this.parent.setActiveGui(next, jda);
			return;
		}
		
		if(actionId.equals("prefix")) {
			// Prefix
			EventWaiterManager ewm = this.bonziReference.eventWaiter;
			MessageChannel mc = this.parent.getChannel(jda);
			mc.sendMessage(BonziUtils.quickEmbed("Setting Prefix...",
				"Send the new prefix here (max " + Constants.MAX_PREFIX_LENGTH + " characters):", Color.gray).build()).queue(sent -> {
					long sentId = sent.getIdLong();
					ewm.waitForResponse(this.parent.ownerId, msg -> {
						mc.deleteMessageById(sentId).queue();
						msg.delete().queue(null, f -> {});
						String nPrefix = msg.getContentRaw().replaceAll(Constants.WHITESPACE_REGEX, "");
						if(nPrefix.length() > Constants.MAX_PREFIX_LENGTH)
							nPrefix = nPrefix.substring(0, 32);
						settings.setPrefix(nPrefix.trim());
						gsm.setSettings(guildId, settings);
						this.parent.redrawMessage(jda);
						mc.sendMessage(BonziUtils.successEmbedIncomplete
							("Prefix set!", "The new prefix will now be:\n`" + nPrefix + "`")
							.setFooter("Forget the prefix? Type b:stuck!").build()).queue();
					});
				});
		} else if(actionId.equals("rules")) {
			// Rules
			GuiRules gui = new GuiRules(this.guildId, this.guildName);
			this.parent.setActiveGui(gui, jda);
		}
	}
}
