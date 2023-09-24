package com.lukecreator.BonziBot.Commands.Admin;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube.Search;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.EnumArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.NoUpload.Constants;

/**
 * Interesting discoveries by KVN AUST. Use at your own discretion.
 * https://www.youtube.com/watch?v=-0kmMJ6F9WM
 * @author lukec
 */
public class GetDepthsVideoCommand extends Command {
	
	protected static String formatString(String str) {
		LocalDateTime now = LocalDateTime.now();
		String ymd = BonziUtils.DH_YMD.format(now);
		String mdy = BonziUtils.DH_MDY.format(now);
		String dmy = BonziUtils.DH_DMY.format(now);
		String y = String.valueOf(now.getYear());
		
		return str.replace("{YMD}", ymd)
				.replace("{MDY}", mdy)
				.replace("{DMY}", dmy)
				.replace("{Y}", y);
	}

	private static final long CACHE_COUNT = 25;
	public static HashMap<String, List<String>> CACHED_RESULTS = new HashMap<String, List<String>>();
	
	public enum VideoCategory {
		SMARTPHONE("IMG", "MVI", "{YMD}", "{MDY}", "{DMY}"),
		WEBCAM("WIN {YMD}", "VID {YMD}"),
		WHATSAPP("WhatsApp Video {Y}"),
		PHOTO_ALBUM("/Storage/Emulated/"),
		MISC("FullSizeRender", "My Movie"),
		VIDEO_GAME("DVR", "SWF", "VLC Record"),
		PC(".MP4", ".3GP", ".MOV", ".AVI", ".WMV"),
		SPAM(".MKV", ".MPEG", ".FLV"),
		MUSIC(".FLAC", ".WAV");
		
		public String[] variants;
		private VideoCategory(String...variants) {
			this.variants = variants;
		}
		
		public String[] getVariants() {
			String[] clone = new String[this.variants.length];
			for(int i = 0; i < this.variants.length; i++) {
				clone[i] = formatString(this.variants[i]);
			}
			return clone;
		}
	}
	
	
	public GetDepthsVideoCommand() {
		this.subCategory = 0;
		this.name = "getdepthsvideo";
		this.icon = GenericEmoji.fromEmoji("🕳️");
		this.description = "Gets a video from the depths of youtube under a given category.";
		this.args = new CommandArgCollection(new EnumArg("type", VideoCategory.class));
		this.brosOnly = true;
		this.category = CommandCategory._HIDDEN;
	}

	public void sendResult(CommandExecutionInfo e, String url) {
		e.message.reply(url).queue();
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		VideoCategory category = (VideoCategory)e.args.get("type");
		String[] variants = category.getVariants();
		String variant = variants[BonziUtils.randomInt(variants.length)];
		
		if(CACHED_RESULTS.containsKey(variant)) {
			// send random cached result
			List<String> cache = CACHED_RESULTS.get(variant);
			if(cache.size() > 0) {
				int index = BonziUtils.randomInt(cache.size());
				String pull = cache.remove(index);
				this.sendResult(e, pull);
				return;
			}
		}
		
		// not cached yet, or cache was dry
		
		Instant now = Instant.now();
		now = now.minusMillis(BonziUtils.getMsForDays(1));
		DateTime googleNow = new DateTime(now.toEpochMilli());
		
		try {
			BonziUtils.sendTempMessage(e.channel, "Busy: Caching videos under term `" + variant + "`...", 3);
			Search.List request = e.bonzi.youtube.search().list("snippet");
			SearchListResponse _results = request
				.setKey(Constants.YTAPI_KEY_DH)
				.setMaxResults(CACHE_COUNT)
				.setQ(variant)
				.setType("video")
				.setOrder("date")
				.setPublishedAfter(googleNow)
				.execute();
			
			List<SearchResult> results = _results.getItems();
			if(results.isEmpty()) {
				BonziUtils.sendTempMessage(e.channel, "No results for this category were found...", 3);
				return;
			}
			
			// turn the results into urls
			List<String> cacheResults = new ArrayList<String>();
			for(SearchResult result: results)
				cacheResults.add("https://www.youtube.com/watch?v=" + result.getId().getVideoId());
			
			// choose and send a random result.
			int index = BonziUtils.randomInt(cacheResults.size());
			String pull = cacheResults.remove(index);
			this.sendResult(e, pull);
			
			// cache the results for next run
			CACHED_RESULTS.put(variant, cacheResults);
			
		} catch(IOException ioe) {
			InternalLogger.printError(ioe);
			e.message.reply("IOException: ```\n" + ioe.toString() + "\n```");
		}
	}
}