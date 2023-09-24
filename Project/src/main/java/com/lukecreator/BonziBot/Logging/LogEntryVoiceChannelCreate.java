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
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class LogEntryVoiceChannelCreate extends LogEntry {
	
	private static final long serialVersionUID = 1L;

	public LogEntryVoiceChannelCreate() {
		super(Type.VOICECHANNELCREATE);
		this.buttonFlags = LogButtons.UNDO.flag;
	}

	public String name;
	public long creator;
	public long id;
	
	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle("Voice Channel Created");
		input.setDescription("Name: `" + this.name + "`");
		input.addField("Created by:", "<@" + this.creator + '>', false);
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		if(!(dataStructure instanceof ChannelCreateEvent))
			return;
		
		ChannelCreateEvent event = (ChannelCreateEvent)dataStructure;
		if(!event.isFromGuild())
			return;
		Guild guild = event.getGuild();
		
		this.name = event.getChannel().getName();
		this.id = event.getChannel().getIdLong();
		
		guild.retrieveAuditLogs()
			.limit(1)
			.type(ActionType.CHANNEL_CREATE)
			.queue(logs -> {
				if(logs.isEmpty())
					return;
				AuditLogEntry ale = logs.get(0);
				String reason = ale.getReason();
				if(Credible.isCredibleString(reason)) {
					this.creator = Credible.from(reason);
				} else {
					User banner = ale.getUser();
					if(banner != null)
						this.creator = banner.getIdLong();
				}
				_success.accept(this);
			});
	}

	@Override
	public void performActionUndo(BonziBot bb, ButtonInteractionEvent event) {
		Guild guild = event.getGuild();
		VoiceChannel vc = guild.getVoiceChannelById(this.id);
		
		if(vc != null)
			vc.delete()
				.reason(Credible.create(event.getUser().getIdLong()))
				.queue(null, fail -> {});
		
		setOriginalFooter(event, "Undone by " + event.getUser().getName());
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
