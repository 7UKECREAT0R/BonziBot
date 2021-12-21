package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.TimeSpan;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.StringRemainderArg;
import com.lukecreator.BonziBot.CommandAPI.TimeSpanArg;
import com.lukecreator.BonziBot.Data.AfkData;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class AfkCommand extends Command {
	
	public AfkCommand() {
		this.subCategory = 3;
		this.name = "AFK";
		this.unicodeIcon = "❌⌨️";
		this.description = "Set yourself as 'AFK' to let users know that you're not ghosting them!";
		this.args = new CommandArgCollection(new TimeSpanArg("length"), new StringRemainderArg("reason"));
		this.worksInDms = true;
		this.category = CommandCategory.FUN;
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		TimeSpan _length = e.args.getTimeSpan("length");
		String reason = e.args.getString("reason");
		reason = BonziUtils.cutOffString(reason, 1024);
		long length = _length.getMillis();
		
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount account = uam.getUserAccount(e.executor);
		if(account.afkData == null)
			account.afkData = new AfkData();
		account.afkData.goAfk(reason, length);
		uam.setUserAccount(e.executor, account);
		
		String name = e.executor.getName();
		MessageEmbed msg = BonziUtils.quickEmbed(name +
			" is now AFK.", reason, e.executor, Color.gray).build();
		
		if(e.isGuildMessage && e.message != null)
			e.message.delete().queue(null, ignore -> {});
		
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(msg).queue();
		else
			e.channel.sendMessageEmbeds(msg).queue();
	}
}