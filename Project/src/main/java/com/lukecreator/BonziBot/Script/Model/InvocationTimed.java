package com.lukecreator.BonziBot.Script.Model;

import java.util.HashMap;

import com.lukecreator.BonziBot.TimeSpan;

import net.dv8tion.jda.api.entities.User;

public class InvocationTimed implements InvocationMethod {

	private static final long serialVersionUID = 1L;
	
	public int timesRun = 0;
	
	@Override
	public Implementation getImplementation() {
		return Implementation.TIMED;
	}
	
	public TimeSpan time;
	
	public HashMap<Long, Long> userDelays = new HashMap<Long, Long>();
	public long getCooldownRemaining(User user) {
		return this.getCooldownRemaining(user.getIdLong());
	}
	public long getCooldownRemaining(long userId) {
		if(this.userDelays.containsKey(userId)) {
			return this.userDelays.get(userId).longValue() - System.currentTimeMillis();
		} else {
			return -1;
		}
	}
	
	public void setCooldown(User user) {
		this.setCooldown(user.getIdLong());
	}
	public void setCooldown(long userId) {
		this.userDelays.put(userId, System.currentTimeMillis() + time.ms);
	}
	
	@Override
	public String[] getEventVariables() {
		return new String[] { "total_runs" };
	}
	
	@Override
	public String getAsExplanation() {
		return "Run every " + time.toLongString() + '.';
	}
}
