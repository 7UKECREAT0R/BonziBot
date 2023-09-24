package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.PremiumItem;
import com.lukecreator.BonziBot.Gui.GuiCalculator;

public class CalculatorCommand extends Command {

	public CalculatorCommand() {
		this.subCategory = 0;
		this.name = "Calculator";
		this.icon = GenericEmoji.fromEmoji("🔢");
		this.description = "An interactive calculator! (optionally multiplayer)";
		this.args = null;
		this.category = CommandCategory._SHOP_COMMAND;
		this.setPremiumItem(PremiumItem.CALCULATOR);
	}

	@Override
	public void run(CommandExecutionInfo e) {
		GuiCalculator gui = new GuiCalculator(e.isGuildMessage);
		BonziUtils.sendGui(e, gui);
	}
}