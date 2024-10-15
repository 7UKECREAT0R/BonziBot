package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.api.services.youtube.YouTube.Search;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.EmoteCache;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.DropdownItem;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiButton.ButtonColor;
import com.lukecreator.BonziBot.GuiAPI.GuiDropdown;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Music.MusicPlayMode;
import com.lukecreator.BonziBot.Music.MusicQueue;
import com.lukecreator.BonziBot.Music.QueueOutOfRoomException;
import com.lukecreator.BonziBot.NoUpload.Constants;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class GuiMusic extends Gui {
	
	public static final GenericEmoji EMPTY_LEFT = GenericEmoji.fromEmote(904924570769846283l, false);
	public static final GenericEmoji EMPTY_MID = GenericEmoji.fromEmote(904924570891485274l, false);
	public static final GenericEmoji EMPTY_RIGHT = GenericEmoji.fromEmote(904924570614640671l, false);
	public static final GenericEmoji PLAYER_LEFT = GenericEmoji.fromEmote(904924570383974411l, false);
	public static final GenericEmoji PLAYER_MID = GenericEmoji.fromEmote(904924570522370068l, false);
	public static final GenericEmoji PLAYER_RIGHT = GenericEmoji.fromEmote(904924570367176774l, false);
	public static final int BAR_LENGTH = 10;
	
	public static final GenericEmoji PAUSE = GenericEmoji.fromEmote(904922767764365352l, false);
	public static final GenericEmoji PLAY = GenericEmoji.fromEmote(904922767881818172l, false);
	
	static final String[] STOPPED_MESSAGES = {
			"listening party's over!",
			"music player gone!",
			"okay you guys can go home now!",
			"alright yall i gotta take a break",
			"music is gone",
			"no more songz (ono)",
			"i'm outta here!",
			"\\*doing back flips\\*",
			"alright, who did it",
			"finally, i am freed",
			"dang you guys listen to some weird stuff",
			"catch yall later",
			"my mac n cheese is done",
			"yall got... tastes",
			"now playing Literally Nothing",
			"buh bye"
	};
	static String getStoppedMessage() {
		int index = BonziUtils.randomInt(STOPPED_MESSAGES.length);
		return STOPPED_MESSAGES[index];
	}
	
	/**
	 * Build a progress bar.
	 * @param progress The progress out of 100.
	 * @return A set of emojis to form a progress bar.
	 */
	public static String buildBar(float progress) {
		if(progress < 0f)
			progress = 0f;
		if(progress > 100f)
			progress = 100f;
		
		String[] parts = new String[BAR_LENGTH];
		parts[0] = EMPTY_LEFT.toString();
		parts[BAR_LENGTH - 1] = EMPTY_RIGHT.toString();
		
		String midPart = EMPTY_MID.toString();
		for(int i = 1; i < BAR_LENGTH - 1; i++)
			parts[i] = midPart;
		
		// find out where the bar should be
		float divisor = (float)BAR_LENGTH;
		float funit = progress / divisor;
		int unit = (int)Math.round(funit);
		
		if(unit <= 0)
			parts[0] = PLAYER_LEFT.toString();
		else if(unit >= BAR_LENGTH - 1)
			parts[BAR_LENGTH - 1] = PLAYER_RIGHT.toString();
		else
			parts[unit] = PLAYER_MID.toString();
		
		return String.join("", parts);
	}
	/**
	 * Build an empty progress bar without a progress shown.
	 * Used to indicate that no track is playing.
	 * @return
	 */
	public static String buildEmptyBar() {
		String[] parts = new String[BAR_LENGTH];
		parts[0] = EMPTY_LEFT.toString();
		parts[BAR_LENGTH - 1] = EMPTY_RIGHT.toString();
		
		String midPart = EMPTY_MID.toString();
		for(int i = 1; i < BAR_LENGTH - 1; i++)
			parts[i] = midPart;
		
		return String.join("", parts);
	}
	/**
	 * Build a string that describes an AudioTrack.
	 * @param track
	 * @return
	 */
	public static String buildTrackString(AudioTrack track) {
		
		if(track == null)
			return "(not playing anything)";
		
		AudioTrackInfo info = track.getInfo();
		
		if(info == null)
			return "`<no clue>`";
		
		return "`" + info.title + "` - `" + info.author + "`";
	}
	
	public final MusicQueue queue;
	public boolean closed = false;
	public String iconUrl = null;
	public String lastAction = null;
	
	public GuiMusic(MusicQueue player) {
		this.queue = player;
	}
	/**
	 * Just calls enableAnimation with the right timings.
	 * @param totalMs The total number of milliseconds this next song is.
	 */
	public void animate(long totalMs, long playerOffset) {
		int totalSeconds = (int)(totalMs / 1000L);
		int totalOffset = (int)(playerOffset / 1000L);
		int timeBetweenEachChange = totalSeconds / BAR_LENGTH;
		
		if(timeBetweenEachChange < 2)
			timeBetweenEachChange = 2;
		
		this.enableAnimation(timeBetweenEachChange, totalSeconds - totalOffset);
	}
	
	private void close(JDA jda) {
		// stops the player entirely and clears queue
		this.queue.stop();
		
		Guild g = jda.getGuildById(this.parent.guildId);
		g.getAudioManager().closeAudioConnection();
		
		this.elements.removeIf(e -> !e.idEqual("bye")); // delete all but this button
		this.elements.get(0).asEnabled(false); // disable it
		this.closed = true;
		
		this.disableAnimation();
		this.parent.redrawMessage(jda);
		this.parent.disableSilent();
		return;
	}
	
	
	@Override
	public void initialize(JDA jda) {
		this.parent.globalWhitelist = true;
		this.reinitialize();
	}
	public void reinitialize() {
		this.elements.clear();
		AudioPlayer player = this.queue.player;
		
		this.elements.add(new GuiButton("<<<", ButtonColor.GRAY, "restart"));
		this.elements.add(new GuiButton("< 10s", ButtonColor.GRAY, "seekback"));
		
		if(player.isPaused())
			this.elements.add(GuiButton.singleEmoji(PLAY, "toggle"));
		else
			this.elements.add(GuiButton.singleEmoji(PAUSE, "toggle"));
		
		this.elements.add(new GuiButton("10s >", ButtonColor.GRAY, "seekforward"));
		this.elements.add(new GuiButton(">>>", ButtonColor.GRAY, "skip"));
		
		
		GuiDropdown dropdown = new GuiDropdown("Play mode...", "playmode", false)
			.addItemsTransform(MusicPlayMode.values(), pm -> {
				return new DropdownItem(pm, pm.name)
					.withDescription(pm.desc)
					.withEmoji(GenericEmoji.fromEmote(pm.emote, false));
			});
		dropdown.setSelectedIndex(this.queue.playMode.ordinal());
		this.elements.add(dropdown);
		
		
		this.elements.add(new GuiButton(GenericEmoji.fromEmote(EmoteCache.getEmoteByName("youtube")), "Add Song", ButtonColor.GREEN, "addsong"));
		this.elements.add(new GuiButton(GenericEmoji.fromEmote(EmoteCache.getEmoteByName("youtube")), "Add Video", ButtonColor.GREEN, "addvideo"));
		this.elements.add(new GuiButton(GenericEmoji.fromEmoji("✖️"), "CLOSE", ButtonColor.RED, "bye"));
	}
	
	@Override
	public Object draw(JDA jda) {
		if(this.closed) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(BonziUtils.COLOR_BONZI_PURPLE);
			eb.setTitle(getStoppedMessage());
			eb.setFooter(this.lastAction, this.iconUrl);
			return eb.build();
		}
		
		AudioPlayer audioPlayer = this.queue.player;
		AudioTrack track = audioPlayer.getPlayingTrack();
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(BonziUtils.COLOR_BONZI_PURPLE);
		
		String playingString;
		String barString;
		
		if(track == null) {
			barString = buildEmptyBar();
			playingString = buildTrackString(null);
			// stop animating, there's no point
			this.disableAnimation();
		} else {
			double current = track.getPosition();
			double total = track.getDuration();
			double fraction = current / total;
			
			float percent = ((float)fraction) * 100f;
			barString = buildBar(percent);
			playingString = "🎵 Playing: " + buildTrackString(track);
		}
		
		eb.setDescription(playingString);
		eb.appendDescription("\n");
		eb.appendDescription(barString);
		
		// next in queue
		BlockingQueue<AudioTrack> queue = this.queue.rawTracks;
		AudioTrack next = queue.peek();
		
		if(next == null)
			eb.appendDescription("\n\nno songs in queue");
		else {
			String text = BonziUtils.stringJoinTransform("\n", at -> {
				return buildTrackString((AudioTrack)at);
			}, queue);
			text = BonziUtils.cutOffString(text, 3000);
			eb.appendDescription("\n\nnext up:\n" + text);
		}
		
		if(this.lastAction != null) {
			eb.setFooter(this.lastAction, this.iconUrl);
		}
		return eb.build();
	}
	
	@Override
	public void onButtonClick(String buttonId, long clickerId, JDA jda) {
		
		Guild guild = jda.getGuildById(this.parent.guildId);
		Member member = guild.getMemberById(clickerId);
		this.iconUrl = member.getEffectiveAvatarUrl();
		String userName = member.getEffectiveName();
		
		Member selfMember = guild.getSelfMember();
		GuildVoiceState currentState = selfMember.getVoiceState();
		if(!currentState.inAudioChannel()) {
			this.iconUrl = selfMember.getEffectiveAvatarUrl();
			this.lastAction = "im no longer in the voice chat";
			this.close(jda);
			return;
		}
		
		if(buttonId.equals("restart")) {
			AudioTrack playing = this.queue.player.getPlayingTrack();
			if(playing == null)
				return;
			playing.setPosition(0);
			
			this.lastAction = "Song restarted by " + userName;
			this.parent.redrawMessage(jda);
			this.animate(playing.getDuration(), 0L);
			return;
		}
		if(buttonId.equals("seekback")) {
			AudioTrack playing = this.queue.player.getPlayingTrack();
			if(playing == null)
				return;
			long position = playing.getPosition();
			position -= 10 * 1000;
			if(position < 0)
				position = 0;
			playing.setPosition(position);
			
			this.lastAction = userName + " skipped back 10 seconds";
			this.parent.redrawMessage(jda);
			this.animate(playing.getDuration(), position);
			return;
		}
		if(buttonId.equals("toggle")) {
			AudioPlayer ap = this.queue.player;
			boolean paused = !ap.isPaused();
			ap.setPaused(paused);
			
			if(paused) {
				this.lastAction = "Paused by " + userName;
				this.disableAnimation();
			} else {
				this.lastAction = "Resumed by " + userName;
				
				AudioTrack playing = this.queue.player.getPlayingTrack();
				this.animate(playing.getDuration(), playing.getPosition());
			}
			
			this.reinitialize();
			this.parent.redrawMessage(jda);
			return;
		}
		if(buttonId.equals("seekforward")) {
			AudioTrack playing = this.queue.player.getPlayingTrack();
			if(playing == null)
				return;
			long position = playing.getPosition();
			position += 10 * 1000;
			if(position > playing.getDuration())
				position = playing.getDuration() - 1;
			playing.setPosition(position);
			
			this.lastAction = userName + " skipped forward 10 seconds";
			
			this.parent.redrawMessage(jda);
			this.animate(playing.getDuration(), position);
			return;
		}
		if(buttonId.equals("skip")) {
			AudioTrack playing = this.queue.player.getPlayingTrack();
			boolean startedAnother = this.queue.next(playing);
			
			if(startedAnother) {
				this.lastAction = userName + " skipped the song";
				this.animate(this.queue.player.getPlayingTrack().getDuration(), 0L);
			} else {
				this.lastAction = userName + " skipped the last song";
			}
			
			this.parent.redrawMessage(jda);
			return;
		}
		
		
		// big guys
		
		if(buttonId.equals("addsong")) {
			MessageChannelUnion channel = this.parent.getChannel(jda);
			channel.sendMessageEmbeds(BonziUtils.quickEmbed("Add Song", "Send the URL or title of the song...", Color.orange).build()).queue(msg1 -> {
				EventWaiterManager ewm = this.bonziReference.eventWaiter;
				ewm.waitForResponse(clickerId, response -> {
					msg1.delete().queue();
					response.delete().queue(null, fail -> {});
					
					String url = response.getContentRaw().replace("`", "");
					AudioPlayerManager apm = this.bonziReference.audioPlayerManager;
					boolean validUrl = Constants.URL_REGEX_COMPILED.matcher(url).matches();
					
					// references for handler to use
					final MusicQueue queueReference = this.queue;
					final GuiMusic selfReference = this;
					
					AudioLoadResultHandler resultHandler = new AudioLoadResultHandler() {
						@Override
						public void trackLoaded(AudioTrack track) {
							try {
								queueReference.queue(track);
								selfReference.lastAction = userName + " added '" + track.getInfo().title + "'";
								selfReference.reinitialize();
								selfReference.parent.redrawMessage(jda);
								selfReference.animate(track.getDuration(), 0L);
							} catch (QueueOutOfRoomException e) {
								MessageEmbed me = BonziUtils.failureEmbed(EmbedBuilder.ZERO_WIDTH_SPACE, "The queue's full right now!");
								channel.sendMessageEmbeds(me).queue(msg -> {
									msg.delete().queueAfter(3, TimeUnit.SECONDS);
								});
							}
						}
						@Override
						public void playlistLoaded(AudioPlaylist playlist) {
							int count = 0;
							AudioTrack first = null;
							
							try {
								List<AudioTrack> list = playlist.getTracks();
								count = list.size();
								first = playlist.getSelectedTrack();
								if(first != null) {
									list.remove(first);
									queueReference.queue(first);
								} else if(!list.isEmpty()) {
									first = list.get(0);
								}
								
								for(AudioTrack track: list)
									queueReference.queue(track);
							} catch (QueueOutOfRoomException e) {
								// ignoring these since it could be common with big playlists
								return;
							} finally {
								selfReference.lastAction = userName + " added " + count + " songs";
								selfReference.reinitialize();
								selfReference.parent.redrawMessage(jda);
								
								if(first != null)
									selfReference.animate(first.getDuration(), 0L);
							}
						}
						@Override
						public void noMatches() {
							MessageEmbed me = BonziUtils.failureEmbed("Invalid Video", "This isn't a supported song/video.");
							channel.sendMessageEmbeds(me).queue(msg -> {
								msg.delete().queueAfter(4, TimeUnit.SECONDS);
							});
						}
						@Override
						public void loadFailed(FriendlyException exception) {
							switch(exception.severity) {
							case COMMON:
								MessageEmbed fail = BonziUtils.failureEmbed("Load Failed", "This song might be unavailable in the United States or age restricted; error code:\n```"
									+ exception + "\n```");
								channel.sendMessageEmbeds(fail).queue(msg -> {
									msg.delete().queueAfter(6, TimeUnit.SECONDS);
								});
								break;
							case FAULT:
							case SUSPICIOUS:
								MessageEmbed failBig = BonziUtils.failureEmbed("Load Failed bigtime; error code:", "```\n" + exception + "\n```");
								channel.sendMessageEmbeds(failBig).queue();
								break;
							default:
								break;
							}

						}
					};
					
					if(validUrl) {
						apm.loadItemOrdered(this.queue, url, resultHandler);
						return;
					}
					
					try {
						BonziUtils.sendTempMessage(channel, "Searching youtube for `" + url + "`...", 3);
						Search.List request = this.bonziReference.youtube.search().list("snippet");
						SearchListResponse _results = request
							.setKey(Constants.YTAPI_KEY)
							.setMaxResults(1L)
							.setQ(url)
							.setVideoCategoryId("10") // music
							.setType("video")
							.setOrder("relevance")
							.execute();
						List<SearchResult> results = _results.getItems();
						if(results.isEmpty()) {
							BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("No results found.", "For search query:\n`" + url + "`"), 3);
							return;
						}
						SearchResult result = results.get(0);
						String id = result.getId().getVideoId();
						String findUrl = "https://www.youtube.com/watch?v=" + id;
						apm.loadItemOrdered(this.queue, findUrl, resultHandler);
						return;
					} catch (IOException e) {
						channel.sendMessage("Everything blew up.\n```" + e + "```").queue();
						return;
					}
				});
			});;
		}
		if(buttonId.equals("addvideo")) {
			MessageChannelUnion channel = this.parent.getChannel(jda);
			channel.sendMessageEmbeds(BonziUtils.quickEmbed("Add Video", "Send the URL or title of the video...", Color.orange).build()).queue(msg1 -> {
				EventWaiterManager ewm = this.bonziReference.eventWaiter;
				ewm.waitForResponse(clickerId, response -> {
					msg1.delete().queue();
					response.delete().queue(null, fail -> {});
					
					String url = response.getContentRaw();
					AudioPlayerManager apm = this.bonziReference.audioPlayerManager;
					boolean validUrl = Constants.URL_REGEX_COMPILED.matcher(url).matches();
					
					// references for handler to use
					final MusicQueue queueReference = this.queue;
					final GuiMusic selfReference = this;
					
					AudioLoadResultHandler resultHandler = new AudioLoadResultHandler() {
						@Override
						public void trackLoaded(AudioTrack track) {
							try {
								queueReference.queue(track);
								selfReference.lastAction = userName + " added '" + track.getInfo().title + "'";
								selfReference.reinitialize();
								selfReference.parent.redrawMessage(jda);
								selfReference.animate(track.getDuration(), 0L);
							} catch (QueueOutOfRoomException e) {
								MessageEmbed me = BonziUtils.failureEmbed(EmbedBuilder.ZERO_WIDTH_SPACE, "The queue's full right now!");
								channel.sendMessageEmbeds(me).queue(msg -> {
									msg.delete().queueAfter(3, TimeUnit.SECONDS);
								});
							}
						}
						@Override
						public void playlistLoaded(AudioPlaylist playlist) {
							int count = 0;
							AudioTrack first = null;
							
							try {
								List<AudioTrack> list = playlist.getTracks();
								count = list.size();
								first = playlist.getSelectedTrack();
								if(first != null) {
									list.remove(first);
									queueReference.queue(first);
								} else if(!list.isEmpty()) {
									first = list.get(0);
								}
								for(AudioTrack track: list)
									queueReference.queue(track);
							} catch (QueueOutOfRoomException e) {
								// ignoring these since it could be common with big playlists
								return;
							} finally {
								selfReference.lastAction = userName + " added " + count + " songs";
								selfReference.reinitialize();
								selfReference.parent.redrawMessage(jda);
								
								if(first != null)
									selfReference.animate(first.getDuration(), 0L);
							}
						}
						@Override
						public void noMatches() {
							MessageEmbed me = BonziUtils.failureEmbed("Invalid Video", "This isn't a supported song/video.");
							channel.sendMessageEmbeds(me).queue(msg -> {
								msg.delete().queueAfter(4, TimeUnit.SECONDS);
							});
						}
						@Override
						public void loadFailed(FriendlyException exception) {
							switch(exception.severity) {
							case COMMON:
								MessageEmbed fail = BonziUtils.failureEmbed("Load Failed", "This song might be unavailable in the United States or age restricted; error code:\n```"
										+ exception + "\n```");
								channel.sendMessageEmbeds(fail).queue(msg -> {
									msg.delete().queueAfter(6, TimeUnit.SECONDS);
								});
								break;
							case FAULT:
							case SUSPICIOUS:
								MessageEmbed failBig = BonziUtils.failureEmbed("Load Failed bigtime; error code:", "```\n" + exception + "\n```");
								channel.sendMessageEmbeds(failBig).queue();
								break;
							default:
								break;
							}

						}
					};
					
					if(validUrl) {
						apm.loadItemOrdered(this.queue, url, resultHandler);
						return;
					}
					
					try {
						BonziUtils.sendTempMessage(channel, "Searching youtube for `" + url + "`...", 3);
						Search.List request = this.bonziReference.youtube.search().list("snippet");
						SearchListResponse _results = request
							.setKey(Constants.YTAPI_KEY)
							.setMaxResults(1L)
							.setQ(url)
							.setOrder("relevance")
							.setType("video")
							.execute();
						List<SearchResult> results = _results.getItems();
						if(results.isEmpty()) {
							BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("No results found.", "For search query:\n`" + url + "`"), 3);
							return;
						}
						SearchResult result = results.get(0);
						String id = result.getId().getVideoId();
						String findUrl = "https://www.youtube.com/watch?v=" + id;
						apm.loadItemOrdered(this.queue, findUrl, resultHandler);
						return;
					} catch (IOException e) {
						channel.sendMessage("Everything blew up.\n```" + e + "```").queue();
						return;
					}
				});
			});;
		}
		
		if(buttonId.equals("bye")) {
			MessageChannelUnion channel = this.parent.getChannel(jda);
			EventWaiterManager ewm = this.bonziReference.eventWaiter;
			ewm.getConfirmation(clickerId, channel, "Close the music player? This will clear the queue.", b -> {
				if(!b) {
					return;
				}
				
				this.lastAction = userName + " closed the music player.";
				this.close(jda);
				return;
			});
		}
		
	}
	@Override
	public void onDropdownChanged(GuiDropdown dropdown, long clickerId, JDA jda) {
		this.queue.playMode = (MusicPlayMode)dropdown.getSelectedObject();
	}
}
