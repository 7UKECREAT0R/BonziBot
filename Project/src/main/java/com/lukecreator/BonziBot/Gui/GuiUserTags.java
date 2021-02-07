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
import net.dv8tion.jda.api.entities.User;

public class GuiUserTags extends GuiPaging {
	
	public static final int PER_PAGE = 10;
	
	boolean none;
	boolean privateTags;
	long userId;
	String userName,
		fullUserName,
		avatarUrl;
	long privateGuildId;
	TagData[] contents;
	
	/*
	 * Create a new TagLeaderboard. If privateId == -1
	 * then it is considered a public tag leaderboard.
	 */
	public GuiUserTags(BonziBot bb, User u, long privateGuildId) {
		this.privateTags = privateGuildId != -1;
		this.privateGuildId = privateGuildId;
		this.userId = u.getIdLong();
		this.userName = u.getName();
		this.fullUserName = u.getAsTag();
		this.avatarUrl = u.getEffectiveAvatarUrl();
		
		List<TagData> allTags;
		if(this.privateTags)
			allTags = bb.tags.getPrivateTagsOfUser(u.getIdLong(), privateGuildId);
		else
			allTags = bb.tags.getPublicTagsOfUser(u);
		
		// Sort by uses.
		TagSort sorter = new TagSort();
		Comparator<TagData> sort = sorter.reversed();
		allTags.sort(sort);
		
		this.contents = (TagData[])allTags
			.toArray(new TagData[allTags.size()]);
		int count = this.contents.length;
		this.maxPage = (count / PER_PAGE) + 1;
		this.none = this.contents.length <= 0;
	}
	
	@Override
	public MessageEmbed draw(JDA jda) {
		
		// Fill the current page information.
		TagData[] currentPage = new TagData[PER_PAGE];
		int page = this.currentPage - 1;
		int startIndex = page * PER_PAGE;
		int endIndex = startIndex + PER_PAGE;
		if(endIndex > this.contents.length)
			endIndex = this.contents.length;
		for(int i = startIndex; i < endIndex; i++) {
			currentPage[i-startIndex] = this.contents[i];
		}
		
		// Draw the embed.
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(userName, null, avatarUrl);
		eb.setColor(Color.magenta);
		if(none) {
			if(this.privateTags)
				eb.setTitle("This user hasn't made any tags here in this server yet.");
			else
				eb.setTitle("This user hasn't made any tags yet.");
		} else {
			int len = this.contents.length;
			int per = PER_PAGE > len ? len : PER_PAGE;
			String tEnding = none ? "" : (" (" + per + "/" + len + " shown)");
			if(this.privateTags)
				eb.setTitle(this.userName + "'s top tags in this server! " + tEnding);
			else
				eb.setTitle(this.userName + "'s top tags! " + tEnding);
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
