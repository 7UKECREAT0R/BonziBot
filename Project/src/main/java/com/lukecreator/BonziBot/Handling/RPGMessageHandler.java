package com.lukecreator.BonziBot.Handling;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.Modifier;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class RPGMessageHandler implements MessageHandler {
	
	@Override
	public void handleGuildMessage(BonziBot bb, GuildMessageReceivedEvent e, Modifier[] modifiers) {
		TextChannel tc = e.getChannel();
		
		EmbedBuilder eb = new EmbedBuilder()
			.setTitle("RPG Status")
			.setDescription("bonzibot RPG isnt dead yet! its on my roadmap for the future and I have a ton of much more awesome ideas in store..."
					+ " right now, however, development is being focused on the core features which are on my to-do list!")
			.setColor(BonziUtils.COLOR_BONZI_PURPLE)
			.setFooter("- luk cratr");
		
		tc.sendMessageEmbeds(eb.build()).queue();
	}
	@Override
	public void handlePrivateMessage(BonziBot bb, PrivateMessageReceivedEvent e) {
		return; // impossible
	}

	@Override
	public boolean appliesInChannel(MessageChannel channel) {
		return channel.getType() == ChannelType.TEXT;
	}

	@Override
	public boolean appliesInModifiers(Modifier[] modifiers) {
		for(Modifier mod: modifiers)
			if(mod == Modifier.RPG)
				return true;
		return false;
	}
	
}
