package com.lukecreator.BonziBot.Music;

/**
 * Indicates that a music queue has hit its track limit as set by {@value #com.lukecreator.BonziBot.Music.MusicQueue.MAX_TRACKS}
 * @author Lukec
 */
public class QueueOutOfRoomException extends Exception {
	private static final long serialVersionUID = 1L;
}