package com.lukecreator.BonziBot.Logging;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class LogEntryClearWarns extends LogEntry {
	
	private static final long serialVersionUID = 1L;
	
	public class ClearWarnsDataPacket {
		final long cleared;
		final long clearer;
		final int count;
		
		private ClearWarnsDataPacket(long cleared, long clearer, int count) {
			this.cleared = cleared;
			this.clearer = clearer;
			this.count = count;
		}
	}
	
	public LogEntryClearWarns() {
		super(Type.CLEARWARNS);
		this.buttonFlags = LogButtons.NO_BUTTONS.flag;
	}

	public long cleared;
	public long clearer;
	public int count;
	
	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle(this.count + BonziUtils.plural(" Warn", this.count) + " Cleared");
		input.setDescription("For user: <@" + this.cleared + '>');
		input.addField("Cleared by:", "<@" + this.clearer + '>', false);
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		if(!(dataStructure instanceof ClearWarnsDataPacket))
			return;
		
		ClearWarnsDataPacket packet = (ClearWarnsDataPacket)dataStructure;
		
		this.cleared = packet.cleared;
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
