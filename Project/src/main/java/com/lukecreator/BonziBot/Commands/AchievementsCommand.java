package com.lukecreator.BonziBot.Commands;

import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.Achievement;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class AchievementsCommand extends Command {
	
	public AchievementsCommand() {
		this.subCategory = 3;
		this.name = "Achievements";
		this.unicodeIcon = "🔓";
		this.description = "Show yours or someone else's achievements!";
		this.args = new CommandArgCollection(new UserArg("target").optional());
		this.category = CommandCategory.FUN;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		
		boolean specified = e.args.argSpecified("target");
		User target = specified ? e.args.getUser("target") : e.executor;
		
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount account = uam.getUserAccount(target);
		List<Achievement> achievements = account.getAchievements();
		
		String title = target.getName() + "'s Achievements";
		String desc = achievements.isEmpty() ? "No achievements yet!" : "";
		EmbedBuilder eb = BonziUtils.quickEmbed(title,
			desc, target, BonziUtils.COLOR_BONZI_PURPLE);
		for(Achievement a: achievements)
			eb.addField(a.icon.toString() + " " + a.name, a.desc, true);
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(eb.build()).queue();
		else
			e.channel.sendMessage(eb.build()).queue();
	}
}