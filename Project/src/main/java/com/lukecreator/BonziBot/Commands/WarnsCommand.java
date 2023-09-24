package com.lukecreator.BonziBot.Commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.ModernWarn;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Gui.GuiWarns;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class WarnsCommand extends Command {

	public WarnsCommand() {
		this.subCategory = 1;
		this.name = "Warns";
		this.icon = GenericEmoji.fromEmoji("📜");
		this.description = "View and manage a user's warns.";
		this.args = new CommandArgCollection(new UserArg("target"));
		this.setCooldown(5000);
		this.userRequiredPermissions = new Permission[] { Permission.MESSAGE_MANAGE };
		this.worksInDms = false;
		this.category = CommandCategory.MODERATION;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		User target = e.args.getUser("target");
		Member targetMember = e.guild.getMember(target);
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount account = uam.getUserAccount(target);
		ModernWarn[] warns = account.getWarns(e.guild);
		
		if(warns.length < 1) {
			MessageEmbed send = BonziUtils.quickEmbed("Came up clean!", "User doesn't have any warns on record.", Color.orange).build();
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(send).queue();
			else
				e.channel.sendMessageEmbeds(send).queue();
			return;
		}
		
		List<ModernWarn> list = new ArrayList<ModernWarn>(warns.length);
		for(int i = 0; i < warns.length; i++)
			list.add(warns[i]);
		GuiWarns gui = new GuiWarns(list, targetMember, e.guild);
		BonziUtils.sendGui(e, gui);
	}
}