package com.lukecreator.BonziBot.Logging;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.Credible;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class LogEntryUnban extends LogEntry {
	
	private static final long serialVersionUID = 1L;
	
	String reason;
	long unbannerId;
	long unbannedId;
	
	public LogEntryUnban() {
		super(Type.UNBAN);
		this.buttonFlags = LogButtons.NO_BUTTONS.flag;
	}

	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle("User was Unbanned.");
		if(this.reason == null)
			input.setDescription("Name: <@" + this.unbannedId + '>');
		else {
			input.setDescription(
				"Name: <@" + this.unbannedId + 
				">\nReason: " + this.reason);
		}
		input.addField("Unbanned by:", "<@" + this.unbannerId + '>', false);
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		if(!(dataStructure instanceof GuildUnbanEvent))
			return;
		
		GuildUnbanEvent event = (GuildUnbanEvent)dataStructure;
		
		User banned = event.getUser();
		Guild guild = event.getGuild();
		
		this.unbannedId = banned.getIdLong();
		
		guild.retrieveAuditLogs()
			.limit(1)
			.type(ActionType.UNBAN)
			.queue(entries -> {
				if(entries.isEmpty())
					return;
				AuditLogEntry entry = entries.get(0);
				this.reason = entry.getReason();
				if(Credible.isCredibleString(this.reason)) {
					this.unbannerId = Credible.from(this.reason);
				} else {
					User banner = entry.getUser();
					if(banner != null)
						this.unbannerId = banner.getIdLong();
				}
				_success.accept(this);
			}, _failure);
	}

	@Override
	public void performActionUndo(BonziBot bb, ButtonInteractionEvent event) {
		event.getGuild()
			.ban(UserSnowflake.fromId(this.unbannedId), 0, TimeUnit.DAYS)
			.reason(Credible.create(event.getUser()))
			.queue(null, fail -> {});
		
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
