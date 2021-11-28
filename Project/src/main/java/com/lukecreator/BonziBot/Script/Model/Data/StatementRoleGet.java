package com.lukecreator.BonziBot.Script.Model.Data;

import java.util.ArrayList;

import com.lukecreator.BonziBot.Script.Model.ScriptGetter;

import net.dv8tion.jda.api.entities.Role;

public class StatementRoleGet extends ScriptGetter {
	
	private static final long serialVersionUID = 1L;
	
	public StatementRoleGet() {
		super();
		this.nameOfType = "Role";
		this.keyword = "r_data";
		this.requiredType = Role.class;
		
		this.propertyBindings = new ArrayList<Binding>();
		
		this.propertyBindings.add(new Binding("Name", role -> {
			return ((Role)role).getName();
		}));
		this.propertyBindings.add(new Binding("Mention", role -> {
			return ((Role)role).getAsMention();
		}));
		this.propertyBindings.add(new Binding("Permissions", role -> {
			String[] strings = (String[])((Role)role).getPermissions()
				.stream().map(p -> p.getName()).toArray();
			return String.join(", ", strings);
		}));
		this.propertyBindings.add(new Binding("Color", role -> {
			return ((Role)role).getColor();
		}));
	}
	
}
