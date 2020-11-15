package com.lukecreator.BonziBot.Gui;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.EmojiCache;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/*
 * Base Gui class. Represents a message containing a Gui.
 */
public class GuiContainer {
	
	public boolean isGuild = false;
	public boolean isDm = false;
	
	// Message Stuff
	public boolean hasSentMessage = false;
	public long messageId = -1;
	
	// If Guild:
	public long guildId = -1;
	public long channelId = -1;
	// If User:
	public long userDmId = -1;
	
	Gui gui;
	
	public GuiContainer(Gui gui, TextChannel tc) {
		this.gui = gui;
		this.gui.parent = this;
		this.isGuild = true;
		this.guildId = tc.getGuild().getIdLong();
		this.channelId = tc.getIdLong();
	}
	public GuiContainer(Gui gui, PrivateChannel pc) {
		this.gui = gui;
		this.gui.parent = this;
		this.isDm = true;
		this.userDmId = pc.getUser().getIdLong();
	}
	
	/*
	 * This is BY FAR the most PAINFUL function
	 *  I will ever write in BonziBot I swear
	 */
	public void sendMessage(JDA jda) {
		if(isGuild) {
			Guild g = jda.getGuildById(guildId);
			TextChannel tc = g.getTextChannelById(channelId);
			tc.sendMessage(gui.draw()).queue(mmm -> {
				for(GuiButton gb: gui.buttons) {
					boolean gen = gb.icon.getIsGeneric();
					if(gen) mmm.addReaction(gb.icon.getGenericEmoji()).queue();
					else {
						long emoteId = gb.icon.getGuildEmojiId();
						Emote e = EmojiCache.getEmoteById(emoteId);
						mmm.addReaction(e).queue();
					}
				}
				this.messageId = mmm.getIdLong();
				hasSentMessage = true;
			});
		} else {
			User user = jda.getUserById(userDmId);
			if(user == null) {
				// copy of BonziUtils::messageUser
				jda.retrieveUserById(userDmId).queue(u -> {
					if(u.hasPrivateChannel() && BonziUtils.userPrivateChannels.containsKey(userDmId)) {
						long cId = BonziUtils.userPrivateChannels.get(userDmId);
						PrivateChannel pc = u.getJDA().getPrivateChannelById(cId);
						pc.sendMessage(gui.draw()).queue(mmm -> {
							for(GuiButton gb: gui.buttons) {
								boolean gen = gb.icon.getIsGeneric();
								if(gen) mmm.addReaction(gb.icon.getGenericEmoji()).queue();
								else {
									long emoteId = gb.icon.getGuildEmojiId();
									Emote e = EmojiCache.getEmoteById(emoteId);
									mmm.addReaction(e).queue();
								}
							}
							this.messageId = mmm.getIdLong();
							hasSentMessage = true;
						});
					} else {
						u.openPrivateChannel().queue(p -> {
							long privateChannelId = p.getIdLong();
							BonziUtils.userPrivateChannels.put(userDmId, privateChannelId);
							p.sendMessage(gui.draw()).queue(mmm -> {
								for(GuiButton gb: gui.buttons) {
									boolean gen = gb.icon.getIsGeneric();
									if(gen) mmm.addReaction(gb.icon.getGenericEmoji()).queue();
									else {
										long emoteId = gb.icon.getGuildEmojiId();
										Emote e = EmojiCache.getEmoteById(emoteId);
										mmm.addReaction(e).queue();
									}
								}
								this.messageId = mmm.getIdLong();
								hasSentMessage = true;
							});
						});
					}
				});
			} else {
				// copy of BonziUtils::messageUser
				if(user.hasPrivateChannel() && BonziUtils.userPrivateChannels.containsKey(userDmId)) {
					long cId = BonziUtils.userPrivateChannels.get(userDmId);
					PrivateChannel pc = user.getJDA().getPrivateChannelById(cId);
					pc.sendMessage(gui.draw()).queue(mmm -> {
						for(GuiButton gb: gui.buttons) {
							boolean gen = gb.icon.getIsGeneric();
							if(gen) mmm.addReaction(gb.icon.getGenericEmoji()).queue();
							else {
								long emoteId = gb.icon.getGuildEmojiId();
								Emote e = EmojiCache.getEmoteById(emoteId);
								mmm.addReaction(e).queue();
							}
						}
						this.messageId = mmm.getIdLong();
						hasSentMessage = true;
					});
				} else {
					user.openPrivateChannel().queue(p -> {
						long privateChannelId = p.getIdLong();
						BonziUtils.userPrivateChannels.put(userDmId, privateChannelId);
						p.sendMessage(gui.draw()).queue(mmm -> {
							for(GuiButton gb: gui.buttons) {
								boolean gen = gb.icon.getIsGeneric();
								if(gen) mmm.addReaction(gb.icon.getGenericEmoji()).queue();
								else {
									long emoteId = gb.icon.getGuildEmojiId();
									Emote e = EmojiCache.getEmoteById(emoteId);
									mmm.addReaction(e).queue();
								}
							}
							this.messageId = mmm.getIdLong();
							hasSentMessage = true;
						});
					});
				}
			}
		}
	}
	public void redrawMessage(JDA jda) {
		if(!hasSentMessage) return;
		if(messageId == -1) return;
		
		if(isGuild) {
			Guild g = jda.getGuildById(guildId);
			TextChannel tc = g.getTextChannelById(messageId);
			MessageChannel mc = (MessageChannel)tc;
			mc.editMessageById(messageId, gui.draw()).queue();
		} else {
			User sender = jda.getUserById(userDmId);
			if(sender == null) {
				jda.retrieveUserById(userDmId).queue(u -> {
					BonziUtils.messageUser(u, gui.draw());
				});
			} else {
				BonziUtils.messageUser(sender, gui.draw());
			}
		}
	}
	
	public void onReaction(ReactionEmote emote) {
		gui.receiveReaction(emote);
	}
}