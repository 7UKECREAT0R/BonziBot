package com.lukecreator.BonziBot.Data;

import java.io.Serializable;
import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/**
 * A user submitted ban appeal awaiting review from a moderator.
 * @author Lukec
 *
 */
public class BanAppeal implements Serializable {
	
	private static final long serialVersionUID = 7838786200973148685L;
	
	public String username;
	public String avatarUrl;
	public long userId;
	public String content;
	
	public boolean messageSent = false;
	public long sentMessage = 0l;
	
	public BanAppeal(User user, String content) {
		this.username = user.getName() + " (" + user.getEffectiveName() + ")";
		this.avatarUrl = user.getEffectiveAvatarUrl();
		this.userId = user.getIdLong();
		this.content = content;
	}
	
	public void sendMessage(TextChannel tc, Consumer<Message> completion) {
		this.sendMessage(tc, completion, null);
	}
	public void sendMessage(TextChannel tc, Consumer<Message> completion, Consumer<Throwable> failure) {
		Guild guild = tc.getGuild();
		guild.retrieveBan(UserSnowflake.fromId(this.userId)).queue(ban -> {
			String reason = ban.getReason();
			if(reason == null)
				reason = "unspecified";
			
			EmbedBuilder eb = BonziUtils.quickEmbed("Ban Appeal",
				"Banned for: `" + reason + "`\n```" +
				this.content + "```", BonziUtils.COLOR_BONZI_PURPLE);
			eb.setAuthor(this.username, null, this.avatarUrl);
			eb.setFooter("Should this user be unbanned?");
			
			GuiButton[] buttons = new GuiButton[] {
				new GuiButton("Accept Appeal", GuiButton.ButtonColor.GREEN, "_appeal:accept:" + this.userId),
				new GuiButton("Reject Appeal", GuiButton.ButtonColor.RED, "_appeal:reject:" + this.userId)
			};
			
			BonziUtils.appendComponents(tc.sendMessageEmbeds(eb.build()), buttons, false).queue(success -> {
				completion.accept(success);
			}, failure);
		}, fail -> { /* Ban not valid. */ });
	}
}
