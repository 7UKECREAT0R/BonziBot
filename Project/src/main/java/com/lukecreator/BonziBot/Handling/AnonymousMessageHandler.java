package com.lukecreator.BonziBot.Handling;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.Modifier;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AnonymousMessageHandler implements MessageHandler {
	
	@Override
	public void handleGuildMessage(BonziBot bb, MessageReceivedEvent e, Modifier[] modifiers) {
		Message msg = e.getMessage();
		msg.delete().queue(null, COLLISION_IGNORE);
		
		GuildSettings settings = bb.guildSettings.getSettings(e.getGuild());
		
		if(settings.testMessageInFilter(msg)) {
			String content = e.getMessage().getContentRaw();
			TextChannel channel = (TextChannel)e.getChannel();
			channel.sendMessage(content).queue();
		}
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
			if(mod == Modifier.ANONYMOUS)
				return true;
		return false;
	}

}
