package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
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
	
	boolean deleteMode = false;
	int deleteCursor = 0;
	
	long guildId;
	String guildName;
	
	public GuiCustomFilter(long guildId, String guildName) {
		this.guildId = guildId;
		this.guildName = guildName;
	}
	
	@Override
	public void initialize(JDA jda) {
		GuildSettings settings = this.bonziReference
				.guildSettings.getSettings(guildId);
		List<String> customFilter = settings.customFilter;
		this.reinitialize(customFilter);
	}
	public void reinitialize(List<String> customFilter) {
		this.elements.clear();
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬅️"), "return"));
		if(this.deleteMode) {
			this.elements.add(new GuiButton("Up", GuiButton.ButtonColor.BLUE, "up"));
			this.elements.add(new GuiButton("Down", GuiButton.ButtonColor.BLUE, "down"));
			this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("❌"), "delete").withColor(GuiButton.ButtonColor.RED));
		} else {
			this.elements.add(new GuiButton("New Word", GuiButton.ButtonColor.GREEN, "new"));
			this.elements.add(new GuiButton("Remove Word", GuiButton.ButtonColor.RED, "remove").asEnabled(customFilter.size() > 0));
		}
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
		for(String thing: customFilter) {
			String toAdd;
			if(this.deleteMode) {
				toAdd = (++i) + ". " + thing;
				if((i - 1) == this.deleteCursor)
					toAdd += " `< ❌`";
			} else toAdd = (++i) + ". ||" + thing + "||";
			numbered.add(toAdd);
		}
		int count = customFilter.size();
		int max = MessageEmbed.VALUE_MAX_LENGTH;
		String title = count + " " + BonziUtils.plural("word", count) + ":";
		String desc = String.join("\n", numbered);
		desc = BonziUtils.cutOffString(desc, max);
		
		menu.addField(title, desc, false);
		return menu.build();
	}
	
	@Override
	public void onButtonClick(String actionId, long executorId, JDA jda) {
		GuildSettingsManager gsm = this.bonziReference.guildSettings;
		EventWaiterManager waiter = this.bonziReference.eventWaiter;
		GuildSettings settings = gsm.getSettings(guildId);
		List<String> customFilter = settings.customFilter;
		
		if(this.deleteMode) {
			if(actionId.equals("return")) {
				// Back
				this.deleteMode = false;
				this.reinitialize(customFilter);
				this.parent.redrawMessage(jda);
				return;
			}
			if(actionId.equals("up")) {
				if(customFilter.size() > 0) {
					this.deleteCursor--;
					if(this.deleteCursor < 0)
						this.deleteCursor = customFilter.size() - 1;
					if(this.deleteCursor >= customFilter.size())
						this.deleteCursor = 0;
					this.parent.redrawMessage(jda);
				}
			}
			if(actionId.equals("down")) {
				if(customFilter.size() > 0) {
					this.deleteCursor++;
					if(this.deleteCursor < 0)
						this.deleteCursor = customFilter.size() - 1;
					if(this.deleteCursor >= customFilter.size())
						this.deleteCursor = 0;
					this.parent.redrawMessage(jda);
				}		
			}
			if(actionId.equals("delete")) {
				customFilter.remove(this.deleteCursor);
				settings.customFilter = customFilter;
				gsm.setSettings(guildId, settings);
				
				if(customFilter.size() > 0) {
					if(this.deleteCursor > 0)
						this.deleteCursor--;
				} else {
					this.deleteMode = false;
					this.deleteCursor = 0;
					this.reinitialize(customFilter);
				}
				this.parent.redrawMessage(jda);
			}
		} else {
			if(actionId.equals("return")) {
				// Back
				Gui back = new GuiGuildSettingsPage1(guildId, guildName);
				this.parent.setActiveGui(back, jda);
				return;
			}
			if(actionId.equals("new")) {
				// Add
				MessageChannel ch = this.parent.getChannel(jda);
				if(customFilter.size() > MAX_FILTER_COUNT) {
					ch.sendMessageEmbeds(BonziUtils.failureEmbed(
						"You can only have " + MAX_FILTER_COUNT + " items in your custom filter!",
						"Remove a few items before adding more.")).queue();
					return;
				}
				ch.sendMessageEmbeds(BonziUtils.quickEmbed("Type the word(s) you want to add.",
					"Max " + MAX_FILTER_LENGTH + " characters.", Color.orange).build()).queue(sent -> {
					waiter.waitForResponse(this.parent.ownerId, message -> {
						sent.delete().queue();
						message.delete().queue();
						String text = message.getContentRaw();
						if(text.length() < 2) {
							BonziUtils.sendTempMessage(ch, BonziUtils.failureEmbed("Cancelled."), 2);
							return;
						}
						if(text.length() > MAX_FILTER_LENGTH) {
							BonziUtils.sendTempMessage(ch, BonziUtils.failureEmbed("Text is too long!"), 3);
							return;
						}
						if(customFilter.stream().anyMatch(str -> str.equalsIgnoreCase(text))) {
							BonziUtils.sendTempMessage(ch, BonziUtils.failureEmbed("Word already in filter."), 3);
							return;
						}
						customFilter.add(text);
						settings.customFilter = customFilter;
						gsm.setSettings(guildId, settings);
						this.reinitialize(customFilter);
						this.parent.redrawMessage(message.getJDA());
					});
				});
			}
			if(actionId.equals("remove")) {
				// Remove
				MessageChannel ch = this.parent.getChannel(jda);
				if(customFilter.size() < 1) {
					BonziUtils.sendTempMessage(ch, BonziUtils.failureEmbed("There are no words to remove!"), 3);
					return;
				}
				this.deleteMode = true;
				this.reinitialize(customFilter);
				this.parent.redrawMessage(jda);
			}
		}
	}
}