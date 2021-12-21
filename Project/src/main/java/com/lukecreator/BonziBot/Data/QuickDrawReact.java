package com.lukecreator.BonziBot.Data;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class QuickDrawReact extends QuickDraw {
	
	QuickDrawReact(BonziBot bb) {
		this.reward = BonziUtils.randomInt(10) + 25;
	}
	
	@Override
	public MessageAction constructMessage(TextChannel channel) {
		return channel.sendMessage("`Quick Draw!` React to this message!");
	}
	@Override
	public MessageAction constructWinnerMessage(User winner, int coinsGained, TextChannel channel) {
		return channel.sendMessage(winner.getAsMention() + "` won the Quick Draw!` `+" + coinsGained + " coins!`");
	}
	
	@Override
	public boolean tryInput(ReactionEmote reaction) {
		return true;
	}
}