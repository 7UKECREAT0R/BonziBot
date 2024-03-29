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
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

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
	
	private static final GuiButton CONFIRM_YES = new GuiButton("Yes", GuiButton.ButtonColor.GREEN, "_cyes");
	private static final GuiButton CONFIRM_NO = new GuiButton("No", GuiButton.ButtonColor.RED, "_cno");
	
	// Base methods used for raw event waiting.
	public void waitForResponse(User user, Consumer<? super Message> onResponse) {
		this.waiters.put(user.getIdLong(), onResponse);
	}
	public void waitForResponse(long id, Consumer<? super Message> onResponse) {
		this.waiters.put(id, onResponse);
	}
	public void stopWaitingForResponse(User user) {
		this.waiters.remove(user.getIdLong());
	}
	public void stopWaitingForResponse(long id) {
		this.waiters.remove(id);
	}
	
	// Arg based waiters which will return a specific arg-type.
	public void waitForArgument(User user, CommandArg type, Consumer<Object> onResponse) {
		this.waitForArgument(user.getIdLong(), type, onResponse);
	}
	public void waitForArgument(long id, CommandArg type, Consumer<Object> onResponse) {
		Tuple<CommandArg, Consumer<Object>> tuple = new Tuple
			<CommandArg, Consumer<Object>>(type, onResponse);
		this.argWaiters.put(id, tuple);
	}
	public void stopWaitingForArgument(User user) {
		this.argWaiters.remove(user.getIdLong());
	}
	public void stopWaitingForArgument(long id) {
		this.argWaiters.remove(id);
	}
	
	// Reaction waiters which return index pressed.
	// DEPRECATED as of 6-8-21. Use waitForAction(...)
	@Deprecated
	public void waitForReaction(User user, Message msg, GenericEmoji[] emoji, Consumer<Integer> consumer) {
		this.waitForReaction(user.getIdLong(), msg, emoji, consumer);
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
	public MessageCreateAction waitForAction(User user, MessageCreateAction msgAction, Consumer<String> consumer, GuiButton... buttons) {
		return this.waitForAction(user.getIdLong(), msgAction, consumer, buttons);
	}
	public MessageCreateAction waitForAction(long userId, MessageCreateAction msgAction, Consumer<String> consumer, GuiButton... buttons) {
		this.actionWaiters.put(userId, new Tuple<GuiButton[], Consumer<String>>(buttons, consumer));
		return BonziUtils.appendComponents(msgAction, buttons, false);
	}
	public MessageEditAction waitForAction(User user, MessageEditAction msgAction, Consumer<String> consumer, GuiButton... buttons) {
		return this.waitForAction(user.getIdLong(), msgAction, consumer, buttons);
	}
	public MessageEditAction waitForAction(long userId, MessageEditAction msgAction, Consumer<String> consumer, GuiButton... buttons) {
		this.actionWaiters.put(userId, new Tuple<GuiButton[], Consumer<String>>(buttons, consumer));
		return BonziUtils.appendComponents(msgAction, buttons, false);
	}
	public ReplyCallbackAction waitForAction(User user, ReplyCallbackAction msgAction, Consumer<String> consumer, GuiButton... buttons) {
		return this.waitForAction(user.getIdLong(), msgAction, consumer, buttons);
	}
	public ReplyCallbackAction waitForAction(long userId, ReplyCallbackAction msgAction, Consumer<String> consumer, GuiButton... buttons) {
		this.actionWaiters.put(userId, new Tuple<GuiButton[], Consumer<String>>(buttons, consumer));
		return BonziUtils.appendComponents(msgAction, buttons, false);
	}
	public void stopWaitingForAction(User user) {
		this.stopWaitingForAction(user.getIdLong());
	}
	public void stopWaitingForAction(long userId) {
		this.actionWaiters.remove(userId);
	}
	public void waitForGlobalAction(MessageCreateAction msgAction, Consumer<Tuple<User, String>> consumer, GuiButton... buttons) {
		BonziUtils.appendComponents(msgAction, buttons, false).queue(msg -> {
			this.globalActionWaiters.put(msg.getIdLong(), new Tuple<GuiButton[], Consumer<Tuple<User, String>>>(buttons, consumer));
		});
	}
	public void waitForGlobalAction(MessageEditAction msgAction, Consumer<Tuple<User, String>> consumer, GuiButton... buttons) {
		BonziUtils.appendComponents(msgAction, buttons, false).queue(msg -> {
			this.globalActionWaiters.put(msg.getIdLong(), new Tuple<GuiButton[], Consumer<Tuple<User, String>>>(buttons, consumer));
		});
	}
	public void waitForGlobalAction(ReplyCallbackAction msgAction, Consumer<Tuple<User, String>> consumer, GuiButton... buttons) {
		BonziUtils.appendComponents(msgAction, buttons, false).queue(hook -> {
			hook.retrieveOriginal().queue(msg -> {
				this.globalActionWaiters.put(msg.getIdLong(), new Tuple<GuiButton[], Consumer<Tuple<User, String>>>(buttons, consumer));
			});
		});
	}
	public void stopWaitingForGlobalAction(Message msg) {
		this.stopWaitingForGlobalAction(msg.getIdLong());
	}
	public void stopWaitingForGlobalAction(long messageId) {
		this.globalActionWaiters.remove(messageId);
	}
	
	// Allows a boolean response. Confirms something.
	public void getConfirmation(User user, MessageChannelUnion channel, String title, Consumer<Boolean> consumer) {
		this.getConfirmation(user.getIdLong(), channel, title, consumer);
	}
	public void getConfirmation(long id, MessageChannelUnion channel, String title, Consumer<Boolean> consumer) {
		Consumer<String> wrapper = (str -> { consumer.accept(str.equals("_cyes")); });
		EmbedBuilder eb = BonziUtils.quickEmbed(title, "<@" + id + ">, click yes or no to confirm.").setColor(Color.orange);
		this.waitForAction(id, channel.sendMessageEmbeds(eb.build()), wrapper, CONFIRM_YES, CONFIRM_NO).queue();
	}
	public void getConfirmation(User user, MessageChannelUnion channel, MessageEmbed me, Consumer<Boolean> consumer) {
		this.getConfirmation(user.getIdLong(), channel, me, consumer);
	}
	public void getConfirmation(long id, MessageChannelUnion channel, MessageEmbed me, Consumer<Boolean> consumer) {
		Consumer<String> wrapper = (str -> { consumer.accept(str.equals("_cyes")); });
		this.waitForAction(id, channel.sendMessageEmbeds(me), wrapper, CONFIRM_YES, CONFIRM_NO).queue();
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
		
		if(this.waiters.containsKey(id)) {
			Consumer<? super Message> event = this.waiters.remove(id);
			event.accept(msg);
			return true;
		}
		
		if(this.argWaiters.containsKey(id)) {
			this.onArgMessage(msg, id);
			return true;
		}
		
		return false;
	}
	public void onArgMessage(Message msg, long id) {
		
		Tuple<CommandArg, Consumer<Object>> obj
			= this.argWaiters.get(id);
		
		CommandArg arg = obj.getA();
		Consumer<Object> event = obj.getB();
		String content = msg.getContentRaw();
		
		if(content.length() > 1024)
			content = content.substring(0, 1024);
		
		final boolean deleteMessage;
		
		if(content.length() < 1 && !msg.getAttachments().isEmpty()) {
			Attachment file = msg.getAttachments().get(0);
			if(file.isImage())
				content = file.getUrl();
			deleteMessage = false;
		} else
			deleteMessage = true;
		
		MessageChannelUnion channel = msg.getChannel();
		Guild guild = msg.isFromGuild() ? msg.getGuild() : null;
		JDA jda = msg.getJDA();
		
		if(!arg.isWordParsable(content, guild)) {
			msg.delete().queue();
			EmbedBuilder eb = BonziUtils.quickEmbed(
				"Incorrect type of response.",
				arg.getErrorDescription(), Color.red);
			channel.sendMessageEmbeds(eb.build()).queue(sent -> {
				sent.delete().queueAfter(5, TimeUnit.SECONDS);
			});
			return;
		}
		
		this.argWaiters.remove(id);
		arg.parseWord(content, jda, msg.getAuthor(), guild);
		Object parsed = arg.object;
		
		if(deleteMessage)
			msg.delete().queue();
		
		if(parsed == null) {
			MessageEmbed me = BonziUtils.failureEmbed
				("An error occurred. Cancelled waiting.");
			channel.sendMessageEmbeds(me).queue();
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
			EmojiUnion emote = e.reactionEmote;
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
		e.channel.sendMessageEmbeds(me).queue();
		this.reactionWaiters.remove(uid);
		return;
	}
	public boolean onClick(ButtonInteractionEvent e) {
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
				if(test.idEqual(cId)) {
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
				if(test.idEqual(cId)) {
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