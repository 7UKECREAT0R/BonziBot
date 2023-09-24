package com.lukecreator.BonziBot.Gui;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class GuiGuildSettingsPage3 extends Gui {
	
	long guildId;
	String guildName;
	
	public GuiGuildSettingsPage3(long guildId, String guildName) {
		this.guildId = guildId;
		this.guildName = guildName;
	}
	public GuiGuildSettingsPage3(Guild guild) {
		this.guildId = guild.getIdLong();
		this.guildName = guild.getName();
	}
	
	@Override
	public void initialize(JDA jda) {
		GuildSettingsManager mgr = this.bonziReference.guildSettings;
		GuildSettings settings = mgr.getSettings(this.guildId);
		this.reinitialize(settings);
	}
	public void reinitialize(GuildSettings settings) {
		this.elements.clear();
		
		boolean lvling = settings.levellingEnabled;
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬅️"), "lastpage"));
		this.elements.add(new GuiButton(GenericEmoji.fromEmoji("🌟"), "Starboard", GuiButton.ButtonColor.GRAY, "starboard"));
		this.elements.add(new GuiButton(GenericEmoji.fromEmoji("📈"), lvling?"Disable Levelling":"Enable Levelling", lvling?GuiButton.ButtonColor.RED:GuiButton.ButtonColor.GREEN, "levelling"));
	}
	
	@Override
	public Object draw(JDA jda) {
		EmbedBuilder menu = BonziUtils.quickEmbed
			(this.guildName, "Server Settings - Press "
			+ "a button to toggle/enter an option.",
			BonziUtils.COLOR_BONZI_PURPLE);
		
		GuildSettings settings = this.bonziReference
			.guildSettings.getSettings(this.guildId);
		
		boolean starboardEnabled = settings.starboard != 0l;
		int starboardLimit = settings.starboardLimit;
		String starboard = starboardEnabled ? "`✅ ENABLED`" : "`🔳 DISABLED`";
		String starboardDesc = "If enough users star a message, it will be placed into the starboard channel!";
		if(starboardEnabled)
			starboardDesc = "<#" + settings.starboard + "> with `" + starboardLimit + "` reactions.\n" + starboardDesc;
		menu.addField("🌟 Starboard: " + starboard, starboardDesc, false);
		
		boolean lvling = settings.levellingEnabled;
		String levellingTitle = lvling ? "`✅ ENABLED`" : "`🔳 DISABLED`";
		String levellingDesc = "Choose if users should gain XP for talking in this server.";
		menu.addField("📈 Levelling: " + levellingTitle, levellingDesc, false);
		
		menu.setFooter("Page 3/3");
		return menu.build();
	}
	
	@Override
	public void onButtonClick(String actionId, long executorId, JDA jda) {
		if(actionId.equals("lastpage")) {
			Gui next = new GuiGuildSettingsPage2(this.guildId, this.guildName);
			this.parent.setActiveGui(next, jda);
			return;
		}
		
		if(actionId.equals("starboard")) {
			GuiStarboard gui = new GuiStarboard(this.guildId, this.guildName);
			this.parent.setActiveGui(gui, jda);
			return;
		}
		
		GuildSettingsManager gsm = this.bonziReference.guildSettings;
		GuildSettings settings = gsm.getSettings(this.guildId);
		
		if(actionId.equals("levelling")) {
			settings.levellingEnabled = !settings.levellingEnabled;
			this.reinitialize(settings);
			this.parent.redrawMessage(jda);
			return;
		}
	}
}
