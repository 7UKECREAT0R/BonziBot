package com.lukecreator.BonziBot.Data;

import java.io.Serializable;

import net.dv8tion.jda.api.entities.User;

/**
 * Like a User, but it's actually
 * serializable and stuff (cool)
 * @author Lukec
 */
public class SUser implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public final String name;
	public final String discriminator;
	public String getFullName() {
		return name + "#" + discriminator;
	}
	
	public final long id;
	public final String avatarUrl;
	
	public SUser(User user) {
		this.name = user.getName();
		this.discriminator = user.getDiscriminator();
		this.id = user.getIdLong();
		this.avatarUrl = user.getEffectiveAvatarUrl();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SUser)
			return this.id == ((SUser)obj).id;
		else if(obj instanceof User)
			return this.id == ((User)obj).getIdLong();
		else if(obj instanceof String)
			return this.name.equals((String)obj);
		else if(obj instanceof Long)
			return this.id == ((Long)obj).longValue();
		else return false;
	}
}