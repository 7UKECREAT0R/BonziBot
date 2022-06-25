package com.lukecreator.BonziBot.Data;

import com.lukecreator.BonziBot.BonziUtils;

/**
 * A mention in discord that can be resolved from a string and toString'd.
 * @see Mention.parse(String mention)
 * @see BonziUtils.resolveMention(String mention)
 * @author Lukec
 */
public class Mention {
	
	public enum Type {
		ROLE,
		USER,
		CHANNEL
	}
	
	public final Type type;
	public final long id;
	
	private Mention(Type type, long id) {
		this.type = type;
		this.id = id;
	}
	
	/**
	 * Create a mention for a role.
	 * @param id
	 * @return
	 */
	public static Mention role(long id) {
		return new Mention(Type.ROLE, id);
	}
	/**
	 * Create a mention for a user/member.
	 * @param id
	 * @return
	 */
	public static Mention user(long id) {
		return new Mention(Type.USER, id);
	}
	/**
	 * Create a mention for a channel.
	 * @param id
	 * @return
	 */
	public static Mention channel(long id) {
		return new Mention(Type.CHANNEL, id);
	}
	
	/**
	 * Parse a mention from a string. Indentical to: <br />
	 * <code>BonziUtils.resolveMention(mention);</code>
	 * @param mention
	 * @return
	 */
	public static Mention parse(String mention) {
		return BonziUtils.parseMention(mention);
	}
	
	@Override
	public String toString() {
		switch (this.type) {
		case ROLE:
			return "<@&" + this.id + ">";
		case CHANNEL:
			return "<#" + this.id + ">";
		case USER:
			return "<@" + this.id + ">";
		default:
			return String.valueOf(this.id);
		}
	}
}
