package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class PollCommand extends Command {
	
	public PollCommand() {
		this.subCategory = 0;
		
		this.name = "Poll";
		this.description = "Make a poll people can vote on!";
		this.args = CommandArgCollection.single("question");
		this.category = CommandCategory.FUN;
		this.unicodeIcon = "📑";
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		String poll = e.args.getString("question");
		EmbedBuilder eb;
		if(e.isGuildMessage) {
			Member m = e.member;
			eb = BonziUtils.quickEmbed("Community Poll:", poll, m);
		} else {
			User u = e.executor;
			eb = BonziUtils.quickEmbed("Community Poll:", poll, u);
		}
		
		e.channel.sendMessage(eb.build()).queue(msg -> {
			msg.addReaction("👍").queue();
			msg.addReaction("👎").queue();
		});
	}
}
