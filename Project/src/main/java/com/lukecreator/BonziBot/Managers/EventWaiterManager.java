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
import com.lukecreator.BonziBot.GuiAPI.GuiButton;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

public class EventWaiterManager {
	
	// User ID, Consumer
	HashMap<Long, Consumer<? super Message>> waiters =
		new HashMap<Long, Consumer<? super Message>>();
	
	// User ID, Tuple of Arg Type, Consumer
	HashMap<Long, Tuple<CommandArg, Consumer<Object>>> argWaiters
		= new HashMap<Long, Tuple<CommandArg, Consumer<Object>>>();
	
	// User ID, Array of Tuple of Reaction[] and Consumer
	@Deprecated
	HashMap<Long, Tuple<GenericEmoji[], Consumer<Integer>>> reactionWaiters
		= new HashMap<Long, Tuple<GenericEmoji[], Consumer<Integer>>>();
	
	// User ID, Array of Tuple of GuiButton[] and Consumer
	HashMap<Long, Tuple<GuiButton[], Consumer<String>>> actionWaiters
		= new HashMap<Long, Tuple<GuiButton[], Consumer<String>>>();
	HashMap<Long, Tuple<GuiButton[], Consumer<Tuple<User, String>>>> globalActionWaiters
		= new HashMap<Long, Tuple<GuiButton[], Consumer<Tuple<User, String>>>>();
	private static final GuiButton CONFIRM_YES = new GuiButton("Yes", GuiButton.Color.GREEN, "_cyes");
	private static final GuiButton CONFIRM_NO = new GuiButton("No", GuiButton.Color.RED, "_cno");
	
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
	// OBSOLETE as of 6-8-21. Use waitForAction(...)
	@Deprecated
	public void waitForReaction(User user, Message msg, GenericEmoji[] emoji, Consumer<Integer> consumer) {
		waitForReaction(user.getIdLong(), msg, emoji, consumer);
	}
	@Deprecated
	public void waitForReaction(long userId, Message msg, GenericEmoji[] emoji, Consumer<Integer> consumer) {
		this.reactionWaiters.put(userId, new Tuple<GenericEmoji[], Consumer<Integer>>(emoji, consumer));
		for(GenericEmoji e: emoji)
			e.react(msg);
	}
	@Deprecated
	public void stopWaitingForReaction(User user) {
		this.reactionWaiters.remove(user.getIdLong());
	}
	@Deprecated
	public void stopWaitingForReaction(long userId) {
		this.reactionWaiters.remove(userId);
	}
	
	// Button click waiters which return the action ID clicked.
	// These return a MessageAction that is yet to be sent.
	public MessageAction waitForAction(User user, MessageAction msgAction, Consumer<String> consumer, GuiButton... buttons) {
		return waitForAction(user.getIdLong(), msgAction, consumer, buttons);
	}
	public MessageAction waitForAction(long userId, MessageAction msgAction, Consumer<String> consumer, GuiButton... buttons) {
		this.actionWaiters.put(userId, new Tuple<GuiButton[], Consumer<String>>(buttons, consumer));
		return BonziUtils.appendButtons(msgAction, buttons, false);
	}
	public ReplyAction waitForAction(User user, ReplyAction msgAction, Consumer<String> consumer, GuiButton... buttons) {
		return waitForAction(user.getIdLong(), msgAction, consumer, buttons);
	}
	public ReplyAction waitForAction(long userId, ReplyAction msgAction, Consumer<String> consumer, GuiButton... buttons) {
		this.actionWaiters.put(userId, new Tuple<GuiButton[], Consumer<String>>(buttons, consumer));
		return BonziUtils.appendButtons(msgAction, buttons);
	}
	public void stopWaitingForAction(User user) {
		stopWaitingForAction(user.getIdLong());
	}
	public void stopWaitingForAction(long userId) {
		this.actionWaiters.remove(userId);
	}
	public void waitForGlobalAction(MessageAction msgAction, Consumer<Tuple<User, String>> consumer, GuiButton... buttons) {
		BonziUtils.appendButtons(msgAction, buttons, false).queue(msg -> {
			this.globalActionWaiters.put(msg.getIdLong(), new Tuple<GuiButton[], Consumer<Tuple<User, String>>>(buttons, consumer));
		});
	}
	public void waitForGlobalAction(ReplyAction msgAction, Consumer<Tuple<User, String>> consumer, GuiButton... buttons) {
		BonziUtils.appendButtons(msgAction, buttons).queue(hook -> {
			hook.retrieveOriginal().queue(msg -> {
				this.globalActionWaiters.put(msg.getIdLong(), new Tuple<GuiButton[], Consumer<Tuple<User, String>>>(buttons, consumer));
			});
		});
	}
	public void stopWaitingForGlobalAction(Message msg) {
		stopWaitingForGlobalAction(msg.getIdLong());
	}
	public void stopWaitingForGlobalAction(long messageId) {
		this.globalActionWaiters.remove(messageId);
	}
	
