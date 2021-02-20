package com.lukecreator.BonziBot.Commands;

import java.awt.Color;
import java.awt.FontMetrics;
import java.io.File;
import java.io.IOException;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Graphics.FontLoader;
import com.lukecreator.BonziBot.Graphics.FontStyle;
import com.lukecreator.BonziBot.Graphics.Image;
import com.lukecreator.BonziBot.Graphics.Rect;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.entities.User;

public class XpCommand extends Command {
	
	public static final Color BACK_COLOR = new Color(24, 24, 24);
	public static final Color EMPTY_COLOR = new Color(32, 32, 32);
	public static final int IMG_WIDTH = 480;
	public static final int IMG_HEIGHT = 240;
	public static final int EDGE_DIST = 24;
	public static final int BAR_ROUND = 16;
	public static final int BAR_HEIGHT = 48;
	
	public XpCommand() {
		this.subCategory = 2;
		this.name = "XP";
		this.unicodeIcon = "🎓";
		this.description = "View your xp or someone else's xp.";
		this.args = new CommandArgCollection(new UserArg("target").optional());
		this.category = CommandCategory.FUN;
		this.setCooldown(10000);
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		User target = e.args.argSpecified("target") ?
			e.args.getUser("target") : e.executor;
		
		UserAccountManager uam = e.bonzi.accounts;
		UserAccount acc = uam.getUserAccount(target);
		String name = target.getName();
		
		int xp = acc.getXP();
		int level = acc.getLevel();
		int nextLevel = level + 1;
		int startXp = BonziUtils.calculateXpForLevel(level);
		int nextXp = BonziUtils.calculateXpForLevel(nextLevel);
		float percent = xp - startXp;
		percent /= (float)(nextXp - startXp);
		
		String xpString = BonziUtils.comma(xp);
		String levelString = BonziUtils.comma(level);
		String avatar = target.getEffectiveAvatarUrl();
		
		Image image = new Image(IMG_WIDTH, IMG_HEIGHT, true);
		Image background = null;
		if(acc.backgroundImage != null)
			background = Image.download(acc.backgroundImage);
		
		if(background != null)
			image.fillImage(background);
		else
			image.fill(BACK_COLOR);
		
		if(background != null)
			background.dispose();
		
		int barLeft = EDGE_DIST;
		int barRight = IMG_WIDTH - EDGE_DIST;
		int barBottom = IMG_HEIGHT - EDGE_DIST;
		int barTop = barBottom - BAR_HEIGHT;
		float barWidth = barRight - barLeft;
		float fillWidth = barWidth * percent;
		
		Image avatarImage = Image.download(avatar);
		int avTop = EDGE_DIST;
		int avLeft = EDGE_DIST;
		int avSize = (barTop - EDGE_DIST) - avTop;
		
		if(avatarImage != null) {
			image.drawImage(avatarImage.round(BAR_ROUND),
				avLeft, avTop, avSize, avSize);
			avatarImage.dispose();
		}
		
		image.setFont(FontLoader.BEBAS_FONT, FontStyle.NORMAL, 42);
		FontMetrics metrics = image.getFontMetrics();
		int ascent = metrics.getAscent();
		int textX = avLeft + avSize + EDGE_DIST;
		int textY = ascent + (ascent >> 1);
		Color textColor = Color.white;
		image.drawString(name, textColor, textX, textY);
		metrics = image.setFontSize(18);
		textY += metrics.getAscent() + 2;
		textColor = acc.favoriteColor;
		image.drawString("LVL " + levelString, textColor, textX, textY);
		
		Rect bar = Rect.fromSides(barLeft, barRight, barTop, barBottom);
		Rect partBar = Rect.fromSides(barLeft, barLeft + (int)fillWidth, barTop, barBottom);
		Rect stBar = Rect.fromSides(partBar.right - BAR_ROUND, partBar.right, partBar.top, partBar.bottom);
		
		image.drawRoundedRect(bar, BAR_ROUND, EMPTY_COLOR);
		image.drawRoundedRect(partBar, BAR_ROUND, acc.favoriteColor);
		image.drawRect(stBar, acc.favoriteColor);
		image.setFontSize(24);
		image.drawCenteredString(xpString + " XP", Color.white, bar);
		
		try {
			File saved = image.save("xpImages/xp_" + target.getIdLong() + ".png", true);
			e.channel.sendFile(saved).queue(finished -> {
				saved.delete();
			}, fail -> {
				saved.delete();
			});
		}
		catch(IOException exc) {
			exc.printStackTrace();
			e.channel.sendMessage("error occurred saving/uploading maybe try again? `" + exc.getLocalizedMessage() + "`").queue();
		}
	}
	
}
