package com.lukecreator.BonziBot.Logging;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.Credible;
import com.lukecreator.BonziBot.Data.UserAccount;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class LogEntryDeletedMessage extends LogEntry {
	
	private static final long serialVersionUID = 1L;

	public LogEntryDeletedMessage() {
		super(Type.DELETEDMESSAGE);
		this.buttonFlags = LogButtons.WARN.flag | LogButtons.MUTE.flag | LogButtons.KICK.flag;
	}
	
	public long sender;
	public int attachments;
	public String content;

	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle("Message Deleted");
		input.setDescription("Sender: <@" + this.sender + '>');
		
		String display = "```\n" + this.content + "\n```";
		if(attachments > 0)
			display = "🖼️ " + this.attachments + BonziUtils.plural(" Attachment", this.attachments) + "\n" + display;
		
		input.addField("Content:", display, false);
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		if(!(dataStructure instanceof Message))
			return;
		
		Message msg = (Message)dataStructure;
		this.attachments = msg.getAttachments().size();
		this.sender = msg.getAuthor().getIdLong();
		
		if(bb.accounts.getUserAccount(this.sender).optOutExpose)
			this.content = "[content hidden]";
		else
			this.content = msg.getContentRaw();
		
		_success.accept(this);
	}

	@Override
	public void performActionUndo(BonziBot bb, ButtonClickEvent event) {
		return;
	}

	@Override
	public void performActionWarn(BonziBot bb, ButtonClickEvent event) {
		String preview = BonziUtils.cutOffString(this.content, 50);
		String warnFor = "Sending \"" + preview + "\"";
		
		User user = event.getUser();
		Guild guild = event.getGuild();
		UserAccount account = bb.accounts.getUserAccount(user);

		warnUser(account, user, guild, warnFor);
		setOriginalFooter(event, "Warned by " + event.getUser().getAsTag());
	}

	@Override
	public void performActionMute(BonziBot bb, ButtonClickEvent event) {
		// TODO prompt for timeout length
	}

	@Override
	public void performActionKick(BonziBot bb, ButtonClickEvent event) {
		Guild guild = event.getGuild();
		
		guild.kick(String.valueOf(this.sender))
			.reason(Credible.create(event.getUser().getIdLong()))
			.queue(null, fail -> {});
		
		setOriginalFooter(event, "Kicked by " + event.getUser().getAsTag());
	}

	@Override
	public void performActionBan(BonziBot bb, ButtonClickEvent event) {
		return;
	}

}
