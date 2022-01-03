package com.lukecreator.BonziBot.Handling;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.Modifier;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class PremiumOnlyMessageHandler implements MessageHandler {

	@Override
	public void handleGuildMessage(BonziBot bb, GuildMessageReceivedEvent e, Modifier[] modifiers) {
		
		User user = e.getAuthor();
		UserAccountManager uam = bb.accounts;
		UserAccount account = uam.getUserAccount(user);

		if(!account.isPremium) {
			Message msg = e.getMessage();
			msg.delete().queue(null, COLLISION_IGNORE);
		}
		
		// forward to default message handler after check
		BonziBot.DEFAULT_MESSAGE_HANDLER.handleGuildMessage(bb, e, modifiers);
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
			if(mod == Modifier.PREMIUM_ONLY)
				return true;
		
		return false;
	}

}