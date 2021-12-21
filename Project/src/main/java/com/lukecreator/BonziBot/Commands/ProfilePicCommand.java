package com.lukecreator.BonziBot.Commands;

import java.time.LocalDate;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.PremiumItem;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class ProfilePicCommand extends Command {

	public ProfilePicCommand() {
		this.subCategory = 0;
		this.name = "Profile Pic";
		this.unicodeIcon = "🖼️";
		this.description = "Get your own or somebody else's profile picture!";
		this.args = new CommandArgCollection(new UserArg("target").optional());
		this.category = CommandCategory._SHOP_COMMAND;
		this.setPremiumItem(PremiumItem.PROFILE_PIC);
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		
		User u = e.args.argSpecified("target") ?
			e.args.getUser("target") : e.executor;
		String pfp = u.getEffectiveAvatarUrl() + "?size=512";
		
		EmbedBuilder eb = BonziUtils.quickEmbed
			("Profile Picture",
			u.getName() + "'s profile picture on " + LocalDate.now()
			.format(BonziUtils.MMddyy), BonziUtils.COLOR_BONZI_PURPLE)
			.setImage(pfp);
		
		if(e.isSlashCommand) {
			e.slashCommand.replyEmbeds(eb.build()).queue();
		} else {
			e.channel.sendMessageEmbeds(eb.build()).queue();
		}
	}
}