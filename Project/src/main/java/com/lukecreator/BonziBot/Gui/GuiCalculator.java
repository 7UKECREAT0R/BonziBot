package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiNewline;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class GuiCalculator extends Gui {
	
	enum OpType {
		NONE,
		ADD, SUB, MUL, DIV
	}
	
	List<String> addedFriends = new ArrayList<String>();
	boolean inGuild;
	public GuiCalculator(boolean inGuild) {
		this.inGuild = inGuild;
	}
	
	boolean closed = false;
	
	// The operation to be performed.
	OpType operation = OpType.NONE;
	
	// The left value.
	boolean hasValue = false;
	double value = 0.0;
	
	// The right value being entered.
	double buffer = 0.0;
	
	boolean dot = false;
	int dotLevel = 0;
	
	@Override
	public void initialize(JDA jda) {
		this.reinitialize();
	}
	public void reinitialize() {
		this.elements.clear();
		
		this.elements.add(new GuiButton(this.closed ? "/\\" : "\\/",
			this.closed ? GuiButton.ButtonColor.GREEN : GuiButton.ButtonColor.BLUE, "close"));
		
		if(this.closed)
			return;
		
		this.elements.add(((GuiButton)GuiButton.singleEmoji(GenericEmoji.fromEmoji("🫂"), "friends")
				.asEnabled(this.inGuild)).withColor(GuiButton.ButtonColor.BLUE));
		
		this.elements.add(new GuiButton("C", GuiButton.ButtonColor.RED, "clear"));
		this.elements.add(new GuiButton("/", GuiButton.ButtonColor.BLUE, "div"));
		this.elements.add(new GuiNewline());
		
		this.elements.add(new GuiButton("7", GuiButton.ButtonColor.GRAY, "7"));
		this.elements.add(new GuiButton("8", GuiButton.ButtonColor.GRAY, "8"));
		this.elements.add(new GuiButton("9", GuiButton.ButtonColor.GRAY, "9"));
		this.elements.add(new GuiButton("x", GuiButton.ButtonColor.BLUE, "mul"));
		this.elements.add(new GuiNewline());
		
		this.elements.add(new GuiButton("4", GuiButton.ButtonColor.GRAY, "4"));
		this.elements.add(new GuiButton("5", GuiButton.ButtonColor.GRAY, "5"));
		this.elements.add(new GuiButton("6", GuiButton.ButtonColor.GRAY, "6"));
		this.elements.add(new GuiButton("-", GuiButton.ButtonColor.BLUE, "sub"));
		this.elements.add(new GuiNewline());
		
		this.elements.add(new GuiButton("1", GuiButton.ButtonColor.GRAY, "1"));
		this.elements.add(new GuiButton("2", GuiButton.ButtonColor.GRAY, "2"));
		this.elements.add(new GuiButton("3", GuiButton.ButtonColor.GRAY, "3"));
		this.elements.add(new GuiButton("+", GuiButton.ButtonColor.BLUE, "add"));
		this.elements.add(new GuiNewline());
		
		this.elements.add(new GuiButton("+/-", GuiButton.ButtonColor.GRAY, "invert"));
		this.elements.add(new GuiButton("0", GuiButton.ButtonColor.GRAY, "0"));
		this.elements.add(new GuiButton(".", GuiButton.ButtonColor.GRAY, "dot"));
		this.elements.add(new GuiButton("=", GuiButton.ButtonColor.BLUE, "equ"));
	}
	
	@Override
	public void onButtonClick(String actionId, long executorId, JDA jda) {
		if(actionId.equals("friends")) {
			if(!this.inGuild)
				return;
			MessageChannel mc = this.parent.getChannel(jda);
			EventWaiterManager ewm = this.bonziReference.eventWaiter;
			
			mc.sendMessageEmbeds(BonziUtils.quickEmbed("Mention somebody!", "Add your bros to the calculator session!",
					BonziUtils.COLOR_BONZI_PURPLE).build()).queue(deleteSoon -> {
				ewm.waitForResponse(this.parent.ownerId, response -> {
					List<User> add = response.getMentionedUsers();
					response.delete().queue(null, fail -> {});
					deleteSoon.delete().queue(null, fail -> {});
					if(this.addedFriends.size() + add.size() > 24) {
						mc.sendMessage("ok thats a little excessive lmao").queue();
						return;
					}
					for(User user: add) {
						if(user.getIdLong() == this.parent.ownerId)
							continue;
						this.parent.ownerWhitelist.add(user.getIdLong());
						this.addedFriends.add(user.getName());
					}
					this.parent.redrawMessage(jda);
				});
			});
		}
		if(actionId.equals("close")) {
			this.closed = !this.closed;
			this.reinitialize();
			this.parent.redrawMessage(jda);
			return;
		}
		if(actionId.equals("clear")) {
			hasValue = false;
			value = 0;
			buffer = 0;
			dot = false;
			dotLevel = 0;
			operation = OpType.NONE;
			this.parent.redrawMessage(jda);
			return;
		}
		if(actionId.equals("invert")) {
			this.buffer *= -1;
			this.parent.redrawMessage(jda);
			return;
		}
		if(actionId.equals("dot")) {
			this.dot = true;
			this.dotLevel = 0;
			this.parent.redrawMessage(jda);
			return;
		}
		
		if(actionId.equals("div")) {
			this.changeMode(OpType.DIV);
			this.parent.redrawMessage(jda);
			return;
		}
		if(actionId.equals("mul")) {
			this.changeMode(OpType.MUL);
			this.parent.redrawMessage(jda);
			return;
		}
		if(actionId.equals("sub")) {
			this.changeMode(OpType.SUB);
			this.parent.redrawMessage(jda);
			return;
		}
		if(actionId.equals("add")) {
			this.changeMode(OpType.ADD);
			this.parent.redrawMessage(jda);
			return;
		}
		
		if(actionId.equals("equ")) {
			combineValues();
			this.parent.redrawMessage(jda);
			return;
		}
		
		try {
			int num = Integer.parseInt(actionId);
			if(this.dot) {
				double exp = Math.pow(0.1, ++this.dotLevel);
				this.buffer += exp * num;
			} else {
				this.buffer = this.buffer * 10.0 + num;
			}
			
			this.parent.redrawMessage(jda);
			return;
		} catch(NumberFormatException nfe) {}
	}
	
	void changeMode(OpType type) {
		if(this.operation == OpType.NONE) {
			if(this.hasValue) {
				combineValues();
				return;
			} else {
				this.hasValue = true;
				this.value = this.buffer;
				this.buffer = 0;
				this.dot = false;
				this.dotLevel = 0;
			}
			this.operation = type;
		} else {
			this.operation = type;
		}
	}
	void combineValues() {
		switch(this.operation) {
		case ADD:
			if(this.value == 9 && this.buffer == 10)
				this.buffer = 21;
			else
				this.buffer = this.value + this.buffer;
			break;
		case SUB:
			this.buffer = this.value - this.buffer;
			break;
		case MUL:
			this.buffer = this.value * this.buffer;
			break;
		case DIV:
			this.buffer = this.value / this.buffer;
			break;
		default:
			break;
		}
		
		if(this.operation != OpType.NONE) {
			this.operation = OpType.NONE;
			this.dot = false;
			this.dotLevel = 0;
			this.hasValue = false;
			this.value = 0;
		}
	}
	
	@Override
	public Object draw(JDA jda) {
		String desc = "```\n" + this.drawDesc() + "\n```";
		if(!this.addedFriends.isEmpty()) {
			String list = BonziUtils.stringJoinAnd(", ", this.addedFriends);
			desc = "Calculating with " + list + "\n" + desc;
		}
		EmbedBuilder eb = new EmbedBuilder()
			.setColor(Color.gray)
			.setTitle("CALCULATOR")
			.setDescription(desc);
		return eb.build();
	}
	public String drawDesc() {
		String buf = doubleFmt(this.buffer);
		if(this.dot && this.dotLevel == 0)
			buf += ".";
		
		if(this.operation == OpType.NONE)
			return buf;
		String ret = doubleFmt(this.value) + " " +
			this.operationChar() + " " + buf;
		return ret;
	}
	public String doubleFmt(double value) {
	    if(value == (long)value)
	        return String.format("%d",(long)value);
	    else
	        return String.format("%s", value);
	}
	public char operationChar() {
		switch(this.operation) {
		case ADD:
			return '+';
		case DIV:
			return '/';
		case MUL:
			return 'x';
		case SUB:
			return '-';
		default:
			return ' ';
		}
	}
}