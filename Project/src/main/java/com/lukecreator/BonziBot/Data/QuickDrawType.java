package com.lukecreator.BonziBot.Data;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Graphics.FontLoader;
import com.lukecreator.BonziBot.Graphics.FontStyle;
import com.lukecreator.BonziBot.Graphics.Image;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class QuickDrawType extends QuickDraw {
	
	String word;
	File file = null;
	
	QuickDrawType(BonziBot bb) {
		this.reward = BonziUtils.randomInt(30) + 25;
		this.word = bb.strings.getWord();
	}
	
	@Override
	public MessageAction constructMessage(TextChannel channel) {
		Image draw = new Image(720, 240, false);
		draw.fill(new Color(24, 24, 24));
		draw.setFont(FontLoader.BEBAS_FONT, FontStyle.NORMAL, 72);
		draw.drawCenteredString(word, Color.white);
		try {
			this.file = draw.save("qdt" + channel.getIdLong() + ".jpg", false);
			return channel.sendFile(this.file).append("`Quick Draw!` Type this word:");
		} catch (IOException e) {
			e.printStackTrace();
			return channel.sendMessage("`Quick Draw!` Type '" + this.word + "'");
		} finally {
			if(draw != null)
				draw.dispose();
		}
	}
	@Override
	public MessageAction constructWinnerMessage(User winner, int coinsGained, TextChannel channel) {
		return channel.sendMessage(winner.getAsMention() + "` won the Quick Draw!` `+" + coinsGained + " coins!`");
	}
	
	@Override
	public void postConstructMessage(Message selfMessage) {
		if(this.file != null)
			this.file.delete();
	}
	
	@Override
	public boolean tryInput(Message message) {
		if(message.getContentStripped().equalsIgnoreCase(this.word)) {
			message.delete().queue(null, fail -> {});
			return true;
		}
		return false;
	}
}