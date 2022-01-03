package com.lukecreator.BonziBot.Logging;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class LogEntryUserJoined extends LogEntry {
	
	private static final long serialVersionUID = 1L;

	public LogEntryUserJoined() {
		super(Type.USERJOINED);
		this.buttonFlags = LogButtons.NO_BUTTONS.flag;
	}

	public String avatar;
	public String fullName;
	public long id;
	
	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle("User Joined Server");
		input.setAuthor(this.fullName, null, this.avatar);
		input.setDescription("Name: `" + fullName + "`\n"
				+ "ID: `" + id + '`');
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		if(!(dataStructure instanceof GuildMemberJoinEvent))
			return;
		
		GuildMemberJoinEvent event = (GuildMemberJoinEvent)dataStructure;
		User user = event.getUser();
		
		this.fullName = user.getAsTag();
		this.avatar = user.getEffectiveAvatarUrl();
		this.id = user.getIdLong();
		
		_success.accept(this);
	}

	@Override
	public void performActionUndo(BonziBot bb, ButtonClickEvent event) {
		return;
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
