package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class GuiCustomFilter extends Gui {
	
	public static final int MAX_FILTER_LENGTH = 32;
	public static final int MAX_FILTER_COUNT = MessageEmbed.VALUE_MAX_LENGTH / MAX_FILTER_LENGTH;
	
	long guildId;
	String guildName;
	
	public GuiCustomFilter(long guildId, String guildName) {
		this.guildId = guildId;
		this.guildName = guildName;
	}
	
	@Override
	public void initialize(JDA jda) {
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("⬅️"), 0));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🆕"), 1));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("❌"), 2));
	}
	
	@Override
	public Object draw(JDA jda) {
		BonziBot bb = this.bonziReference;
		GuildSettings settings = bb
			.guildSettings.getSettings(guildId);
		List<String> customFilter = settings.customFilter;
		
		EmbedBuilder menu = BonziUtils.quickEmbed
			(this.guildName, "Server Settings - Custom Filter",
			BonziUtils.COLOR_BONZI_PURPLE);
		
		if(customFilter.isEmpty()) {
			menu.setFooter("No words have been added to the filter yet!");
			return menu.build();
		}
		
		int i = 0;
		List<String> numbered = new ArrayList<String>();
		for(String thing: customFilter)
			numbered.add((++i) + ". ||" + thing + "||");
		int count = customFilter.size();
		int max = MessageEmbed.VALUE_MAX_LENGTH;
		String title = count + " " + BonziUtils.plural("word", count) + ":";
		String desc = String.join("\n", numbered);
		desc = BonziUtils.cutOffString(desc, max);
		
		menu.addField(title, desc, false);
		return menu.build();
	}
	
	@Override
	public void onAction(int buttonId, JDA jda) {
		GuildSettingsManager gsm = this.bonziReference.guildSettings;
		EventWaiterManager waiter = this.bonziReference.eventWaiter;
		GuildSettings settings = gsm.getSettings(guildId);
		List<String> customFilter = settings.customFilter;
		
		if(buttonId == 0) {
			// Back
			Gui back = new GuiGuildSettingsPage1(guildId, guildName);
			this.parent.setActiveGui(back, jda);
			return;
		}
		if(buttonId == 1) {
			// Add
			MessageChannel ch = this.parent.getChannel(jda);
			if(customFilter.size() > MAX_FILTER_COUNT) {
				ch.sendMessage(BonziUtils.failureEmbed(
					"You can only have " + MAX_FILTER_COUNT + " items in your custom filter!",
					"Remove a word with the `❌` button to free up a slot.")).queue();
				return;
			}
			ch.sendMessage(BonziUtils.quickEmbed("Type the word you want to add.",
				"Max " + MAX_FILTER_LENGTH + " characters.", Color.orange).build()).queue(sent -> {
				waiter.waitForResponse(this.parent.ownerId, message -> {
					sent.delete().queue();
					message.delete().queue();
					String text = message.getContentRaw();
					if(text.length() < 2) {
						BonziUtils.sendTempMessage(ch, BonziUtils.failureEmbed("Cancelled."), 3);
						return;
					}
					if(text.length() > MAX_FILTER_LENGTH) {
						BonziUtils.sendTempMessage(ch, BonziUtils.failureEmbed("Text is too long!"), 3);
						return;
					}
					customFilter.add(text);
					settings.customFilter = customFilter;
					gsm.setSettings(guildId, settings);
					this.parent.redrawMessage(message.getJDA());
				});
			});
		}
		if(buttonId == 2) {
			// Remove
			MessageChannel ch = this.parent.getChannel(jda);
			ch.sendMessage(BonziUtils.quickEmbed("Type number of the word/phrase you want to remove.", null, Color.orange).build()).queue(sent -> {
				IntArg arg = new IntArg("");
				waiter.waitForArgument(this.parent.ownerId, arg, _index -> {
					sent.delete().queue();
					int index = ((int)_index) - 1;
					if(index < 0 || index >= customFilter.size()) {
						BonziUtils.sendTempMessage(ch, BonziUtils.failureEmbed("Cancelled."), 3);
						return;
					}
					customFilter.remove(index);
					settings.customFilter = customFilter;
					gsm.setSettings(guildId, settings);
					this.parent.redrawMessage(jda);
				});
			});
		}
	}
}