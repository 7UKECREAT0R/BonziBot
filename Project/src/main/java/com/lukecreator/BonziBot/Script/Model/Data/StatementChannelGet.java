package com.lukecreator.BonziBot.Script.Model.Data;

import java.util.ArrayList;

import com.lukecreator.BonziBot.Script.Model.ScriptGetter;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.GuildChannel;

public class StatementChannelGet extends ScriptGetter {
	
	public StatementChannelGet() {
		super();
		this.nameOfType = "Channel";
		this.keyword = "ch_data";
		this.requiredType = GuildChannel.class;
		
		this.propertyBindings = new ArrayList<Binding>();
		
		this.propertyBindings.add(new Binding("Name", channel -> {
			return ((GuildChannel)channel).getName();
		}));
		this.propertyBindings.add(new Binding("Mention", channel -> {
			return ((GuildChannel)channel).getAsMention();
		}));
		this.propertyBindings.add(new Binding("Category Name", channel -> {
			Category category = ((GuildChannel)channel).getParent();
			if(category == null)
				return "<no category>";
			return category.getName();
		}));
	}
	
}
