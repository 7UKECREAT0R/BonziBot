package com.lukecreator.BonziBot.Logging;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.TimeSpan;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class LogEntryMute extends LogEntry {

	private static final long serialVersionUID = 1L;
	
	long muterId;
	long mutedId;
	
	TimeSpan length;
	
	public LogEntryMute() {
		super(Type.MUTE);
		this.buttonFlags = LogButtons.UNDO.flag;
	}

	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle("User was Timed-out");
		input.setDescription("Name: <@" + this.mutedId +
			">\nTime: " + this.length.toLongString() +
			"\nUntil: " + this.length.toTimeMarkdown());
		input.addField("Muted by:", "<@" + this.muterId + '>', false);
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		// This should be implemented in JDA5 once timeouts are present.

		_success.accept(this);
	}

	@Override
	public void performActionUndo(BonziBot bb, ButtonClickEvent event) {
		// TODO un-timeout the user
		LogEntry.setOriginalFooter(event, "Undone by " + event.getUser().getAsTag());
	}
	@Override
	public void performActionWarn(BonziBot bb, ButtonClickEvent event) {
		return;
	}
	@Override
	public void performActionMute(BonziBot bb, ButtonClickEvent event) {
		return;
	}
	@Override
	public void performActionKick(BonziBot bb, ButtonClickEvent event) {
		return;
	}
	@Override
	public void performActionBan(BonziBot bb, ButtonClickEvent event) {
		return;
	}

}
