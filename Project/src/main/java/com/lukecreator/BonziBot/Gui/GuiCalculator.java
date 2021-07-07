package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
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
		this.buttons.clear();
		
		this.buttons.add(new GuiButton(this.closed ? "/\\" : "\\/",
			this.closed ? GuiButton.Color.GREEN : GuiButton.Color.BLUE, "close"));
		
		if(this.closed)
			return;
		
		this.buttons.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("🫂"), "friends")
			.asEnabled(this.inGuild).withColor(GuiButton.Color.BLUE));
		this.buttons.add(new GuiButton("C", GuiButton.Color.RED, "clear"));
		this.buttons.add(new GuiButton("/", GuiButton.Color.BLUE, "div"));
		this.buttons.add(GuiButton.newline());
		
		this.buttons.add(new GuiButton("7", GuiButton.Color.GRAY, "7"));
		this.buttons.add(new GuiButton("8", GuiButton.Color.GRAY, "8"));
		this.buttons.add(new GuiButton("9", GuiButton.Color.GRAY, "9"));
		this.buttons.add(new GuiButton("x", GuiButton.Color.BLUE, "mul"));
		this.buttons.add(GuiButton.newline());
		
		this.buttons.add(new GuiButton("4", GuiButton.Color.GRAY, "4"));
		this.buttons.add(new GuiButton("5", GuiButton.Color.GRAY, "5"));
		this.buttons.add(new GuiButton("6", GuiButton.Color.GRAY, "6"));
		this.buttons.add(new GuiButton("-", GuiButton.Color.BLUE, "sub"));
		this.buttons.add(GuiButton.newline());
		
		this.buttons.add(new GuiButton("1", GuiButton.Color.GRAY, "1"));
		this.buttons.add(new GuiButton("2", GuiButton.Color.GRAY, "2"));
		this.buttons.add(new GuiButton("3", GuiButton.Color.GRAY, "3"));
		this.buttons.add(new GuiButton("+", GuiButton.Color.BLUE, "add"));
		this.buttons.add(GuiButton.newline());
		
		this.buttons.add(new GuiButton("+/-", GuiButton.Color.GRAY, "invert"));
		this.buttons.add(new GuiButton("0", GuiButton.Color.GRAY, "0"));
		this.buttons.add(new GuiButton(".", GuiButton.Color.GRAY, "dot"));
		this.buttons.add(new GuiButton("=", GuiButton.Color.BLUE, "equ"));
	}
	
	@Override
	public void onAction(String actionId, long executorId, JDA jda) {
		if(actionId.equals("friends")) {
			if(!this.inGuild)
				return;
			MessageChannel mc = this.parent.getChannel(jda);
			EventWaiterManager ewm = this.bonziReference.eventWaiter;
			
			mc.sendMessage(BonziUtils.quickEmbed("Mention somebody!", "Add your bros to the calculator session!",
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