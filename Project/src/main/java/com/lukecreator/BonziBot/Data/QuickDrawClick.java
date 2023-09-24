package com.lukecreator.BonziBot.Data;

import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiButton.ButtonColor;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class QuickDrawClick extends QuickDraw {

	enum MatchMode {
		Text,
		Color
	}
	
	MatchMode mode;
	GuiButton.ButtonColor color;
	
	QuickDrawClick(BonziBot bb) {
		this.reward = BonziUtils.randomInt(10, 35);
		this.mode = MatchMode.values()[BonziUtils.randomInt(2)];
		this.color = GuiButton.ButtonColor.values()[BonziUtils.randomInt(GuiButton.ButtonColor.values().length)];
	}
	String getText() {
		return this.mode == MatchMode.Text ?
			"Click the button that says ` " + this.color.toString() + " `":
			"Click the button that is ` " + this.color.toString() + " `";
	}
	MessageCreateAction populateMessage(MessageCreateAction action) {
		List<GuiButton.ButtonColor> colors = new ArrayList<GuiButton.ButtonColor>();
		List<String> colorStrings = new ArrayList<String>();
		List<ItemComponent> components = new ArrayList<ItemComponent>();
		
		for(GuiButton.ButtonColor color: GuiButton.ButtonColor.values()) {
			colors.add(color);
			colorStrings.add(color.toString());
		}
		
		// pick random combos
		do {
			int pick0 = BonziUtils.randomInt(colors.size());
			int pick1 = BonziUtils.randomInt(colorStrings.size());
			
			GuiButton.ButtonColor color = colors.get(pick0);
			String colorString = colorStrings.get(pick1);
			
			colors.remove(pick0);
			colorStrings.remove(pick1);
			
			GuiButton button = new GuiButton(colorString, color, encodeProtocol(colorString));
			components.add(button.toDiscord());
		}
		while(colors.size() > 0 && colorStrings.size() > 0);
		
		// add to message
		action.addActionRow(components);
		return action;
	}
	
	@Override
	public MessageCreateAction constructMessage(TextChannel channel) {
		return this.populateMessage(channel.sendMessage("` Quick Draw! ` " + this.getText()));
	}
	@Override
	public MessageCreateAction constructWinnerMessage(User winner, int coinsGained, TextChannel channel) {
		return channel.sendMessage(winner.getAsMention() + "` won the Quick Draw! ` ` +" + coinsGained + " coins! `");
	}
	
	@Override
	public boolean tryInput(Button button, String data) {
		if(this.mode == MatchMode.Text) {
			String match = this.color.toString();
			return data.equals(match);
		} else if(this.mode == MatchMode.Color) {
			return this.color == ButtonColor.fromDiscord(button.getStyle());
		}
		
		return false;
	}
	
}
