package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.*;
import com.lukecreator.BonziBot.Tuple;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.LotteryManager;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class LotteryCommand extends Command {

    public LotteryCommand() {
        this.subCategory = 1;
        this.name = "Lottery";
        this.icon = GenericEmoji.fromEmoji("🎟️");
        this.description = "Pay " + LotteryManager.S_TICKET_COST + " coins for a chance to win the whole lot!";
        this.category = CommandCategory.COINS;
        this.args = null;
    }

    @Override
    public void run(CommandExecutionInfo e) {
        User u = e.executor;
        UserAccountManager uam = e.bonzi.accounts;
        UserAccount account = uam.getUserAccount(u);
        long currentCoins = account.getCoins();
        long cost = LotteryManager.TICKET_COST;
        String costString = BonziUtils.comma(cost);

        if (currentCoins < cost) {
            e.reply(BonziUtils.failureEmbed("You need " + costString + " coins to buy a lottery ticket!", "You currently have " + BonziUtils.comma(currentCoins) + " coins."));
            return;
        }

        Tuple<Boolean, Integer> output = e.bonzi.lottery.doLottery(u, e.bonzi, 1);
        boolean win = output.getA();
        int winnings = output.getB();
        String oldCoins = BonziUtils.comma(currentCoins);
        currentCoins += winnings;
        String newCoins = BonziUtils.comma(currentCoins);
        String winningsString = BonziUtils.comma(winnings);

        if (win) {
            EmbedBuilder eb = BonziUtils.quickEmbed("💰 IT'S A WINNER! 💰", "You just won " +
                    winningsString + " coins!\nYou went from " + oldCoins + " to " + newCoins + " coins!", Color.yellow);
            e.reply(eb.build());
        } else {
            MessageEmbed me = BonziUtils.failureEmbed("You didn't win this time...  " + winningsString + " coins.",
                    "The lottery now has " + BonziUtils.comma(e.bonzi.lottery.getLottery()) + " coins in it!");
            e.reply(me);
        }
    }
}