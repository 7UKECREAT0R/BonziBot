package com.lukecreator.BonziBot.Logging;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.Credible;
import com.lukecreator.BonziBot.Data.UserAccount;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class LogEntryNicknameChange extends LogEntry {
	
	private static final long serialVersionUID = 1L;
	
	public LogEntryNicknameChange() {
		super(Type.NICKNAMECHANGE);
		this.buttonFlags = LogButtons.UNDO.flag | LogButtons.WARN.flag;
	}
	
	public long user;
	public long changer;
	
	public String defaultName;
	public @Nullable String oldNickname;
	public @Nullable String newNickname;
	
	@Override
	public MessageEmbed toEmbed(EmbedBuilder input) {
		input.setTitle("Nickname Changed");
		input.setDescription("User: <@" + this.user + ">\n");
		
		String old = (this.oldNickname == null ? this.defaultName : this.oldNickname).replace("`", "\\`");
		String now = (this.newNickname == null ? this.defaultName : this.newNickname).replace("`", "\\`");
		String change = "`" + old + "` ➜ `" + now + "`";
		
		input.addField("Change:", change, false);
		input.addField("Changed by:", "<@" + this.changer + '>', false);
		
		return input.build();
	}

	@Override
	public void loadData(Object dataStructure, BonziBot bb, Consumer<LogEntry> _success, Consumer<Throwable> _failure) {
		if(!(dataStructure instanceof GuildMemberUpdateNicknameEvent))
			return;
		
		GuildMemberUpdateNicknameEvent event = (GuildMemberUpdateNicknameEvent)dataStructure;
		
		User user = event.getUser();
		Guild guild = event.getGuild();
		
		this.user = user.getIdLong();
		this.oldNickname = event.getOldNickname();
		this.newNickname = event.getNewNickname();
		this.defaultName = user.getName();
		
		// consult audit logs for changer
		guild.retrieveAuditLogs()
			.limit(1)
			.type(ActionType.MEMBER_UPDATE)
			.queue(logs -> {
				if(logs.isEmpty())
					return;
				AuditLogEntry ale = logs.get(0);
				String reason = ale.getReason();
				
				if(Credible.isCredibleString(reason))
					this.changer = Credible.from(reason);
				else {
					User performer = ale.getUser();
					if(performer != null)
						this.changer = performer.getIdLong();
				}
				_success.accept(this);
			}, _failure);
	}

	@Override
	public void performActionUndo(BonziBot bb, ButtonInteractionEvent event) {
		Guild guild = event.getGuild();
		Member target = guild.getMemberById(this.user);
		
		guild.modifyNickname(target, this.oldNickname)
			.reason(Credible.create(this.user))
			.queue(null, fail -> {});
		
		setOriginalFooter(event, "Undone by " + event.getUser().getName());
	}

	@Override
	public void performActionWarn(BonziBot bb, ButtonInteractionEvent event) {
		String effectiveNickname = this.newNickname == null ? this.defaultName : this.newNickname;
		String warnFor = "Setting nickname to \"" + effectiveNickname + "\"";
		
		User user = event.getUser();
		Guild guild = event.getGuild();
		UserAccount account = bb.accounts.getUserAccount(user);

		warnUser(account, user, guild, warnFor);
		setOriginalFooter(event, "Warned by " + event.getUser().getName());
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
