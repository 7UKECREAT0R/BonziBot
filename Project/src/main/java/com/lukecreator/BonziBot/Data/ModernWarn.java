package com.lukecreator.BonziBot.Data;

import java.io.Serializable;
import java.time.ZoneOffset;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Warn;

import net.dv8tion.jda.api.entities.Guild;

/**
 * Represents a warning which can be dished
 * out by moderators for breaking a rule.
 */
public class ModernWarn implements Serializable {
	
	private static final long serialVersionUID = 2l;
	private static final int MAX_REASON_LEN = 100;
	
	public long acquiredGuild;
	public long id;
	public String reason;
	public long timestamp;
	
	public ModernWarn(String reason, Guild guild) {
		if(reason.length() > MAX_REASON_LEN)
			reason = reason.substring(0, MAX_REASON_LEN);
		
		this.id = BonziUtils.generateId();
		this.reason = reason;
		this.timestamp = System.currentTimeMillis();
		this.acquiredGuild = guild.getIdLong();
	}
	public ModernWarn(String reason, long guild) {
		if(reason.length() > MAX_REASON_LEN)
			reason = reason.substring(0, MAX_REASON_LEN);
		
		this.id = BonziUtils.generateId();
		this.reason = reason;
		this.timestamp = System.currentTimeMillis();
		this.acquiredGuild = guild;
	}
	public ModernWarn(Warn old, long acquiredGuild) {
		this.id = BonziUtils.generateId();
		this.reason = old.reason;
		this.timestamp = old.date
			.atStartOfDay(ZoneOffset.UTC)
			.toInstant()
			.toEpochMilli();
		this.acquiredGuild = acquiredGuild;
	}
	@Override
	public String toString() {
		return id + ": \"" + reason + "\", at " + timestamp + " in the guild " + acquiredGuild;
	}
}
