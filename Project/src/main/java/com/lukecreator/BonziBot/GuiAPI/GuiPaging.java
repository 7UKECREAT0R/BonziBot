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
		return this.minPage;
	}
	public int getMaxPage() {
		return this.maxPage;
	}
	public int getPage() {
		return this.currentPage;
	}
	public boolean getPagingEnabled() {
		return this.pagingEnabled;
	}
	public String getPageString() {
		return this.currentPage + " / " + this.maxPage;
	}
	
	protected boolean pagingEnabled = true;
	protected int minPage = 1;
	protected int maxPage = 10;
	protected int currentPage = this.minPage;
	
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
			if(--this.currentPage < this.minPage) {
				this.currentPage = this.minPage;
				return;
			}
		}
		if(right) {
			if(++this.currentPage > this.maxPage) {
				this.currentPage = this.maxPage;
				return;
			}
		}
		
		if(left | right) {
			GuiButton button0 = (GuiButton)this.elements.get(0);
			button0.asEnabled(this.currentPage > this.minPage);
			GuiButton button1 = (GuiButton)this.elements.get(1);
			button1.asEnabled(this.currentPage < this.maxPage);
			this.parent.redrawMessage(jda);
		}
	}
}
