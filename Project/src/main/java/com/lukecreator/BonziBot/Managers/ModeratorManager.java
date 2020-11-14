package com.lukecreator.BonziBot.Managers;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;

import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class ModeratorManager implements IStorableData {
	
	public HashMap<Long, Long> modRoles = new HashMap<Long, Long>();
	
	public boolean hasModRole(long id) {
		return modRoles.containsKey(id);
	}
	public boolean hasModRole(Guild guild) {
		return hasModRole(guild.getIdLong());
	}

	public boolean canCreateModRole(Guild guild) {
		Member self = guild.getSelfMember();
		return self.hasPermission(Permission.MANAGE_ROLES);
	}
	public void passiveAssignModRole(Guild guild) {
		long id = guild.getIdLong();
		if(hasModRole(id)) return;
		
		if(searchForModRole(guild))
			return; // Successful in finding one.
		createModRole(guild);
	}
	public boolean searchForModRole(Guild guild) {
		
		String[] attempts = new String[] {
			"mod", "moderator", "staff", "admin", "administrator", "owner"
		};
		
		long gid = guild.getIdLong();
		for(String a: attempts) {
			List<Role> potential = guild.getRolesByName(a, true);
			if(potential.isEmpty()) continue;
			Role target = potential.get(0);
			long rid = target.getIdLong();
			modRoles.put(gid, rid);
			return true;
		}
		
		return false;
	}
	public void createModRole(Guild guild) {
		long rid = guild.createRole()
			.setColor(Color.magenta)
			.setName("Bonzi Moderator")
			.setMentionable(true)
			.complete().getIdLong(); // bad practice but required for this method stack
		modRoles.put(guild.getIdLong(), rid);
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(modRoles, "modRoles");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() {
		Object o = DataSerializer.retrieveObject("modRoles");
		if(o == null) return;
		modRoles = (HashMap<Long, Long>) o;
	}
}