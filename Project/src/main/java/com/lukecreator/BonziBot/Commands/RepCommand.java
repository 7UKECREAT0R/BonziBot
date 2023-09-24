package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;

import net.dv8tion.jda.api.EmbedBuilder;

public class RepCommand extends Command {

	public RepCommand() {
		this.subCategory = 3;
		this.name = "Rep";
		this.icon = GenericEmoji.fromEmoji("🖲️");
		this.description = "Learn how to increase or decrease people's reputation!";
		this.args = null;
		this.category = CommandCategory.FUN;
		this.setCooldown(2000);
	}

	@Override
	public void run(CommandExecutionInfo e) {
		EmbedBuilder eb = BonziUtils.quickEmbed("Repping People",
			  "You can rep a user once every 24 hours.",
			  e.executor, BonziUtils.COLOR_BONZI_PURPLE);
		eb.addField("Simple", "Type `+rep` or `-rep` in chat to give/remove reputation from the last user that sent a message.", false);
		eb.addField("Specific", "Want to rep someone specific? Type `+rep <@user>` or `-rep <@user>` to do that, too!", false);
		
		String prefix = BonziUtils.getPrefixOrDefault(e);
		
		eb.addField("Reputation Use", "Your reputation shows up on your `" + prefix + "profile` and shows up as either a positive or negative number."
			+ "The higher the reputation you have, the better!", false);
		
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(eb.build()).queue();
		else
			e.channel.sendMessageEmbeds(eb.build()).queue();
	}
}
