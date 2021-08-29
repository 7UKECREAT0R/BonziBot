package com.lukecreator.BonziBot.Data;

import java.io.Serializable;

import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

/*
 * A reference to a previous message.
 */
public class MessageReference implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final int PREVIEW_LENGTH = 128;
	
	public final boolean fromGuild;
	public final long guildId;
	
	public final long messageId;
	public final String messageUrl;
	public final String authorName;
	public String messagePreview;
	
	public MessageReference(Message msg, boolean excludeContent) {
		this.fromGuild = msg.isFromGuild();
		this.guildId = msg.getGuild().getIdLong();
		this.messageId = msg.getIdLong();
		this.messageUrl = msg.getJumpUrl();
		
		if(!excludeContent) {
			String preview = msg.getContentDisplay();
			if(preview.length() > PREVIEW_LENGTH)
				preview = preview.substring(0, PREVIEW_LENGTH) + "...";
			this.messagePreview = preview;
		} else this.messagePreview = "`[content not saved]`";
		
		// "📁 2 Attachments"
		if(!msg.getAttachments().isEmpty()) {
			int sz = msg.getAttachments().size();
			this.messagePreview += "\n`📁 " + sz + BonziUtils.plural(" Attachment", sz) + "`";
		}
		// "📑 1 Embed"
		if(!msg.getEmbeds().isEmpty()) {
			int sz = msg.getEmbeds().size();
			this.messagePreview += "\n`📑 " + sz + BonziUtils.plural(" Embed", sz) + "`";
		}
		
		User user = msg.getAuthor();
		String name = user.getName();
		if(user.isBot())
			name += " " + EmoteCache.mentionEmoteByName("bot_tag");
		this.authorName = name;
	}
}
