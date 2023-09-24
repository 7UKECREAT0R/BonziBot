package com.lukecreator.BonziBot.Handling;

import java.util.regex.Matcher;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.Modifier;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PicturesOnlyMessageHandler implements MessageHandler {

	@Override
	public void handleGuildMessage(BonziBot bb, MessageReceivedEvent e, Modifier[] modifiers) {
		Message msg = e.getMessage();
		
		if(!msg.getAttachments().isEmpty())
			return; // Found attachment.
		
		String content = msg.getContentRaw();
		Matcher matcher = Constants.IMAGE_URL_REGEX_COMPILED.matcher(content);
		if(matcher.find())
			return; // Found image URL.
		
		msg.delete().queue(null, COLLISION_IGNORE);
		return;
	}

	@Override
	public void handlePrivateMessage(BonziBot bb, MessageReceivedEvent e) {
		return;
	}

	@Override
	public boolean appliesInChannel(MessageChannelUnion channel) {
		return channel.getType() == ChannelType.TEXT;
	}

	@Override
	public boolean appliesInModifiers(Modifier[] modifiers) {
		for(Modifier mod: modifiers)
			if(mod == Modifier.PICTURES_ONLY)
				return true;
		return false;
	}

}
