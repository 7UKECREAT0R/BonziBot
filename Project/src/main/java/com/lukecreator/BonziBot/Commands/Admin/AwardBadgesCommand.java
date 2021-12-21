package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.BooleanArg;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.Badge;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.SpecialPeopleManager;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

public class AwardBadgesCommand extends Command {

	public AwardBadgesCommand() {
		this.subCategory = 0;
		this.name = "Award Badges";
		this.unicodeIcon = "🏆";
		this.description = "Award a badge to all predefined users.";
		this.args = new CommandArgCollection(new BooleanArg("clear"), new BooleanArg("allbadges"));
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = true;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		SpecialPeopleManager spm = e.bonzi.special;
		UserAccountManager uam = e.bonzi.accounts;
		
		boolean clear = e.args.argSpecified("clear") ?
			e.args.getBoolean("clear") : false;
		boolean all = e.args.argSpecified("allbadges") ?
			e.args.getBoolean("allbadges") : false;
		
		for(long bro: spm.getBros()) {
			UserAccount acc = uam.getUserAccount(bro);
			if(clear) acc.clearBadges();
			acc.awardBadge(Badge.FRIEND);
			uam.setUserAccount(bro, acc);
		}
		for(long admin: spm.getAdmins()) {
			UserAccount acc = uam.getUserAccount(admin);
			if(clear) acc.clearBadges();
			acc.awardBadge(Badge.DEVELOPER);
			if(all) {
				acc.awardBadge(Badge.ACHIEVEMENT_MASTER);
				acc.awardBadge(Badge.BUG_HUNTER);
				acc.awardBadge(Badge.CREATIVE);
				acc.awardBadge(Badge.FRIEND);
				acc.awardBadge(Badge.LOTTERY_WINNER);
			}
			uam.setUserAccount(admin, acc);
		}
		
		e.channel.sendMessage("All friends and developers now have their badges!").queue();
	}
}