package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Gui.GuiPins;

public class PinsCommand extends Command {

	public PinsCommand() {
		this.subCategory = 0;
		this.name = "Pins";
		this.icon = GenericEmoji.fromEmoji("📌");
		this.description = "Check out your personal message pin board.";
		this.args = null;
		this.category = CommandCategory.UTILITIES;
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		GuiPins gui = new GuiPins(e.executor, e.getExecutorColor(), e.isGuildMessage ? e.guild.getIdLong() : 0l);
		BonziUtils.sendGui(e, gui);
	}
}