	// Allows a boolean response. Confirms something.
	public void getConfirmation(User user, MessageChannel channel, String title, Consumer<Boolean> consumer) {
		getConfirmation(user.getIdLong(), channel, title, consumer);
	}
	public void getConfirmation(long id, MessageChannel channel, String title, Consumer<Boolean> consumer) {
		Consumer<String> wrapper = (str -> { consumer.accept(str.equals("_cyes")); });
		EmbedBuilder eb = BonziUtils.quickEmbed(title, "<@" + id + ">, click yes or no to confirm.").setColor(Color.orange);
		this.waitForAction(id, channel.sendMessage(eb.build()), wrapper, CONFIRM_YES, CONFIRM_NO).queue();
	}
	public void getConfirmation(User user, MessageChannel channel, MessageEmbed me, Consumer<Boolean> consumer) {
		getConfirmation(user.getIdLong(), channel, me, consumer);
	}
	public void getConfirmation(long id, MessageChannel channel, MessageEmbed me, Consumer<Boolean> consumer) {
		Consumer<String> wrapper = (str -> { consumer.accept(str.equals("_cyes")); });
		this.waitForAction(id, channel.sendMessage(me), wrapper, CONFIRM_YES, CONFIRM_NO).queue();
	}
	@Deprecated
	public boolean isWaitingForReaction(User u) {
		return this.reactionWaiters.containsKey(u.getIdLong());
	}
	@Deprecated
	public boolean isWaitingForReaction(long id) {
		return this.reactionWaiters.containsKey(id);
	}
	public boolean isWaitingForAction(User u) {
		return this.actionWaiters.containsKey(u.getIdLong());
	}
	public boolean isWaitingForAction(long id) {
		return this.actionWaiters.containsKey(id);
	}
	public boolean isWaitingForGlobalAction(Message msg) {
		return this.globalActionWaiters.containsKey(msg.getIdLong());
	}
	public boolean isWaitingForGlobalAction(long messageId) {
		return this.globalActionWaiters.containsKey(messageId);
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
		long uid = e.user.getIdLong();
		
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
	public boolean onClick(ButtonClickEvent e) {
		long id = e.getUser().getIdLong();
		long mid = e.getMessageIdLong();
		
		if(this.actionWaiters.containsKey(id)) {
			Tuple<GuiButton[], Consumer<String>>
				data = this.actionWaiters.get(id);
			GuiButton[] possible = data.getA();
			Consumer<String> result = data.getB();
			String cId = e.getComponentId();
			
			for(int i = 0; i < possible.length; i++) {
				GuiButton test = possible[i];
				if(test.actionIdEqual(cId)) {
					Message m = e.getMessage();
					if(m != null) m.delete().queue(null, fail -> {});
					this.actionWaiters.remove(id);
					result.accept(cId);
					e.deferEdit().queue();
					return true;
				}
			}
			
			this.actionWaiters.remove(id);
			MessageEmbed me = BonziUtils.failureEmbed
				("Stopped waiting for button press.", "If you're trying to use a GUI, try again!");
			e.replyEmbeds(me).setEphemeral(true).queue();
			return true;
		} else if(this.globalActionWaiters.containsKey(mid)) {
			Tuple<GuiButton[], Consumer<Tuple<User, String>>>
				data = this.globalActionWaiters.get(mid);
			GuiButton[] possible = data.getA();
			Consumer<Tuple<User, String>> result = data.getB();
			String cId = e.getComponentId();
			
			for(int i = 0; i < possible.length; i++) {
				GuiButton test = possible[i];
				if(test.actionIdEqual(cId)) {
					Message m = e.getMessage();
					if(m != null) m.delete().queue(null, fail -> {});
					this.globalActionWaiters.remove(mid);
					result.accept(new Tuple<User, String>(e.getUser(), cId));
					e.deferEdit().queue();
					return true;
				}
			}
			
			MessageEmbed me = BonziUtils.failureEmbed
				("Invalid button pressed.");
			e.replyEmbeds(me).setEphemeral(true).queue();
			return true;
		}
		
		return false;
	}
}