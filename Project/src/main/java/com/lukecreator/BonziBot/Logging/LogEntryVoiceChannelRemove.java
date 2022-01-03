package com.lukecreator.BonziBot.Logging;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.Credible;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class LogEntryVoiceChannelRemove extends LogEntry {
	
	private static final long serialVersionUID = 1L;

	public LogEntryVoiceChannelRemove() {
		super(Type.VOICECHANNELREMOVE);
		this.buttonFlags = LogButtons.UNDO.flag;
	}

	public String name;
	public long deleter;
	public int position;
	
	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle("Voice Channel Removed");
		input.setDescription("Name: `" + this.name + "`");
		input.addField("Removed by:", "<@" + this.deleter + '>', false);
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		if(!(dataStructure instanceof VoiceChannelDeleteEvent))
			return;
		
		VoiceChannelDeleteEvent event = (VoiceChannelDeleteEvent)dataStructure;
		Guild guild = event.getGuild();
		
		VoiceChannel tc = event.getChannel();
		this.name = tc.getName();
		this.position = tc.getPositionRaw();
		
		guild.retrieveAuditLogs()
			.limit(1)
			.type(ActionType.CHANNEL_DELETE)
			.queue(logs -> {
				if(logs.isEmpty())
					return;
				AuditLogEntry ale = logs.get(0);
				String reason = ale.getReason();
				if(Credible.isCredibleString(reason)) {
					this.deleter = Credible.from(reason);
				} else {
					User banner = ale.getUser();
					if(banner != null)
						this.deleter = banner.getIdLong();
				}
				_success.accept(this);
			});
	}

	@Override
	public void performActionUndo(BonziBot bb, ButtonClickEvent event) {
		Guild guild = event.getGuild();
		
		guild.createVoiceChannel(this.name)
			.setPosition(this.position)
			.reason(Credible.create(event.getUser().getIdLong()))
			.queue();
		
		setOriginalFooter(event, "Undone by " + event.getUser().getAsTag() + ". Settings were not restored.");
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
