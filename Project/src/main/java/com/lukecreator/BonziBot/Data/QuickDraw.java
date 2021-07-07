package com.lukecreator.BonziBot.Data;

import java.util.Random;

import com.lukecreator.BonziBot.BonziBot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

/**
 * A quickdraw game.
 * @author Lukec
 */
public abstract class QuickDraw {
	
	private static Random random = null; 
	
	/**
	 * Create a random QuickDraw game.
	 */
	public static QuickDraw create(BonziBot bb) {
		if(random == null)
			random = new Random();
		
		switch(random.nextInt(4)) {
		case 0:
			return new QuickDrawMath(bb);
		case 1:
			return new QuickDrawReact(bb);
		case 2:
			return new QuickDrawType(bb);
		case 3:
			return new QuickDrawUnscramble(bb);
		default:
			return null;
		}
	}
	
	public long sentMessageId = 0l;
	public int reward = 50;
	
	/**
	 * Construct the message to be sent.
	 */
	public abstract MessageAction constructMessage(TextChannel channel);
	/**
	 * Perform any actions on the sent message such as adding reactions.
	 */
	public void postConstructMessage(Message selfMessage) {};
	
	// Input methods. Not all have to be implemented, only the ones you need.
	// A true return value will indicate that this user is a winner.
	public boolean tryInput(Message message) 		{ return false; }
	public boolean tryInput(String actionId) 		{ return false; }
	public boolean tryInput(ReactionEmote reaction) { return false; }
}