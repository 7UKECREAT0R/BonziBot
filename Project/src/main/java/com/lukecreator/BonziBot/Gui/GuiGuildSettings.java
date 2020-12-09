package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Invite.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class GuiGuildSettings extends Gui {
	
	long guildId;
	String guildName;
	
	public GuiGuildSettings(long guildId, String guildName) {
		this.guildId = guildId;
		this.guildName = guildName;
	}
	public GuiGuildSettings(Guild guild) {
		this.guildId = guild.getIdLong();
		this.guildName = guild.getName();
	}
	
	@Override
	public void initialize(JDA jda) {
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🤬"), 0));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🗒️"), 1));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("📜"), 2));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🕵️"), 3));
	}
	
	@Override
	public MessageEmbed draw(JDA jda) {
		EmbedBuilder menu = BonziUtils.quickEmbed
			(this.guildName, "Server Settings - Press "
			+ "a button to toggle/enter an option.", Color.magenta);
		
		GuildSettings settings = this.bonziReference
			.guildSettings.getSettings(guildId);
		GuildSettings.FilterLevel filter = settings.filter;
		
		String filterTitle = "🤬 Filter Level: " + filter.name();
		String filterDesc = filter.desc;
		menu.addField(filterTitle, filterDesc, false);
		
		List<String> cFilter = settings.customFilter;
		int cFS = cFilter.size();
		String cFDesc = "Filtering " + cFS + BonziUtils.plural(" word", cFS);
		menu.addField("🗒️ Custom Filter", cFDesc, false);
		
		menu.addBlankField(false);
		
		boolean tags = settings.enableTags;
		boolean ptags = settings.privateTags;
		menu.addField("📜 Tags: " + (tags?"ENABLED":"DISABLED"), "Tags use user generated content from around the world so they are off by default.", false);
		menu.addField("🕵️ Tag Privacy: " + (ptags?"PRIVATE":"PUBLIC"), "Enabling private tags will use your server's own tags rather than the public ones.", false);
		
		return menu.build();
	}
	
	@Override
	public void onAction(int buttonId, JDA jda) {
		GuildSettingsManager gsm = this
			.bonziReference.guildSettings;
		GuildSettings settings = gsm.getSettings(guildId);
		
		if(buttonId == 0) {
			// Filtering setting.
			settings.cycleFilter();
			gsm.setSettings(guildId, settings);
			this.parent.redrawMessage(jda);
		}
		if(buttonId == 1) {
			// TODO Custom filter.
			
		}
		if(buttonId == 2) {
			// Tags enabled.
			settings.enableTags = !settings.enableTags;
			gsm.setSettings(guildId, settings);
			this.parent.redrawMessage(jda);
		}
		if(buttonId == 3) {
			// Tag privacy.
			settings.privateTags = !settings.privateTags;
			gsm.setSettings(guildId, settings);
			this.parent.redrawMessage(jda);
		}
	}
}
