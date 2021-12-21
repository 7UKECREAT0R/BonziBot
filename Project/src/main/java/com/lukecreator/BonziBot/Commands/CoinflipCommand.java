package com.lukecreator.BonziBot.Commands;

import java.util.Random;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.NoUpload.Constants;

public class CoinflipCommand extends Command {
	
	Random rand = new Random();
	
	public CoinflipCommand() {
		this.subCategory = 0;
		
		this.name = "Coin Flip";
		this.description = "Literally just flip a coin.";
		this.category = CommandCategory.FUN;
		this.unicodeIcon = "🌑";
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		int side = rand.nextInt(2);
		String msg = (side == 0) ?
			Constants.COINFLIP_H:
			Constants.COINFLIP_T;
		if(e.isSlashCommand)
			e.slashCommand.reply(msg).queue();
		else
			e.channel.sendMessage(msg).queue();
	}
}