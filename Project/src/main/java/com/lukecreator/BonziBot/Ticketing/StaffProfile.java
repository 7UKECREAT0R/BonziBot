package com.lukecreator.BonziBot.Ticketing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.TimeSpan;

/**
 * Profile for staff members, specifically related to tickets.
 * @author Lukec
 */
public class StaffProfile implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	static final TimeSpan ONE_DAY = TimeSpan.fromHours(24);
	static final TimeSpan ONE_WEEK = TimeSpan.fromDays(7);
	static final TimeSpan ONE_MONTH = TimeSpan.fromDays(30);
	
	public StaffProfile(long owner) {
		this.owner = owner;
	}
	
	public final long owner;
	List<Long> ticketsCompletedTimestamps = new ArrayList<Long>();
	
	/**
	 * Log that this staff member has completed a ticket.
	 */
	public void completedTicket() {
		this.ticketsCompletedTimestamps.add(System.currentTimeMillis());
	}
	
	/**
	 * Returns the total number of completed tickets.
	 * @return
	 */
	public int ticketsCompleted() {
		return this.ticketsCompletedTimestamps.size();
	}
	/**
	 * Returns the number of tickets completed in the last X time.
	 * @param within
	 * @return
	 */
	public int ticketsCompleted(TimeSpan within) {
		long now = System.currentTimeMillis();
		int count = 0;
		
		for(long timestamp: this.ticketsCompletedTimestamps) {
			if(now  < timestamp + within.ms)
				count++;
		}
		
		return count;
	}
	
	public int ticketsCompletedThisDay() {
		return this.ticketsCompleted(ONE_DAY);
	}
	public int ticketsCompletedThisWeek() {
		return this.ticketsCompleted(ONE_WEEK);
	}
	public int ticketsCompletedThisMonth() {
		return this.ticketsCompleted(ONE_MONTH);
	}
}