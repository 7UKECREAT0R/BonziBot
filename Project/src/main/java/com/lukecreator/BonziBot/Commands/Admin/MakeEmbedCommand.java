package com.lukecreator.BonziBot.Commands.Admin;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.ColorArg;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.DropdownItem;
import com.lukecreator.BonziBot.GuiAPI.GuiDropdown;
import com.lukecreator.BonziBot.GuiAPI.GuiEditDialog;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryChoice;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;

import net.dv8tion.jda.api.EmbedBuilder;

public class MakeEmbedCommand extends Command {
	
	public MakeEmbedCommand() {
		this.name = "makeembed";
		this.description = "Tests the GuiDialog system.";
		this.icon = GenericEmoji.fromEmoji("✅");
		this.category = CommandCategory._HIDDEN;
		this.adminOnly = false; // u guys go wild and use it lmao
	}
	
	@Override
	public void run(CommandExecutionInfo e) {

		GuiEditEntry[] fields = {
			new GuiEditEntryText(new StringArg("title"), null, "Title", "The title that will go on the embed."),
			new GuiEditEntryText(new StringArg("desc"), null, "Subtitle", "The main text of the embed."),
			new GuiEditEntryText(new ColorArg("color"), "🖌️", "Color", "The color of the embed."),
			new GuiEditEntryText(new StringArg("imageurl"), "🖼️", "Image", "The image on the embed.").optional(),
			
			// whoahhh is that a choice entry? it automatically goes into a dropdown box and is managed?
			new GuiEditEntryChoice(new GuiDropdown("Select an image mode...", "imgmode", false).addItems(
				new DropdownItem(0, "Large Image"),
				new DropdownItem(1, "Small Image"),
				new DropdownItem(2, "Avatar")), null,
					"Image Size", "The size of the image, if given.").optional()
		};
		
		GuiEditDialog dialog = new GuiEditDialog(null, "Embed Maker-inator 5000", fields).after(o -> {
			if(o.wasCancelled)
				return;
			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor(e.executor.getName(), null, e.executor.getEffectiveAvatarUrl());
			eb.setTitle((String)o.values[0]);
			eb.setDescription((String)o.values[1]);
			eb.setColor((Color)o.values[2]);
			if(o.wasSet[3]) {
				Integer mode = o.wasSet[4] ? (Integer)o.values[4] : 0;
				String url = (String)o.values[3];
				switch(mode.intValue()) {
				case 0:
					eb.setImage(url);
					break;
				case 1:
					eb.setThumbnail(url);
					break;
				case 2:
					eb.setAuthor(e.executor.getName(), null, url);
					break;
				}
			}
			e.channel.sendMessageEmbeds(eb.build()).queue();
		});
		
		BonziUtils.sendGui(e, dialog);
	}
}