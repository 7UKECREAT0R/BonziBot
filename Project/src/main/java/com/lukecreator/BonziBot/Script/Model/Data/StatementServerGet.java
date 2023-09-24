package com.lukecreator.BonziBot.Script.Model.Data;

import java.util.ArrayList;

import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptGetter;

public class StatementServerGet extends ScriptGetter {
	
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_URL = "https://cdn.discordapp.com/icons/529089349762023436/de43f3f240e355be98bd71760a9ab842.webp?size=256";
	
	public StatementServerGet() {
		super();
		this.nameOfType = "Server";
		this.keyword = "sv_data";
		this.requiredType = ScriptContextInfo.class;
		
		this.propertyBindings = new ArrayList<Binding>();
		
		this.propertyBindings.add(new Binding("Name", info -> {
			return ((ScriptContextInfo)info).guild.getName();
		}));
		this.propertyBindings.add(new Binding("Owner", info -> {
			return ((ScriptContextInfo)info).guild.getOwner();
		}));
		this.propertyBindings.add(new Binding("Icon", info -> {
			String url = ((ScriptContextInfo)info).guild.getIconUrl();
			if(url == null)
				return DEFAULT_URL;
			return url;
		}));
		this.propertyBindings.add(new Binding("Banner", info -> {
			String url = ((ScriptContextInfo)info).guild.getBannerUrl();
			if(url == null)
				return DEFAULT_URL;
			return url;
		}));
		this.propertyBindings.add(new Binding("All User Count", info -> {
			return ((ScriptContextInfo)info).guild.getMemberCount();
		}));
		this.propertyBindings.add(new Binding("Member Count", info -> {
			return Long.valueOf(((ScriptContextInfo)info).guild.getMembers()
				.stream().filter(m -> !m.getUser().isBot()).count());
		}));
		this.propertyBindings.add(new Binding("Bot Count", info -> {
			return Long.valueOf(((ScriptContextInfo)info).guild.getMembers()
				.stream().filter(m -> m.getUser().isBot()).count());
		}));
	}
}
