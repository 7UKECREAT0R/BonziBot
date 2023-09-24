package com.lukecreator.BonziBot.Script.Model;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiButton.ButtonColor;

import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class InvocationButton implements InvocationMethod {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Implementation getImplementation() {
		return Implementation.BUTTON;
	}
	
	public String buttonText;
	public ButtonColor buttonColor;
	
	public MessageCreateAction addButton(MessageCreateAction in, String call) {
		return BonziUtils.appendComponents(in, new GuiButton[] {
			new GuiButton(this.buttonText, this.buttonColor, "::" + call)
		}, false);
	}
	public MessageEditAction addButton(MessageEditAction in, String call) {
		return BonziUtils.appendComponents(in, new GuiButton[] {
			new GuiButton(this.buttonText, this.buttonColor, "::" + call)
		}, false);
	}
	public ReplyCallbackAction addButton(ReplyCallbackAction in, String call) {
		return BonziUtils.appendComponents(in, new GuiButton[] {
			new GuiButton(this.buttonText, this.buttonColor, "::" + call)
		}, false);
	}
	@Override
	public String[] getEventVariables() {
		return new String[] {
			"member", "channel"
		};
	}
	@Override
	public String getAsExplanation() {
		return "Run when the button \"" + this.buttonText + "\" is clicked. /scriptbutton";
	}
}
