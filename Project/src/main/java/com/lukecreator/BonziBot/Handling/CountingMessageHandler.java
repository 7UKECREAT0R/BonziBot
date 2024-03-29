package com.lukecreator.BonziBot.Handling;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.Achievement;
import com.lukecreator.BonziBot.Data.Modifier;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CountingMessageHandler implements MessageHandler {
	int count = 0;
	@Override
	public void handleGuildMessage(BonziBot bb, MessageReceivedEvent e, Modifier[] modifiers) {
		Message message = e.getMessage();
		Guild guild = e.getGuild();
		long guildId = guild.getIdLong();
		
		int nextNumber = bb.counting.getNextNumber(guildId);
		String content = message.getContentRaw().trim();
		
		if(!String.valueOf(nextNumber).equals(content)) {
			message.delete().queue(null, COLLISION_IGNORE);
			return;
		}
		
		if(nextNumber % 1000 == 0) {
			TextChannel channel = (TextChannel)e.getChannel();
			User author = e.getAuthor();
			BonziUtils.tryAwardAchievement(channel, bb, author, Achievement.MILESTONER);
		}
		
		nextNumber++;
		bb.counting.setNextNumber(guildId, nextNumber);
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
		for(Modifier mod: modifiers) {
			if(mod == Modifier.COUNTING_GAME)
				return true;
		}
		return false;
	}

}
