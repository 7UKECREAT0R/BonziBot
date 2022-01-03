package com.lukecreator.BonziBot.Data;

import java.io.Serializable;

import com.lukecreator.BonziBot.BonziUtils;

/**
 * A <String, Long> pair of values representing a reaction role item.
 * @author Lukec
 *
 */
public class ReactionRole implements Serializable {
	private static final int MAX_TEXT_LENGTH = 100;
	private static final long serialVersionUID = 1L;
	
	public final String text;
	public final long roleId;
	public final GenericEmoji emoji;
	
	/**
	 * Creates a reaction role.
	 * @param text Max length {@value #MAX_TEXT_LENGTH}, cuts off string
	 * @param roleId The role ID
	 */
	public ReactionRole(String text, long roleId, GenericEmoji emoji) {
		this.text = BonziUtils.cutOffString(text, MAX_TEXT_LENGTH);
		this.roleId = roleId;
		this.emoji = emoji;
	}
	/**
	 * Mention the role related to this ReactionRole.
	 * @return
	 */
	public String mentionRole() {
		return "<@&" + this.roleId + '>';
	}
	
	@Override
	public String toString() {
		return this.emoji.toString() + " - " + this.mentionRole() + " - " + this.text;
	}
}
