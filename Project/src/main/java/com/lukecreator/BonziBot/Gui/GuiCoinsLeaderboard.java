package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.CoinSort;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.GuiAPI.GuiPaging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class GuiCoinsLeaderboard extends GuiPaging {
	
	public static final int PER_PAGE = 10;
	
	boolean none;
	boolean localCoins;
	long privateGuildId;
	List<Entry<Long, UserAccount>> leaderboardContents;
	
	/**
	 * Create a new Coins Leaderboard. If privateId == -1
	 * then it is considered a public coins leaderboard.
	 */
	public GuiCoinsLeaderboard(BonziBot bb, JDA jda, long privateGuildId) {
		this.localCoins = privateGuildId != -1;
		this.privateGuildId = privateGuildId;
		
		if(this.localCoins) {
			HashMap<Long, UserAccount> accounts = bb.accounts.getAccounts();
			Guild guild = jda.getGuildById(this.privateGuildId);
			this.leaderboardContents = new ArrayList<Entry<Long, UserAccount>>();
			
			for(Member member: guild.getMembers()) {
				long id = member.getIdLong();
				if(!accounts.containsKey(id))
					continue;
				UserAccount account = accounts.get(id);
				this.leaderboardContents.add(new AbstractMap.SimpleEntry
						<Long, UserAccount>(Long.valueOf(id), account));
			}
		} else
			this.leaderboardContents = new ArrayList<Entry<Long, UserAccount>>
				(bb.accounts.getAccounts().entrySet());
		
		// Sort by uses.
		CoinSort sorter = new CoinSort();
		Comparator<Entry<Long, UserAccount>> sort = sorter.reversed();
		this.leaderboardContents.sort(sort);
		int count = this.leaderboardContents.size();
		this.maxPage = (count / PER_PAGE) + 1;
		this.none = this.leaderboardContents.size() <= 0;
	}
	
	@Override
	public Object draw(JDA jda) {
		
		// Fill the current page information.
		List<Entry<Long, UserAccount>> currentPage = new ArrayList<Entry<Long, UserAccount>>(PER_PAGE);
		for(int i = 0; i < PER_PAGE; i++)
			currentPage.add(null);
		
		int page = this.currentPage - 1;
		int startIndex = page * PER_PAGE;
		int endIndex = startIndex + PER_PAGE;
		
		// bound end index
		if(endIndex > this.leaderboardContents.size())
			endIndex = this.leaderboardContents.size();
		
		if (this.leaderboardContents.size() > 0) {
			for(int i = startIndex; i < endIndex; i++) {
				currentPage.set(i - startIndex, this.leaderboardContents.get(i));
			}
		}
		
		// Draw the embed.
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.magenta);
		if(this.none) {
			eb.setTitle("No.. users.. have any coins?");
		} else {
			int len = this.leaderboardContents.size();
			int per = PER_PAGE > len ? len : PER_PAGE;
			String tEnding = this.none ? "" : (" (" + per + "/" + len + " shown)");
			if(this.localCoins)
				eb.setTitle("Richest members in this server!" + tEnding);
			else
				eb.setTitle("Richest users of all time!" + tEnding);
		}
		
		String pageString = "Page " + this.currentPage + "/" + this.maxPage;
		eb.setFooter(pageString);
		
		for(int i = 0; i < PER_PAGE; i++) {
			Entry<Long, UserAccount> coin = currentPage.get(i);
			if(coin == null)
				continue;
			int place = startIndex + i + 1;
			
			long id = coin.getKey();
			User user = jda.getUserById(id);
			
			String name;
			if(user == null)
				name = place + ". UNKNOWN USER";
			else
				name = place + ". " + user.getName();
			
			String uses = "**" + BonziUtils.comma(coin.getValue().getCoins()) + " coins**";
			eb.addField(name, uses, false);
		}
		
		return eb.build();
	}
}
