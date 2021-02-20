package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.EnumArg;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.PremiumItem;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class BuyCommand extends Command {

	public BuyCommand() {
		this.subCategory = 2;
		this.name = "Buy";
		this.unicodeIcon = "💸";
		this.description = "Buy an item from the BonziBot shop for yourself, or gift it to someone else!";
		this.args = CommandArgCollection.single("item").withUsageOverride("<item> [@gift receiver]");
		this.category = CommandCategory.COINS;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		
		String itemName = e.args.getString("item");
		EnumArg parser = new EnumArg("", PremiumItem.class);
		UserArg userParser = new UserArg("");
		String lastArg = e.inputArgs[e.inputArgs.length-1];
		boolean gift = userParser.isWordParsable(lastArg);
		
		String prefix = BonziUtils.getPrefixOrDefault(e);
		String redirToShopT = "Not a valid item in the BonziBot shop!";
		String redirToShopD = "Use `" + prefix + "shop` to see all the available items for purchase!";
		
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount account = uam.getUserAccount(e.executor);
		
		if(gift) {
			int len = lastArg.length() + 1;
			itemName = itemName.substring(0, itemName.length() - len);
			userParser.parseWord(lastArg, e.bot, e.executor, null);
		}
		User giftReceiver = gift ? (User)userParser.object : null;
		boolean validReceiver = giftReceiver != null;
		long giftId = validReceiver ? giftReceiver.getIdLong() : 0l;
		UserAccount giftAccount = validReceiver ? uam.getUserAccount(giftReceiver) : null;
		
		if(parser.isWordParsable(itemName)) {
			parser.parseWord(itemName, e.bot, e.executor, null);
			PremiumItem item = (PremiumItem)parser.object;
			
			if(!item.enabled) {
				MessageEmbed me = BonziUtils.failureEmbed
					("This item is disabled right now.");
				e.channel.sendMessage(me).queue();
				return;
			}
			
			int cost = item.price;
			int bal = account.getCoins();
			
			if(cost > bal) {
				String sCost = BonziUtils.comma(cost);
				String sBal = BonziUtils.comma(bal);
				MessageEmbed me = BonziUtils.failureEmbed(
					"You can't afford that item yet!",
					"Needed `" + sCost + "` coins, balance is `" + sBal + "`.");
				e.channel.sendMessage(me).queue();
				return;
			}
			
			if(!gift && (account.isPremium || account.hasItem(item))) {
				MessageEmbed me = BonziUtils.failureEmbed(
					"You already have that item!",
					"You can mention someone at the end of the command to gift them this item.");
				e.channel.sendMessage(me).queue();
				return;
			}
			
			if(gift && (giftAccount.isPremium || giftAccount.hasItem(item))) {
				MessageEmbed me = BonziUtils.failureEmbed(
					giftReceiver.getName() + " already has that item!");
				e.channel.sendMessage(me).queue();
				return;
			}
			
			EventWaiterManager waiter = e.bonzi.eventWaiter;
			String itemDisplay = BonziUtils.titleString(item.name());
			String title = gift ?
				"Gifting " + itemDisplay + " to " + giftReceiver.getAsTag() + "...":
				"Buying " + itemDisplay + "...";
			waiter.getConfirmation(e.executor, e.channel, title, confirm -> {
				if(!confirm) {
					MessageEmbed me = BonziUtils.failureEmbedIncomplete
						("Cancelled purchase.").setColor(Color.orange).build();
					e.channel.sendMessage(me).queue();
					return;
				}
				if(gift) {
					account.subCoins(cost);
					giftAccount.items.add(item);
					uam.setUserAccount(e.executor, account);
					uam.setUserAccount(giftId, giftAccount);
					sendGiftSuccess(e.channel, giftReceiver.getName());
					sendGiftDM(e.executor, giftReceiver, itemDisplay);
					return;
				}
				account.subCoins(cost);
				account.items.add(item);
				uam.setUserAccount(e.executor, account);
				
				MessageEmbed me = BonziUtils.quickEmbed("You've successfully bought " + itemDisplay + "!",
					"Check out `" + prefix + "purchases` to check out the command.", Color.magenta).build();
				e.channel.sendMessage(me).queue();
				return;
			});
		} else {
			itemName = itemName.toUpperCase();
			if(!itemName.contains("PREMIUM")) {
				MessageEmbed me = BonziUtils.failureEmbed(redirToShopT, redirToShopD);
				e.channel.sendMessage(me).queue();
				return;
			}
			
			// yeah they want premium
			int cost = PremiumItem.getPremiumPrice();
			int bal = account.getCoins();
			
			if(cost > bal) {
				String sCost = BonziUtils.comma(cost);
				String sBal = BonziUtils.comma(bal);
				MessageEmbed me = BonziUtils.failureEmbed(
					"You can't afford Premium yet!",
					"Needed `" + sCost + "` coins, balance is `" + sBal + "`.");
				e.channel.sendMessage(me).queue();
				return;
			}
			
			if(!gift && account.isPremium) {
				MessageEmbed me = BonziUtils.failureEmbed("You already have Premium!");
				e.channel.sendMessage(me).queue();
				return;
			}
			
			if(gift && giftAccount.isPremium) {
				MessageEmbed me = BonziUtils.failureEmbed("That user already has Premium on their account!");
				e.channel.sendMessage(me).queue();
				return;
			}
			
			EventWaiterManager waiter = e.bonzi.eventWaiter;
			String title = gift ?
				"Gifting BonziBot Premium to " + giftReceiver.getAsTag() + "...":
				"Buying BonziBot Premium...";
			waiter.getConfirmation(e.executor, e.channel, title, confirm -> {
				if(!confirm) {
					MessageEmbed me = BonziUtils.failureEmbedIncomplete
						("Cancelled purchase.").setColor(Color.orange).build();
					e.channel.sendMessage(me).queue();
					return;
				}
				if(gift) {
					account.subCoins(cost);
					giftAccount.isPremium = true;
					uam.setUserAccount(e.executor, account);
					uam.setUserAccount(giftId, giftAccount);
					sendGiftSuccess(e.channel, giftReceiver.getName());
					sendGiftDM(e.executor, giftReceiver, "BonziBot Premium 👑");
					return;
				}
				account.subCoins(cost);
				account.isPremium = true;
				uam.setUserAccount(e.executor, account);
				
				MessageEmbed me = BonziUtils.quickEmbed("You've successfully bought BonziBot Premium 👑!",
					"Check out `" + prefix + "purchases` to check out all your fancy new commands!", Color.magenta).build();
				e.channel.sendMessage(me).queue();
				return;
			});
		}
	}
	
	void sendGiftSuccess(MessageChannel channel, String receiverName) {
		MessageEmbed me = BonziUtils.successEmbed
				("Success! Your gift to " + receiverName + " is on its way!");
		channel.sendMessage(me).queue();
	}
	void sendGiftDM(User sender, User user, String item) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("You've been bought a gift!")
			.setAuthor(sender.getName(), null, sender.getEffectiveAvatarUrl())
			.setDescription(sender.getName() + " has gifted you " + item + "!")
			.setColor(Color.magenta);
		BonziUtils.messageUser(user, eb.build());
	}
}