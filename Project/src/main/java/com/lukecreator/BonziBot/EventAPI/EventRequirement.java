package com.lukecreator.BonziBot.EventAPI;

import java.io.Serializable;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.UserAccount;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

/*
 * Requirement for a member to join an event.
 */
public class EventRequirement implements Serializable {
	private static final long serialVersionUID = 1L;
	
	final RequirementType type; // The type of requirement
	final boolean inverted;		// Whether this requirement is inverted.
	final long value;			// The value of this requirement.
	
	private EventRequirement(RequirementType type, boolean inverted, long value) {
		this.type = type;
		this.inverted = inverted;
		this.value = value;
	}
	
	/**
	 * The base factory method to create an EventRequirement object.
	 */
	public static EventRequirement create(RequirementType type, boolean inverted, long value) {
		return new EventRequirement(type, inverted, value);
	}
	
	// Factory methods to describe with more concise language.
	public static EventRequirement levelOrHigher(int level) {
		return new EventRequirement(RequirementType.LEVEL, false, (long)level);
	}
	public static EventRequirement levelOrLower(int level) {
		return new EventRequirement(RequirementType.LEVEL, true, (long)level);
	}
	public static EventRequirement coinsOrHigher(int coins) {
		return new EventRequirement(RequirementType.COINS, false, (long)coins);
	}
	public static EventRequirement coinsOrLower(int coins) {
		return new EventRequirement(RequirementType.COINS, true, (long)coins);
	}
	public static EventRequirement booster() {
		return new EventRequirement(RequirementType.BOOSTER, false, 0);
	}
	public static EventRequirement notBooster() {
		return new EventRequirement(RequirementType.BOOSTER, true, 0);
	}
	public static EventRequirement upgrader() {
		return new EventRequirement(RequirementType.UPGRADER, false, 0);
	}
	public static EventRequirement notUpgrader() {
		return new EventRequirement(RequirementType.UPGRADER, true, 0);
	}
	public static EventRequirement role(Role role) {
		return new EventRequirement(RequirementType.ROLE, false, role.getIdLong());
	}
	public static EventRequirement role(long roleId) {
		return new EventRequirement(RequirementType.ROLE, false, roleId);
	}
	public static EventRequirement notRole(Role role) {
		return new EventRequirement(RequirementType.ROLE, true, role.getIdLong());
	}
	public static EventRequirement notRole(long roleId) {
		return new EventRequirement(RequirementType.ROLE, true, roleId);
	}
	public static EventRequirement roleOrHigher(Role role) {
		return new EventRequirement(RequirementType.ROLEORHIGHER, false, role.getIdLong());
	}
	public static EventRequirement roleOrHigher(long roleId) {
		return new EventRequirement(RequirementType.ROLEORHIGHER, false, roleId);
	}
	public static EventRequirement roleOrLower(Role role) {
		return new EventRequirement(RequirementType.ROLEORHIGHER, true, role.getIdLong());
	}
	public static EventRequirement roleOrLower(long roleId) {
		return new EventRequirement(RequirementType.ROLEORHIGHER, true, roleId);
	}
	
	public boolean qualifies(Member member, UserAccount account) {
		Guild guild = member.getGuild();
		Role boosterRole = guild.getBoostRole();
		
		switch(type) {
		case BOOSTER:
			if(boosterRole == null)
				return false;
			boolean boosthas = false;
			for(Role test: member.getRoles())
				if(test.getIdLong() == boosterRole.getIdLong()) {
					boosthas = true;
					break;
				}
			return this.inverted ? !boosthas : boosthas;
		case COINS:
			long coins = account.getCoins();
			boolean cpass = coins >= this.value;
			return this.inverted ? !cpass : cpass;
		case LEVEL:
			long level = account.getCoins();
			boolean lpass = level >= this.value;
			return this.inverted ? !lpass : lpass;
		case ROLE:
			boolean hasRole = false;
			for(Role test: member.getRoles())
				if(test.getIdLong() == this.value) {
					hasRole = true;
					break;
				}
			return this.inverted ? !hasRole : hasRole;
		case ROLEORHIGHER:
			List<Role> roles = member.getRoles();
			if(roles.isEmpty())
				return this.inverted;
			Role role = roles.get(0);
			boolean hasRoleHigher = role.getPosition() >= this.value;
			return this.inverted ? !hasRoleHigher : hasRoleHigher;
		case UPGRADER:
			// TODO
			break;
		default:
			break;
		}
		return false;
	}
	public String toStringError(Guild guild) {
		Role r;
		String rName;
		
		switch(type) {
		case BOOSTER:
			return this.inverted ?
				"You cannot enter if you have boosted the server." :
				"You can only enter if you have boosted the server.";
		case COINS:
			return this.inverted ? 
				"You must have less than " + BonziUtils.comma((int)this.value) + " coins to enter." :
				"You must have " + BonziUtils.comma((int)this.value) + " or more coins to enter.";
		case LEVEL:
			return this.inverted ?
				"You must be less than level " + this.value + " to enter." :
				"You must be level " + this.value + " or higher to enter.";
		case ROLE:
			r = guild.getRoleById(this.value);
			rName = r == null ? "[deleted role]" : r.getName();
			return this.inverted ?
				"You must not have the \"" + rName + "\" role to enter." :
				"You need to have the \"" + rName + "\" role to enter.";
		case ROLEORHIGHER:
			r = guild.getRoleById(this.value);
			rName = r == null ? "[deleted role]" : r.getName();
			return this.inverted ?
				"Your role needs to be lower than " + rName + " to enter." :
				"You need to have the role " + rName + " or higher to enter.";
		case UPGRADER:
			return this.inverted ?
				"You cannot enter if you have upgraded the server through BonziBot." :
				"You can only enter if you have upgraded the server through BonziBot.";
		default:
			return "no idea";
		}
	}
	public String toString(Guild guild) {
		Role r;
		String rName;
		
		switch(type) {
		case BOOSTER:
			return this.inverted ?
				"You must not have boosted the server." :
				"You need to have boosted the server.";
		case COINS:
			return this.inverted ? 
				"You must have less than " + BonziUtils.comma((int)this.value) + " coins." :
				"You must have " + BonziUtils.comma((int)this.value) + " or more coins.";
		case LEVEL:
			return this.inverted ?
				"You must be less than level " + this.value + "." :
				"You must be level " + this.value + " or higher.";
		case ROLE:
			r = guild.getRoleById(this.value);
			rName = r == null ? "[deleted role]" : r.getName();
			return this.inverted ?
				"You must not have the \"" + rName + "\" role." :
				"You need to have the \"" + rName + "\" role.";
		case ROLEORHIGHER:
			r = guild.getRoleById(this.value);
			rName = r == null ? "[deleted role]" : r.getName();
			return this.inverted ?
				"Your role needs to be lower than " + rName + "." :
				"You need to have the role " + rName + " or higher.";
		case UPGRADER:
			return this.inverted ?
				"You must not have upgraded the server through BonziBot." :
				"You must have upgraded the server through BonziBot.";
		default:
			return "no idea";
		}
	}
}