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
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class LogEntryBan extends LogEntry {
	
	private static final long serialVersionUID = 1L;
	public static long ignoreBanId = 0l;
	
	String reason;
	long bannerId;
	long bannedId;
	
	public LogEntryBan() {
		super(Type.BAN);
		this.buttonFlags = LogButtons.UNDO.flag;
	}

	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		if(this.bannedId == ignoreBanId)
			return null;
		
		input.setTitle("User was Banned.");
		if(reason == null)
			input.setDescription("Name: <@" + this.bannedId + '>');
		else {
			input.setDescription(
				"Name: <@" + this.bannedId + 
				">\nReason: " + this.reason);
		}
		input.addField("Banned by:", "<@" + this.bannerId + '>', false);
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		if(!(dataStructure instanceof GuildBanEvent))
			return;
		
		GuildBanEvent event = (GuildBanEvent)dataStructure;
		
		User banned = event.getUser();
		Guild guild = event.getGuild();
		
		this.bannedId = banned.getIdLong();
		
		guild.retrieveAuditLogs()
			.limit(1)
			.type(ActionType.BAN)
			.queue(entries -> {
				if(entries.isEmpty())
					return;
				AuditLogEntry entry = entries.get(0);
				this.reason = entry.getReason();
				if(Credible.isCredibleString(this.reason)) {
					this.bannerId = Credible.from(this.reason);
				} else {
					User banner = entry.getUser();
					if(banner != null)
						this.bannerId = banner.getIdLong();
				}

				_success.accept(this);
			}, _failure);
	}

	@Override
	public void performActionUndo(BonziBot bb, ButtonClickEvent event) {
		event.getGuild()
			.unban(String.valueOf(this.bannedId))
			.reason(Credible.create(event.getUser()))
			.queue(null, fail -> {});
		
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
