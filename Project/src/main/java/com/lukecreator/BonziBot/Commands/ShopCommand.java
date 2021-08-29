package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.PremiumItem;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class ShopCommand extends Command {

	public ShopCommand() {
		this.subCategory = 2;
		this.name = "Shop";
		this.unicodeIcon = "🛒";
		this.description = "Buy extra fancy commands that normal users can't use!";
		this.args = null;
		this.category = CommandCategory.COINS;
		this.setCooldown(5000);
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		
		User user = e.executor;
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount account = uam.getUserAccount(user);
		boolean premium = account.isPremium;
		
		//   For testing purposes;
		// -np stands for "no premium"
		if(e.inputArgs.length > 0)
			if(e.inputArgs[0].equals("-np"))
				premium = false;
		
		PremiumItem[] items = PremiumItem.values();
		String prefix = BonziUtils.getPrefixOrDefault(e);
		EmbedBuilder eb = BonziUtils.quickEmbed(
			"BonziBot Shop", "", e.executor, Color.ORANGE);
		BonziBot bb = e.bonzi;
		
		if(premium) {
			eb.addField("👑 Premium User", "You have all the commands! Check `" + prefix + "purchases` to see all of your commands.", false);
		} else {
			for(PremiumItem item: items) {
				if(!item.enabled) continue;
				Command linked = item.getLinkedCommand(bb);
				String title = BonziUtils.titleString(item.name());
				String cost = "\n`" + BonziUtils.comma(item.price) + " coins`";
				String desc = linked.description + cost;
				String icon = linked.unicodeIcon;
				title = icon + " " + title;
				
				if(account.hasItem(item))
					title += " ✅";
				
				eb.addField(title, desc, true);
			}
			int _premiumPrice = PremiumItem.getPremiumPrice();
			String premiumPrice = BonziUtils.comma(_premiumPrice);
			eb.addBlankField(false);
			String pTitle = "👑 Premium" + (account.isPremium ? " ✅" : "");
			eb.addField(pTitle, "Get access to all the paid commands forever! Lots of other"
				+ "perks are included in the package too. `" + premiumPrice + " coins`", false);
		}
		
		eb.setFooter(premium?
			"If you put '-np' at the end of the command it will display all of the commands anyway.":
			("You can purchase any of these commands with " + prefix + "buy <item>"));
		
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(eb.build()).queue();
		else
			e.channel.sendMessageEmbeds(eb.build()).queue();
	}
}