package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.TagData;
import com.lukecreator.BonziBot.Data.TagSort;
import com.lukecreator.BonziBot.GuiAPI.GuiPaging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class GuiTagLeaderboard extends GuiPaging {
	
	public static final int PER_PAGE = 10;
	
	boolean none;
	boolean privateTags;
	long privateGuildId;
	TagData[] leaderboardContents;
	
	/*
	 * Create a new TagLeaderboard. If privateId == -1
	 * then it is considered a public tag leaderboard.
	 */
	public GuiTagLeaderboard(BonziBot bb, long privateGuildId) {
		this.privateTags = privateGuildId != -1;
		this.privateGuildId = privateGuildId;
		
		List<TagData> allTags;
		if(this.privateTags)
			allTags = bb.tags.getPrivateTags(privateGuildId);
		else
			allTags = bb.tags.getPublicTags();
		
		// Sort by uses.
		TagSort sorter = new TagSort();
		Comparator<TagData> sort = sorter.reversed();
		allTags.sort(sort);
		
		this.leaderboardContents = (TagData[])allTags
			.toArray(new TagData[allTags.size()]);
		int count = this.leaderboardContents.length;
		this.maxPage = (count / PER_PAGE) + 1;
		this.none = this.leaderboardContents.length <= 0;
	}
	
	@Override
	public MessageEmbed draw(JDA jda) {
		
		// Fill the current page information.
		TagData[] currentPage = new TagData[PER_PAGE];
		int page = this.currentPage - 1;
		int startIndex = page * PER_PAGE;
		int endIndex = startIndex + PER_PAGE;
		if(endIndex > this.leaderboardContents.length)
			endIndex = this.leaderboardContents.length;
		for(int i = startIndex; i < endIndex; i++) {
			currentPage[i-startIndex] = this.leaderboardContents[i];
		}
		
		// Draw the embed.
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.magenta);
		if(none) {
			eb.setTitle("No tags have been made here yet!");
			eb.setDescription("Use " + this.prefixOfLocation + "tag to create the first one!");
		} else {
			int len = this.leaderboardContents.length;
			int per = PER_PAGE > len ? len : PER_PAGE;
			String tEnding = none ? "" : (" (" + per + "/" + len + " shown)");
			if(this.privateTags)
				eb.setTitle("Top tags in this server!" + tEnding);
			else
				eb.setTitle("Top tags of all time!" + tEnding);
		}
		
		String pageString = "Page " + this.currentPage + "/" + this.maxPage;
		eb.setFooter(pageString);
		
		for(int i = 0; i < PER_PAGE; i++) {
			TagData tag = currentPage[i];
			if(tag == null) continue;
			int place = startIndex + i + 1;
			String name = place + ". " + tag.name;
			String uses = "Used **" + tag.uses + "** times.";
			eb.addField(name, uses, false);
		}
		
		return eb.build();
	}
}
