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
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class LogEntryTextChannelCreate extends LogEntry {
	
	private static final long serialVersionUID = 1L;

	public LogEntryTextChannelCreate() {
		super(Type.TEXTCHANNELCREATE);
		this.buttonFlags = LogButtons.UNDO.flag;
	}

	public String name;
	public long creator;
	public long id;
	
	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle("Text Channel Created");
		input.setDescription("Name: `" + this.name + "`");
		input.addField("Created by:", "<@" + this.creator + '>', false);
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		if(!(dataStructure instanceof TextChannelCreateEvent))
			return;
		
		TextChannelCreateEvent event = (TextChannelCreateEvent)dataStructure;
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
	public void performActionUndo(BonziBot bb, ButtonClickEvent event) {
		Guild guild = event.getGuild();
		TextChannel tc = guild.getTextChannelById(this.id);
		
		if(tc != null)
			tc.delete()
				.reason(Credible.create(event.getUser().getIdLong()))
				.queue(null, fail -> {});
		
		setOriginalFooter(event, "Undone by " + event.getUser().getAsTag());
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
