package com.lukecreator.BonziBot.GuiAPI;

import com.lukecreator.BonziBot.Data.GenericEmoji;

import net.dv8tion.jda.api.JDA;

public class GuiPaging extends Gui {
	
	public int getMinPage() {
		return minPage;
	}
	public int getMaxPage() {
		return maxPage;
	}
	public int getPage() {
		return currentPage;
	}
	public boolean getPagingEnabled() {
		return pagingEnabled;
	}
	public String getPageString() {
		return this.currentPage + "/" + this.maxPage;
	}
	
	protected boolean pagingEnabled = true;
	protected int minPage = 0;
	protected int maxPage = 10;
	protected int currentPage = minPage;
	
	public GuiPaging() {}
	
	@Override
	public void initialize(JDA jda) {
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("⬅️"), 0));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("➡️"), 1));
	}
	
	@Override
	public void onAction(int buttonId, JDA jda) {
		if(buttonId == 0 && pagingEnabled) {
			if(--currentPage < minPage)
				currentPage = minPage;
			else parent.redrawMessage(jda);
		}
		if(buttonId == 1 && pagingEnabled) {
			if(++currentPage > maxPage)
				currentPage = maxPage;
			else parent.redrawMessage(jda);
		}
	}
}
