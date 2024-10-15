package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.*;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Managers.RewardManager;

public class ManageDailyCommand extends Command {
	public enum DailySubcommand {
		GRACE,
		ADD_TO_ALL
	}
    public ManageDailyCommand() {
        this.subCategory = 1;
        this.name = "ManageDaily";
        this.icon = GenericEmoji.fromEmoji("�");
        this.description = "Manage different aspects of people's daily rewards in dire circumstances.";
        this.args = new CommandArgCollection(
				new EnumArg("subcommand", DailySubcommand.class),
				new IntArg("amount").optional()
		);
        this.category = CommandCategory._HIDDEN;
        this.adminOnly = true;
    }

    @Override
    public void run(CommandExecutionInfo e) {
		DailySubcommand subcommand = (DailySubcommand)e.args.get("subcommand");
		int amount = e.args.argSpecified("amount") ? (int)e.args.getInt("amount") : 0;

        var accounts = e.bonzi.accounts.getAccounts().keySet();
		RewardManager rm = e.bonzi.rewards;
		long now = System.currentTimeMillis();

		switch (subcommand) {
            case GRACE:
				// iterate over all users, and give them grace on their daily rewards for the next 24 hours.
				for (var entry: accounts) {
					rm.setLastCollectionTime(entry, now - BonziUtils.getMsForHours(24));
				}
				e.reply("Given 24 hours of grace to " + accounts.size() + " users.");
                break;
            case ADD_TO_ALL:
				// add to all daily rewards
				for (var entry: accounts) {
					int streak = rm.getStreak(entry);
					rm.setStreak(entry, streak + amount);
				}
				e.reply("Added " + amount + " to " + accounts.size() + " users' daily rewards.");
                break;
        }
    }
}