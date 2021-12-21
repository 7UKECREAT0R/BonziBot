package com.lukecreator.BonziBot.Handling;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.Modifier;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class AnonymousMessageHandler implements MessageHandler {
	
	
	
	@Override
	public void handleGuildMessage(BonziBot bb, GuildMessageReceivedEvent e, Modifier[] modifiers) {
		Message msg = e.getMessage();
		msg.delete().queue(null, COLLISION_IGNORE);
		
		GuildSettings settings = bb.guildSettings.getSettings(e.getGuild());
		
		if(settings.testMessageInFilter(msg)) {
			String content = e.getMessage().getContentRaw();
			e.getChannel().sendMessage(content);
		}
	}

	@Override
	public void handlePrivateMessage(BonziBot bb, PrivateMessageReceivedEvent e) {
		return;
	}

	@Override
	public boolean appliesInChannel(MessageChannel channel) {
		return channel.getType() == ChannelType.TEXT;
	}

	@Override
	public boolean appliesInModifiers(Modifier[] modifiers) {
		for(Modifier mod: modifiers) 
			if(mod == Modifier.ANONYMOUS)
				return true;
		return false;
	}

}
