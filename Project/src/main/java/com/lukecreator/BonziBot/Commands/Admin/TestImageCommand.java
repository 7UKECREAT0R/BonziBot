package com.lukecreator.BonziBot.Commands.Admin;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import com.lukecreator.BonziBot.CommandAPI.ColorArg;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.CommandAPI.StringRemainderArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Graphics.FontStyle;
import com.lukecreator.BonziBot.Graphics.Image;

import net.dv8tion.jda.api.utils.FileUpload;

public class TestImageCommand extends Command {

	public TestImageCommand() {
		this.subCategory = 0;
		this.name = "Test Image";
		this.icon = GenericEmoji.fromEmoji("🖼️");
		this.description = "Test the image API by constructing an image with text on it.";
		this.args = new CommandArgCollection(
			new ColorArg("BG Color"),
			new ColorArg("Text Color"),
			new StringArg("Font Name"),
			new IntArg("Font Size"),
			new StringRemainderArg("Text"));
		this.category = CommandCategory._HIDDEN;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		
		Color bgColor = e.args.getColor("BG Color");
		Color fgColor = e.args.getColor("Text Color");
		String fontName = e.args.getString("Font Name");
		int fontSize = e.args.getInt("Font Size");
		String text = e.args.getString("Text");
		
		long start = System.currentTimeMillis();
		Image image = new Image(720, 240, false);
		
		image.fill(bgColor);
		image.setFont(fontName, FontStyle.NORMAL, fontSize);
		image.drawCenteredString(text, fgColor);
		
		long end = System.currentTimeMillis();
		long timeMs = end - start;
		
		try {
			File saved = image.save("tempImages/test.jpg", true);
			FileUpload upload = FileUpload.fromData(saved, saved.toPath().getFileName().toString());
			e.channel.sendMessage("Finished in " + timeMs + "ms.").addFiles(upload).queue();
			
		} catch(IOException exc) {
			exc.printStackTrace();
			e.channel.sendMessage("IOException.\n\n" + exc.toString()).queue();
		}
	}
}