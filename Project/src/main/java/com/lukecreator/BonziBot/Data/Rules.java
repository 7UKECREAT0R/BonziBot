package com.lukecreator.BonziBot.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Rules implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public void setRulesMessage(Message msg) {
		this.rulesMessageChannelId = msg.getChannel().getIdLong();
		this.rulesMessageId = msg.getIdLong();
	}
	public void retrieveRulesMessage(JDA jda, long guildId, Consumer<Message> success, Consumer<Throwable> messageGone) {
		Guild guild = jda.getGuildById(guildId);
		this.retrieveRulesMessage(guild, success, messageGone);
	}
	public void retrieveRulesMessage(Guild guild, Consumer<Message> success, Consumer<Throwable> messageGone) {
		if(this.rulesMessageChannelId == -1)
			return;
		if(this.rulesMessageId == -1)
			return;
		
		TextChannel channel = guild.getTextChannelById(this.rulesMessageChannelId);
		
		if(channel == null) {
			this.rulesMessageChannelId = -1;
			this.rulesMessageId = -1;
			return;
		}
		
		channel.retrieveMessageById(this.rulesMessageId).queue(success, fail -> {
			this.rulesMessageChannelId = -1;
			this.rulesMessageId = -1;
			if(messageGone != null)
				messageGone.accept(fail);
			return;
		});
		return;
	}
	
	private long rulesMessageChannelId = -1;
	private long rulesMessageId = -1;
	
	private List<String> rules = new ArrayList<String>();
	private Formatting formatting = Formatting.NUMBERED;
	
	//private int infractionCount = 3; // maximum amount of infractions.
	//private List<Infraction> infractionPunishments = new ArrayList<Infraction>();
	
	public void addRule(String rule) {
		this.rules.add(rule);
	}
	public String getRule(int index) {
		return this.rules.get(index);
	}
	/**
	 * Returns a copy of the rules list as an array.
	 * @return
	 */
	public String[] getRules() {
		return (String[])rules.toArray(new String[rules.size()]);
	}
	/**
	 * Get the amount of rules currently.
	 * @return
	 */
	public int getRulesCount() {
		return this.rules.size();
	}
	/**
	 * Remove a rule by its index.
	 * @param index
	 */
	public void removeRule(int index) {
		this.rules.remove(index);
	}
	public void setRule(int index, String rule) {
		if(index < 0 || index >= this.rules.size())
			return;
		this.rules.set(index, rule);
	}
	
	public Formatting getFormatting() {
		return this.formatting;
	}
	/**
	 * Scrolls through the available formatting options. Returns the new value.
	 * @return
	 */
	public Formatting scrollFormatting() {
		switch(this.formatting) {
		case NUMBERED:
			this.formatting = Formatting.DOTTED;
			break;
		case DOTTED:
			this.formatting = Formatting.PLAIN;
			break;
		case PLAIN:
			this.formatting = Formatting.NUMBERED;
			break;
		}
		return this.formatting;
	}
	
	/*
	public int getInfractionCount() {
		return this.infractionCount;
	}
	public void setInfractionCount(int count) {
		if(count < 0)
			return;
		if(count > 10)
			count = 10;
		this.infractionCount = count;
	}
	public void setInfraction(int index, Infraction object) {
		if(index >= this.infractionCount)
			index = this.infractionCount - 1;
		else if(index < 0)
			index = 0;
		this.infractionPunishments.set(index, object);
	}
	public Infraction[] getInfractions() {
		Infraction[] array = new Infraction[this.infractionCount];
		int i = -1;
		for(Infraction f: this.infractionPunishments) {
			if(++i >= this.infractionCount)
				break;
			array[i] = f;
		}
		return array;
	}
	*/
	
	@Override
	public String toString() {
		int s = this.rules.size();
		return s + BonziUtils.plural(" Rule", s) + ".";
	}
	
	public enum Formatting {
		NUMBERED, DOTTED, PLAIN
	}
}
