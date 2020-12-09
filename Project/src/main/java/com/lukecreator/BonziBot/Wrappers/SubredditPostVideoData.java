package com.lukecreator.BonziBot.Wrappers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/*
 * *sigh* guess im making this too...
 */
public class SubredditPostVideoData {
	
	public boolean postExists;
	
	public boolean hasVideoMedia = false;
	public String videoURL;
	
	public SubredditPostVideoData(JSONObject json) {
		try {
			JSONObject data = (JSONObject)json.get("data");
			JSONArray array = (JSONArray)data.get("children");
			JSONObject first = (JSONObject)array.get(0);
			JSONObject data2 = (JSONObject)first.get("data");
			JSONObject secureMedia = (JSONObject)data2.get("secure_media");
			JSONObject redditVideo = (JSONObject)secureMedia.get("reddit_video");
			this.hasVideoMedia = true;
			this.videoURL = (String)redditVideo.get("scrubber_media_url");
		} catch(Exception exc) {
			// Probably doesn't exist.
			postExists = false;
		}
	}
	
}
