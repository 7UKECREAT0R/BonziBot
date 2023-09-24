package com.lukecreator.BonziBot.Commands;

import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.Achievement;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.PremiumItem;
import com.lukecreator.BonziBot.Managers.LoggingManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.User;

public class ExposeCommand extends Command {

	public ExposeCommand() {
		this.subCategory = 0;
		this.name = "Expose";
		this.icon = GenericEmoji.fromEmoji("😮");
		this.description = "Expose the last deleted message!";
		this.args = null;
		this.category = CommandCategory._SHOP_COMMAND;
		this.worksInDms = false;
		this.setPremiumItem(PremiumItem.EXPOSE);
	}

	@Override
	public void run(CommandExecutionInfo e) {
		LoggingManager lm = e.bonzi.logging;
		Message message = lm.getExposeData(e.guild);
		
		if(message == null) {
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("There's nothing to expose yet!")).queue();
			else
				e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("There's nothing to expose yet!")).queue();
			return;
		}
		
		List<Attachment> files = message.getAttachments();
		if(!files.isEmpty() && message.getContentRaw().isEmpty()) {
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(BonziUtils.failureEmbed(
						"unfortunately the solar powered monkey men at discord hq said i cant expose messages with attachments in them...",
						"their words, not mine!")).queue();
			else e.channel.sendMessageEmbeds(BonziUtils.failureEmbed(
						"unfortunately the solar powered monkey men at discord hq said i cant expose messages with attachments in them...",
						"their words, not mine!")).queue();
			return;
		}
		
		User author = message.getAuthor();
		
		EmbedBuilder eb = BonziUtils.quickEmbed
			("Exposed Message", message.getContentRaw(), author, BonziUtils.COLOR_BONZI_PURPLE);
		if(!files.isEmpty()) {
			int c = files.size();
			String last = BonziUtils.plural("attachment", c);
			eb.setFooter("With " + c + " " + last + ".");
		}
		BonziUtils.tryAwardAchievement(e.channel, e.bonzi, e.executor, Achievement.SPY);
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(eb.build()).queue();
		else e.channel.sendMessageEmbeds(eb.build()).queue();
		return;
	}
}