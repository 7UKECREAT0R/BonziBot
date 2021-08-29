package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.StringRemainderArg;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.ModernWarn;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class WarnCommand extends Command {

	public WarnCommand() {
		this.subCategory = 1;
		this.name = "Warn";
		this.unicodeIcon = "⚠️";
		this.description = "Place a warning on a user that can be recalled later.";
		this.args = new CommandArgCollection(new UserArg("target"), new StringRemainderArg("text"));
		this.setCooldown(5000);
		this.userRequiredPermissions = new Permission[] { Permission.MESSAGE_MANAGE };
		this.worksInDms = false;
		this.category = CommandCategory.MODERATION;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		User target = e.args.getUser("target");
		String reason = e.args.getString("text");
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount account = uam.getUserAccount(target);
		
		ModernWarn[] warns = account.getWarns(e.guild);
		int wm = warns.length;
		
		if(wm >= 64) {
			MessageEmbed send = BonziUtils.quickEmbed("that's a lot of warns...", "Max limit is 64 warns per user.", Color.red).build();
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(send).queue();
			else
				e.channel.sendMessageEmbeds(send).queue();
			return;
		}
		
		account.addWarn(new ModernWarn(reason, e.guild));
		uam.setUserAccount(target, account);
		
		MessageEmbed send = BonziUtils.successEmbedIncomplete("User has been warned ⚠️", "Reason:\n```" + reason
			+ "```").setFooter("User has " + (wm + 1) + ' ' + BonziUtils.plural("warn", wm + 1) + " on record.").build();
		
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(send).queue();
		else
			e.channel.sendMessageEmbeds(send).queue();
	}
}