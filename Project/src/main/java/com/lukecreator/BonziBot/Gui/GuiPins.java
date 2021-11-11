package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.MessageReference;
import com.lukecreator.BonziBot.Data.SUser;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiPaging;
import com.lukecreator.BonziBot.Managers.UserAccountManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class GuiPins extends GuiPaging {
	
	public static final int PER_PAGE = 5;
	
	final long guildId;
	
	boolean currentServer = false;
	boolean deleteMode = false;
	int deleteCursor = 0;
	
	public SUser owner;
	public Color color;
	public List<MessageReference> allPins;
	public List<MessageReference> pins;
	
	public GuiPins(User user, Color color, long guildId) {
		this.owner = new SUser(user);
		this.color = color;
		this.guildId = guildId;
	}
	public void setPins() {
		this.pins.clear();
		if(this.currentServer) {
			for(MessageReference ref: this.allPins)
				if(ref.fromGuild && ref.guildId == guildId)
					this.pins.add(ref);
		} else {
			for(MessageReference all: this.allPins)
				this.pins.add(all);
		}
	}
	@Override
	public void initialize(JDA jda) {
		this.allPins = this
			.bonziReference
			.accounts
			.getUserAccount(owner.id)
			.getPersonalPins();
		this.pins = new ArrayList<MessageReference>();
		this.setPins();
		
		this.currentPage = 1;
		
		this.reinitialize();
	}
	public void reinitialize() {
		this.minPage = 1;
		this.maxPage = pins.size() / PER_PAGE;
		if(pins.size() % PER_PAGE != 0)
			this.maxPage++;
		
		this.elements.clear();
		if(this.deleteMode) {
			this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬅️"), "return"));
			this.elements.add(new GuiButton("Up", GuiButton.ButtonColor.BLUE, "up"));
			this.elements.add(new GuiButton("Down", GuiButton.ButtonColor.BLUE, "down"));
			this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("❌"), "delete").withColor(GuiButton.ButtonColor.RED));
		} else {
			super.initialize(null);
			this.elements.add(new GuiButton("Remove Pins", GuiButton.ButtonColor.RED, "remove").asEnabled(this.pins.size() > 0));
			this.elements.add(new GuiButton(currentServer ? "This Server" : "All Pins", GuiButton.ButtonColor.GRAY, "guildonly").asEnabled(guildId != 0l));
			
		}
	}
	
	@Override
	public Object draw(JDA jda) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(owner.name, null, owner.avatarUrl);
		eb.setColor(color);
		eb.setTitle("Personal Pins");
		eb.setDescription("To pin something, react to any message with `📌`!");
		
		if(this.pins.size() < 1) {
			eb.setFooter("You haven't pinned anything yet!");
		} else {
			int offset = (this.currentPage - 1) * PER_PAGE;
			int end = offset + PER_PAGE;
			if(end > pins.size())
				end = pins.size();
			
			for(int i = offset; i < end; i++) {
				MessageReference current = pins.get(i);
				String title = (i + 1) + ". " + current.authorName + ":";
				if(this.deleteMode && (i - offset) == this.deleteCursor)
					title += " `< ❌`";
				String desc = current.messagePreview + "\n[Jump to Pin](" + current.messageUrl + ")";
				eb.addField(title, desc, false);
			}
			if(this.deleteMode)
				eb.setFooter("Page " + this.getPageString());
			else
				eb.setFooter("Page " + this.getPageString());
		}
		return eb.build();
	}
	
	@Override
	public void onButtonClick(String actionId, long executorId, JDA jda) {
		if(this.deleteMode) {
			if(actionId.equals("return")) {
				this.deleteMode = false;
				this.reinitialize();
				this.parent.redrawMessage(jda);
				return;
			}
			
			int limit = PER_PAGE;
			if(this.currentPage == this.maxPage)
				limit = this.pins.size() % PER_PAGE;
			
			if(actionId.equals("up")) {
				if(this.pins.size() > 0) {
					this.deleteCursor--;
					if(this.deleteCursor < 0)
						this.deleteCursor = limit - 1;
					if(this.deleteCursor >= limit)
						this.deleteCursor = 0;
					this.parent.redrawMessage(jda);
				}
			}
			if(actionId.equals("down")) {
				if(this.pins.size() > 0) {
					this.deleteCursor++;
					if(this.deleteCursor < 0)
						this.deleteCursor = limit - 1;
					if(this.deleteCursor >= limit)
						this.deleteCursor = 0;
					this.parent.redrawMessage(jda);
				}		
			}
			if(actionId.equals("delete")) {
				int offset = (this.currentPage - 1) * PER_PAGE;
				this.allPins.remove(this.pins.remove(offset + this.deleteCursor));
				
				
				UserAccountManager uam = this.bonziReference.accounts;
				UserAccount account = uam.getUserAccount(executorId);
				account.setPersonalPins(this.allPins);
				uam.setUserAccount(executorId, account);
				
				if(this.pins.size() > 0) {
					if(--this.deleteCursor < 0) {
						if(--this.currentPage < 1) {
							this.deleteCursor = 0;
							this.currentPage = 1;
						} else {
							this.deleteCursor = PER_PAGE - 1;
						}
					}
				} else {
					this.deleteMode = false;
					this.deleteCursor = 0;
					this.reinitialize();
				}
				this.parent.redrawMessage(jda);
			}
		} else {
			super.onButtonClick(actionId, executorId, jda);
			if(actionId.equals("remove")) {
				this.deleteMode = true;
				this.deleteCursor = 0;
				this.reinitialize();
				this.parent.redrawMessage(jda);
				return;
			}
			if(actionId.equals("guildonly")) {
				this.currentServer = !this.currentServer;
				
				this.setPins();
				if(this.pins.size() < 1) {
					this.parent.getChannel(jda).sendMessageEmbeds
						(BonziUtils.failureEmbed("No pins in this server!")).queue(msg -> {
							msg.delete().queueAfter(3, TimeUnit.SECONDS);
						});
					this.currentServer = !this.currentServer;
					this.setPins(); // set pins back
					return;
				}
				
				this.reinitialize();
				this.currentPage = 1;
				this.parent.redrawMessage(jda);
			}
		}
	}
}
