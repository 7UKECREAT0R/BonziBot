package com.lukecreator.BonziBot.Commands;

import java.util.List;
import java.util.stream.Collectors;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.CommandAPI.UserArg;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ClearCommand extends Command {

	public ClearCommand() {
		this.subCategory = 0;
		this.name = "Clear";
		this.unicodeIcon = "💢";
		this.description = "Bulk delete a set of messages. You can also specify a user to limit the clear to.";
		this.args = new CommandArgCollection(new IntArg("amount"), new UserArg("limit").optional());
		this.worksInDms = false;
		this.setCooldown(5000);
		this.neededPermissions = new Permission[] { Permission.MESSAGE_MANAGE };
		this.userRequiredPermissions = new Permission[] { Permission.MESSAGE_MANAGE };
		this.category = CommandCategory.MODERATION;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		int amount = e.args.getInt("amount");
		boolean limit = e.args.argSpecified("limit");
		long limitUser = limit ? e.args.getUser("limit").getIdLong() : 0l;
		TextChannel channel = e.tChannel;
		long messageId = channel.getLatestMessageIdLong();
		
		if(amount < 0) {
			e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("you cant bring messages back 😔")).queue();
			return;
		}
		if(amount > 100) {
			e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("Max limit is 100.")).queue();
			return;
		}
		
		channel.getHistoryBefore(messageId, amount).queue(history -> {
			e.message.delete().queue();
			List<Message> msgs = history.getRetrievedHistory();
			if(limit) {
				msgs = msgs
					.stream()
					.filter(m -> m.getAuthor().getIdLong() == limitUser)
					.collect(Collectors.toList());
			}

			BonziUtils.sendTempMessage(e.channel, BonziUtils.successEmbed("Clearing " + amount + " messages..."), 3);
			channel.purgeMessages(msgs);
		});
	}
}