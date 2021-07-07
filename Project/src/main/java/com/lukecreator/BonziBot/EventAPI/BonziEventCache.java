package com.lukecreator.BonziBot.EventAPI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.TimeSpan;
import com.lukecreator.BonziBot.Data.SUser;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * List of active events. Used in the BonziEventManager.
 * 
 * This also manages the sending, activation, and
 * passing on of reactions/commands to events.
 *    
 * @author Lukec
 */
public class BonziEventCache implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private transient ScheduledThreadPoolExecutor executor;
	private transient HashMap<Long, ScheduledFuture<?>> executorEntries;
	
	private List<BonziEvent> unstartedEvents;
	private List<BonziEvent> startedEvents;
	
	public int currentId = 0;
	
	public BonziEventCache() {
		this.unstartedEvents = new ArrayList<BonziEvent>();
		this.startedEvents = new ArrayList<BonziEvent>();
		executor = new ScheduledThreadPoolExecutor(0);
		executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
		executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		executor.setRemoveOnCancelPolicy(true);
	}
	/**
	 * Start all executors based off of active events.
	 */
	public void startAllTimedEvents() {
		for(BonziEvent e: this.startedEvents)
			this.startExecutorFor(e);
	}
	public void stopAllTimedEvents() {
		for(BonziEvent e: this.startedEvents)
			this.stopExecutorFor(e);
	}
	public void startExecutorFor(BonziEvent event) {
		if(!event.requestTimedEvents)
			return;
		TimeSpan span = event.timedEventsTiming;
		
		// there's NO WAY this works but i'm gonna try it anyways
		Runnable toRun = new Runnable() {
			@Override
			public void run() {
				event.onTimedEvent();
				event.timedEventCount++;
			}
		};
		
		ScheduledFuture<?> future = this.executor.scheduleWithFixedDelay(toRun,
			span.ms, span.ms, TimeUnit.MILLISECONDS);
		if(future != null)
			this.executorEntries.put(event.id, future);
	}
	public void stopExecutorFor(BonziEvent event) {
		Long id = new Long(event.id);
		if(!this.executorEntries.containsKey(id))
			return;
		ScheduledFuture<?> cancel = this.executorEntries.get(id);
		this.executorEntries.remove(id);
		
		if(cancel == null)
			return;
		if(cancel.isCancelled())
			return;
		cancel.cancel(false);
	}
	
	/**
	 * Send the message for and begin letting users enter this event.
	 * 
	 * This is the official way to add an event to unstartedEvents,
	 * send the message, and let users start joining. You can
	 * forcefully start the event by using startEvent()
	 * 
	 * @param event
	 * @param bb
	 * @return The ID of the newly started event.
	 */
	public long beginEvent(BonziEvent event, TextChannel channel) {
		event.eventStarted = false;
		event.enteredMembers.clear();
		this.stopExecutorFor(event);
		
		event.sentTextChannel = channel.getIdLong();
		if(event.autoStart) {
			MessageEmbed me = event.onEventStarted();
			if(me == null) me = BonziUtils.failureEmbed("no starting embed was given.");
			channel.sendMessage(me).queue(sent -> {
				sent.addReaction(BonziEvent.JOIN_BUTTON).queue();
				long id = sent.getIdLong();
				event.sentMessageId = id;
				event.sentMessage = true;
				event.eventStarted = true;
				event.timedEventCount = 0;
				this.startExecutorFor(event);
				this.startedEvents.add(event);
			});
		} else {
			channel.sendMessage(event.constructQueueMessage()).queue(sent -> {
				sent.addReaction(BonziEvent.JOIN_BUTTON).queue();
				long id = sent.getIdLong();
				event.sentMessageId = id;
				event.sentMessage = true;
				this.unstartedEvents.add(event);
			});
		}
		
		return event.id;
	}
	public void stopEventsByType(Class<? extends BonziEvent> type, Guild guild) {
		for(int i = 0; i < startedEvents.size(); i++) {
			BonziEvent event = startedEvents.get(i);
			if(type.isInstance(event)) {
				event.eventEnded = true;
				MessageEmbed embed = event.onEventEnded();
				if(event.sentMessage) {
					TextChannel tc = guild.getTextChannelById(event.sentTextChannel);
					tc.editMessageById(event.sentMessageId, embed).queue();
				}
				startedEvents.remove(i--);
				return;
			}
		}
		
		// Non-started events. (psst its the same thing)
		for(int i = 0; i < unstartedEvents.size(); i++) {
			BonziEvent event = unstartedEvents.get(i);
			if(type.isInstance(event)) {
				event.eventEnded = true;
				MessageEmbed embed = event.onEventEnded();
				if(event.sentMessage) {
					TextChannel tc = guild.getTextChannelById(event.sentTextChannel);
					tc.editMessageById(event.sentMessageId, embed).queue();
				}
				unstartedEvents.remove(i--);
				return;
			}
		}
	}
	public void stopRunningEvent(BonziEvent event, Guild guild) {
		for(int i = 0; i < startedEvents.size(); i++) {
			BonziEvent test = startedEvents.get(i);
			if(test.id == event.id) {
				event.eventEnded = true;
				MessageEmbed embed = event.onEventEnded();
				if(event.sentMessage) {
					TextChannel tc = guild.getTextChannelById(event.sentTextChannel);
					tc.editMessageById(event.sentMessageId, embed).queue();
				}
				startedEvents.remove(i--);
				return;
			}
		}
	}
	public void startEvent(BonziEvent event, Guild guild) {
		for(int i = 0; i < this.unstartedEvents.size(); i++) {
			BonziEvent test = this.unstartedEvents.get(i);
			if(event.id == test.id) {
				this.startEvent(i, guild);
				return;
			}
		}
	}
	public void startEvent(int index, Guild guild) {
		BonziEvent event = this.unstartedEvents.remove(index);
		this.startedEvents.add(event);
		
		// TODO when textchannel is deleted, check if an event was in it.
		// the less stale events that stack up the better.
		TextChannel tc = guild.getTextChannelById(event.sentTextChannel);
		if(tc == null) {
			this.startedEvents.remove(event);
			return;
		}
		
		tc.retrieveMessageById(event.sentMessageId).queue(sent -> {
			event.eventStarted = true;
			event.timedEventCount = 0;
			this.startExecutorFor(event); // checks requestTimedEvents in method
			MessageEmbed newMessage = event.onEventStarted();
			if(newMessage != null)
				sent.editMessage(newMessage).queue();
		}, fail -> {
			this.startedEvents.remove(event);
			return;
		});
	}
	
	// Receiving events.
	
	/**
	 * Returns false if the user was denied joining. True if the user joined.
	 * @param bb
	 * @param member
	 * @param tc
	 * @param messageId
	 * @return if the user successfully joined the event.
	 */
	public boolean onJoinButtonPressed(BonziBot bb, Member member, TextChannel tc, long messageId) {
		BonziEvent event = null; int i;
		for(i = 0; i < this.unstartedEvents.size(); i++) {
			BonziEvent test = this.unstartedEvents.get(i);
			if(test.sentMessageId == messageId) {
				event = test;
				break;
			}
		}
		if(event == null)
			return false;
		
		if(event.eventEnded) {
			MessageEmbed me = BonziUtils.failureEmbed("This event has ended.");
			BonziUtils.sendTempMessage(tc, me, 3);
			return false;
		}
		
		if(!event.memberQualifiesRequirements(member, bb)) {
			Guild g = member.getGuild();
			Collection<EventRequirement> unqualified = event.getUnqualified(member, bb);
			EmbedBuilder eb = BonziUtils.quickEmbed(event.error_cantJoin,
				BonziUtils.plural("Reason", unqualified.size()) + ":", member);
			for(EventRequirement require: unqualified)
				eb.appendDescription("\n" + require.toStringError(g));
			BonziUtils.sendTempMessage(tc, eb.build(), 5);
			return false;
		}
		
		if(event.eventStarted && !event.canJoinWhileRunning) {
			MessageEmbed me = BonziUtils.failureEmbed(event.error_started);
			BonziUtils.sendTempMessage(tc, me, 3);
			return false;
		}
		
		if(event.eventFull()) {
			MessageEmbed me = BonziUtils.failureEmbed(event.error_full);
			BonziUtils.sendTempMessage(tc, me, 3);
			return false;
		}
		
		SUser ser = new SUser(member.getUser());
		event.enteredMembers.add(ser);
		int count = event.enteredMembers.size();
		
		if(event.startsAtMemberCount && count >= event.startAtMemberCount) {
			this.startEvent(i, member.getGuild());
		} else if(!event.eventStarted) {
			MessageEmbed update = event.constructQueueMessage();
			tc.editMessageById(event.sentMessageId, update).queue();
		}
		
		return true;
	}
	/**
	 * Returns false if the user did not leave the event (for example, if it
	 * already has started and users are locked via canJoinWhileRunning.)
	 * @param bb
	 * @param member
	 * @param tc
	 * @param messageId
	 * @return if the user successfully left the event.
	 */
	@SuppressWarnings("unlikely-arg-type")
	public boolean onLeaveButtonPressed(BonziBot bb, Member member, TextChannel tc, long messageId) {
		BonziEvent event = null; int i;
		for(i = 0; i < this.unstartedEvents.size(); i++) {
			BonziEvent test = this.unstartedEvents.get(i);
			if(test.sentMessageId == messageId) {
				event = test;
				break;
			}
		}
		if(event == null)
			return false;
		
		if(event.eventStarted && !event.canJoinWhileRunning) 
			return false;
		
		event.enteredMembers.remove(member.getUser());
		MessageEmbed update = event.constructQueueMessage();
		tc.editMessageById(event.sentMessageId, update).queue();
		return true;
	}
	/**
	 * Called when a message in a guild is run and a valid BonziBot command was not triggered.
	 * This should be checked and redirected to an event if command messages are set up in it.
	 * 
	 * Returns if any command message(s) were successfully sent.
	 * @param bb
	 * @param channel
	 * @param message
	 * @param prefix
	 * @return if a command message was processed.
	 */
	public boolean onMessageSentWithPrefix(BonziBot bb, TextChannel channel, Message message, String prefix) {
		String raw = message.getContentRaw();
		int prefixLength = prefix.length();
		raw = raw.substring(prefixLength);
		
		Member member = message.getMember();
		boolean admin = bb.special.getIsAdmin(member)
			|| member.hasPermission(Permission.ADMINISTRATOR);
		
		String[] parts = raw.split(Constants.WHITESPACE_REGEX);
		if(parts.length < 1 || raw.isEmpty())
			return false;
		String name = parts[0];
		String[] args = new String[parts.length - 1];
		for(int i = 1; i < parts.length; i++)
			args[i - 1] = parts[i];
		
		boolean $return = false;
		for(BonziEvent event: this.unstartedEvents) {
			for(String cmdMessage: event.publicCommandMessages) {
				if(name.equalsIgnoreCase(cmdMessage)) {
					$return = true;
					MessageEmbed me = event.onCommandEvent(member, name, args);
					if(me != null)
						channel.editMessageById(event.sentMessageId, me).queue();
				}
			}
			for(String cmdMessage: event.moderatorCommandMessages) {
				if(name.equalsIgnoreCase(cmdMessage)) {
					$return = true;
					if(!admin) {
						BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("You don't have permission to use this event command."), 4);
						break;
					}
					MessageEmbed me = event.onCommandEvent(member, name, args);
					if(me != null)
						channel.editMessageById(event.sentMessageId, me).queue();
				}
			}
		}
		return $return;
	}
}