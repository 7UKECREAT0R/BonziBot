package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Managers.MuteManager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class UnmuteCommand extends Command {

	public UnmuteCommand() {
		this.subCategory = 2;
		this.name = "Unmute";
		this.icon = GenericEmoji.fromEmoji("🗣️");
		this.description = "Allow a user to talk again.";
		this.args = new CommandArgCollection(new UserArg("target"));
		this.userRequiredPermissions = new Permission[] { Permission.MANAGE_ROLES };
		this.neededPermissions = new Permission[] { Permission.MANAGE_ROLES };
		this.setCooldown(1000);
		this.worksInDms = false;
		this.category = CommandCategory.MODERATION;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		
		User target = e.args.getUser("target");
		
		long roleId = e.settings.mutedRole;
		Role role = roleId == 0l ? null : e.guild.getRoleById(roleId);
		
		MuteManager mutes = e.bonzi.mutes;
		long time = mutes.getMuteTime(e.guild, target);
		
		if(role == null || time == MuteManager.NOT_MUTED) {
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("User isn't muted.")).setEphemeral(true).queue();
			else
				e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("User isn't muted.")).queue();
			return;
		}
		
		mutes.unmute(e.guild, target);
		e.guild.removeRoleFromMember(target, role).queue();
		
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(BonziUtils.successEmbed("Unmuted user successfully.", "User: `" + target.getName() + "`")).queue();
		else
			e.channel.sendMessageEmbeds(BonziUtils.successEmbed("Unmuted user successfully.", "User: `" + target.getName() + "`")).queue();
	}
}