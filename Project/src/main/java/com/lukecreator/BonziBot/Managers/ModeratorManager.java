package com.lukecreator.BonziBot.Managers;

import java.awt.Color;
import java.io.EOFException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.RoleAction;

public class ModeratorManager implements IStorableData {
	
	public HashMap<Long, Long> modRoles = new HashMap<Long, Long>();
	
	public boolean hasModRole(long id) {
		return modRoles.containsKey(id);
	}
	public boolean hasModRole(Guild guild) {
		if(guild == null) return false;
		return hasModRole(guild.getIdLong());
	}
	public boolean modRoleStillExists(Guild guild) {
		if(guild == null) return false;
		if(!hasModRole(guild.getIdLong())) return false;
		long roleId = modRoles.get(guild.getIdLong());
		Role role = guild.getRoleById(roleId);
		return role != null;
	}
	public boolean canCreateModRole(Guild guild) {
		Member self = guild.getSelfMember();
		return self.hasPermission(Permission.MANAGE_ROLES);
	}
	
	/**
	 *   Similar to getModRoleAsync but is
	 * synchronous. It doesn't create the role.
	 */
	public Role getModRole(Guild guild) {
		if(guild == null) return null;
		long gid = guild.getIdLong();
		
		if(modRoleStillExists(guild)) {
			long roleId = modRoles.get(gid);
			return guild.getRoleById(roleId);
		}
		
		passiveAssignModRole(guild);
		
		long roleId = modRoles.get(gid);
		return guild.getRoleById(roleId);
	}
	/**
	 *  Asynchronous method to get the moderator role.
	 * If it doesn't exist it will be searched for, and
	 *   then created and accepted into the consumer.
	 */
	public void getModRoleAsync(Guild guild, Consumer<? super Role> success) {
		if(guild == null) return;
		
		if(modRoleStillExists(guild)) {
			long roleId = modRoles.get(guild.getIdLong());
			Role r = guild.getRoleById(roleId);
			success.accept(r);
			return;
		}
		
		// Create the role or find it.
		passiveAssignModRoleAsync(guild, success);
	}
	public void passiveAssignModRoleAsync(Guild guild, Consumer<? super Role> success) {
		long id = guild.getIdLong();
		if(hasModRole(id)) return;
		if(searchForModRole(guild, success))
			return; // Successful in finding one.
		createModRoleAsync(guild, success);
	}
	public void passiveAssignModRole(Guild guild) {
		long id = guild.getIdLong();
		if(hasModRole(id)) return;
		if(searchForModRole(guild, null))
			return; // Successful in finding one.
		createModRole(guild);
	}
	public boolean searchForModRole(Guild guild, Consumer<? super Role> success) {
		
		String[] attempts = new String[] {
			"mod", "moderation", "moderator", "staff", "admin", "administrator", "owner"
		};
		
		long gid = guild.getIdLong();
		for(String a: attempts) {
			List<Role> potential = guild.getRolesByName(a, true);
			if(potential.isEmpty()) continue;
			Role target = potential.get(0);
			long rid = target.getIdLong();
			modRoles.put(gid, rid);
			if(success != null)
				success.accept(target);
			return true;
		}
		
		return false;
	}
	public void createModRoleAsync(Guild guild, Consumer<? super Role> success) {
		beginModRole(guild)
			.queue(r -> {
				modRoles.put(guild.getIdLong(), r.getIdLong());
				if(success != null)
					success.accept(r);
			});
	}
	public Role createModRole(Guild guild) {
		Role r = beginModRole(guild).complete();
		modRoles.put(guild.getIdLong(), r.getIdLong());
		return r;
	}
	public RoleAction beginModRole(Guild guild) {
		return guild.createRole()
			.setColor(Color.magenta)
			.setName("Bonzi Moderator")
			.setMentionable(true);
	}
	@Override
	public void saveData() {
		DataSerializer.writeObject(modRoles, "modRoles");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject("modRoles");
		if(o == null) return;
		modRoles = (HashMap<Long, Long>) o;
	}
}