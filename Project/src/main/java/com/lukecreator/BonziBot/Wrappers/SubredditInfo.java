package com.lukecreator.BonziBot.Wrappers;

import java.awt.Color;
import java.util.Date;

import org.json.simple.JSONObject;

/*
 * minimal implementation of subreddit info
 */
public class SubredditInfo {
	
	public String colorHex;
	public int colorInteger;
	public Color color;
	
	public String name;
	public String description;
	public String description_trimmed;
	public String visibility;
	public boolean nsfw;
	public Date created;
	public double createdMs_d;
	public long createdMs;
	
	public SubredditInfo(JSONObject json) {
		JSONObject data = (JSONObject)json.get("data");
		this.name = (String)data.get("display_name");
		this.description = (String)data.get("description");
		this.description_trimmed = (this.description.length() > 2048) ?
				this.description.substring(0, 2047) : this.description;
		this.visibility = (String)data.get("subreddit_type");
		this.colorHex = ((String)data.get("key_color")).replace("#", "0x");
		this.nsfw = (boolean)data.get("over18");
		this.createdMs_d = (double)data.get("created");
		this.createdMs = (long)Math.round(this.createdMs_d);
		this.created = new Date(this.createdMs);
		
		this.colorInteger = Integer.decode(this.colorHex);
		this.color = new Color(this.colorInteger);
	}
}
