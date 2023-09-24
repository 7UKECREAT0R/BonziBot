package com.lukecreator.BonziBot.Script.Model.Data;

import java.util.ArrayList;

import com.lukecreator.BonziBot.Script.Model.ScriptGetter;

import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;


public class StatementChannelGet extends ScriptGetter {
	
	private static final long serialVersionUID = 1L;

	public StatementChannelGet() {
		super();
		this.nameOfType = "Channel";
		this.keyword = "ch_data";
		this.requiredType = GuildChannel.class;
		
		this.propertyBindings = new ArrayList<Binding>();
		
		this.propertyBindings.add(new Binding("Name", (channel) -> {
			return ((GuildChannel)channel).getName();
		}));
		this.propertyBindings.add(new Binding("Mention", channel -> {
			return ((GuildChannel)channel).getAsMention();
		}));
		this.propertyBindings.add(new Binding("Category Name", channel -> {
			Category category;
			if(channel instanceof TextChannel)
				category = ((TextChannel)channel).getParentCategory();
			if(channel instanceof VoiceChannel)
				category = ((VoiceChannel)channel).getParentCategory();
			else
				category = null;
			
			if(category == null)
				return "";
			return category.getName();
		}));
	}
	
}
