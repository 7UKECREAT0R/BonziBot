package com.lukecreator.BonziBot.Gui;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.CommandArg;
import com.lukecreator.BonziBot.CommandAPI.TextChannelArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.GuildSettingsManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class GuiStarboard extends Gui {
	
	private String guildName;
	private long guildId;
	
	public static final int LIMIT_MAX = 50;
	public static final int LIMIT_MIN = 2;
	
	private boolean enabled;
	private long starboardChannel;
	private int starboardLimit;
	
	public GuiStarboard(long guildId, String guildName) {
		this.guildId = guildId;
		this.guildName = guildName;
	}
	
	@Override
	public void initialize(JDA jda) {
		GuildSettings settings = this.bonziReference.guildSettings.getSettings(guildId);
		this.enabled = settings.starboard != 0l;
		this.starboardChannel = settings.starboard;
		this.starboardLimit = settings.starboardLimit;
		this.reinitialize();
	}
	public void reinitialize() {
		this.buttons.clear();
		this.buttons.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬅️"), "return"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("#️⃣"), "Channel", GuiButton.Color.BLUE, "channel"));
		this.buttons.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("🔼"), "limitup").asEnabled(enabled));
		this.buttons.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("🔽"), "limitdown").asEnabled(enabled));
		this.buttons.add(GuiButton.newline());
		this.buttons.add(new GuiButton("Disable", GuiButton.Color.RED, "disable").asEnabled(enabled));
	}
	
	@Override
	public Object draw(JDA jda) {
		EmbedBuilder eb = new EmbedBuilder()
			.setColor(BonziUtils.COLOR_BONZI_PURPLE)
			.setTitle("Starboard Settings")
			.setFooter("Add a message to the starboard every time enough users react to it with ⭐!");
		
		String channelDesc = "Enable starboard by setting the channel starred messages go into.";
		if(enabled)
			channelDesc = "<#" + this.starboardChannel + ">\n" + channelDesc;
		eb.addField("#️⃣ Channel", channelDesc, false);
		
		if(enabled)
			eb.addField("🔼🔽 Limit", "Current limit: `" + this.starboardLimit + "`\nNumber of reactions needed to star a message.", false);
		return eb.build();
	}
	
	@Override
	public void onAction(String actionId, long executorId, JDA jda) {
		GuildSettingsManager gsm = this.bonziReference.guildSettings;
		GuildSettings settings = gsm.getSettings(guildId);
		
		if(actionId.equals("return")) {
			this.parent.setActiveGui(new GuiGuildSettingsPage3(guildId, guildName), jda);
			return;
		}
		
		if(actionId.equals("channel")) {
			CommandArg tca = new TextChannelArg("");
			EventWaiterManager ewm = this.bonziReference.eventWaiter;
			MessageChannel mc = this.parent.getChannel(jda);
			mc.sendMessageEmbeds(BonziUtils.quickEmbed(this.enabled ? "Changing starboard channel..." : "Enabling starboard...",
				"Send the channel you want starred messages to magically appear in!", Color.gray).build()).queue(sent -> {
				ewm.waitForArgument(this.parent.ownerId, tca, object -> {
					sent.delete().queue();
					TextChannel channel = (TextChannel)object;
					this.starboardChannel = channel.getIdLong();
					settings.starboard = this.starboardChannel;
					gsm.setSettings(guildId, settings);
					this.enabled = true;
					this.reinitialize();
					this.parent.redrawMessage(jda);
				});
			});
			return;
		}
		if(actionId.equals("limitup")) {
			this.starboardLimit++;
			if(this.starboardLimit > LIMIT_MAX)
				this.starboardLimit = LIMIT_MIN;
			settings.starboardLimit = this.starboardLimit;
			gsm.setSettings(guildId, settings);
			this.parent.redrawMessage(jda);
		}
		if(actionId.equals("limitdown")) {
			this.starboardLimit--;
			if(this.starboardLimit < LIMIT_MIN)
				this.starboardLimit = LIMIT_MAX;
			settings.starboardLimit = this.starboardLimit;
			gsm.setSettings(guildId, settings);
			this.parent.redrawMessage(jda);
		}
		if(actionId.equals("disable")) {
			this.enabled = false;
			this.starboardChannel = 0l;
			settings.starboard = 0l;
			gsm.setSettings(guildId, settings);
			this.reinitialize();
			this.parent.redrawMessage(jda);
		}
	}
}