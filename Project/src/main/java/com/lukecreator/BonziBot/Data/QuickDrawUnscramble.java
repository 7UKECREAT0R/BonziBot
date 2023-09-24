package com.lukecreator.BonziBot.Data;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class QuickDrawUnscramble extends QuickDraw {

	String word;
	String scrambled;
	
	QuickDrawUnscramble(BonziBot bb) {
		this.word = bb.strings.getWord();
		this.reward = this.word.length() * 5 + 30;
		this.scrambled = BonziUtils.scramble(this.word, 1);
	}
	
	@Override
	public MessageCreateAction constructMessage(TextChannel channel) {
		return channel.sendMessage("` Quick Draw! ` Unscramble this word: ` " + this.scrambled + " `");
	}
	@Override
	public MessageCreateAction constructWinnerMessage(User winner, int coinsGained, TextChannel channel) {
		return channel.sendMessage(winner.getAsMention() + "` won the Quick Draw! The word was \"" + this.word + ".\" ` ` +" + coinsGained + " coins! `");
	}
	
	@Override
	public boolean tryInput(Message message) {
		if(message.getContentStripped().equalsIgnoreCase(this.word)) {
			message.delete().queue(null, fail -> {});
			return true;
		}
		return false;
	}
}