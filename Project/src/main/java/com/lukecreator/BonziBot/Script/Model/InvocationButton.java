package com.lukecreator.BonziBot.Script.Model;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiButton.ButtonColor;

import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

public class InvocationButton implements InvocationMethod {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Implementation getImplementation() {
		return Implementation.BUTTON;
	}
	
	public String buttonText;
	public ButtonColor buttonColor;
	
	public MessageAction addButton(MessageAction in, String call) {
		return BonziUtils.appendComponents(in, new GuiButton[] {
			new GuiButton(this.buttonText, this.buttonColor, "::" + call)
		}, false);
	}
	public ReplyAction addButton(ReplyAction in, String call) {
		return BonziUtils.appendComponents(in, new GuiButton[] {
			new GuiButton(this.buttonText, this.buttonColor, "::" + call)
		});
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
