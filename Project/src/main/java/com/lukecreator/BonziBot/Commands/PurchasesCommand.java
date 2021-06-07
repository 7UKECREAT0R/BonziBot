package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.PremiumItem;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class PurchasesCommand extends Command {

	public PurchasesCommand() {
		this.subCategory = 2;
		this.name = "Purchases";
		this.unicodeIcon = "📦";
		this.description = "See all your purchased/owned commands.";
		this.args = null;
		this.category = CommandCategory.COINS;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount account = uam.getUserAccount(e.executor);
		
		// Will always populate with enabled items only.
		PremiumItem[] items = account.getOwnedItems();
		String prefix = BonziUtils.getPrefixOrDefault(e);
		
		EmbedBuilder eb;
		if(items.length > 0) {
			if(e.isGuildMessage)
				eb = BonziUtils.quickEmbed("Owned Commands", "Here's all the commands you own on your account!", e.member);
			else eb = BonziUtils.quickEmbed("Owned Commands", "Here's all the commands you own on your account!", e.executor, Color.orange);
		} else {
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(BonziUtils.failureEmbed(
					"You don't have any commands yet on your account!",
					"Use `" + prefix + "shop` to take a look at some of the things you can buy.")).queue();
			else
				e.channel.sendMessage(BonziUtils.failureEmbed(
					"You don't have any commands yet on your account!",
					"Use `" + prefix + "shop` to take a look at some of the things you can buy.")).queue();
			return;
		}
		
		for(PremiumItem item: items) {
			Command linked = item.getLinkedCommand(e.bonzi);
			String title = BonziUtils.titleString(item.name());
			CommandArgCollection args = linked.args;
			String desc;
			if(args != null) {
				String[] usagesArray = args.buildUsage
					(prefix, linked.getFilteredCommandName());
				String usage = String.join("\n", usagesArray);
				desc = usage + "\n" + linked.description;
			} else
				desc = "`" + prefix + linked.getFilteredCommandName() + "`\n" + linked.description;
			
			String icon = linked.unicodeIcon;
			title = icon + " " + title;
			eb.addField(title, desc, false);
		}
		
		MessageEmbed me = eb.build();
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(me).queue();
		else
			e.channel.sendMessage(me).queue();
		return;
	}
}