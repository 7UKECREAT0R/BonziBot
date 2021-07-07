package com.lukecreator.BonziBot.GuiAPI;

import com.lukecreator.BonziBot.Data.GenericEmoji;

import net.dv8tion.jda.api.JDA;

/**
 * A GUI template which implements a paging system.
 * @author Lukec
 *
 */
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
	protected int minPage = 1;
	protected int maxPage = 10;
	protected int currentPage = minPage;
	
	public GuiPaging() {}
	
	@Override
	public void initialize(JDA jda) {
		this.buttons.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬅️"), "pageleft"));
		this.buttons.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("➡️"), "pageright"));
	}
	
	@Override
	public void onAction(String actionId, long executorId, JDA jda) {
		if(actionId.equals("pageleft") && pagingEnabled) {
			if(--currentPage < minPage)
				currentPage = minPage;
			else parent.redrawMessage(jda);
		}
		if(actionId.equals("pageright") && pagingEnabled) {
			if(++currentPage > maxPage)
				currentPage = maxPage;
			else parent.redrawMessage(jda);
		}
	}
}
