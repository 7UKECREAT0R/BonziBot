package com.lukecreator.BonziBot.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.annotation.Nonnull;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CustomCommand;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.User;

/**
 * Represents a tag. (b:tag)
 */
public class TagData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public final LocalDate created;
	public final String creator;
	public final long creatorId;
	public final String guild;
	
	public int uses;
	public String name;
	public String response;
	
	/**
	 * Create a Tag in a guild.
	 */
	public TagData(String in, String out, @Nonnull Member creator) {
		User u = creator.getUser();
		this.creator = u.getName();
		this.creatorId = u.getIdLong();
		this.guild = creator.getGuild().getName();
		this.name = in;
		this.response = out;
		this.created = LocalDate.now();
		uses = 0;
	}
	/**
	 * Create a Tag in dms.
	 */
	public TagData(String in, String out, @Nonnull User dmsUser) {
		this.creator = dmsUser.getName();
		this.creatorId = dmsUser.getIdLong();
		this.guild = "Private Messages";
		this.name = in;
		this.response = out;
		this.created = LocalDate.now();
		uses = 0;
	}
	/**
	 * Port tag data over to new format.
	 */
	public TagData(CustomCommand cc) {
		this.creator = cc.creator;
		this.created = cc.created;
		this.creatorId = cc.creatorID;
		this.guild = cc.server;
		this.uses = cc.uses;
		this.name = cc.command;
		this.response = cc.response;
	}
	
	public static TagData constructFromMessage(String tagName, Message m) {
		StringBuilder response = new StringBuilder();
		StringBuilder attachments = new StringBuilder();
		String mContent = m.getContentRaw();
		if(!mContent.isEmpty() && !BonziUtils.isWhitespace(mContent)) {
			response.append(mContent);
		}
		if(!m.getAttachments().isEmpty()) {
			List<Attachment> att = m.getAttachments();
			for(Attachment a: att) {
				String url = a.getUrl();
				attachments.append("\n" + url);
			}
		}
		String finalString;
		String attachmentString = attachments.toString();
		String responseString = response.toString();
		int lenA = attachmentString.length();
		int lenR = responseString.length();
		
		if(lenA + lenR <= 2000)
			finalString = responseString + attachmentString;
		else
			finalString = responseString;
		
		if(m.isFromGuild()) {
			Member member = m.getGuild().getMember(m.getAuthor());
			return new TagData(tagName, finalString, member);
		} else {
			User user = m.getAuthor();
			return new TagData(tagName, finalString, user);
		}
	}
	@Override
	public String toString() {
		return "TagData [created=" + created + ", creator=" + creator + ", creatorId=" + creatorId + ", guild=" + guild
				+ ", uses=" + uses + ", name=" + name + ", response=" + response + "]";
	}
	
}
