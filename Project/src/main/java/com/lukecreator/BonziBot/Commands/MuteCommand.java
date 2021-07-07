package com.lukecreator.BonziBot.Commands;

import java.awt.Color;
import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.TimeSpan;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.StringRemainderArg;
import com.lukecreator.BonziBot.CommandAPI.TimeSpanArg;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Managers.MuteManager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class MuteCommand extends Command {

	public MuteCommand() {
		this.subCategory = 2;
		this.name = "Mute";
		this.unicodeIcon = "🤐";
		this.description = "Prevent a user from talking in all channels.";
		this.args = new CommandArgCollection(
			new UserArg("target"),
			new TimeSpanArg("time").optional(),
			new StringRemainderArg("reason").optional());
		this.userRequiredPermissions = new Permission[] { Permission.MANAGE_ROLES };
		this.neededPermissions = new Permission[] { Permission.MANAGE_ROLES };
		this.setCooldown(1000);
		this.worksInDms = false;
		this.category = CommandCategory.MODERATION;
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		
		User target = e.args.getUser("target");
		boolean temporary = e.args.argSpecified("time");
		boolean hasReason = e.args.argSpecified("reason");
		TimeSpan time = e.args.getTimeSpan("time");
		String reason = e.args.getString("reason");
		
		long roleId = e.settings.mutedRole;
		Role role = roleId == 0l ? null : e.guild.getRoleById(roleId);
		
		Consumer<Role> actuallyMute = give -> {
			Guild guild = give.getGuild();
			Member targetMember = guild.getMember(target);
			MuteManager mutes = e.bonzi.mutes;
			if(mutes.isMuted(targetMember)) {
				if(e.isSlashCommand && !e.slashCommand.isAcknowledged())
					e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("User's already muted.")).queue();
				else e.channel.sendMessage(BonziUtils.failureEmbed("User's already muted.")).queue();
				return;
			}
			
			if(temporary)
				mutes.mute(targetMember, System.currentTimeMillis() + time.ms);
			else mutes.mute(targetMember);
			
			try {
				guild.addRoleToMember(targetMember, give).reason(reason).queue(null, fail -> {
					if(e.isSlashCommand && !e.slashCommand.isAcknowledged())
						e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("Could not give 'Silenced' role. (no idea why)")).queue();
					else e.channel.sendMessage(BonziUtils.failureEmbed("Could not give 'Silenced' role. (no idea why)")).queue();
				});
				
				MessageEmbed me = BonziUtils.successEmbed("User has been muted.", "Reason: `" + (hasReason ?
					reason : "unspecified") + "`\nTime: `" + (temporary ? time.toLongString() : "permanently") + "`");
				
				if(e.isSlashCommand && !e.slashCommand.isAcknowledged())
					e.slashCommand.replyEmbeds(me).queue();
				else e.channel.sendMessage(me).queue();
				return;
				
			} catch(HierarchyException he) {
				if(e.isSlashCommand && !e.slashCommand.isAcknowledged())
					e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("My role is not high enough to give the 'Silenced' role to anyone.", "Please do it manually, as the mute has still been logged.")).queue();
				else e.channel.sendMessage(BonziUtils.failureEmbed("My role is not high enough to give the 'Silenced' role to anyone.", "Please do it manually, as the mute has still been logged.")).queue();
			}
		};
		
		// create muted role
		if(role == null) {
			MessageEmbed send = BonziUtils.quickEmbed("Setup", "I'm creating and setting up a 'Silenced' role for you... **Please move it above the roles you want to be able to mute!**", Color.orange).build();
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(send).queue();
			else e.channel.sendMessage(send).queue();
			
			e.guild.createRole().setName("Silenced").setColor(Color.gray).setMentionable(false).queue(newRole -> {
				e.settings.mutedRole = newRole.getIdLong();
				e.bonzi.guildSettings.setSettings(e.guild, e.settings);
				for(TextChannel channel: e.guild.getTextChannels()) {
					channel.getManager().putPermissionOverride(newRole, 0l,
						Permission.MESSAGE_WRITE.getRawValue() |
						Permission.MESSAGE_ADD_REACTION.getRawValue() |
						Permission.MESSAGE_ATTACH_FILES.getRawValue()).queue();
				}
				actuallyMute.accept(newRole);
			});
		} else {
			actuallyMute.accept(role);
		}
	}
}