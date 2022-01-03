package com.lukecreator.BonziBot.Music;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nullable;

import com.lukecreator.BonziBot.BonziUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Holds and manages the queue for music. Also holds instance to a guild's music player.
 * @author Lukec
 */
public class MusicQueue extends AudioEventAdapter {
	
	private static final Random random = new Random();
	public static final int MAX_TRACKS = 25;
	
	// used by this and the GUI itself
	public MusicPlayMode playMode;
	public final long guildId;
	public final AudioPlayer player;
	public BlockingQueue<AudioTrack> rawTracks;
	
	// "kills" this audio player if it goes unused for a long time
	public static final long KILL_TIMER = BonziUtils.getMsForHours(12);
	public long killAfter = Long.MAX_VALUE;
	
	public boolean shouldKill(long currentTime) {
		if(currentTime < this.killAfter)
			return false;
		
		// only kill if its actually playing a track
		return this.player.getPlayingTrack() != null;
	}
	public void updateKillTimer() {
		this.killAfter = System.currentTimeMillis() + KILL_TIMER;
	}
	
	/**
	 * Creates a new MusicQueue and binds its player to the passed-in guild.
	 * @param guild
	 * @param player
	 */
	public MusicQueue(Guild guild, AudioPlayer player) {
		this.playMode = MusicPlayMode.QUEUE;
		this.guildId = guild.getIdLong();
		this.player = player;
		this.rawTracks = new LinkedBlockingQueue<>();
		
		AudioSendHandler handler = new MusicSendHandler(this.player);
		guild.getAudioManager().setSendingHandler(handler);
		
		this.player.addListener(this);
	}
	
	/**
	 * Add a song to the queue and start it if there isn't already one playing.
	 * @param track
	 * @throws QueueOutOfRoomException 
	 */
	public void queue(AudioTrack track) throws QueueOutOfRoomException {
		this.updateKillTimer();
		
		if(this.rawTracks.size() >= MAX_TRACKS)
			throw new QueueOutOfRoomException();
		
		if(!this.player.startTrack(track, true))
			this.rawTracks.offer(track);
	}
	/**
	 * Add a song to the front of the queue but don't cancel the currently playing song.
	 * Due to how a BlockingQueue behaves, a new one needs to be created to put it at the front of the queue.
	 * @param track
	 * @throws QueueOutOfRoomException 
	 */
	public void queueFront(AudioTrack track) throws QueueOutOfRoomException {
		this.updateKillTimer();
		
		if(this.rawTracks.size() >= MAX_TRACKS)
			throw new QueueOutOfRoomException();
		
		BlockingQueue<AudioTrack> clone = new LinkedBlockingQueue<AudioTrack>();
		clone.add(track);
		
		this.rawTracks.drainTo(clone);
		this.rawTracks = clone;
	}
	
	/**
	 * Stop the music player and clear the queue.
	 */
	public void stop() {
		this.killAfter = Long.MAX_VALUE;
		this.rawTracks.clear();
		
		if(player.getPlayingTrack() != null)
			player.stopTrack();
		
		player.setPaused(false);
	}
	
	/**
	 * Starts the next track in the queue, obeying the rules of {@link #playMode}
	 * @return If a new track has begun. False if the player is now stopped.
	 * @param current The track that ended or is currently playing.
	 */
	public boolean next(@Nullable AudioTrack current) {
		
		switch(this.playMode) {
		case LOOP:
			return this.nextLoop(current);
		case LOOP_QUEUE:
			return this.nextLoopQueue(current);
		case SHUFFLE:
			return this.nextShuffle(current);
		case QUEUE:
		default:
			break;
		}

		AudioTrack next = this.rawTracks.poll();
		
		if(next == null) {
			this.player.stopTrack();
			return false;
		}
		
		// start next one anyways
		return this.player.startTrack(next, false);
	}
	/**
	 * Replay the current song from the beginning.
	 * @return
	 */
	private boolean nextLoop(AudioTrack now) {
		
		if(now == null) {
			this.player.stopTrack();
			return false;
		}
		
		AudioTrack clone = now.makeClone();
		if(clone.isSeekable())
			clone.setPosition(0);
		clone.setUserData(now.getUserData());
		
		return this.player.startTrack(clone, false);
	}
	/**
	 * Send a clone of the current song to the end of the queue.
	 * @return
	 */
	private boolean nextLoopQueue(AudioTrack now) {
		
		if(now != null) {
			AudioTrack clone = now.makeClone();
			if(clone.isSeekable())
				clone.setPosition(0);
			clone.setUserData(now.getUserData());
			this.rawTracks.offer(clone);
		}
		
		AudioTrack next = this.rawTracks.poll();
		
		if(next == null) {
			this.player.stopTrack();
			return false;
		}
		
		return this.player.startTrack(next, false);
	}
	private boolean nextShuffle(AudioTrack now) {
		int size = this.rawTracks.size();
		
		if(this.rawTracks.size() == 0) {
			this.player.stopTrack();
			return false;
		}
		
		AudioTrack[] all = new AudioTrack[size];
		this.rawTracks.toArray(all);
		
		AudioTrack next = all[random.nextInt(size)];
		this.rawTracks.remove(next);
		return this.player.startTrack(next, false);
	}
	
	
	// AudioEventAdapter functions
	
	@Override
	public void onPlayerPause(AudioPlayer player) {
		// Player has been paused by a user
		this.updateKillTimer();
	}
	@Override
	public void onPlayerResume(AudioPlayer player) {
		// Player has been resumed by a user
		this.updateKillTimer();
	}
	
	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		// A track has begun
		
	}
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		// A track has ended for whatever reason
		if(!endReason.mayStartNext)
			return;
		
		this.next(track);
	}
	
	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		// A track crumbled
		
	}
	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		// A track has been stuck for a while
		
	}
}
