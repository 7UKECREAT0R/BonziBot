package com.lukecreator.BonziBot.Commands.Admin;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.IntArg;

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
		this.unicodeIcon = "🖼️";
		this.description = "(FRIENDS ONLY) Pull a random image from lightshot.";
		this.args = new CommandArgCollection(new IntArg("amount").optional());
		this.category = CommandCategory._HIDDEN;
		this.random = new Random();
		this.buffer = new char[ID_LENGTH];
	}
	
	Random random;
	char[] buffer;
	private String buildUrlID() {
		for(int i = 0; i < ID_LENGTH; i++)
			buffer[i] = CHARS[random.nextInt(CHARS.length)];
		if(buffer[0] == '1' || buffer[0] == '2') {
			buffer[0] = CHARS[random.nextInt(CHARS.length)];
			return "1" + new String(buffer);
		}
		else return new String(buffer);
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		
		long id = e.executor.getIdLong();
		if(!e.bonzi.special.getIsBro(id) && !e.bonzi.special.getIsAdmin(id)) {
			e.channel.sendMessage("Only luke's bros can use this!").queue();
			return;
		}
		
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