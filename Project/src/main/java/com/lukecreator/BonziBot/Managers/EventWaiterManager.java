package com.lukecreator.BonziBot.Managers;

import java.awt.Color;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Tuple;
import com.lukecreator.BonziBot.CommandAPI.CommandArg;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class EventWaiterManager {
	
	HashMap<Long, Consumer<? super Message>> waiters =
		new HashMap<Long, Consumer<? super Message>>();
	HashMap<Long, Tuple<CommandArg, Consumer<Object>>> argWaiters =
		new HashMap<Long, Tuple<CommandArg, Consumer<Object>>>();
	
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
		this.stopWaitingForArgument(user.getIdLong());
	}
	public void stopWaitingForArgument(long id) {
		argWaiters.remove(id);
	}
	
	public void onMessage(Message msg) {
		User u = msg.getAuthor();
		long id = u.getIdLong();
		
		if(waiters.containsKey(id)) {
			Consumer<? super Message> event = waiters.remove(id);
			event.accept(msg);
			return;
		}
		
		if(argWaiters.containsKey(id)) {
			onArgMessage(msg, id);
			return;
		}
	}
	public void onArgMessage(Message msg, long id) {
		
		Tuple<CommandArg, Consumer<Object>> obj
			= argWaiters.get(id);
		
		CommandArg arg = obj.getA();
		Consumer<Object> event = obj.getB();
		String content = msg.getContentRaw();
		MessageChannel channel = msg.getChannel();
		JDA jda = msg.getJDA();
		
		if(!arg.isWordParsable(content)) {
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
		arg.parseWord(content, jda, msg.getAuthor());
		Object parsed = arg.object;
		
		if(parsed == null) {
			MessageEmbed me = BonziUtils.failureEmbed
				("An error occurred. Cancelled command.");
			channel.sendMessage(me).queue();
			return;
		}
		
		event.accept(parsed);
		return;
	}
}