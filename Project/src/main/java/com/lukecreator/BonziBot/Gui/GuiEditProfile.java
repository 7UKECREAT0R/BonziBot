package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.time.LocalDate;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.ColorArg;
import com.lukecreator.BonziBot.Data.Achievement;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.UserAccountManager;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;

public class GuiEditProfile extends Gui {
	
	long userId;
	
	public GuiEditProfile(long userId) {
		this.userId = userId;
	}
	
	@Override
	public void initialize(JDA jda) {
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🖊️"), "Bio", GuiButton.Color.BLUE, "bio"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🏳️‍🌈"), "Favorite Color", GuiButton.Color.BLUE, "color"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("⏰"), "Timezone", GuiButton.Color.BLUE, "timezone"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🖼️"), "Background", GuiButton.Color.BLUE, "background"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🍰"), "Birthday", GuiButton.Color.BLUE, "birthday"));
	}
	
	@Override
	public Object draw(JDA jda) {
		
		UserAccountManager uam = this.bonziReference.accounts;
		UserAccount account = uam.getUserAccount(userId);
		
		String bio = account.bio;
		TimeZone timezone = account.timeZone;
		Color favColor = account.favoriteColor;
		String background = account.backgroundImage;
		String birthday = account.hasBirthday() ?
			account.getBirthday().format(BonziUtils.MMdd) : "Disabled";
		
		if(bio == null)
			bio = "help somthin went wrnong";
		if(favColor == null)
			favColor = BonziUtils.COLOR_BONZI_PURPLE;
		if(background == null)
			background = "No background.";
		String tzString = timezone != null ?
			timezone.getDisplayName() : "Unset";
		
		EmbedBuilder eb = BonziUtils.quickEmbed("Editing Profile...", "Change your bio, timezone, favorite color, or even background image!", favColor);
		eb.addField("🖊️ Bio", "`A short description of yourself.`\n" + BonziUtils.cutOffString(bio, 128), false);
		eb.addField("🏳️‍🌈 Favorite Color", "`The color used on the xp and profile commands!`\nThe embed color is the currently set favorite color.", false);
		eb.addField("⏰ Timezone", "`Your timezone so that people know what time it is for you.`\n" + tzString, false);
		eb.addField("🖼️ Background", "`The background used on xp and profile images!`\n" + background, false);
		eb.addField("🍰 Birthday", "`Your birthday date! Will show how many days are left, and lets people know when they mention you on your birthday.`\n" + birthday, false);
		return eb.build();
	}
	
	@Override
	public void onAction(String actionId, long executorId, JDA jda) {
		
		UserAccountManager uam = this.bonziReference.accounts;
		EventWaiterManager ewm = this.bonziReference.eventWaiter;
		UserAccount account = uam.getUserAccount(userId);
		MessageChannel channel = this.parent.getChannel(jda);
		
		if(actionId.equals("bio")) {
			EmbedBuilder eb = BonziUtils.successEmbedIncomplete("🖊️ Send your bio in chat.");
			channel.sendMessageEmbeds(eb.setColor(Color.orange).build()).queue(firstMsg -> {
				ewm.waitForResponse(userId, message -> {
					firstMsg.delete().queue();
					message.delete().queue();
					account.bio = message.getContentRaw();
					uam.setUserAccount(userId, account);
					BonziUtils.tryAwardAchievement(channel, this.bonziReference, userId, Achievement.SNAZZY);
					this.parent.redrawMessage(jda);
				});
			});

			return;
		}
		
		if(actionId.equals("color")) {
			EmbedBuilder eb = BonziUtils.successEmbedIncomplete("🏳️‍🌈 Send your favorite color!");
			channel.sendMessageEmbeds(eb.setColor(Color.orange).build()).queue(firstMsg -> {
				ewm.waitForArgument(userId, new ColorArg(""), object -> {
					firstMsg.delete().queue();
					Color color = (Color)object;
					account.favoriteColor = color;
					uam.setUserAccount(userId, account);
					BonziUtils.tryAwardAchievement(channel, this.bonziReference, userId, Achievement.SNAZZY);
					this.parent.redrawMessage(jda);
				});
			});
			return;
		}
		
		if(actionId.equals("timezone")) {
			EmbedBuilder eb = BonziUtils.successEmbedIncomplete("⏰ Send the timezone you're in.",
					"Type 'none' to set no timezone.");
			channel.sendMessageEmbeds(eb.setColor(Color.orange).build()).queue(firstMsg -> {
				ewm.waitForResponse(userId, message -> {
					message.delete().queue();
					firstMsg.delete().queue();
					
					String id = message.getContentStripped();
					
					if(BonziUtils.stripText(id).equalsIgnoreCase("none")) {
						account.timeZone = null;
						uam.setUserAccount(userId, account);
						this.parent.redrawMessage(jda);
						return;
					}
					
					String[] zones = TimeZone.getAvailableIDs();
					boolean valid = false;
					for(String zone: zones) {
						if(id.equals(zone)) {
							valid = true;
							break;
						}
					}
					if(!valid) {
						BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("Invalid Time Zone! Cancelled operation.",
							"If you put an abbreviation (like PST), make sure it's in all capitals!"), 6);
						return;
					}
					TimeZone tz = TimeZone.getTimeZone(id);
					account.timeZone = tz;
					uam.setUserAccount(userId, account);
					BonziUtils.tryAwardAchievement(channel, this.bonziReference, userId, Achievement.SNAZZY);
					this.parent.redrawMessage(jda);
				});
			});
		}
		
		if(actionId.equals("background")) {
			EmbedBuilder eb = BonziUtils.successEmbedIncomplete("🖼️ Attach or link an image to set as your background.",
					"Type 'none' to set no background");
			channel.sendMessageEmbeds(eb.setColor(Color.orange).build()).queue(firstMsg -> {
				ewm.waitForResponse(userId, message -> {
					firstMsg.delete().queue();
					String content = message.getContentRaw();
					
					if(BonziUtils.stripText(content).equalsIgnoreCase("none")) {
						message.delete().queue();
						account.backgroundImage = null;
						uam.setUserAccount(userId, account);
						this.parent.redrawMessage(jda);
						return;
					}
					
					if(!message.getAttachments().isEmpty()) {
						Attachment image = message.getAttachments().get(0);
						String url = image.getUrl();
						if(Constants.IMAGE_URL_REGEX_COMPILED.matcher(url).matches()) {
							account.backgroundImage = url;
							uam.setUserAccount(userId, account);
							BonziUtils.tryAwardAchievement(channel, this.bonziReference, userId, Achievement.SNAZZY);
							this.parent.redrawMessage(jda);
						} else {
							BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("You can only upload image files."), 3);
						}
						return;
					}
					
					content = content.trim();
					message.delete().queue();
					if(Pattern.matches(Constants.IMAGE_URL_REGEX, content)) {
						account.backgroundImage = content;
						uam.setUserAccount(userId, account);
						BonziUtils.tryAwardAchievement(channel, this.bonziReference, userId, Achievement.SNAZZY);
						this.parent.redrawMessage(jda);
						return;
					} else {
						BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("Invalid image URL. Cancelled."), 3);
						return;
					}
				});
			});
		}
		
		if(actionId.equals("birthday")) {
			EmbedBuilder eb = BonziUtils.successEmbedIncomplete("🍰 Send the date of your birthday!",
					"Format: `<month>/<day>`\nType 'none' to disable this feature.");
			channel.sendMessageEmbeds(eb.setColor(Color.orange).build()).queue(firstMsg -> {
				ewm.waitForResponse(userId, message -> {
					firstMsg.delete().queue();
					message.delete().queue(null, silent -> {});
					
					String content = message.getContentRaw();
					
					if(content.equalsIgnoreCase("none")) {
						account.disableBirthday();
						uam.setUserAccount(userId, account);
						this.parent.redrawMessage(jda);
					} else {
						String[] halves = content
							.replace('-', '/')
							.replace('\\', '/')
							.replace(',', '/')
							.replaceAll(Constants.WHITESPACE_REGEX, "")
							.split("/");
						if(halves.length < 2) {
							channel.sendMessageEmbeds(BonziUtils.failureEmbed
								("Hmm... That doesn't look quite right.", "Cancelled operation.\n"
								+ "It should something like this: `2/20`")).queue();
							return;
						}
						String h1 = halves[0];
						String h2 = halves[1];
						try {
							int month = Integer.parseInt(h1);
							int day = Integer.parseInt(h2);
							LocalDate birthday = LocalDate.of(LocalDate.now().getYear(), month, day);
							account.setBirthday(birthday);
							uam.setUserAccount(userId, account);
							BonziUtils.tryAwardAchievement(channel, this.bonziReference, userId, Achievement.SNAZZY);
							this.parent.redrawMessage(jda);
						} catch(NumberFormatException nfe) {
							channel.sendMessageEmbeds(BonziUtils.failureEmbed
								("Hmm... That doesn't look quite right.", "Cancelled operation.\n"
								+ "It should something like this: `2/20`")).queue();
							return;
						}
					}
				});
			});
		}
	}
}
