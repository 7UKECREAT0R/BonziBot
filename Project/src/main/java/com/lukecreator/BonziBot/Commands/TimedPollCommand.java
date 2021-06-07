package com.lukecreator.BonziBot.Commands;

import java.awt.Color;
import java.time.Instant;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.TimeSpan;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.StringRemainderArg;
import com.lukecreator.BonziBot.CommandAPI.TimeSpanArg;

import net.dv8tion.jda.api.EmbedBuilder;

/**
 *    leeches off of the other poll command's
 * methods and stuff but has a timespan argument
 * 
 * @author Lukec
 *
 */
public class TimedPollCommand extends Command {
	
	public static final String POLL_ENDED = "🔒 Poll Ended! 🔒";
	
	public TimedPollCommand() {
		this.subCategory = 0;
		this.name = "Timed Poll";
		this.unicodeIcon = "⏰";
		this.description = "Similar to the poll command, but ends at a certain time.";
		this.args = new CommandArgCollection(new TimeSpanArg("length"), new StringRemainderArg("question"));
		this.category = CommandCategory.FUN;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		TimeSpan time = e.args.getTimeSpan("length");
		String poll = e.args.getString("question");
		
		EmbedBuilder eb;
		if(e.isGuildMessage)
			eb = BonziUtils.quickEmbed(PollCommand.G_EMBED_TITLE, poll);
		else
			eb = BonziUtils.quickEmbed(PollCommand.P_EMBED_TITLE, poll);
		
		eb.setColor(Color.gray);
		eb.setFooter(PollCommand.generateFooter(0, 0));
		eb.setTimestamp(Instant.now().plusMillis(time.ms));
		
		if(e.isSlashCommand)
			e.slashCommand.reply(":white_check_mark: `Created poll for " + BonziUtils.getLongTimeStringMs(time.ms) + "!`").setEphemeral(true).queue();
		
		e.channel.sendMessage(eb.build()).queue(msg -> {
			msg.addReaction("👍").queue();
			msg.addReaction("👎").queue();
		});
	}
}