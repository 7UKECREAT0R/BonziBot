package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.EmojiArg;
import com.lukecreator.BonziBot.CommandAPI.RoleArg;
import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.ReactionRole;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiButton.ButtonColor;
import com.lukecreator.BonziBot.GuiAPI.GuiEditDialog;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class GuiReactionRoles extends Gui {
	
	// discord limit
	private static final int MAX_ITEMS = 20;
	
	public final String text;
	List<ReactionRole> items;
	
	public GuiReactionRoles(String text) {
		this.text = text;
		this.items = new ArrayList<ReactionRole>();
	}
	private ReactionRole[] getItems() {
		return (ReactionRole[])this.items.toArray(new ReactionRole[this.items.size()]);
	}
	
	@Override
	public void initialize(JDA jda) {
		this.reinitialize();
	}
	public void reinitialize() {
		this.elements.clear();
		int size = this.items.size();
		
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("➖"), "remove").asEnabled(size > 0));
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("➕"), "add").asEnabled(size < MAX_ITEMS));
		this.elements.add(new GuiButton("CREATE", ButtonColor.GREEN, "finish").asEnabled(size > 0));
	}
	
	@Override
	public Object draw(JDA jda) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.orange);
		
		if(this.text.length() <= MessageEmbed.TITLE_MAX_LENGTH)
			eb.setTitle(this.text);
		else
			eb.setDescription(this.text);
		
		if(this.items.isEmpty())
			eb.addField("No roles added yet.", "Hit `➕` to add your first role!", false);
		else {
			eb.addField("Roles:", BonziUtils.stringJoinArbitrary("\n", this.items), false);
		}
		
		return eb.build();
	}
	
	@Override
	public void onButtonClick(String buttonId, long clickerId, JDA jda) {
		int size = this.items.size();
		
		if(buttonId.equals("remove")) {
			if(size < 1)
				return;
			this.items.remove(size - 1);
			
			this.reinitialize();
			this.parent.redrawMessage(jda);
			return;
		}
		if(buttonId.equals("add")) {
			if(size >= MAX_ITEMS)
				return;
			
			MessageChannel channel = this.parent.getChannel(jda);
			Guild guild = jda.getGuildById(this.parent.guildId);
			Member member = guild.getMemberById(clickerId);
			
			if(member == null)
				return;
			if(!member.hasPermission(Permission.MANAGE_ROLES)) {
				this.parent.disable(jda);
				this.parent.redrawMessage(jda);
				return;
			}
			
			GuiEditDialog dialog = new GuiEditDialog(this, "Adding role...",
				new GuiEditEntryText(new RoleArg("role"), null, "Role", "The role to be given."),
				new GuiEditEntryText(new StringArg("text"), null, "Text", "The description for this role. Get creative!"),
				new GuiEditEntryText(new EmojiArg("reaction"), null, "Reaction", "The emoji that will give this role."))
				.after(output -> {
					if(output.wasCancelled)
						return;
					Role role = (Role)output.values[0];
					String text = output.stringValues[1];
					GenericEmoji emoji = (GenericEmoji)output.values[2];
					
					if(!member.canInteract(role)) {
						channel.sendMessageEmbeds(BonziUtils.failureEmbed("Couldn't add:",
							"You're not allowed to modify that role.")).queue(del -> {
								del.delete().queueAfter(5, TimeUnit.SECONDS);
							});
						return;
					}
					
					if(this.items.stream().anyMatch(rr -> {
						return rr.emoji.equals(emoji) || rr.roleId == role.getIdLong();
					})) {
						channel.sendMessageEmbeds(BonziUtils.failureEmbed("Couldn't add:",
							"There's already an item with that emoji or role.")).queue(del -> {
								del.delete().queueAfter(5, TimeUnit.SECONDS);
							});
						return;
					}
					
					ReactionRole item = new ReactionRole(text, role.getIdLong(), emoji);
					this.items.add(item);
					this.reinitialize();
					this.parent.redrawMessage(jda);
					return;
				});
			
			this.parent.setActiveGui(dialog, jda);
			return;
		}
		
		if(buttonId.equals("finish")) {
			if(size < 1)
				return;
			
			ReactionRole[] finalItems = this.getItems();
			BonziBot bb = this.bonziReference;
			MessageChannel channel = this.parent.getChannel(jda);
			
			MessageEmbed me = BonziUtils.createReactionRolesMenu(this.text, finalItems);
			
			channel.sendMessageEmbeds(me).queue(msg -> {
				for(ReactionRole rr: finalItems)
					rr.emoji.react(msg);
				bb.reactionRoles.setReactionRoles(msg, finalItems);
			});
			
			this.parent.disableSilent();
			this.parent.delete(jda);
			return;
		}
	}
}
