package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class XpCommand extends Command {
	
	public XpCommand() {
		this.subCategory = 2;
		this.name = "XP";
		this.unicodeIcon = "🎓";
		this.description = "View your xp or someone else's xp.";
		this.args = new CommandArgCollection(new UserArg("target").optional());
		this.category = CommandCategory.FUN;
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		User target = e.args.argSpecified("target") ?
			e.args.getUser("target") : e.executor;
		
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount acc = uam.getUserAccount(target);
		
		String title = target.getName();
		
		EmbedBuilder eb;
		if(e.isGuildMessage) {
			Member m = e.guild.getMember(target);
			eb = BonziUtils.quickEmbed(title, "", m);
		} else {
			eb = BonziUtils.quickEmbed(title, "", target);
		}
		
		String xp = BonziUtils.comma(acc.getXP());
		String level = BonziUtils.comma(acc.getLevel());
		eb.addField("XP:", String.valueOf(xp), true);
		eb.addField("Level:", String.valueOf(level), true);
		e.channel.sendMessage(eb.build()).queue();
	}
	
}
