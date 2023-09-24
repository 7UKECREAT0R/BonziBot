package com.lukecreator.BonziBot.Commands.Admin;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.IntArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;

/**
 * This is hidden. Don't use this in servers because it can have some pretty terrible content.
 * @author Lukec
 */
public class LightshotCommand extends Command {

	public static boolean PROGRESS = false;
	public static final int ID_LENGTH = 6;
	public static final char[] CHARS = "qwertyuiopasdfghjklzxcvbnm1234567890".toCharArray();
	
	public LightshotCommand() {
		this.subCategory = 0;
		this.name = "Lightshot";
		this.icon = GenericEmoji.fromEmoji("🖼️");
		this.description = "Pull a random image from lightshot.";
		this.args = new CommandArgCollection(new IntArg("amount").optional());
		this.category = CommandCategory._HIDDEN;
		this.random = new Random();
		this.buffer = new char[ID_LENGTH];
		this.brosOnly = true;
	}
	
	Random random;
	char[] buffer;
	private String buildUrlID() {
		for(int i = 0; i < ID_LENGTH; i++)
			this.buffer[i] = CHARS[this.random.nextInt(CHARS.length)];
		if(this.buffer[0] == '1' || this.buffer[0] == '2') {
			this.buffer[0] = CHARS[this.random.nextInt(CHARS.length)];
			return "1" + new String(this.buffer);
		}
		else return new String(this.buffer);
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		int count = e.args.argSpecified("amount") ? e.args.getInt("amount") : 1;
		
		if(PROGRESS) {
			e.channel.sendMessage("thingy already in progress!").queue();
			return;
		}
		
		if(count > 100) {
			e.channel.sendMessage("whoah bro slow down u gotta do less than 100").queue();
			return;
		}
		
		PROGRESS = true;
		for(int i = 0; i < count; i++) {
			String urlID = this.buildUrlID();
			if(i + 1 == count)
				e.channel.sendMessage("https://prnt.sc/" + urlID).queueAfter((i + 1) * 1100, TimeUnit.MILLISECONDS, msg -> { PROGRESS = false; });
			else e.channel.sendMessage("https://prnt.sc/" + urlID).queueAfter((i + 1) * 1100, TimeUnit.MILLISECONDS);
		}
	}
}