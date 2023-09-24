package com.lukecreator.BonziBot.Data;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class QuickDrawReact extends QuickDraw {
	
	QuickDrawReact(BonziBot bb) {
		this.reward = BonziUtils.randomInt(10, 35);
	}
	
	@Override
	public MessageCreateAction constructMessage(TextChannel channel) {
		return channel.sendMessage("`Quick Draw!` React to this message!");
	}
	@Override
	public MessageCreateAction constructWinnerMessage(User winner, int coinsGained, TextChannel channel) {
		return channel.sendMessage(winner.getAsMention() + "` won the Quick Draw! ` ` +" + coinsGained + " coins! `");
	}
	
	@Override
	public boolean tryInput(EmojiUnion reaction) {
		return true;
	}
}