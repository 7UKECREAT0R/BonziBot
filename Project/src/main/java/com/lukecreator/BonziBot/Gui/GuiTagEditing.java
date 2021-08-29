package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.EmoteCache;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;

public class GuiTagEditing extends Gui {
	
	boolean isEditing = false;
	long sentEditingMessage = -1;
	
	boolean isPrivate;
	long guildId;
	
	String tagName;
	EmbedBuilder startEmbed;
	
	public GuiTagEditing(EmbedBuilder startEmbed, String tagName, boolean isPrivate, long guildId) {
		super();
		this.startEmbed = startEmbed;
		this.tagName = tagName;
		this.isPrivate = isPrivate;
		if(this.isPrivate)
			this.guildId = guildId;
	}
	
	@Override
	public void initialize(JDA jda) {
		super.initialize(jda);
		this.buttons.add(new GuiButton(GenericEmoji.fromEmoji("📝"), "Edit Tag", GuiButton.Color.BLUE, "edit"));
		this.buttons.add(new GuiButton(GenericEmoji.fromEmote(EmoteCache.getEmoteByName("b_trash")), "Delete Tag", GuiButton.Color.RED, "delete"));
	}
	
	@Override
	public Object draw(JDA jda) {
		return startEmbed.build();
	}
	
	@Override
	public void onAction(String actionId, long executorId, JDA jda) {
		
		// Edit
		if(actionId.equals("edit")) {
			BonziBot ref = this.bonziReference;
			EventWaiterManager waiter = ref.eventWaiter;
			MessageChannel channel = this.parent.getChannel(jda);
			
			if(isEditing) {
				isEditing = false;
				startEmbed.setColor(Color.magenta);
				channel.sendMessageEmbeds(BonziUtils.quickEmbed("No longer editing this command.",
						"You can send messages again!", Color.green).build()).queue(mm -> {
							mm.delete().queueAfter(3, TimeUnit.SECONDS);
						});
				waiter.stopWaitingForResponse(this.parent.ownerId);
				if(sentEditingMessage != -1) {
					channel.deleteMessageById(sentEditingMessage).queue();
					sentEditingMessage = -1;
				}
			} else {
				isEditing = true;
				startEmbed.setColor(Color.white);
				channel.sendMessageEmbeds(BonziUtils.quickEmbed("The next message you send will be the new response.",
						"Hit the edit button again to cancel editing.", Color.white).build()).queue(sent -> {
							sentEditingMessage = sent.getIdLong();
							waiter.waitForResponse(this.parent.ownerId, msg -> {
								this.bonziReference.tags.receiveMessage
									(this.bonziReference, msg, tagName, isPrivate?guildId:-1, true);
								this.startEmbed.setColor(Color.magenta);
								this.isEditing = false;
								this.parent.redrawMessage(jda);
							});
						});
			}
			this.parent.redrawMessage(jda);
		}
		
		// Delete
		if(actionId.equals("delete")) {
			
			if(isPrivate)
				this.bonziReference.tags.removePrivateTagByName(tagName, guildId);
			else this.bonziReference.tags.removeTagByName(tagName);
			
			startEmbed = new EmbedBuilder();
			startEmbed.setTitle("Tag was deleted.");
			startEmbed.setDescription("Previous name: " + tagName);
			startEmbed.setColor(Color.red);
			this.parent.disable(jda);
			this.parent.redrawMessage(jda);
		}
	}
}
