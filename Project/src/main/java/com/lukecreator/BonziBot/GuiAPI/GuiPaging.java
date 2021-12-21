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
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬅️"), "pageleft").asEnabled(this.currentPage > this.minPage));
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("➡️"), "pageright").asEnabled(this.currentPage < this.maxPage));
	}
	
	@Override
	public void onButtonClick(String actionId, long executorId, JDA jda) {
		
		if(!this.pagingEnabled)
			return;
		
		boolean left = actionId.equals("pageleft");
		boolean right = actionId.equals("pageright");
		
		if(left) {
			if(--currentPage < minPage) {
				currentPage = minPage;
				return;
			}
		}
		if(right) {
			if(++currentPage > maxPage) {
				currentPage = maxPage;
				return;
			}
		}
		
		if(left | right) {
			GuiButton button0 = (GuiButton)this.elements.get(0);
			button0.asEnabled(this.currentPage > this.minPage);
			GuiButton button1 = (GuiButton)this.elements.get(1);
			button1.asEnabled(this.currentPage < this.maxPage);
			parent.redrawMessage(jda);
		}
	}
}
