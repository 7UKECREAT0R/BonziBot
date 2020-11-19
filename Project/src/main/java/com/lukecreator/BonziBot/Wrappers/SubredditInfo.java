package com.lukecreator.BonziBot.Wrappers;

import java.awt.Color;

import org.json.simple.JSONObject;

/*
 * minimal implementation of subreddit info
 */
public class SubredditInfo {
	
	public String colorHex;
	public int colorInteger;
	public Color color;
	
	public String description;
	public String visibility;
	public boolean nsfw;
	public long created;
	
	public SubredditInfo(JSONObject json) {
		JSONObject data = (JSONObject)json.get("data");
		this.description = (String)data.get("description");
		this.visibility = (String)data.get("subreddit_type");
		this.colorHex = ((String)data.get("key_color")).replace("#", "0x");
		this.nsfw = (boolean)data.get("over18");
		this.created = (long)data.get("created");
		
		this.colorInteger = Integer.decode(this.colorHex);
		this.color = new Color(this.colorInteger);
	}
}
