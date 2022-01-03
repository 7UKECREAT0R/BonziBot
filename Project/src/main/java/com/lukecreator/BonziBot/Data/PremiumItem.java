package com.lukecreator.BonziBot.Data;

import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandSystem;
import com.lukecreator.BonziBot.Commands.CalculatorCommand;
import com.lukecreator.BonziBot.Commands.ExposeCommand;
import com.lukecreator.BonziBot.Commands.FontGeneratorCommand;
import com.lukecreator.BonziBot.Commands.GuildSettingsCommand;
import com.lukecreator.BonziBot.Commands.JokeCommand;
import com.lukecreator.BonziBot.Commands.LotteryCommand;
import com.lukecreator.BonziBot.Commands.NickAllCommand;
import com.lukecreator.BonziBot.Commands.ProfilePicCommand;
import com.lukecreator.BonziBot.Commands.SlotsCommand;
import com.lukecreator.BonziBot.Commands.TodoListCommand;

/**
 * Items that show up in the BonziBot shop.
 * 
 * If a command definition begins with an
 *    underscore it will be disabled.
 */
public enum PremiumItem {
	
	// classic
	NICK_ALL(		NickAllCommand.class, 		2000, 	true),
	RAINBOW_ROLE(	JokeCommand.class, 			5000, 	false), // disabled due to TOS
	SUPER_PLAY(		LotteryCommand.class, 		3000, 	false), // annoying and hard to impl
	EXPOSE(			ExposeCommand.class, 		1000,	true),
	PROFILE_PIC(	ProfilePicCommand.class,	500, 	true),
	TROLL(			SlotsCommand.class, 		10000, 	false), // not viable
	COMMENT(		GuildSettingsCommand.class, 1500, 	false), // this is dumb
	
	// newer
	CALCULATOR(		CalculatorCommand.class,	750,	true),
	FONT_GENERATOR(	FontGeneratorCommand.class,	1500,	true),
	TODO_LIST(		TodoListCommand.class,		2000,	true);
	
	public int price;
	public boolean enabled;
	Class<? extends Command> attachedCommand;
	PremiumItem(Class<? extends Command> attachedCommand, int price, boolean enabled) {
		this.attachedCommand = attachedCommand;
		this.price = price;
		this.enabled = enabled;
	}
	
	public Command getLinkedCommand(BonziBot bb) {
		return getLinkedCommand(bb.commands);
	}
	public Command getLinkedCommand(CommandSystem system) {
		
		List<Command> cmds = system.getRegisteredCommands();
		Class<? extends Command> a = this.attachedCommand;
		
		for(Command cmd: cmds) {
			Class<? extends Command> b = cmd.getClass();
			if(a.equals(b))
				return cmd;
		}
		
		return null;
	}
	
	static int cachedPremiumPrice = -1;
	public static int getPremiumPrice() {
		if(cachedPremiumPrice == -1) {
			int p = 0;
			for(PremiumItem item: values())
				if(item.enabled)
					p += item.price;
			cachedPremiumPrice = (int) (p * 0.75);
		}
		return cachedPremiumPrice;
	}
}