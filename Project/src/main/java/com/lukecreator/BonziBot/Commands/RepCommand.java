package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

import net.dv8tion.jda.api.EmbedBuilder;

public class RepCommand extends Command {

	public RepCommand() {
		this.subCategory = 3;
		this.name = "Rep";
		this.unicodeIcon = "🖲️";
		this.description = "Learn how to increase or decrease people's reputation!";
		this.args = null;
		this.category = CommandCategory.FUN;
		this.setCooldown(2000);
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		EmbedBuilder eb = BonziUtils.quickEmbed("Repping People",
			  "You can rep a user once every 24 hours.\n"
			+ "Type `+rep` or `-rep` to affect the user who last sent a message.\n"
			+ "Alternatively, `+rep <@user>` and `-rep <@user>` also works.",
			e.executor, BonziUtils.COLOR_BONZI_PURPLE);
		String prefix = BonziUtils.getPrefixOrDefault(e);
		eb.addField("Reputation Use", "Your reputation shows up on your `" + prefix + "profile` and shows up as either a positive or negative number."
			+ "The higher the reputation you have, the better!", false);
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(eb.build()).queue();
		else
			e.channel.sendMessageEmbeds(eb.build()).queue();
	}
}
