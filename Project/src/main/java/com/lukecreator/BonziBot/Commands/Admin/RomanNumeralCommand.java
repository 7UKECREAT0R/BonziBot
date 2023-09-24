package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;

public class RomanNumeralCommand extends Command {

	public RomanNumeralCommand() {
		this.subCategory = 0;
		this.name = "romannumeral";
		this.icon = GenericEmoji.fromEmoji("🐫");
		this.description = "convert an integer to roman numerals";
		this.args = new CommandArgCollection(new IntArg("input"));
		this.category = CommandCategory._HIDDEN;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		int input = e.args.getInt("input");
		String roman = BonziUtils.numeral(input);
		e.channel.sendMessage("`" + input + "` -> `" + roman + "`").queue();
	}
}