package com.lukecreator.BonziBot.GuiAPI;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
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
	private void setMessage(long id) {
		hasSentMessage = true;
		messageId = id;
	}
	
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
	public GuiContainer(Gui gui, GuildChannel gc) {
		this.gui = gui;
		this.gui.parent = this;
		this.isGuild = true;
		this.guildId = gc.getGuild().getIdLong();
		this.channelId = gc.getIdLong();
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
	public void sendMessage(JDA jda, long id, BonziBot bot, AllocGuiList agl) {
		
		if(isGuild) {
			Guild g = jda.getGuildById(guildId);
			TextChannel tc = g.getTextChannelById(channelId);
			tc.sendMessage(gui.draw()).queue(mmm -> {
				for(GuiButton gb: gui.buttons) {
					gb.icon.react(mmm);
				}
				this.setMessage(mmm.getIdLong());
				agl.addNew(this);
				if(isGuild)
					bot.guis.guildGuis.put(id, agl);
				else bot.guis.userGuis.put(id, agl);
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
								gb.icon.react(mmm);
							}
							this.setMessage(mmm.getIdLong());
							agl.addNew(this);
							if(isGuild)
								bot.guis.guildGuis.put(id, agl);
							else bot.guis.userGuis.put(id, agl);
						});
					} else {
						u.openPrivateChannel().queue(p -> {
							long privateChannelId = p.getIdLong();
							BonziUtils.userPrivateChannels.put(userDmId, privateChannelId);
							p.sendMessage(gui.draw()).queue(mmm -> {
								for(GuiButton gb: gui.buttons) {
									gb.icon.react(mmm);
								}
								this.setMessage(mmm.getIdLong());
								agl.addNew(this);
								if(isGuild)
									bot.guis.guildGuis.put(id, agl);
								else bot.guis.userGuis.put(id, agl);
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
							gb.icon.react(mmm);
						}
						this.setMessage(mmm.getIdLong());
						agl.addNew(this);
						if(isGuild)
							bot.guis.guildGuis.put(id, agl);
						else bot.guis.userGuis.put(id, agl);
					});
				} else {
					user.openPrivateChannel().queue(p -> {
						long privateChannelId = p.getIdLong();
						BonziUtils.userPrivateChannels.put(userDmId, privateChannelId);
						p.sendMessage(gui.draw()).queue(mmm -> {
							for(GuiButton gb: gui.buttons) {
								gb.icon.react(mmm);
							}
							this.setMessage(mmm.getIdLong());
							agl.addNew(this);
							if(isGuild)
								bot.guis.guildGuis.put(id, agl);
							else bot.guis.userGuis.put(id, agl);
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
			TextChannel tc = g.getTextChannelById(channelId);
			tc.editMessageById(messageId, gui.draw()).queue();
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
	/*
	 * im sorry
	 */
	public void resetAllReactions(JDA jda) {
		if(!hasSentMessage) return;
		if(messageId == -1) return;
		
		if(isGuild) {
			Guild g = jda.getGuildById(guildId);
			TextChannel tc = g.getTextChannelById(messageId);
			MessageChannel mc = (MessageChannel)tc;
			mc.retrieveMessageById(messageId).queue(m -> {
				m.clearReactions().queue();
				for(GuiButton gb: gui.buttons) {
					gb.icon.react(m);
				}
			});
		} else {
			User sender = jda.getUserById(userDmId);
			if(sender == null) {
				jda.retrieveUserById(userDmId).queue(u -> {
					if(u.hasPrivateChannel() && BonziUtils.userPrivateChannels.containsKey(userDmId)) {
						long cId = BonziUtils.userPrivateChannels.get(userDmId);
						PrivateChannel pc = u.getJDA().getPrivateChannelById(cId);
						pc.retrieveMessageById(messageId).queue(m -> {
							m.clearReactions().queue();
							for(GuiButton gb: gui.buttons) {
								gb.icon.react(m);
							}
						});
					} else {
						u.openPrivateChannel().queue(p -> {
							long privateChannelId = p.getIdLong();
							BonziUtils.userPrivateChannels.put(userDmId, privateChannelId);
							p.retrieveMessageById(messageId).queue(m -> {
								m.clearReactions().queue();
								for(GuiButton gb: gui.buttons) {
									gb.icon.react(m);
								}
							});
						});
					}
				});
			} else {
				if(sender.hasPrivateChannel() && BonziUtils.userPrivateChannels.containsKey(userDmId)) {
					long cId = BonziUtils.userPrivateChannels.get(userDmId);
					PrivateChannel pc = sender.getJDA().getPrivateChannelById(cId);
					pc.retrieveMessageById(messageId).queue(m -> {
						m.clearReactions().queue();
						for(GuiButton gb: gui.buttons) {
							gb.icon.react(m);
						}
					});
				} else {
					sender.openPrivateChannel().queue(p -> {
						long privateChannelId = p.getIdLong();
						BonziUtils.userPrivateChannels.put(userDmId, privateChannelId);
						p.retrieveMessageById(messageId).queue(m -> {
							m.clearReactions().queue();
							for(GuiButton gb: gui.buttons) {
								gb.icon.react(m);
							}
						});
					});
				}
			}
		}
	}

	public MessageChannel getChannel(JDA jda) {
		if(this.isGuild) {
			Guild g = jda.getGuildById(guildId);
			return g.getTextChannelById(channelId);
		}
		
		// DM and message has been sent there
		//   so the channel must be cached.
		User u = jda.getUserById(userDmId);
		PrivateChannel cached = BonziUtils
			.getCachedPrivateChannel(u);
		return cached; // Potentially could be null.
	}
	
	/*
	 * Pretty heavy method, sends a poop
	 *   ton of requests so use wisely!
	 */
	public void setActiveGui(Gui gui, JDA jda) {
		this.gui = gui;
		redrawMessage(jda);
		resetAllReactions(jda);
	}
	public void onReaction(ReactionEmote emote) {
		gui.receiveReaction(emote);
	}
}