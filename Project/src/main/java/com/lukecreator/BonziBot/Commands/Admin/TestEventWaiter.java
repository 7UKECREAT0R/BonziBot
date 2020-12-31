package com.lukecreator.BonziBot.Commands.Admin;

import java.awt.Color;

import com.lukecreator.BonziBot.CommandAPI.ColorArg;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class TestEventWaiter extends Command {
	
	public TestEventWaiter() {
		this.name = "makeembed";
		this.description = "Tests the EventWaiter system by letting the user create an embed.";
		this.unicodeIcon = "✅";
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = false; // u guys go wild and use it lmao
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		e.channel.sendMessage("Send a color.").queue();
		ColorArg arg1 = new ColorArg("");
		StringArg arg2 = new StringArg("");
		UserArg arg3 = new UserArg("");
		
		EventWaiterManager mg = e.bonzi.eventWaiter;
		mg.waitForArgument(e.executor, arg1, o1 -> {
			e.channel.sendMessage("Now, send the text you want.").queue();
			mg.waitForArgument(e.executor, arg2, o2 -> {
				e.channel.sendMessage("Finally, reference the user you want to show.").queue();
				mg.waitForArgument(e.executor, arg3, o3 -> {
					Color color = (Color)o1;
					String title = (String)o2;
					User user = (User)o3;
					
					String icon = user.getEffectiveAvatarUrl();
					EmbedBuilder eb = new EmbedBuilder();
					eb.setAuthor(user.getName(), null, icon);
					eb.setTitle(title);
					eb.setColor(color);
					eb.setFooter("this embed was generated by " + e.executor.getAsTag());
					e.channel.sendMessage(eb.build()).queue();
				});
			});
		});
	}
}