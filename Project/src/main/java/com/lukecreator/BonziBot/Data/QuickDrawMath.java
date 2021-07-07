package com.lukecreator.BonziBot.Data;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class QuickDrawMath extends QuickDraw {
	
	public String problem;
	public String answer;
	
	public QuickDrawMath(BonziBot bb) {
		this.reward = 25 + BonziUtils.randomInt(50);
		
		int mode = BonziUtils.randomInt(3);
		boolean smallDigits = mode > 1; // mul
		int a = BonziUtils.randomInt(smallDigits ? 11 : 101);
		int b = BonziUtils.randomInt(smallDigits ? 11 : 101);
		
		char p;
		switch(mode) {
		case 0:
			this.answer = String.valueOf(a + b);
			p = '+';
			break;
		case 1:
			this.answer = String.valueOf(
				(a < b) ? (b - a) : (a - b));
			p = '-';
			break;
		case 2:
			this.answer = String.valueOf(a * b);
			p = '*';
			break;
		default:
			this.answer = "-1";
			p = '?';
			break;
		}
		
		if(a < b && mode == 1)
			this.problem = b + " " + p + " " + a;
		else
			this.problem = a + " " + p + " " + b;
	}

	@Override
	public MessageAction constructMessage(TextChannel channel) {
		return channel.sendMessage("`Quick Draw!` Answer this: `" + this.problem + "`");
	}
	
	@Override
	public boolean tryInput(Message message) {
		String input = message.getContentStripped().trim();
		if(input.equals(this.answer)) {
			message.delete().queue(null, fail -> {});
			return true;
		}
		return false;
	}
}