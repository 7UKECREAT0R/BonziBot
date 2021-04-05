package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;

import net.dv8tion.jda.api.EmbedBuilder;

public class PollCommand extends Command {
	
	public static final String G_EMBED_TITLE = "Community Poll:";
	public static final String P_EMBED_TITLE = "Private Poll...?:";
	public static String generateFooter(int up, int down) {
		return "✅ " + up + " | ❌ " + down;
	}
	
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
		if(e.isGuildMessage)
			eb = BonziUtils.quickEmbed(G_EMBED_TITLE, poll);
		else
			eb = BonziUtils.quickEmbed(P_EMBED_TITLE, poll);
		
		eb.setColor(Color.gray);
		eb.setFooter(generateFooter(0, 0));
		
		e.channel.sendMessage(eb.build()).queue(msg -> {
			msg.addReaction("👍").queue();
			msg.addReaction("👎").queue();
		});
	}
}
