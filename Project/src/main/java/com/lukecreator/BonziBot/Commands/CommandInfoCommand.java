package com.lukecreator.BonziBot.Commands;

import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.CommandSystem;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.PremiumItem;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class CommandInfoCommand extends Command {

	public CommandInfoCommand() {
		this.subCategory = 0;
		this.name = "Command Info";
		this.icon = GenericEmoji.fromEmoji("📰");
		this.description = "View information about a command.";
		this.args = CommandArgCollection.single("command name");
		this.category = CommandCategory.UTILITIES;
		this.setCooldown(1000);
	}

	@Override
	public void run(CommandExecutionInfo e) {
		String commandName = e.args.getString("command name");
		
		CommandSystem system = e.bonzi.commands;
		List<Command> commands = system.getRegisteredCommands();
		
		for(Command command: commands) {
			boolean checkA = command.name.equalsIgnoreCase(commandName);
			boolean checkB = command.getFilteredCommandName().equalsIgnoreCase(commandName);
			if(!checkA && !checkB)
				continue;
			
			int id = command.id;
			String name = command.name;
			String desc = command.description;
			String icon = command.icon.toString();
			
			PremiumItem item = command.premiumItem;
			Permission[] bonziPermissions = command.neededPermissions;
			Permission[] userPermissions = command.userRequiredPermissions;
			long cooldown = command.cooldownMs;
			
			boolean isShopItem = item != null;
			boolean needsBonziPerms = bonziPermissions != null && bonziPermissions.length > 0 && bonziPermissions[0] != Permission.UNKNOWN;
			boolean needsUserPerms = userPermissions != null && userPermissions.length > 0 && bonziPermissions[0] != Permission.UNKNOWN;
			boolean adminOnly = command.adminOnly;
			boolean hasCooldown = command.hasCooldown;
			boolean isForced = command.forcedCommand;
			
			String prefix = BonziUtils.getPrefixOrDefault(e);
			String usage;
			if(command.args != null) {
				String[] _usage = command.args.buildUsage(prefix, command.getFilteredCommandName());
				usage = String.join("\n", _usage);
			} else {
				usage = '`' + prefix + command.getFilteredCommandName() + '`';
			}
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(BonziUtils.COLOR_BONZI_PURPLE);
			eb.setTitle(icon + " " + name);
			eb.setDescription(desc);
			eb.appendDescription("\n" + usage);
			
			if(needsBonziPerms | needsUserPerms) {
				StringBuilder fieldDesc = new StringBuilder();
				if(needsBonziPerms) {
					fieldDesc.append("BonziBot needs these permissions:\n");
					for(Permission perm: bonziPermissions)
						fieldDesc.append("`" + perm.getName() + "`, ");
					fieldDesc.deleteCharAt(fieldDesc.length() - 1);
					fieldDesc.deleteCharAt(fieldDesc.length() - 1);
					if(needsUserPerms)
						fieldDesc.append('\n');
				}
				if(needsUserPerms) {
					fieldDesc.append("The executor of the command needs these permissions:\n");
					for(Permission perm: userPermissions)
						fieldDesc.append("`" + perm.getName() + "`, ");
					fieldDesc.deleteCharAt(fieldDesc.length() - 1);
					fieldDesc.deleteCharAt(fieldDesc.length() - 1);
				}
				eb.addField("Permissions", fieldDesc.toString(), false);
			}
			
			if(isShopItem)
				eb.addField("Item in Shop", "You can purchase this command on the shop; "
					+ "its name is `" + item.getLinkedCommand(system).name + "`.", false);
			if(adminOnly)
				eb.addField("Admin Only", "Only BonziBot administrators or developers can run this command.", false);
			if(hasCooldown) {
				String time = BonziUtils.getLongTimeStringMs(cooldown);
				eb.addField("Cooldown", "This command has a cooldown of `" + time + "`.", false);
			}
			if(isForced)
				eb.addField("Forced Command", "This is an important command. You cannot disable it and it's always available.", false);
			
			String source = this.getSourceCode(command);
			eb.addField("Developer Information",
				"[Source Code](" + source + ")" +
				"\nCommand ID: " + id +
				"\nCategory: " + command.category.name() +
				"\nSub-Category: " + command.subCategory +
				"\nWorks in DMs? " + command.worksInDms, false);
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(eb.build()).queue();
			else
				e.channel.sendMessageEmbeds(eb.build()).queue();
			return;
		}
		if(e.isSlashCommand)
			e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("Couldn't find a command with that name!")).queue();
		else
			e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("Couldn't find a command with that name!")).queue();
	}
	
	String getSourceCode(Command command) {
		Class<? extends Command> clazz = command.getClass();
		String sourceFile = clazz.getSimpleName() + ".java";
		
		if(clazz.getPackage().getName().contains("Admin"))
			return "https://github.com/7UKECREAT0R/BonziBot/blob/main/Project/src/main/java/com/lukecreator/BonziBot/Commands/Admin/" + sourceFile;
		else
			return "https://github.com/7UKECREAT0R/BonziBot/blob/main/Project/src/main/java/com/lukecreator/BonziBot/Commands/" + sourceFile;
	}
}