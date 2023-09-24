package com.lukecreator.BonziBot.Ticketing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lukecreator.BonziBot.TimeSpan;

/**
 * Guild data for tickets.
 * @author Lukec
 */
public class TicketGuild implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final long NO_CATEGORY = 0l;
	 
	public enum InactiveAction {
		NO_ACTION("Don't perform any action on open tickets."),
		CLOSE_TICKET("Close the ticket."),
		WARN_USER("Warn the user and close the ticket."),
		KICK_FROM_SERVER("Kick the user from the server and close the ticket.");
		
		public final String description;
		private InactiveAction(String description) {
			this.description = description;
		}
	}
	
	public List<Long> blacklist = new ArrayList<Long>();
	public List<Ticket> openTickets = new ArrayList<Ticket>();
	public HashMap<Long, StaffProfile> profiles = new HashMap<Long, StaffProfile>(); 
	
	public final long guildId;					// ID of owning guild.
	public int numberOfTickets = 0;				// Number of tickets opened.
	public int maxTicketsOpen = 50;				// Maximum number of tickets that can be open.
	public boolean oneAtATime = false;			// User can only have one ticket open at a time.
	public String ticketAlias = null;			// Alternate name for at ticket e.g., "ban appeal".
	public String ticketDescription = null;		// Description inside ticket dashboard.
	public boolean saveTranscripts = true;		// Whether to save transcripts of tickets.
	public long transcriptsChannel = 0l;		// Channel ID to send transcripts into.
	public long[] alertRoles = null;			// Role IDs to alert when a new ticket is created.
	public long ticketCategory = NO_CATEGORY;	// ID of category to put tickets in, or NO_CATEGORY.
	
	public TimeSpan inactiveTime = null;		// Time until ticket is marked inactive and action is performed.
	public InactiveAction inactiveAction = InactiveAction.NO_ACTION;
	
	public TicketGuild(long guildId) {
		this.guildId = guildId;
	}
	/**
	 * Get the name of a ticket. See TicketGuild::ticketAlias. If null, returns "ticket"
	 * @return
	 */
	public String getTicketName() {
		if(this.ticketAlias == null)
			return "ticket";
		
		return this.ticketAlias;
	}
	/**
	 * Get the description of a ticket. See TicketGuild::ticketDescription. If null, returns short default message.
	 * @return
	 */
	public String getTicketDescription() {
		if(this.ticketDescription == null)
			return "A staff member will be with you as soon as possible.";
		
		return this.ticketAlias;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		
		// Set ticket guild instances.
		for(Ticket ticket: this.openTickets)
			ticket.guild = this;
	}
}
