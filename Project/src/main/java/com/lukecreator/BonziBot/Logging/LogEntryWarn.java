package com.lukecreator.BonziBot.Logging;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class LogEntryWarn extends LogEntry {
	
	private static final long serialVersionUID = 1L;

	public class WarnDataPacket {
		final long warned;
		final long warner;
		final String warnReason;
		
		public WarnDataPacket(long warned, long warner, String warnReason) {
			this.warned = warned;
			this.warner = warner;
			this.warnReason = warnReason;
		}
	}
	public long warned;
	public long warner;
	public String warnReason;
	
	public LogEntryWarn() {
		super(Type.WARN);
		this.buttonFlags = LogButtons.NO_BUTTONS.flag;
	}

	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle("User Warned");
		input.setDescription("Name: <@" + this.warned + '>');
		input.addField("Warned by:", "<@" + this.warned + '>', false);
		input.addField("Content:", "```\n" + this.warnReason + "\n```", false);
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		if(!(dataStructure instanceof WarnDataPacket))
			return;
		
		WarnDataPacket packet = (WarnDataPacket)dataStructure;
		this.warned = packet.warned;
		this.warner = packet.warner;
		this.warnReason = packet.warnReason;
		
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
