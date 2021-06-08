package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.ArrayArg;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.Data.EmojiCache;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.Rules;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class GuiRules extends Gui {
	
	private String guildName;
	private long guildId;
	private Rules rules;
	
	public GuiRules(long guildId, String guildName) {
		this.guildId = guildId;
		this.guildName = guildName;
	}
	
	@Override
	public void initialize(JDA jda) {
		this.buttons.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬅️"), "return"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🆕"), "Add", GuiButton.Color.BLUE, "new"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmote(EmojiCache.getEmoteByName("b_trash")), "Delete", GuiButton.Color.RED,  "delete"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("📝"), "Edit", GuiButton.Color.BLUE, "edit"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("📋"), "Format", GuiButton.Color.GRAY, "format"));
		this.rules = this.bonziReference.guildSettings.getSettings(guildId).getRules();
	}
	
	@Override
	public Object draw(JDA jda) {
		EmbedBuilder eb = new EmbedBuilder()
			.setColor(BonziUtils.COLOR_BONZI_PURPLE)
			.setTitle(guildName + " Rules Editor")
			.setFooter("Run '" + this.prefixOfLocation + "sendrules' to send the message.");
		String[] lines = rules.getRules();
		StringBuilder full = new StringBuilder();
		for(int i = 0; i < lines.length; i++) {
			full.append((i + 1) + ". " + lines[i] + '\n');
		}
		String desc = full.toString();
		if(desc.length() > 0)
			desc = desc.substring(0, desc.length() - 1);
		desc = BonziUtils.cutOffString(desc, MessageEmbed.TEXT_MAX_LENGTH);
		eb.setDescription(desc);
		
		Emote trash = EmojiCache.getEmoteByName("b_trash");
		eb.addField("🆕 Add Rule", "Add a new rule to the end of the list.", false);
		eb.addField(trash.getAsMention() + " Delete Rule", "Delete a rule by its number.", false);
		eb.addField("📝 Edit Rule", "Edit a rule by its number.", false);
		eb.addField("📋 Formatting: `" + rules.getFormatting().name() + "`", "The formatting of each rule. Click to scroll through the options.", false);
		return eb.build();
	}
	
	@Override
	public void onAction(String actionId, JDA jda) {
		MessageChannel channel = this.parent.getChannel(jda);
		GuildSettingsManager gsm = this.bonziReference.guildSettings;
		GuildSettings settings = gsm.getSettings(guildId);
		
		if(channel == null)
			return;
		
		if(actionId.equals("return")) {
			// Back button.
			this.parent.setActiveGui(new GuiGuildSettingsPage2(guildId, guildName), jda);
			return;
		}
		
		if(actionId.equals("new")) {
			// New
			MessageEmbed msge = BonziUtils.quickEmbed("Creating New Rule...",
				"Send the new rule you want to add here...", Color.orange).build();
			channel.sendMessage(msge).queue(msg -> {
				EventWaiterManager ewm = this.bonziReference.eventWaiter;
				ewm.waitForResponse(this.parent.ownerId, response -> {
					String contents = BonziUtils.cutOffString
						(response.getContentRaw(), 512);
					rules.addRule(contents);
					settings.setRules(rules);
					gsm.setSettings(guildId, settings);
					
					// Update existing rules message, if any.
					Guild guild = jda.getGuildById(guildId);
					rules.retrieveRulesMessage(jda, guildId, edit -> {
						MessageEmbed newRules = BonziUtils.generateRules
							(guild, this.bonziReference).build();
						edit.editMessage(newRules).queue();
					}, fail -> {
						settings.setRules(rules);
						gsm.setSettings(guildId, settings);
					});
					
					msg.delete().queue();
					response.delete().queue();
					this.parent.redrawMessage(response.getJDA());
				});
			});
			return;
		}
		if(actionId.equals("delete")) {
			// Delete
			int rCount = rules.getRulesCount();
			if(rCount < 1) {
				BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("There's no rules yet to delete."), 3);
				return;
			}
			if(rCount == 1) {
				// Only one possibility.
				this.rules.removeRule(0);
				this.parent.redrawMessage(jda);
				return;
			} else {
				MessageEmbed msge = BonziUtils.quickEmbed("Deleting Rule...",
					"Send the number of the rule you want to delete. If you want to delete multiple rules, separate them with a '"
						+ ArrayArg.DELIMITER + "'.", Color.orange).build();
				channel.sendMessage(msge).queue(msg -> {
					EventWaiterManager ewm = this.bonziReference.eventWaiter;
					ArrayArg arg = new ArrayArg("", IntArg.class);
					ewm.waitForArgument(this.parent.ownerId, arg, response -> {
						msg.delete().queue();
						Integer[] step1 = (Integer[])response;
						// sort descending because elements get shifted left
						Arrays.sort(step1, Collections.reverseOrder());
						for(int i = 0; i < step1.length; i++) {
							// user input will be in non-index format
							int index = step1[i].intValue() - 1;
							if(index >= rules.getRulesCount())
								continue;
							if(index < 0)
								continue;
							rules.removeRule(index);
						}
						settings.setRules(rules);
						gsm.setSettings(guildId, settings);
						
						// Update existing rules message, if any.
						Guild guild = jda.getGuildById(guildId);
						rules.retrieveRulesMessage(jda, guildId, edit -> {
							MessageEmbed newRules = BonziUtils.generateRules
								(guild, this.bonziReference).build();
							edit.editMessage(newRules).queue();
						}, fail -> {
							settings.setRules(rules);
							gsm.setSettings(guildId, settings);
						});
						
						this.parent.redrawMessage(msg.getJDA());
						return;
					});
				});
			}
			return;
		}
		if(actionId.equals("edit")) {
			int rCount = rules.getRulesCount();
			
			if(rCount < 1) {
				BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("There's no rules yet to edit."), 3);
				return;
			}
			
			MessageEmbed embed1 = BonziUtils.quickEmbed("Editing Rule...",
				"Send the number of the rule you want to edit.", Color.orange).build();
			MessageEmbed embed2 = BonziUtils.quickEmbed("Editing Rule...",
				"Great, now send what you want the new rule to be.", Color.orange).build();
			channel.sendMessage(embed1).queue(msg1 -> {
				EventWaiterManager ewm = this.bonziReference.eventWaiter;
				IntArg arg1 = new IntArg("");
				ewm.waitForArgument(this.parent.ownerId, arg1, number -> {
					msg1.delete().queue();
					int index = ((Integer)number).intValue() - 1;
					if(index >= rCount || index < 0) {
						BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("Number was out of bounds. Cancelled operation."), 4);
						return;
					}
					
					// double whammy
					msg1.getChannel().sendMessage(embed2).queue(msg2 ->
					ewm.waitForResponse(this.parent.ownerId, newRule -> {
						msg2.delete().queue();
						String contents = BonziUtils.cutOffString
							(newRule.getContentRaw(), 512);
						rules.setRule(index, contents);
						settings.setRules(rules);
						gsm.setSettings(guildId, settings);
						
						// Update existing rules message, if any.
						Guild guild = jda.getGuildById(guildId);
						rules.retrieveRulesMessage(jda, guildId, edit -> {
							MessageEmbed newRules = BonziUtils.generateRules
								(guild, this.bonziReference).build();
							edit.editMessage(newRules).queue();
						}, fail -> {
							settings.setRules(rules);
							gsm.setSettings(guildId, settings);
						});
						
						newRule.delete().queue();
						this.parent.redrawMessage(newRule.getJDA());
						return;
					}));
				});
			});
			return;
		}
		if(actionId.equals("format")) {
			// Formatting
			rules.scrollFormatting();
			settings.setRules(rules);
			gsm.setSettings(guildId, settings);
			
			// Update existing rules message, if any.
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
	}
}