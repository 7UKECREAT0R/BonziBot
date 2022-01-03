package com.lukecreator.BonziBot.Logging;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.Credible;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class LogEntryTextChannelRemove extends LogEntry {
	
	private static final long serialVersionUID = 1L;

	public LogEntryTextChannelRemove() {
		super(Type.TEXTCHANNELREMOVE);
		this.buttonFlags = LogButtons.UNDO.flag;
	}

	public String name;
	public String topic;
	public long deleter;
	public int position;
	
	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle("Text Channel Removed");
		input.setDescription("Name: `" + this.name + '`');
		input.addField("Removed by:", "<@" + this.deleter + '>', false);
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		if(!(dataStructure instanceof TextChannelDeleteEvent))
			return;
		
		TextChannelDeleteEvent event = (TextChannelDeleteEvent)dataStructure;
		Guild guild = event.getGuild();
		
		TextChannel tc = event.getChannel();
		this.name = tc.getName();
		this.topic = tc.getTopic();
		this.position = tc.getPositionRaw();
		
		if(this.topic == null)
			this.topic = "";
		
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
		
		guild.createTextChannel(this.name)
			.setTopic(this.topic)
			.setPosition(this.position)
			.reason(Credible.create(event.getUser().getIdLong()))
			.queue();
		
		setOriginalFooter(event, "Undone by " + event.getUser().getAsTag() + ". Messages could not be restored.");
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
