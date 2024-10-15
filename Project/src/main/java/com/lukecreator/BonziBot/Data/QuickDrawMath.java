package com.lukecreator.BonziBot.Data;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class QuickDrawMath extends QuickDraw {
	
	public String problem;
	public boolean twentyOne;
	public String answer;
	
	public QuickDrawMath(BonziBot bb) {
		this.reward = BonziUtils.randomInt(25, 50);
		
		int mode = BonziUtils.randomInt(3);
		boolean smallDigits = mode == 2; // mul
		int a = BonziUtils.randomInt(smallDigits ? 6 : 26);
		int b = BonziUtils.randomInt(smallDigits ? 6 : 26);
		
		// swap
		if(mode == 1 && b > a) {
			int t = a;
			a = b;
			b = t;
		}
		
		char p;
		switch(mode) {
		case 0:
			if(a == 9 && b == 10) {
				this.answer = "21";
				this.reward += 100;
				this.twentyOne = true;
			} else
				this.answer = String.valueOf(a + b);
			p = '+';
			break;
		case 1:
			this.answer = String.valueOf(a - b);
			p = '-';
			break;
		case 2:
			this.answer = String.valueOf(a * b);
			p = '*';
			break;
		default:
			this.answer = "wtf";
			p = '?';
			break;
		}
		
		this.problem = a + " " + p + " " + b;
	}

	@Override
	public MessageCreateAction constructMessage(TextChannel channel) {
		return channel.sendMessage("` Quick Draw! ` How fast can you math: ` " + this.problem + " `");
	}
	@Override
	public MessageCreateAction constructWinnerMessage(User winner, int coinsGained, TextChannel channel) {
		return channel.sendMessage(winner.getAsMention() + "` won the Quick Draw! The answer was " + this.answer + ". ` ` +" + coinsGained + " coins! `");
	}
	
	@Override
	public boolean tryInput(Message message) {
		String input = message.getContentStripped().trim();
		
		if(input.equals(this.answer)) {
			message.delete().queue(null, fail -> {});
			return true;
		}
		
		// failsafe in case they don't know
		if(this.twentyOne && input.equals("19")) {
			this.reward = 0;
			message.getChannel().sendMessageEmbeds(BonziUtils.failureEmbed("You stupid")).queue();
			message.delete().queue(null, fail -> {});
			return false;
		}
		
		return false;
	}
}