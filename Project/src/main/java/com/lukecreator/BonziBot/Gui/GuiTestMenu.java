package com.lukecreator.BonziBot.Gui;

import java.awt.Color;

import com.lukecreator.BonziBot.GuiAPI.DropdownItem;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiButton.ButtonColor;
import com.lukecreator.BonziBot.GuiAPI.GuiDropdown;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class GuiTestMenu extends Gui {
	
	enum Things {
		Spaghetti,
		Apples,
		Bananas,
		Kelp,
		TripleCheesecake,
		Spinach,
		Hushpuppy,
		OnionRing,
		Wallpaper
	}
	
	final Things[] set = Things.values();
	
	Things selected = null;
	
	@Override
	public void initialize(JDA jda) {
		super.initialize(jda);
		
		this.elements.add(new GuiDropdown("Favorite Food", "fav", false).addItemsTransform(this.set, thing -> {
			return new DropdownItem(thing, thing.toString());
		}));
		this.elements.add(new GuiButton("SEND", ButtonColor.GREEN, "send"));
	}
	
	@Override
	public Object draw(JDA jda) {
		
		String selection = this.selected == null ? "none" : this.selected.toString();
		
		EmbedBuilder eb = new EmbedBuilder()
			.setColor(Color.magenta)
			.setTitle("dropdowns test wOOOoooOOoo")
			.setDescription("Current Selection: " + selection);
		return eb.build();
	}
	
	@Override
	public void onButtonClick(String buttonId, long clickerId, JDA jda) {
		if(buttonId.equals("send")) {
			MessageChannelUnion channel = this.parent.getChannel(jda);
			channel.sendMessage("YOU PICKED: \"" + this.selected.toString() + "\"").queue();
		}
	}
	
	@Override
	public void onDropdownChanged(GuiDropdown dropdown, long clickerId, JDA jda) {
		Things selected = (Things)dropdown.getSelectedObject();
		this.selected = selected;
		this.parent.redrawMessage(jda);
	}
}