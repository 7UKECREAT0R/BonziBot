package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class CookieClickerCommand extends Command {

	public CookieClickerCommand() {
		this.subCategory = 0;
		this.name = "Cookie Clicker";
		this.icon = GenericEmoji.fromEmoji("🍪");
		this.description = "clig";
		this.args = CommandArgCollection.single("text");
		this.category = CommandCategory._HIDDEN;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		String text = e.args.getString("text");
		ActionRow row = ActionRow.of(Button.primary("_clicker", text).withEmoji(Emoji.fromUnicode("🍪")));
		e.channel.sendMessageComponents(row).setContent("`Cookie Clicker!` 0").queue();
	}
}