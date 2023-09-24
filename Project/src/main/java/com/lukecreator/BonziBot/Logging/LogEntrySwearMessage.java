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
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * User has just sworn and is now on the verge of extinction.
 * @author Lukec
 */
public class LogEntrySwearMessage extends LogEntry {
	
	private static final long serialVersionUID = 1L;

	public LogEntrySwearMessage() {
		super(Type.SWEARMESSAGE);
		this.buttonFlags = LogButtons.WARN.flag | LogButtons.MUTE.flag | LogButtons.KICK.flag;
	}
	
	public long sender;
	public int attachments;
	public String content;

	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle("User Violated Filter");
		input.setDescription("Sender: <@" + this.sender + '>');
		
		String display = "```\n" + this.content + "\n```";
		if(this.attachments > 0)
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
		
		// hate that i still have to respect this...
		if(bb.accounts.getUserAccount(this.sender).optOutExpose)
			this.content = "[content private but trust me they did]";
		else
			this.content = msg.getContentRaw();
		
		_success.accept(this);
	}

	@Override
	public void performActionUndo(BonziBot bb, ButtonInteractionEvent event) {
		return;
	}
	@Override
	public void performActionWarn(BonziBot bb, ButtonInteractionEvent event) {
		String preview = BonziUtils.cutOffString(this.content, 50);
		String warnFor = "Violating filter: \"" + preview + "\"";
		
		User user = event.getUser();
		Guild guild = event.getGuild();
		UserAccount account = bb.accounts.getUserAccount(user);

		warnUser(account, user, guild, warnFor);
		setOriginalFooter(event, "Warned by " + event.getUser().getName());
	}
	@Override
	public void performActionMute(BonziBot bb, ButtonInteractionEvent event) {
		// TODO prompt for timeout length
	}
	@Override
	public void performActionKick(BonziBot bb, ButtonInteractionEvent event) {
		Guild guild = event.getGuild();
		
		guild.kick(UserSnowflake.fromId(this.sender))
			.reason(Credible.create(event.getUser().getIdLong()))
			.queue(null, fail -> {});
		
		setOriginalFooter(event, "Kicked by " + event.getUser().getName());
	}
	@Override
	public void performActionBan(BonziBot bb, ButtonInteractionEvent event) {
		return;
	}
}
