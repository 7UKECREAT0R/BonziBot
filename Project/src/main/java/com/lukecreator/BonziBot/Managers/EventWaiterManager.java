package com.lukecreator.BonziBot.Managers;

import java.awt.Color;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Tuple;
import com.lukecreator.BonziBot.CommandAPI.CommandArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GenericReactionEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;

public class EventWaiterManager {
	
	// User ID, Consumer
	HashMap<Long, Consumer<? super Message>> waiters =
		new HashMap<Long, Consumer<? super Message>>();
	
	// User ID, Tuple of Arg Type, Consumer
	HashMap<Long, Tuple<CommandArg, Consumer<Object>>> argWaiters
		= new HashMap<Long, Tuple<CommandArg, Consumer<Object>>>();
	
	// User ID, Array of Tuple of Reaction[] and Consumer
	HashMap<Long, Tuple<GenericEmoji[], Consumer<Integer>>> reactionWaiters
		= new HashMap<Long, Tuple<GenericEmoji[], Consumer<Integer>>>();
	
	// Base methods used for raw event waiting.
	public void waitForResponse(User user, Consumer<? super Message> onResponse) {
		waiters.put(user.getIdLong(), onResponse);
	}
	public void waitForResponse(long id, Consumer<? super Message> onResponse) {
		waiters.put(id, onResponse);
	}
	public void stopWaitingForResponse(User user) {
		waiters.remove(user.getIdLong());
	}
	public void stopWaitingForResponse(long id) {
		waiters.remove(id);
	}
	
	// Arg based waiters which will return a specific arg-type.
	public void waitForArgument(User user, CommandArg type, Consumer<Object> onResponse) {
		this.waitForArgument(user.getIdLong(), type, onResponse);
	}
	public void waitForArgument(long id, CommandArg type, Consumer<Object> onResponse) {
		Tuple<CommandArg, Consumer<Object>> tuple = new Tuple
			<CommandArg, Consumer<Object>>(type, onResponse);
		argWaiters.put(id, tuple);
	}
	public void stopWaitingForArgument(User user) {
		this.argWaiters.remove(user.getIdLong());
	}
	public void stopWaitingForArgument(long id) {
		this.argWaiters.remove(id);
	}
	
	// Reaction waiters which return index pressed.
	public void waitForReaction(User user, Message msg, GenericEmoji[] emoji, Consumer<Integer> consumer) {
		waitForReaction(user.getIdLong(), msg, emoji, consumer);
	}
	public void waitForReaction(long userId, Message msg, GenericEmoji[] emoji, Consumer<Integer> consumer) {
		this.reactionWaiters.put(userId, new Tuple<GenericEmoji[], Consumer<Integer>>(emoji, consumer));
		for(GenericEmoji e: emoji)
			e.react(msg);
	}
	public void stopWaitingForReaction(User user) {
		this.reactionWaiters.remove(user.getIdLong());
	}
	public void stopWaitingForReaction(long userId) {
		this.reactionWaiters.remove(userId);
	}
	
