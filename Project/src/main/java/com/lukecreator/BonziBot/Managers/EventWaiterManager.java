package com.lukecreator.BonziBot.Managers;

import java.util.HashMap;
import java.util.function.Consumer;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

/*
 * Manages waiting events, specifically user responses.
 */
public class EventWaiterManager {
	
	HashMap<Long, Consumer<? super Message>> waiters =
		new HashMap<Long, Consumer<? super Message>>();
	
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
	public void onMessage(Message msg) {
		User u = msg.getAuthor();
		long id = u.getIdLong();
		
		if(waiters.containsKey(id)) {
			Consumer<? super Message> event = waiters.remove(id);
			event.accept(msg);
		}
	}
}