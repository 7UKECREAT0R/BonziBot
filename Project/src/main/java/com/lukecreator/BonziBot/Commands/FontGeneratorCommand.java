package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.PremiumItem;
import com.lukecreator.BonziBot.Gui.GuiFontGenerator;

public class FontGeneratorCommand extends Command {

	public FontGeneratorCommand() {
		this.subCategory = 0;
		this.name = "Font Generator";
		this.icon = GenericEmoji.fromEmoji("🖋️");
		this.description = "Convert your text to tons of different unicode fonts!";
		this.args = CommandArgCollection.single("text");
		this.category = CommandCategory._SHOP_COMMAND;
		this.setPremiumItem(PremiumItem.FONT_GENERATOR);
	}

	@Override
	public void run(CommandExecutionInfo e) {
		String text = e.args.getString("text");
		GuiFontGenerator gui = new GuiFontGenerator(text);
		BonziUtils.sendGui(e, gui);
	}
}