	// Allows a boolean response. Confirms something.
	public void getConfirmation(User user, MessageChannel channel, String title, Consumer<Boolean> consumer) {
		getConfirmation(user.getIdLong(), channel, title, consumer);
	}
	public void getConfirmation(long id, MessageChannel channel, String title, Consumer<Boolean> consumer) {
		Consumer<Integer> wrapper = (i -> {
			consumer.accept(i == 0);
		});
		
		GenericEmoji[] emoji = new GenericEmoji[2];
		emoji[0] = GenericEmoji.fromEmoji("🟩");
		emoji[1] = GenericEmoji.fromEmoji("🟥");
		
		EmbedBuilder eb = BonziUtils.quickEmbed(title,
			"<@" + id + ">, react with yes or no to confirm.")
			.setColor(Color.orange);
		channel.sendMessage(eb.build()).queue(msg -> {
			this.waitForReaction(id, msg, emoji, wrapper);
		});
	}
	public void getConfirmation(User user, MessageChannel channel, MessageEmbed me, Consumer<Boolean> consumer) {
		getConfirmation(user.getIdLong(), channel, me, consumer);
	}
	public void getConfirmation(long id, MessageChannel channel,  MessageEmbed me, Consumer<Boolean> consumer) {
		Consumer<Integer> wrapper = (i -> {
			consumer.accept(i == 0);
		});
		
		GenericEmoji[] emoji = new GenericEmoji[2];
		emoji[0] = GenericEmoji.fromEmoji("🟩");
		emoji[1] = GenericEmoji.fromEmoji("🟥");
		
		channel.sendMessage(me).queue(msg -> {
			this.waitForReaction(id, msg, emoji, wrapper);
		});
	}
	public boolean isWaitingForReaction(User u) {
		return this.reactionWaiters.containsKey(u.getIdLong());
	}
	public boolean isWaitingForReaction(long id) {
		return this.reactionWaiters.containsKey(id);
	}
	
	/**
	 * Receive a message and pass it through
	 * a series of tests to check if it should
	 * be received by a waiting event.
	 * @param msg
	 * @return <code>true</code> if the event shouldn't be passed through to the command system. (cancel)
	 */
	public boolean onMessage(Message msg) {
		User u = msg.getAuthor();
		long id = u.getIdLong();
		
		if(waiters.containsKey(id)) {
			Consumer<? super Message> event = waiters.remove(id);
			event.accept(msg);
			return true;
		}
		
		if(argWaiters.containsKey(id)) {
			onArgMessage(msg, id);
			return true;
		}
		
		return false;
	}
	public void onArgMessage(Message msg, long id) {
		
		Tuple<CommandArg, Consumer<Object>> obj
			= argWaiters.get(id);
		
		CommandArg arg = obj.getA();
		Consumer<Object> event = obj.getB();
		String content = msg.getContentRaw();
		MessageChannel channel = msg.getChannel();
		Guild guild = msg.isFromGuild() ? msg.getGuild() : null;
		JDA jda = msg.getJDA();
		
		if(!arg.isWordParsable(content, guild)) {
			msg.delete().queue();
			EmbedBuilder eb = BonziUtils.quickEmbed(
				"Incorrect type of response.",
				arg.getErrorDescription(), Color.red);
			channel.sendMessage(eb.build()).queue(sent -> {
				sent.delete().queueAfter(5, TimeUnit.SECONDS);
			});
			return;
		}
		
		argWaiters.remove(id);
		arg.parseWord(content, jda, msg.getAuthor(), guild);
		Object parsed = arg.object;
		
		msg.delete().queue();
		
		if(parsed == null) {
			MessageEmbed me = BonziUtils.failureEmbed
				("An error occurred. Cancelled waiting.");
			channel.sendMessage(me).queue();
			return;
		}
		
		event.accept(parsed);
		return;
	}
	public void onReaction(GenericReactionEvent e) {
		User user = e.user;
		long uid = user.getIdLong();
		
		if(!this.reactionWaiters.containsKey(uid))
			return;
		
		Tuple<GenericEmoji[], Consumer<Integer>> rData
			= this.reactionWaiters.get(uid);
		GenericEmoji[] possible = rData.getA();
		Consumer<Integer> result = rData.getB();
		
		for(int i = 0; i < possible.length; i++) {
			ReactionEmote emote = e.reactionEmote;
			if(possible[i].isEqual(emote)) {
				this.reactionWaiters.remove(uid);
				result.accept(i);
				return;
			}
		}
		
		// Not valid emoji.
		MessageEmbed me = BonziUtils.failureEmbed
			("Stopped waiting for your reaction."
			,"Invalid option specified.");
		e.channel.sendMessage(me).queue();
		this.reactionWaiters.remove(uid);
		return;
	}
}