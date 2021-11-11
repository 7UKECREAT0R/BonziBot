package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.ModernWarn;
import com.lukecreator.BonziBot.Data.ModernWarnSort;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiPaging;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class GuiWarns extends GuiPaging {
	
	public static final int PER_PAGE = 8;
	
	boolean removeMode = false;
	int removeCursor = 0;
	
	long guildId;
	long userId;
	List<ModernWarn> contents;
	String guildName;
	String userName;
	
	public GuiWarns(List<ModernWarn> contents, Member member, Guild guild) {
		this.guildName = guild.getName();
		this.guildId = guild.getIdLong();
		this.userName = member.getUser().getName();
		this.userId = member.getUser().getIdLong();
		Collections.sort(contents, new ModernWarnSort().reversed());
		this.contents = contents;
		int count = contents.size();
		this.maxPage = (count / PER_PAGE);
		if(count % PER_PAGE != 0)
			this.maxPage++;
	}
	
	@Override
	public void initialize(JDA jda) {
		this.reinitialize(jda);
	}
	public void reinitialize(JDA jda) {
		this.elements.clear();
		
		if(this.removeMode) {
			this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬅️"), "return"));
			this.elements.add(new GuiButton("Up", GuiButton.ButtonColor.BLUE, "up"));
			this.elements.add(new GuiButton("Down", GuiButton.ButtonColor.BLUE, "down"));
			this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("❌"), "delete").withColor(GuiButton.ButtonColor.RED));
		} else {
			super.initialize(jda);
			this.elements.add(new GuiButton("Remove Warns", GuiButton.ButtonColor.RED, "removemode"));
		}
	}
	
	@Override
	public Object draw(JDA jda) {
		
		if(!this.parent.getEnabled()) {
			EmbedBuilder eb = new EmbedBuilder()
				.setColor(Color.orange);
			eb.setTitle("Warns for user " + userName);
			eb.setDescription("All have been deleted.");
			return eb.build();
		}
		
		ModernWarn[] currentPage = new ModernWarn[PER_PAGE];
		int page = this.currentPage - 1;
		int startIndex = page * PER_PAGE;
		int endIndex = startIndex + PER_PAGE;
		int count = contents.size();
		if(endIndex > count)
			endIndex = count;
		for(int i = startIndex; i < endIndex; i++) {
			currentPage[i - startIndex] = this.contents.get(i);
		}
		this.maxPage = (count / PER_PAGE);
		if(count % PER_PAGE != 0)
			this.maxPage++;
		
		EmbedBuilder eb = new EmbedBuilder()
			.setColor(Color.orange);
		int per = PER_PAGE > count ? count : PER_PAGE;
		eb.setTitle("Warns for user " + userName);
		eb.setDescription(per + "/" + count + " shown.");
		
		for(int i = 0; i < PER_PAGE; i++) {
			ModernWarn warn = currentPage[i];
			if(warn == null)
				break;
			LocalDate instant = Instant
				.ofEpochMilli(warn.timestamp)
				.atZone(ZoneOffset.UTC)
				.toLocalDate();
			String date = instant.format(BonziUtils.MMddyy);
			if(this.removeMode && i == this.removeCursor)
				date += " `< ❌`";
			String reason = warn.reason;
			eb.addField(date, reason, false);
		}
		
		String pageString = "Page " + this.currentPage + "/" + this.maxPage;
		eb.setFooter(pageString);
		
		return eb.build();
	}
	
	@Override
	public void onButtonClick(String actionId, long executorId, JDA jda) {
		UserAccountManager uam = this.bonziReference.accounts;
		UserAccount account = uam.getUserAccount(this.userId);
		
		if(this.removeMode) {
			if(actionId.equalsIgnoreCase("return")) {
				this.removeMode = false;
				this.reinitialize(jda);
				this.parent.redrawMessage(jda);
				return;
			}
			// Highest point of the cursor.
			int highBound = (this.currentPage == this.maxPage)
				? this.contents.size() % PER_PAGE : PER_PAGE;
			if(actionId.equalsIgnoreCase("up")) {
				this.removeCursor--;
				if(this.removeCursor < 0)
					this.removeCursor = highBound - 1;
				if(this.removeCursor >= highBound)
					this.removeCursor = highBound - 1;
				this.parent.redrawMessage(jda);
				return;
			}
			if(actionId.equalsIgnoreCase("down")) {
				this.removeCursor++;
				if(this.removeCursor >= highBound)
					this.removeCursor = highBound - 1;
				if(this.removeCursor < 0)
					this.removeCursor = 0;
				this.parent.redrawMessage(jda);
				return;
			}
			if(actionId.equalsIgnoreCase("delete")) {
				int page = this.currentPage - 1;
				int pageOffset = page * PER_PAGE;
				int index = pageOffset + this.removeCursor;
				this.contents.remove(index);
				account.setWarns(this.guildId, this.contents);
				uam.setUserAccount(this.userId, account);
				
				if(this.contents.size() > 0) {
					if(highBound == 1)
						this.currentPage--;
					else if(this.removeCursor > 0)
						this.removeCursor--;
				} else {
					this.parent.disable(jda);
					this.parent.redrawMessage(jda);
				}
				this.parent.redrawMessage(jda);
			}
		} else {
			super.onButtonClick(actionId, executorId, jda);
			if(actionId.equalsIgnoreCase("removemode")) {
				this.removeMode = true;
				this.removeCursor = 0;
				this.reinitialize(jda);
				this.parent.redrawMessage(jda);
				return;
			}
		}
	}
}
