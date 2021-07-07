package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.CommandArg;
import com.lukecreator.BonziBot.CommandAPI.TextChannelArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.Rules;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;
import com.lukecreator.BonziBot.Managers.QuickDrawManager;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

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
		GuildSettingsManager mgr = this.bonziReference.guildSettings;
		GuildSettings settings = mgr.getSettings(guildId);
		this.reinitialize(settings);
	}
	public void reinitialize(GuildSettings settings) {
		
		boolean qd = settings.quickDraw;
		boolean appeals = settings.banAppeals;
		boolean banMsg = settings.banMessage;
		boolean token = settings.tokenScanning;
		
		this.buttons.clear();
		this.buttons.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬅️"), "lastpage"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("↪️"), "Prefix", GuiButton.Color.BLUE, "prefix"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("📖"), "Rules", GuiButton.Color.BLUE,  "rules"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🚫"), "Disable Commands", GuiButton.Color.BLUE,  "disable"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🎲"), (qd ? "Disable" : "Enable") + " Quick Draw", qd ? GuiButton.Color.RED : GuiButton.Color.GREEN,  "quickdraw"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("📥"), (appeals ? "Disable" : "Enable") + " Ban Appeals", appeals ? GuiButton.Color.RED : GuiButton.Color.GREEN, "appeals"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("📳"), (banMsg ? "Disable" : "Enable") + " Ban Message", banMsg ? GuiButton.Color.RED : GuiButton.Color.GREEN, "banmsg"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🔬"), (token ? "Disable" : "Enable") + " Token Scanning", token ? GuiButton.Color.RED : GuiButton.Color.GREEN, "tokenscan"));
		this.buttons.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("➡️"), "nextpage"));
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
		
		List<Integer> disabled = settings.disabledCommands;
		if(disabled == null || disabled.isEmpty()) {
			menu.addField("🚫 Disabled Commands", "No commands are disabled.", false);
		} else {
			int size = disabled.size();
			String desc = size + BonziUtils.plural(" command", size) + " disabled.\nDisable commands that you don't want people to run.";
			menu.addField("🚫 Disabled Commands", desc, false);
		}
		
		boolean qd = settings.quickDraw;
		menu.addField("🎲 Quick Draw: `" + (qd?"✅ ENABLED`":"🔳 DISABLED`"),
				"Periodic mini-games where the first person to perform an action gets coins!", false);
		
		boolean appeals = settings.banAppeals;
		String appealDesc;
		if(appeals) {
			if(settings.banAppealsChannel == 0l)
				appealDesc = "Enabled, but not setup all the way.";
			else
				appealDesc = "Channel: <#" + settings.banAppealsChannel + ">";
			appealDesc += "\nAllow Bonzi-banned users to appeal their bans with a form.";
		} else {
			appealDesc = "Allow Bonzi-banned users to appeal their bans with a form.";
		}
		menu.addField("📥 Ban Appeals `" + (appeals?"✅ ENABLED`":"🔳 DISABLED`"), appealDesc, false);
		
		boolean banMsg = settings.banMessage;
		String banString = settings.banMessageString;
		String banDesc = (banMsg ? "'" + banString + "'\n" : "" )
			+ "Send a formatted message to users who were banned via BonziBot.";
		menu.addField("📳 Ban Message `" + (banMsg?"✅ ENABLED`":"🔳 DISABLED`"), banDesc, false);
		
		boolean tokenScan = settings.tokenScanning;
		menu.addField("🔬 Token Scanning`" + (tokenScan?"✅ ENABLED`":"🔳 DISABLED`"),
				"Constantly scan for bot tokens and automatically invalidate them if they're accidentally sent.", false);
		
		menu.setFooter("Page 2/3");
		return menu.build();
	}
	
	@Override
	public void onAction(String actionId, long executorId, JDA jda) {
		GuildSettingsManager gsm = this
			.bonziReference.guildSettings;
		GuildSettings settings = gsm.getSettings(guildId);
		
		if(actionId.equals("lastpage")) {
			Gui next = new GuiGuildSettingsPage1(this.guildId, this.guildName);
			this.parent.setActiveGui(next, jda);
			return;
		}
		
		if(actionId.equals("prefix")) {
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
			return;
		}
		if(actionId.equals("rules")) {
			GuiRules gui = new GuiRules(this.guildId, this.guildName);
			this.parent.setActiveGui(gui, jda);
			return;
		}
		if(actionId.equals("disable")) {
			GuiDisableCommands gui = new GuiDisableCommands(this.guildId, this.guildName);
			this.parent.setActiveGui(gui, jda);
			return;
		}
		if(actionId.equals("quickdraw")) {
			settings.quickDraw = !settings.quickDraw;
			gsm.setSettings(guildId, settings);
			
			QuickDrawManager qdm = this.bonziReference.quickDraw;
			if(settings.quickDraw)
				qdm.tryOptGuild(guildId);
			else
				qdm.tryUnoptGuild(guildId);
			
			this.reinitialize(settings);
			this.parent.redrawMessage(jda);
			return;
		}
		if(actionId.equals("appeals")) {
			if(settings.banAppeals) {
				settings.banAppeals = false;
				settings.banAppealsChannel = 0l;
				gsm.setSettings(guildId, settings);
				this.reinitialize(settings);
				this.parent.redrawMessage(jda);
			} else {
				CommandArg tca = new TextChannelArg("");
				EventWaiterManager ewm = this.bonziReference.eventWaiter;
				MessageChannel mc = this.parent.getChannel(jda);
				mc.sendMessage(BonziUtils.quickEmbed("Turning on Ban Appeals...",
					"Send the channel you want ban appeals to go into for review!\n*Users can only appeal if banned using __Bonzi's ban command__.*", Color.gray).build()).queue(sent -> {
					ewm.waitForArgument(this.parent.ownerId, tca, object -> {
						sent.delete().queue();
						TextChannel channel = (TextChannel)object;
						settings.banAppeals = true;
						settings.banAppealsChannel = channel.getIdLong();
						gsm.setSettings(guildId, settings);
						this.reinitialize(settings);
						this.parent.redrawMessage(jda);
						BonziUtils.sendTempMessage(mc, BonziUtils.successEmbed("Users are now able to appeal bans!"), 4);
					});
				});
			}
			return;
		}
		if(actionId.equals("banmsg")) {
			if(settings.banMessage) {
				settings.banMessage = false;
				settings.banMessageString = null;
				gsm.setSettings(guildId, settings);
				this.reinitialize(settings);
				this.parent.redrawMessage(jda);
			} else {
				EventWaiterManager ewm = this.bonziReference.eventWaiter;
				MessageChannel mc = this.parent.getChannel(jda);
				mc.sendMessage(BonziUtils.quickEmbed("Turning on Ban Messages...",
					"Users who are banned with __Bonzi's ban command__ will be messaged this.\n"
					+ "Insert these variables to customize the message:\n"
					+ "`{name}` The name of the user.\n"
					+ "`{server}` The name of the server.\n"
					+ "`{reason}` The reason of the ban.\n"
					+ "`{time}` The length of the ban.", Color.gray).build()).queue(sent -> {
					ewm.waitForResponse(this.parent.ownerId, msg -> {
						sent.delete().queue();
						settings.banMessage = true;
						settings.banMessageString = msg.getContentRaw();
						gsm.setSettings(guildId, settings);
						this.reinitialize(settings);
						this.parent.redrawMessage(jda);
						BonziUtils.sendTempMessage(mc, BonziUtils.successEmbed("Users will now be messaged upon being banned."), 4);
					});
				});
			}
			return;
		}
		if(actionId.equals("tokenscan")) {
			settings.tokenScanning = !settings.tokenScanning;
			gsm.setSettings(guildId, settings);
			this.reinitialize(settings);
			this.parent.redrawMessage(jda);
			return;
		}
	}
}
