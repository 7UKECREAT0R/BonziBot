package com.lukecreator.BonziBot.EventAPI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.TimeSpan;
import com.lukecreator.BonziBot.Data.SUser;
import com.lukecreator.BonziBot.Data.UserAccount;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * The abstraction of a server-wide event.
 * Handles allowing people to join, limiting
 * player counts, handling user requirements,
 * and actually running the whole event.
 * 
 * @author Lukec
 */
public abstract class BonziEvent implements Serializable {
	
	public static final String JOIN_BUTTON = "🔘";
	private static final long serialVersionUID = 1L;
	
	// Data
	public String eventName;
	public String eventDescription;
	public String eventIcon;
	
	public final String error_cantJoin = "You can't join this event!"; 		// requirements not met
	public final String error_started = "The event has already started!"; 	// event started already
	public final String error_full = "This event is full!";					// event is full of members
	
	// Members can join the event while it's running.
	public boolean canJoinWhileRunning = false;
	
	// Limit how many members can join the event.
	public boolean hasMemberLimit = false;
	public int memberLimit = 10;
	public void limitMembers(int limit) {
		this.hasMemberLimit = true;
		this.memberLimit = limit;
	}
	
	// Limit members based off of requirements.
	public EventRequirement[] requirements = new EventRequirement[0];
	public boolean memberQualifiesRequirements(Member member, BonziBot bb) {
		if(this.requirements == null)
			return true;
		if(this.requirements.length < 1)
			return true;
		UserAccount account = bb.accounts
			.getUserAccount(member.getUser());
		for(EventRequirement requirement: this.requirements) {
			if(!requirement.qualifies(member, account))
				return false;
		}
		return true;
	}
	public Collection<EventRequirement> getUnqualified(Member member, BonziBot bb) {
		if(this.requirements == null)
			return new ArrayList<EventRequirement>();
		if(this.requirements.length < 1)
			return new ArrayList<EventRequirement>();
		
		List<EventRequirement> fails = new ArrayList<EventRequirement>();
		UserAccount account = bb.accounts
			.getUserAccount(member.getUser());
		for(EventRequirement requirement: this.requirements) {
			if(!requirement.qualifies(member, account))
				fails.add(requirement);
		}
		return fails;
	}
	
	// Start criteria.
	public boolean autoStart = true; // starts immediately
	public boolean startsAtMemberCount = false;
	public int startAtMemberCount = 10;
	public void startAtMembers(int members) {
		this.startsAtMemberCount = true;
		this.startAtMemberCount = members;
	}
	
	// BonziEvents can be sent "command messages"
	// which act like pseudo-commands. The events
	// themselves handle all the commands however
	// they wish to, and they come in through a
	// method named "onCommandEvent".
	public String[] publicCommandMessages = new String[0];
	public String[] moderatorCommandMessages = new String[0];
	public boolean requestTimedEvents = false;
	public TimeSpan timedEventsTiming = null;
	public void enableTimedEvents(TimeSpan timeBetween) {
		this.requestTimedEvents = true;
		this.timedEventsTiming = timeBetween;
	}
	
	// state
	public BonziEvent() {
		this.id = BonziUtils.generateId();
	}
	public long id;
	public List<SUser> enteredMembers = new ArrayList<SUser>();
	public boolean eventFull() {
		if(!this.hasMemberLimit)
			return false;
		return this.enteredMembers.size() >= this.memberLimit;
	}
	public BonziEventCache parentReference;
	public boolean eventStarted = false;
	public boolean eventEnded = false;
	public boolean sentMessage = false;
	public long sentTextChannel = 0l;
	public long sentMessageId = 0l;
	public int timedEventCount = 0;
	
	// Abstract events for the event itself to use. You can
	// return a MessageEmbed to edit the event message to.
	//
	// If you return null, the message will not be updated
	// and no edit request will be made.
	
	/**
	 * Called once when the event message is sent and once every time a member joins.
	 * @return
	 */
	public abstract MessageEmbed constructQueueMessage();
	
	/**
	 * Called when one of the command messages (listed in this.commandMessages)
	 * is written as a chat message with a prefix. The message contents are split
	 * and passed on as "arguments" (doesn't include the first argument obviously).
	 * 
	 * These can be sent at ANY time.
	 * @param message
	 * @param args
	 * @return
	 */
	public abstract MessageEmbed onCommandEvent(Member user, String message, String[] args);
	
	/**
	 * Called only after the event has started and if enableTimedEvents() has been called.
	 * This is sent every x milliseconds as defined by timedEventsTiming.
	 * @return
	 */
	public abstract MessageEmbed onTimedEvent();
	
	/**
	 * Called when the event is started by members joining,
	 * when it's manually started, or by the code.
	 * @return 
	 */
	public abstract MessageEmbed onEventStarted();
	
	/**
	 * Called when the event is ended by a user, or the code.
	 * @return
	 */
	public abstract MessageEmbed onEventEnded();
}