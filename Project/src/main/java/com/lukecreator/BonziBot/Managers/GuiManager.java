package com.lukecreator.BonziBot.Managers;

import java.util.HashMap;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.GuiAPI.AllocGuiList;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiContainer;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionRemoveEvent;

public class GuiManager {
	
	public HashMap<Long, AllocGuiList> guildGuis;
	public HashMap<Long, AllocGuiList> userGuis;
	
	public GuiManager() {
		guildGuis = new HashMap<Long, AllocGuiList>();
		userGuis = new HashMap<Long, AllocGuiList>();
	}
	
	/**
	 * Initialize the GUI if it was not on constructor.
	 */
	Gui initGuiIfNot(Gui gui, JDA jda, TextChannel tc, BonziBot bonzi) {
		if(!gui.wasInitialized())
			gui.hiddenInit(jda, tc.getGuild(), bonzi);
		return gui;
	}
	Gui initGuiIfNot(Gui gui, JDA jda, PrivateChannel pc, BonziBot bonzi) {
		if(!gui.wasInitialized())
			gui.hiddenInit(jda, pc.getUser(), bonzi);
		return gui;
	}
	public void sendAndCreateGui(TextChannel tc, User owner, Gui gui, BonziBot main) {
		
		GuiContainer container = new GuiContainer(gui, tc, owner);
		initGuiIfNot(gui.setParent(container), tc.getJDA(), tc, main);
		Guild guild = tc.getGuild();
		JDA jda = tc.getJDA();
		long gId = guild.getIdLong();
		
		AllocGuiList agl;
		if(!guildGuis.containsKey(gId)) {
			agl = new AllocGuiList();
		} else {
			agl = guildGuis.get(gId);
		}
		
		container.sendMessage(jda, gId, main, agl); // Applies automatically.
	}
	public void sendAndCreateGui(PrivateChannel pc, Gui gui, BonziBot main) {
		GuiContainer container = new GuiContainer(gui, pc);
		initGuiIfNot(gui.setParent(container), pc.getJDA(), pc, main);
		User user = pc.getUser();
		JDA jda = pc.getJDA();
		long uId = user.getIdLong();
		
		AllocGuiList agl;
		if(!userGuis.containsKey(uId)) {
			agl = new AllocGuiList();
		} else {
			agl = userGuis.get(uId);
		}
		
		container.sendMessage(jda, uId, main, agl); // Applies automatically.
	}
	
	
	// Outdated as of 6/6/2021.
	// These were ported to buttons. (see GuiManager#onButtonClick)
/*	public void onReactionAdd(GuildMessageReactionAddEvent e) {
		
		if(e.getUser().isBot()) return;
		
		Guild g = e.getGuild();
		User reactor = e.getUser();
		long gId = g.getIdLong();
		
		if(!guildGuis.containsKey(gId))
			return;
		
		AllocGuiList guiList = guildGuis.get(gId);
		long mId = e.getMessageIdLong();
		if(!guiList.hasMessageId(mId))
			return;
		
		// Remove the newly added reaction.
		// This can't be done in private channels.
		ReactionEmote re = e.getReactionEmote();
		e.retrieveMessage().queue(msg -> {
			if(re.isEmoji())
				 msg.removeReaction(re.getEmoji(), reactor).queue(null, fail -> {});
			else
				msg.removeReaction(re.getEmote(), reactor).queue(null, fail -> {});
		}, fail -> {});
		
		guiList.onReactionAdd(e.getReactionEmote(), mId, e.getUser());
		guildGuis.put(gId, guiList);
	}
	public void onReactionAdd(PrivateMessageReactionAddEvent e) {
		
		if(e.getUser().isBot()) return;
		
		User u = e.getUser();
		long uId = u.getIdLong();
		
		if(!userGuis.containsKey(uId))
			return;
		
		AllocGuiList guiList = userGuis.get(uId);
		long mId = e.getMessageIdLong();
		if(!guiList.hasMessageId(mId)) {
			return;
		}
		
		guiList.onReactionAdd(e.getReactionEmote(), mId, e.getUser());
		userGuis.put(uId, guiList);
	}*/
	
	// Unused for right now.
	public void onReactionRemove(GuildMessageReactionRemoveEvent e) {
		if(e.getUser().isBot()) return;
		// unused for now
	}
	public void onReactionRemove(PrivateMessageReactionRemoveEvent e) {
		if(e.getUser().isBot()) return;
		// unused for now
	}
	
	/**
	 * The current event listener for gui-interactions.
	 */
	public void onButtonClick(ButtonClickEvent e) {
		if(e.isFromGuild()) {
			Guild g = e.getGuild();
			long gId = g.getIdLong();
			User clicker = e.getUser();
			
			if(!guildGuis.containsKey(gId)) {
				e.reply(":warning: `The bot has restarted since this was sent. Please open the GUI again!`").setEphemeral(true).queue();
				return;
			}
			
			AllocGuiList guiList = guildGuis.get(gId);
			long mId = e.getMessageIdLong();
			if(!guiList.hasMessageId(mId)) {
				e.reply(":warning: `This GUI has expired. Please open it again!`").setEphemeral(true).queue();
				return;
			}
			
			guiList.onButtonClick(e, mId, clicker);
			guildGuis.put(gId, guiList);
		} else {
			User clicker = e.getUser();
			long uId = clicker.getIdLong();
			
			if(!userGuis.containsKey(uId)) {
				e.reply(":warning: `The bot has restarted since this was sent. Please open the GUI again!`").setEphemeral(true).queue();
				return;
			}
			
			AllocGuiList guiList = userGuis.get(uId);
			long mId = e.getMessageIdLong();
			if(!guiList.hasMessageId(mId)) {
				e.reply(":warning: `This GUI has expired. Please open it again!`").setEphemeral(true).queue();
				return;
			}
			
			guiList.onButtonClick(e, mId, clicker);
			userGuis.put(uId, guiList);
		}
	}
}