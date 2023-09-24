package com.lukecreator.BonziBot.Ticketing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.lukecreator.BonziBot.Data.DataSerializer;

/**
 * Manages ticket files.
 * @author Lukec
 */
public class TicketIO {
	
	public static final String BASE_DIRECTORY = DataSerializer.baseDataPath + "transcripts/";
	
	public static Path directoryFor(long guildId) {
		return Paths.get("home", "pi", "transcripts", String.valueOf(guildId)).toAbsolutePath();
	}
	/**
	 * Get the file path to a transcript for a ticket, creating directories if necessary.
	 * @param userId
	 * @return
	 */
	public static Path filePathFor(Ticket ticket) throws IOException {
		long guildId = ticket.guild.guildId;
		long ticketId = ticket.ticketId;
		return filePathFor(guildId, ticketId);
	}
	/**
	 * Get the file path to a transcript for a guild/ticket pair, creating directories if necessary.
	 * @param userId
	 * @return
	 */
	public static Path filePathFor(long guildId, long ticketId) throws IOException {
		Path guildDirectory = directoryFor(guildId);
		Files.createDirectories(guildDirectory);
		
		return guildDirectory.resolve(String.valueOf(ticketId) + ".txt");
	}
}
