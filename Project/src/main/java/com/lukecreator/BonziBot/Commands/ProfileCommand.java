package com.lukecreator.BonziBot.Commands;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Gui.GuiProfile;

import net.dv8tion.jda.api.entities.User;

public class ProfileCommand extends Command {
	
	static final Color BG_COLOR = new Color(40, 40, 40);
	static final Color BIO_COLOR = new Color(30, 30, 30);
	
	static final int IMG_WIDTH = 360;
	static final int IMG_HEIGHT = 480;
	static final int BIO_HEIGHT = 220;
	static final int PFP_SIZE = 120;
	static final int PADDING = 12;
	static final int ROUNDING = 16;
	
	static final int NAME_FONT_SIZE = 28;
	static final int BADGE_FONT_SIZE = 18;
	static final int BIO_FONT_SIZE = 18;
	static final int INFO_FONT_SIZE = 24;
	
	public ProfileCommand() {
		this.subCategory = 3;
		this.name = "Profile";
		this.icon = GenericEmoji.fromEmoji("📔");
		this.description = "View yours or someone else's profile. Completely customizable!";
		this.args = new CommandArgCollection(new UserArg("target").optional());
		this.category = CommandCategory.FUN;
		this.setCooldown(10000);
	}

	@Override
	public void run(CommandExecutionInfo e) {
		
		boolean specified = e.args.argSpecified("target");
		User target = specified ? e.args.getUser("target") : e.executor;
		
		long id = target.getIdLong();
		boolean self = id == e.executor.getIdLong();
		GuiProfile gui = new GuiProfile(id, self);
		BonziUtils.sendGui(e, gui);
	}
}