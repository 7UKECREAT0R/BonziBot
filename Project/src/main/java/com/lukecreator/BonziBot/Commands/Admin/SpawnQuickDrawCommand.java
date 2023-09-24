package com.lukecreator.BonziBot.Commands.Admin;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Managers.QuickDrawManager;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class SpawnQuickDrawCommand extends Command {
	
	public SpawnQuickDrawCommand() {
		this.subCategory = 0;
		this.name = "spawnquickdraw";
		this.icon = GenericEmoji.fromEmoji("✏");
		this.description = "Spawns a quick draw minigame instantly!";
		this.args = null;
		this.category = CommandCategory._HIDDEN;
		this.brosOnly = true;
		this.worksInDms = false;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		QuickDrawManager qd = e.bonzi.quickDraw;
		long gid = e.guild.getIdLong();
		TextChannel tc = e.tChannel;
		qd.spawnQuickDraw(gid, tc, e.bonzi);
	}
}
