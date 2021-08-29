package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.ArrayArg;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandSystem;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.Data.EmoteCache;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class GuiDisableCommands extends Gui {
	
	private String guildName;
	private long guildId;
	private List<Integer> disabledCommands;
	
	public GuiDisableCommands(long guildId, String guildName) {
		this.guildId = guildId;
		this.guildName = guildName;
	}
	
	@Override
	public void initialize(JDA jda) {
		this.buttons.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬅️"), "return"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🆕"), "Add Command", GuiButton.Color.BLUE, "new"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmote(EmoteCache.getEmoteByName("b_trash")), "Remove Command", GuiButton.Color.RED,  "delete"));
		this.disabledCommands = this.bonziReference.guildSettings.getSettings(guildId).disabledCommands;
		if(this.disabledCommands == null)
			this.disabledCommands = new ArrayList<Integer>();
	}
	
	@Override
	public Object draw(JDA jda) {
		EmbedBuilder eb = new EmbedBuilder()
			.setColor(BonziUtils.COLOR_BONZI_PURPLE)
			.setTitle(guildName + " Disabled Commands");
		
		CommandSystem cmds = this.bonziReference.commands;
		List<Command> commands = this.disabledCommands.stream().map(id -> {
			return cmds.getCommandById(id);
		}).collect(Collectors.toList());
		
		int i = 0;
		StringBuilder full = new StringBuilder();
		for(Command cmd: commands) {
			if(cmd == null)
				full.append(++i + ". [outdated entry]" + '\n');
			else
				full.append(++i + ". " + cmd.unicodeIcon + " " + cmd.name + '\n');
		}
		String desc = full.toString();
		if(desc.length() > 0)
			desc = desc.substring(0, desc.length() - 1);
		desc = BonziUtils.cutOffString(desc, MessageEmbed.TEXT_MAX_LENGTH);
		eb.setDescription(desc);
		
		Emote trash = EmoteCache.getEmoteByName("b_trash");
		eb.addField("🆕 Add Command", "Add a new command to the blacklist", false);
		eb.addField(trash.getAsMention() + " Remove Command", "Remove a command from the blacklist.", false);
		return eb.build();
	}
	
	@Override
	public void onAction(String actionId, long executorId, JDA jda) {
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
			MessageEmbed msge = BonziUtils.quickEmbed("Adding command to blacklist..",
				"Send the name of the command here.", Color.orange).build();
			channel.sendMessageEmbeds(msge).queue(msg -> {
				EventWaiterManager ewm = this.bonziReference.eventWaiter;
				ewm.waitForResponse(this.parent.ownerId, response -> {
					String contents = BonziUtils.cutOffString
						(response.getContentRaw(), 64);
					if(contents.startsWith(this.prefixOfLocation))
						contents = contents.substring(this.prefixOfLocation.length());
					
					Command pick = this.bonziReference.commands.getCommandByName(contents);
					if(pick == null) {
						msg.delete().queue();
						response.delete().queue();
						BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("No command was found."), 3);
						return;
					}
					if(pick.forcedCommand) {
						msg.delete().queue();
						response.delete().queue();
						BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("You cannot disable this command."), 3);
						return;
					}
					for(Integer already: this.disabledCommands) {
						if(already.intValue() == pick.id) {
							msg.delete().queue();
							response.delete().queue();
							BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("You have already disabled this command."), 3);
							return;
						}
					}
					this.disabledCommands.add(pick.id);
					settings.disabledCommands = this.disabledCommands;
					gsm.setSettings(guildId, settings);
					
					msg.delete().queue();
					response.delete().queue();
					this.parent.redrawMessage(response.getJDA());
				});
			});
			return;
		}
		if(actionId.equals("delete")) {
			// Delete
			int count = this.disabledCommands.size();
			if(count < 1) {
				BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("There's no commands to delete."), 3);
				return;
			}
			if(count == 1) {
				// Only one possibility.
				this.disabledCommands.remove(0);
				settings.disabledCommands = this.disabledCommands;
				gsm.setSettings(guildId, settings);
				this.parent.redrawMessage(jda);
				return;
			} else {
				MessageEmbed msge = BonziUtils.quickEmbed("Removing command from blacklist...",
					"Send the number of the command you want to remove. If you want to remove multiple commands, separate them with a '"
						+ ArrayArg.DELIMITER + "'.", Color.orange).build();
				channel.sendMessageEmbeds(msge).queue(msg -> {
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
							if(index >= this.disabledCommands.size())
								continue;
							if(index < 0)
								continue;
							this.disabledCommands.remove(index);
						}
						settings.disabledCommands = this.disabledCommands;
						gsm.setSettings(guildId, settings);
						this.parent.redrawMessage(msg.getJDA());
						return;
					});
				});
			}
			return;
		}
	}
}