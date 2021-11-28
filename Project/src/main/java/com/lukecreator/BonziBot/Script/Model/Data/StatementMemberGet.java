package com.lukecreator.BonziBot.Script.Model.Data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Script.Model.ScriptGetter;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class StatementMemberGet extends ScriptGetter {
	
	private static final long serialVersionUID = 1L;
	
	public static final Comparator<Role> ROLE_SORT = new Comparator<Role>() {
		@Override
		public int compare(Role o1, Role o2) {
			return o1.getPosition() - o2.getPosition();
		}
	};
	
	public StatementMemberGet() {
		super();
		this.nameOfType = "Member";
		this.keyword = "m_data";
		this.requiredType = Member.class;
		
		this.propertyBindings = new ArrayList<Binding>();
		
		this.propertyBindings.add(new Binding("Mention", member -> {
			return ((Member)member).getAsMention();
		}));
		this.propertyBindings.add(new Binding("Nickname", member -> {
			return ((Member)member).getEffectiveName();
		}));
		this.propertyBindings.add(new Binding("Full Name", member -> {
			return ((Member)member).getUser().getAsTag();
		}));
		this.propertyBindings.add(new Binding("Color", member -> {
			Color color = ((Member)member).getColor();
			if(color == null)
				return new Color(Role.DEFAULT_COLOR_RAW);
			return color;
		}));
		this.propertyBindings.add(new Binding("Profile Picture", member -> {
			return ((Member)member).getUser().getEffectiveAvatarUrl();
		}));
		this.propertyBindings.add(new Binding("Date Joined Server", member -> {
			return ((Member)member).getTimeJoined().format(BonziUtils.MMddyy);
		}));
		this.propertyBindings.add(new Binding("Date Account Created", member -> {
			return ((Member)member).getUser().getTimeCreated().format(BonziUtils.MMddyy);
		}));
		this.propertyBindings.add(new Binding("Highest Role", member -> {
			return ((Member)member).getRoles().stream().sorted(ROLE_SORT).findFirst().get();
		}));
	}
	
}
