package com.lukecreator.BonziBot.Gui;

import java.awt.Color;

import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiPaging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;

public class GuiTestMenu extends GuiPaging {
	
	enum Things {
		Spaghetti,
		Apples,
		Bananas,
		Kelp,
		TripleCheesecake,
		Spinach
	}
	final Things[] set = Things.values();
	
	@Override
	public void initialize(JDA jda) {
		super.initialize(jda);
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("🔵"), "Choose!", GuiButton.Color.GREEN, "pick"));
		this.maxPage = set.length;
	}
	
	@Override
	public Object draw(JDA jda) {
		String sel = set[this.currentPage - 1].toString();
		EmbedBuilder eb = new EmbedBuilder()
			.setColor(Color.magenta)
			.setTitle("Paging Test!")
			.setDescription("Pick your favorite food!");
		eb.addField(this.getPageString(), sel, false);
		return eb.build();
	}
	
	@Override
	public void onAction(String actionId, long executorId, JDA jda) {
		super.onAction(actionId, executorId, jda);
		
		if(actionId.equals("pick")) {
			this.pagingEnabled = false;
			String sel = set[this.currentPage - 1].toString();
			MessageChannel channel = parent.getChannel(jda);
			channel.sendMessage("Selected: " + sel).queue();
		}
	}
}