package com.lukecreator.BonziBot.Logging;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class LogEntryUnmute extends LogEntry {

	private static final long serialVersionUID = 1L;
	
	long mutedId;
	
	public LogEntryUnmute() {
		super(Type.UNMUTE);
		this.buttonFlags = LogButtons.UNDO.flag;
	}

	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle("User has been un-timed-out.");
		input.setDescription("Name: <@" + this.mutedId + '>');
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		// This should be implemented in JDA5 once timeouts are present.

		_success.accept(this);
	}

	@Override
	public void performActionUndo(BonziBot bb, ButtonInteractionEvent event) {
		// TODO prompt for time to re-timeout the user for
		LogEntry.setOriginalFooter(event, "Undone by " + event.getUser().getName());
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
