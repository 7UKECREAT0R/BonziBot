package com.lukecreator.BonziBot.Data;

import java.util.Random;

import com.lukecreator.BonziBot.BonziBot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

/**
 * A quickdraw game.
 * @author Lukec
 */
public abstract class QuickDraw {
	
	public static final String BUTTON_PROTOCOL = "_qd";
	public static String encodeProtocol(String data) {
		return BUTTON_PROTOCOL + ':' + data;
	}
	
	private static Random random = new Random(); 
	
	/**
	 * Create a random QuickDraw game.
	 */
	public static QuickDraw create(BonziBot bb) {
		switch(random.nextInt(6)) {
		case 0:
			return new QuickDrawMath(bb);
		case 1:
			return new QuickDrawReact(bb);
		case 2:
			return new QuickDrawType(bb);
		case 3:
			return new QuickDrawUnscramble(bb);
		case 4:
			return new QuickDrawClick(bb);
		case 5:
			return new QuickDrawIdentify(bb);
		default:
			return null;
		}
	}
	
	public long sentMessageId = 0l;
	public int reward = 50;
	
	/**
	 * Construct the message to be sent.
	 */
	public abstract MessageCreateAction constructMessage(TextChannel channel);
	/**
	 * Construct the message to be sent when a user wins.
	 */
	public abstract MessageCreateAction constructWinnerMessage(User winner, int coinsGained, TextChannel channel);
	/**
	 * Perform any actions on the sent message such as adding reactions.
	 */
	public void postConstructMessage(Message selfMessage) {};
	
	// Input methods. Not all have to be implemented, only the ones you need.
	// A true return value will indicate that this user is a winner.
	public boolean tryInput(Message message) 			{ return false; }
	public boolean tryInput(String actionId) 			{ return false; }
	public boolean tryInput(EmojiUnion reaction) 		{ return false; }
	public boolean tryInput(Button click, String data)	{ return false; }
}