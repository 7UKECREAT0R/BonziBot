package com.lukecreator.BonziBot.Managers;

import java.util.HashMap;
import java.util.List;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandSystem;

public class CooldownManager {
	
	HashMap<Integer, HashMap<Long, Long>> cooldowns =
		new HashMap<Integer, HashMap<Long, Long>>();
	
	boolean isInit = false;
	
	public CooldownManager() {}
	public boolean isInitialized() {
		return isInit;
	}
	public void initialize(CommandSystem commands) {
		List<Command> cmds = commands.getRegisteredCommands();
		for(Command ac: cmds) {
			if(!ac.hasCooldown) continue;
			if(ac.cooldownMs < 1l) continue;
			cooldowns.put(ac.id, new HashMap<Long, Long>());
		}
		isInit = true;
	}
	
	/**
	 * Get cooldown entries for a command.
	 */
	public HashMap<Long, Long> getCooldownsForCommand(Command command) {
		if(!command.hasCooldown || command.cooldownMs < 1)
			return null;
		if(!cooldowns.containsKey(command.id))
			return null;
		return cooldowns.get(command.id);
	}
	/**
	 * Get cooldown entries for command with a specific id.
	 */
	public HashMap<Long, Long> getCooldownsForCommand(int id) {
		if(!cooldowns.containsKey(id))
			return null;
		return cooldowns.get(id);
	}
	/**
	 *  Gets the duration left on cooldown for a user on a command.
	 *  Returns -1 if there is no cooldown remaining or command doesn't have a cooldown..
	 */
	public long getUserCooldown(Command command, long userId) {
		HashMap<Long, Long> cooldown = getCooldownsForCommand(command);
		if(cooldown == null) return -1;
		if(!cooldown.containsKey(userId)) return -1;
		long c = cooldown.get(userId);
		
		// Get time remaining.
		long time = System.currentTimeMillis();
		long diff = c - time;
		
		// Clamp to -1 and return.
		if(diff < 1) {
			diff = -1;
			cooldown.remove(userId);
			cooldowns.put(command.id, cooldown);
		}
		return diff;
	}
	/**
	 * Gets if the user is on cooldown for this command. 
	 */
	public boolean userOnCooldown(Command command, long userId) {
		long cooldown = getUserCooldown(command, userId);
		return cooldown != -1;
	}
	
	/**
	 * Starts a cooldown for this user/command combo.
	 */
	public void applyCooldown(Command command, long userId) {
		if(!command.hasCooldown) return;
		if(command.cooldownMs < 1) return;
		
		HashMap<Long, Long> cooldown = getCooldownsForCommand(command);
		if(cooldown == null) return;
		
		long time = System.currentTimeMillis();
		time += command.cooldownMs;
		
		cooldown.put(userId, time);
		cooldowns.put(command.id, cooldown);
		return;
	}
	/**
	 * Remove the cooldown for this user/command combo.
	 */
	public void resetCooldown(Command command, long userId) {
		if(!command.hasCooldown) return;
		if(command.cooldownMs < 1) return;
		
		HashMap<Long, Long> cooldown = getCooldownsForCommand(command);
		if(cooldown == null) return;
		
		cooldown.remove(userId);
		cooldowns.put(command.id, cooldown);
	}
}