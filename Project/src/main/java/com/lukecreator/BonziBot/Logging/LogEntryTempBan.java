package com.lukecreator.BonziBot.Logging;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.TimeSpan;
import com.lukecreator.BonziBot.Data.Credible;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class LogEntryTempBan extends LogEntry {
	
	public class TempBanDataPacket {
		final long banner;
		final long banned;
		final TimeSpan length;
		
		public TempBanDataPacket(long bannerId, long bannedId, TimeSpan length) {
			this.banner = bannerId;
			this.banned = bannedId;
			this.length = length;
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	long banner;
	long banned;
	TimeSpan length;
	
	public LogEntryTempBan() {
		super(Type.BANTEMP);
		this.buttonFlags = LogButtons.UNDO.flag;
	}

	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle("User was Temporarily Banned.");
		input.setDescription(
			"Name: <@" + this.banned +
			">\nTime: " + this.length.toLongString() +
			"\nUntil: " + this.length.toTimeMarkdown());
		input.addField("Banned by:", "<@" + this.banner + '>', false);
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		if(!(dataStructure instanceof TempBanDataPacket))
			return;
		
		TempBanDataPacket packet = (TempBanDataPacket)dataStructure;
		
		this.length = packet.length;
		this.banned = packet.banned;
		this.banner = packet.banner;
		
		LogEntryBan.ignoreBanId = this.banned;
		_success.accept(this);
	}

	@Override
	public void performActionUndo(BonziBot bb, ButtonInteractionEvent event) {
		event.getGuild()
			.unban(UserSnowflake.fromId(this.banned))
			.reason(Credible.create(event.getUser()))
			.queue(success -> {
				bb.bans.unban(event.getGuild().getIdLong(), this.banned);
			}, fail -> {});
		
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
