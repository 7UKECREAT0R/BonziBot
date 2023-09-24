package com.lukecreator.BonziBot.Logging;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class LogEntryClearCommand extends LogEntry {
	
	private static final long serialVersionUID = 1L;
	
	public class ClearCommandDataPacket {
		final long clearer;
		final int count;
		
		public ClearCommandDataPacket(long clearer, int count) {
			this.clearer = clearer;
			this.count = count;
		}
	}
	
	public LogEntryClearCommand() {
		super(Type.CLEARWARNS);
		this.buttonFlags = LogButtons.NO_BUTTONS.flag;
	}

	public long clearer;
	public int count;
	
	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle(this.count + BonziUtils.plural(" Message", this.count) + " Cleared");
		input.setDescription("Cleared by: <@" + this.clearer + ">");
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		if(!(dataStructure instanceof ClearCommandDataPacket))
			return;
		
		ClearCommandDataPacket packet = (ClearCommandDataPacket)dataStructure;
		
		this.clearer = packet.clearer;
		this.count = packet.count;
		
		_success.accept(this);
	}

	@Override
	public void performActionUndo(BonziBot bb, ButtonInteractionEvent event) {
		return;
	}
	@Override
	public void performActionWarn(BonziBot bb, ButtonInteractionEvent event) {
		return;
	}
	@Override
	public void performActionMute(BonziBot bb, ButtonInteractionEvent event) {
		return;
	}
	@Override
	public void performActionKick(BonziBot bb, ButtonInteractionEvent event) {
		return;
	}
	@Override
	public void performActionBan(BonziBot bb, ButtonInteractionEvent event) {
		return;
	}

}
