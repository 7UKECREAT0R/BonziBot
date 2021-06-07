package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.Badge;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Graphics.FontLoader;
import com.lukecreator.BonziBot.Graphics.FontStyle;
import com.lukecreator.BonziBot.Graphics.Image;
import com.lukecreator.BonziBot.Graphics.Rect;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.GuiManager;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class GuiProfile extends Gui {
	
	static final Color BG_COLOR = new Color(40, 40, 40);
	static final Color BIO_COLOR = new Color(30, 30, 30);
	
	static final int IMG_WIDTH = 360;
	static final int IMG_HEIGHT = 480;
	static final int BIO_HEIGHT = 225;
	static final int PFP_SIZE = 120;
	static final int BIO_PADDING = 6;
	static final int PADDING = 12;
	static final int ROUNDING = 18;
	
	static final int NAME_FONT_SIZE = 28;
	static final int BADGE_FONT_SIZE = 18;
	static final int BIO_FONT_SIZE = 14;
	static final int INFO_FONT_SIZE = 18;
	
	long userId;
	boolean self;
	
	public GuiProfile(long userId, boolean self) {
		this.userId = userId;
		this.self = self;
	}
	
	@Override
	public void initialize(JDA jda) {
		if(self) this.buttons.add(new GuiButton
			(GenericEmoji.fromEmoji("📝"), 0));
	}
	
	@Override
	public Object draw(JDA jda) {
		
		UserAccountManager uam = this.bonziReference.accounts;
		UserAccount account = uam.getUserAccount(userId);
		
		Color favColor = account.favoriteColor;
		String bgImageUrl = account.backgroundImage;
		User target = jda.getUserById(userId);
		String avatarUrl = target.getEffectiveAvatarUrl();
		
		Font nameFont = new Font(FontLoader.BEBAS_FONT, 0, NAME_FONT_SIZE);
		Font bioFont = new Font(FontLoader.SEGOE_FONT, 0, BIO_FONT_SIZE);
		Image image = new Image(IMG_WIDTH, IMG_HEIGHT, true);
		
		// Coordinates
		int pfpLeft = PADDING;
		int pfpTop = PADDING;
		int pfpRight = PADDING + PFP_SIZE;
		int pfpBottom = PADDING + PFP_SIZE;
		int nameX = pfpRight + PADDING;
		int nameY = PADDING; // add height to later
		
		int bioTop = pfpBottom + PADDING;
		int bioBottom = bioTop + BIO_HEIGHT;
		int bioLeft = PADDING;
		int bioRight = IMG_WIDTH - PADDING;
		
		int bioTextTop = bioTop + (BIO_PADDING >> 1);
		int bioTextBottom = bioBottom - BIO_PADDING;
		int bioTextLeft = bioLeft + BIO_PADDING;
		int bioTextRight = bioRight - BIO_PADDING;
		
		// Draw template (background and bio rect)
		if(bgImageUrl == null) {
			image.fill(BG_COLOR);
			image.drawRectCorners(bioLeft, bioTop,
				bioRight, bioBottom, BIO_COLOR);
		} else {
			Image background = Image.download(bgImageUrl);
			if(background == null) {
				image.dispose();
				return BonziUtils.failureEmbed("something went super wrong:", Image.downloadMessage);
			}
			image.fillImageKeepAspect(background);
			Color trans = new Color(0, 0, 0, 128);
			image.drawRectCorners(bioLeft, bioTop,
				bioRight, bioBottom, trans);
			background.dispose();
		}
		
		// Draw profile picture.
		Image pfp = Image.download(avatarUrl);
		if(pfp == null) {
			image.dispose();
			return BonziUtils.failureEmbed("something went super wrong:", Image.downloadMessage);
		}
		image.drawImage(pfp.round(ROUNDING),
			pfpLeft, pfpTop, PFP_SIZE, PFP_SIZE);
		pfp.dispose();
		
		// Draw name text.
		String name = target.getName();
		image.setFont(nameFont);
		FontMetrics metrics = image.getFontMetrics();
		int nameHeight = metrics.getAscent();
		nameY += nameHeight;
		image.drawString(name, Color.white, nameX, nameY);
		
		// Draw badges, if any.
		List<Badge> badges = account.getBadges();
		if(!badges.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for(Badge b: badges)
				sb.append(b.icon.getGenericEmoji() + " ");
			String bString = sb.substring(0, sb.length() - 1);
			Font badgeFont = new Font(FontLoader.EMOJI_FONT,
				FontStyle.NORMAL.constant, BADGE_FONT_SIZE);
			nameY += nameHeight;
			image.setFont(badgeFont);
			image.drawString(bString, Color.white, nameX, nameY);
		}
		
		// Draw bio.
		String bio = account.bio;
		if(bio == null || BonziUtils.isWhitespace(bio))
			bio = "no bio... yet!";
		image.setFont(bioFont);
		Rect bioTextBounds = Rect.fromSides(bioTextLeft,
			bioTextRight, bioTextTop, bioTextBottom);
		image.drawStringWrapping(bio, favColor, bioTextBounds);
		
		// Draw info at the bottom.
		Font infoFont = nameFont.deriveFont((float)INFO_FONT_SIZE);
		image.setFont(infoFont);
		metrics = image.getFontMetrics();
		int infoHeight = metrics.getAscent();
		int infoX = PADDING;
		int infoY = bioBottom + PADDING + infoHeight;
		
		
		String registered = "Account made on " + target.getTimeCreated().format(BonziUtils.MMddyy) + ".";
		image.drawString(registered, Color.white, infoX, infoY);
		
		if(account.timeZone != null) {
			infoY += infoHeight;
			String timezone = "Timezone: " + account.timeZone.getDisplayName();
			image.drawString(timezone, Color.white, infoX, infoY);
		}
		
		if(account.hasBirthday()) {
			infoY += infoHeight;
			int daysUntil = account.daysUntilBirthday();
			String birthday;
			Color bdayColor;
			if(daysUntil == 0) {
				birthday = "Birthday is TODAY!";
				bdayColor = account.favoriteColor != null ?
					account.favoriteColor : Color.green;
			} else if(daysUntil == 1) {
				birthday = "Birthday is tomorrow!";
				bdayColor = Color.orange;
			} else {
				birthday = "Birthday is in " + daysUntil + " days.";
				bdayColor = Color.white;
			}
			image.drawString(birthday, bdayColor, infoX, infoY);
		}
		
		int rep = account.getRep();
		String reputation = "Reputation: " + rep;
		infoY += infoHeight;
		Color repColor = (rep > 0) ? Color.white : (rep == 0) ? Color.lightGray : Color.gray;
		image.drawString(reputation, repColor, infoX, infoY);
		
		try {
			File saved = image.save("profileImages/p_" + target.getId() + ".png", true);
			return saved;
		} catch(IOException exc) {
			exc.printStackTrace();
			return BonziUtils.failureEmbed("error occurred saving/uploading... maybe try again in a few seconds?", exc.getLocalizedMessage());
		}
	}
	
	@Override
	public void onAction(String actionId, JDA jda) {
		if(actionId == 0) {
			Gui theGui = new GuiEditProfile(userId);
			BonziBot bb = this.bonziReference;
			GuiManager guis = bb.guis;
			
			MessageChannel channel = this.parent.getChannel(jda);
			ChannelType type = channel.getType();
			
			channel.deleteMessageById(this.parent.messageId).queue();
			
			if(type == ChannelType.TEXT) {
				User sender = jda.getUserById(this.parent.ownerId);
				guis.sendAndCreateGui((TextChannel)channel, sender, theGui, bb);
			} else {
				guis.sendAndCreateGui((PrivateChannel)channel, theGui, bb);
			}
			
			return;
		}
	}
}
