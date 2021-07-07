package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Data.UserAccountSort;
import com.lukecreator.BonziBot.Data.UserAccountSort.UASType;
import com.lukecreator.BonziBot.GuiAPI.GuiPaging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class GuiXpLeaderboard extends GuiPaging {
	
	class LbEntry {
		public UserAccount account;
		public long userId;
		public String username;
		
		public LbEntry(UserAccount account, long userId, String username) {
			this.account = account;
			this.userId = userId;
			this.username = username;
		}
	}
	
	public static final int PER_PAGE = 10;
	
	boolean none;
	LbEntry[] leaderboardContents;
	
	public GuiXpLeaderboard(BonziBot bb, JDA jda) {
		
		HashMap<Long, UserAccount> _accts = bb.accounts.getAccounts();
		List<Map.Entry<Long, UserAccount> > list = 
			new LinkedList<Map.Entry<Long, UserAccount> >(_accts.entrySet()); 
		
		// Sort by uses.
		UserAccountSort sort = new UserAccountSort(UASType.XP);
		Collections.sort(list, sort.reversed());
		
		List<LbEntry> temp = new ArrayList<LbEntry>();
		Iterator<Map.Entry<Long, UserAccount>> iterator = list.iterator();
		while(iterator.hasNext()) {
			Map.Entry<Long, UserAccount> entry = iterator.next();
			User theUser = jda.getUserById(entry.getKey());
			
			if(theUser == null)
				continue;
			
			LbEntry lbe = new LbEntry(entry.getValue(),
				entry.getKey(), theUser.getName());
			temp.add(lbe);
		}
		this.leaderboardContents = (LbEntry[])temp
			.toArray(new LbEntry[temp.size()]);
		
		int count = this.leaderboardContents.length;
		this.maxPage = (count / PER_PAGE);
		if(count % PER_PAGE != 0)
			this.maxPage++;
		this.none = this.leaderboardContents.length <= 0;
	}
	
	@Override
	public Object draw(JDA jda) {
		
		// Fill the current page information.
		LbEntry[] currentPage = new LbEntry[PER_PAGE];
		int page = this.currentPage - 1;
		int startIndex = page * PER_PAGE;
		int endIndex = startIndex + PER_PAGE;
		if(endIndex > this.leaderboardContents.length)
			endIndex = this.leaderboardContents.length;
		for(int i = startIndex; i < endIndex; i++) {
			currentPage[i-startIndex] = this.leaderboardContents[i];
		}
		
		// "draw" the embed.
		EmbedBuilder eb = new EmbedBuilder()
			.setColor(Color.green);
		if(none) {
			eb.setTitle("Having trouble loading the XP leaderboard...");
			eb.setDescription("Leaderboard users fetched: " + this.leaderboardContents.length);
		} else {
			int len = this.leaderboardContents.length;
			int per = PER_PAGE > len ? len : PER_PAGE;
			String tEnding = none ? "" : (" (" + per + "/" + len + " shown)");
			eb.setTitle("Top no-lifers! " + tEnding);
		}
		
		String pageString = "Page " + this.currentPage + "/" + this.maxPage;
		eb.setFooter(pageString);
		
		for(int i = 0; i < PER_PAGE; i++) {
			LbEntry entry = currentPage[i];
			if(entry == null) break;
			int place = startIndex + i + 1;
			UserAccount acc = entry.account;
			String name = place + ". " + entry.username;
			
			String icon = null;
			if(acc.isPremium)
				icon = "| 👑 Premium";
			if(this.bonziReference.special.getIsBro(entry.userId))
				icon = "| ❤️ Admin Friend";
			if(this.bonziReference.special.getIsAdmin(entry.userId))
				icon = "| 💻 Admin";
			if(icon != null)
				name += " " + icon;
			
			String xp = BonziUtils.comma(acc.getXP()) + " XP";
			String lvl = "Level " + acc.getLevel();
			eb.addField(name, xp + "\n" + lvl, false);
		}
		
		return eb.build();
	}
}