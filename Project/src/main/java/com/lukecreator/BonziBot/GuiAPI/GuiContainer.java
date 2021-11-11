package com.lukecreator.BonziBot.GuiAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

/**
 * Base Gui class. Represents a message containing a Gui.
 */
public class GuiContainer {
	
	private boolean enabled = true;
	
	public long ownerId;
	public List<Long> ownerWhitelist = new ArrayList<Long>();
	
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
	
	public GuiContainer(Gui gui, TextChannel tc, User u) {
		this.gui = gui;
		this.gui.parent = this;
		this.isGuild = true;
		this.guildId = tc.getGuild().getIdLong();
		this.channelId = tc.getIdLong();
		this.ownerId = u.getIdLong();
	}
	public GuiContainer(Gui gui, GuildChannel gc, User u) {
		this.gui = gui;
		this.gui.parent = this;
		this.isGuild = true;
		this.guildId = gc.getGuild().getIdLong();
		this.channelId = gc.getIdLong();
		this.ownerId = u.getIdLong();
	}
	public GuiContainer(Gui gui, PrivateChannel pc) {
		this.gui = gui;
		this.gui.parent = this;
		this.isDm = true;
		this.userDmId = pc.getUser().getIdLong();
		this.ownerId = pc.getUser().getIdLong();
	}
	private MessageAction components(MessageAction in) {
		return BonziUtils.appendComponents(in, this.gui);
	}
	
	/**
	 * This is BY FAR the most PAINFUL function
	 *  I will ever write in BonziBot I swear
	 */
	public void sendMessage(JDA jda, long id, BonziBot bot, AllocGuiList agl) {
		
		Object drawn = gui.draw(jda);
		
		Consumer<? super Message> success = mmm -> {
			/*for(GuiButton gb: gui.buttons) {
				gb.icon.react(mmm);					Legacy; This is handled in the send method now.
			}*/
			this.setMessage(mmm.getIdLong());
			agl.addNew(this);
			if(isGuild)
				bot.guis.guildGuis.put(id, agl);
			else bot.guis.userGuis.put(id, agl);
		};
		Consumer<? super Message> fileSuccess = mmm -> {
			((File)drawn).delete();
			/*for(GuiButton gb: gui.buttons) {
				gb.icon.react(mmm);					Legacy; This is handled in the send method now.
			}*/
			this.setMessage(mmm.getIdLong());
			agl.addNew(this);
			if(isGuild)
				bot.guis.guildGuis.put(id, agl);
			else bot.guis.userGuis.put(id, agl);
		};
		
		if(isGuild) {
			Guild g = jda.getGuildById(guildId);
			TextChannel tc = g.getTextChannelById(channelId);
			if(drawn instanceof MessageEmbed)
				components(tc.sendMessageEmbeds((MessageEmbed)drawn)).queue(success);
			if(drawn instanceof EmbedBuilder)
				components(tc.sendMessageEmbeds(((EmbedBuilder)drawn).build())).queue(success);
			if(drawn instanceof String)
				components(tc.sendMessage((String)drawn)).queue(success);
			if(drawn instanceof File)
				components(tc.sendFile((File)drawn)).queue(fileSuccess);
		} else {
			
			User user = jda.getUserById(userDmId);
			if(user == null) {
				// copy of BonziUtils::messageUser
				jda.retrieveUserById(userDmId).queue(u -> {
					if(u.hasPrivateChannel() && BonziUtils.userPrivateChannels.containsKey(userDmId)) {
						long cId = BonziUtils.userPrivateChannels.get(userDmId);
						PrivateChannel pc = u.getJDA().getPrivateChannelById(cId);
						if(drawn instanceof MessageEmbed)
							components(pc.sendMessageEmbeds((MessageEmbed)drawn)).queue(success);
						if(drawn instanceof EmbedBuilder)
							components(pc.sendMessageEmbeds(((EmbedBuilder)drawn).build())).queue(success);
						if(drawn instanceof String)
							components(pc.sendMessage((String)drawn)).queue(success);
						if(drawn instanceof File)
							components(pc.sendFile((File)drawn)).queue(fileSuccess);
					} else {
						u.openPrivateChannel().queue(p -> {
							long privateChannelId = p.getIdLong();
							BonziUtils.userPrivateChannels.put(userDmId, privateChannelId);
							if(drawn instanceof MessageEmbed)
								components(p.sendMessageEmbeds((MessageEmbed)drawn)).queue(success);
							if(drawn instanceof EmbedBuilder)
								components(p.sendMessageEmbeds(((EmbedBuilder)drawn).build())).queue(success);
							if(drawn instanceof String)
								components(p.sendMessage((String)drawn)).queue(success);
							if(drawn instanceof File)
								components(p.sendFile((File)drawn)).queue(fileSuccess);
						});
					}
				});
			} else {
				// copy of BonziUtils.messageUser
				if(user.hasPrivateChannel() && BonziUtils.userPrivateChannels.containsKey(userDmId)) {
					long cId = BonziUtils.userPrivateChannels.get(userDmId);
					PrivateChannel pc = user.getJDA().getPrivateChannelById(cId);
					if(drawn instanceof MessageEmbed)
						components(pc.sendMessageEmbeds((MessageEmbed)drawn)).queue(success);
					if(drawn instanceof EmbedBuilder)
						components(pc.sendMessageEmbeds(((EmbedBuilder)drawn).build())).queue(success);
					if(drawn instanceof String)
						components(pc.sendMessage((String)drawn)).queue(success);
					if(drawn instanceof File)
						components(pc.sendFile((File)drawn)).queue(fileSuccess);
				} else {
					user.openPrivateChannel().queue(p -> {
						long privateChannelId = p.getIdLong();
						BonziUtils.userPrivateChannels.put(userDmId, privateChannelId);
						if(drawn instanceof MessageEmbed)
							components(p.sendMessageEmbeds((MessageEmbed)drawn)).queue(success);
						if(drawn instanceof EmbedBuilder)
							components(p.sendMessageEmbeds(((EmbedBuilder)drawn).build())).queue(success);
						if(drawn instanceof String)
							components(p.sendMessage((String)drawn)).queue(success);
						if(drawn instanceof File)
							components(p.sendFile((File)drawn)).queue(fileSuccess);
					});
				}
			}
		}
	}
	public void redrawMessage(JDA jda) {
		if(!hasSentMessage) return;
		if(messageId == -1) return;
		
		Object drawn = gui.draw(jda);
		
		if(isGuild) {
			Guild g = jda.getGuildById(guildId);
			TextChannel tc = g.getTextChannelById(channelId);
			if(drawn instanceof MessageEmbed)
				components(tc.editMessageEmbedsById(messageId, (MessageEmbed)drawn)).queue();
			if(drawn instanceof EmbedBuilder)
				components(tc.editMessageEmbedsById(messageId, ((EmbedBuilder)drawn).build())).queue();
			if(drawn instanceof String)
				components(tc.editMessageById(messageId, (String)drawn)).queue();
		} else {
			User user = jda.getUserById(userDmId);
			if(user == null) {
				jda.retrieveUserById(userDmId).queue(u -> {
					PrivateChannel pc = BonziUtils.getCachedPrivateChannel(u);
					if(pc == null) {
						u.openPrivateChannel().queue(p -> {
							if(drawn instanceof MessageEmbed)
								components(p.editMessageEmbedsById(messageId, (MessageEmbed)drawn)).queue();
							if(drawn instanceof EmbedBuilder)
								components(p.editMessageEmbedsById(messageId, ((EmbedBuilder)drawn).build())).queue();
							if(drawn instanceof String)
								components(p.editMessageById(messageId, (String)drawn)).queue();
							BonziUtils.userPrivateChannels.put(u.getIdLong(), p.getIdLong());
						});
					} else {
						if(drawn instanceof MessageEmbed)
							components(pc.editMessageEmbedsById(messageId, (MessageEmbed)drawn)).queue();
						if(drawn instanceof EmbedBuilder)
							components(pc.editMessageEmbedsById(messageId, ((EmbedBuilder)drawn).build())).queue();
						if(drawn instanceof String)
							components(pc.editMessageById(messageId, (String)drawn)).queue();
					}
				});
			} else {
				// User exists.
				PrivateChannel pc = BonziUtils.getCachedPrivateChannel(user);
				if(pc == null) {
					user.openPrivateChannel().queue(p -> {
						if(drawn instanceof MessageEmbed)
							components(p.editMessageEmbedsById(messageId, (MessageEmbed)drawn)).queue();
						if(drawn instanceof EmbedBuilder)
							components(p.editMessageEmbedsById(messageId, ((EmbedBuilder)drawn).build())).queue();
						if(drawn instanceof String)
							components(p.editMessageById(messageId, (String)drawn)).queue();
						BonziUtils.userPrivateChannels.put(user.getIdLong(), p.getIdLong());
					});
				} else {
					if(drawn instanceof MessageEmbed)
						components(pc.editMessageEmbedsById(messageId, (MessageEmbed)drawn)).queue();
					if(drawn instanceof EmbedBuilder)
						components(pc.editMessageEmbedsById(messageId, ((EmbedBuilder)drawn).build())).queue();
					if(drawn instanceof String)
						components(pc.editMessageById(messageId, (String)drawn)).queue();
				}
			}
		}
	}
	/**
	 * im sorry for cursing your eyes
	 */
	/*public void resetAllReactions(JDA jda) {
		if(!hasSentMessage) return;
		if(messageId == -1) return;
		
		if(isGuild) {
			Guild g = jda.getGuildById(guildId);
			TextChannel tc = g.getTextChannelById(channelId);
			tc.retrieveMessageById(messageId).queue(m -> {
				m.clearReactions().queue(v -> {
					for(GuiButton gb: gui.buttons) {
						gb.icon.react(m);
					}
				});
			});
		} else {
			User sender = jda.getUserById(userDmId);
			if(sender == null) {
				jda.retrieveUserById(userDmId).queue(u -> {
					if(u.hasPrivateChannel() && BonziUtils.userPrivateChannels.containsKey(userDmId)) {
						long cId = BonziUtils.userPrivateChannels.get(userDmId);
						PrivateChannel pc = u.getJDA().getPrivateChannelById(cId);
						pc.retrieveMessageById(messageId).queue(m -> {
							for(MessageReaction r: m.getReactions())
								if(r.isSelf()) r.removeReaction().queue();
							for(GuiButton gb: gui.buttons)
								gb.icon.react(m);
						});
					} else {
						u.openPrivateChannel().queue(p -> {
							long privateChannelId = p.getIdLong();
							BonziUtils.userPrivateChannels.put(userDmId, privateChannelId);
							p.retrieveMessageById(messageId).queue(m -> {
								for(MessageReaction r: m.getReactions())
									if(r.isSelf()) r.removeReaction().queue();
								for(GuiButton gb: gui.buttons)
									gb.icon.react(m);
							});
						});
					}
				});
			} else {
				if(sender.hasPrivateChannel() && BonziUtils.userPrivateChannels.containsKey(userDmId)) {
					long cId = BonziUtils.userPrivateChannels.get(userDmId);
					PrivateChannel pc = sender.getJDA().getPrivateChannelById(cId);
					pc.retrieveMessageById(messageId).queue(m -> {
						for(MessageReaction r: m.getReactions())
							if(r.isSelf()) r.removeReaction().queue();
						for(GuiButton gb: gui.buttons)
							gb.icon.react(m);
					});
				} else {
					sender.openPrivateChannel().queue(p -> {
						long privateChannelId = p.getIdLong();
						BonziUtils.userPrivateChannels.put(userDmId, privateChannelId);
						p.retrieveMessageById(messageId).queue(m -> {
							for(MessageReaction r: m.getReactions())
								if(r.isSelf()) r.removeReaction().queue();
							for(GuiButton gb: gui.buttons)
								gb.icon.react(m);
						});
					});
				}
			}
		}
	}
	public void removeAllReactions(JDA jda) {
		if(!hasSentMessage) return;
		if(messageId == -1) return;
		
		if(isGuild) {
			Guild g = jda.getGuildById(guildId);
			TextChannel tc = g.getTextChannelById(channelId);
			tc.retrieveMessageById(messageId).queue(m -> {
				m.clearReactions().queue();
			});
		} else {
			User sender = jda.getUserById(userDmId);
			if(sender == null) {
				jda.retrieveUserById(userDmId).queue(u -> {
					if(u.hasPrivateChannel() && BonziUtils.userPrivateChannels.containsKey(userDmId)) {
						long cId = BonziUtils.userPrivateChannels.get(userDmId);
						PrivateChannel pc = u.getJDA().getPrivateChannelById(cId);
						pc.retrieveMessageById(messageId).queue(m -> {
							for(MessageReaction r: m.getReactions())
								if(r.isSelf()) r.removeReaction().queue();
						});
					} else {
						u.openPrivateChannel().queue(p -> {
							long privateChannelId = p.getIdLong();
							BonziUtils.userPrivateChannels.put(userDmId, privateChannelId);
							p.retrieveMessageById(messageId).queue(m -> {
								for(MessageReaction r: m.getReactions())
									if(r.isSelf()) r.removeReaction().queue();
							});
						});
					}
				});
			} else {
				if(sender.hasPrivateChannel() && BonziUtils.userPrivateChannels.containsKey(userDmId)) {
					long cId = BonziUtils.userPrivateChannels.get(userDmId);
					PrivateChannel pc = sender.getJDA().getPrivateChannelById(cId);
					pc.retrieveMessageById(messageId).queue(m -> {
						for(MessageReaction r: m.getReactions())
							if(r.isSelf()) r.removeReaction().queue();
					});
				} else {
					sender.openPrivateChannel().queue(p -> {
						long privateChannelId = p.getIdLong();
						BonziUtils.userPrivateChannels.put(userDmId, privateChannelId);
						p.retrieveMessageById(messageId).queue(m -> {
							for(MessageReaction r: m.getReactions())
								if(r.isSelf()) r.removeReaction().queue();
						});
					});
				}
			}
		}
	}*/
	
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
		return cached; // Rarely could be null.
	}
	public void retrieveMessage(JDA jda, Consumer<Message> receive) {
		if(this.isGuild) {
			Guild g = jda.getGuildById(guildId);
			g.getTextChannelById(channelId).retrieveMessageById(messageId).queue(receive);
			return;
		}
		
		User u = jda.getUserById(userDmId);
		PrivateChannel cached = BonziUtils
			.getCachedPrivateChannel(u);
		cached.retrieveMessageById(messageId).queue(receive);
	}
	/**
	 * Delete this GUI from Discord.
	 * @param jda
	 */
	public void delete(JDA jda) {
		enabled = false;
		retrieveMessage(jda, msg -> {
			msg.delete().queue();
		});
	}
	public void disable(JDA jda) {
		//resetAllReactions(jda);
		enabled = false;
	}
	public boolean getEnabled() {
		return this.enabled;
	}
	
	/**
	 * Pretty heavy method, sends a poop
	 *   ton of requests so use wisely!
	 */
	public void setActiveGui(Gui gui, JDA jda) {
		if(!gui.wasInitialized()) {
			gui.parent = this;
			if(gui.elements == null)
				gui.elements = new ArrayList<GuiElement>();
			if(isGuild) {
				TextChannel tc = jda
					.getGuildById(guildId)
					.getTextChannelById(channelId);
				gui.hiddenInit(jda, tc.getGuild(), this.gui.bonziReference);
			} else {
				User u = jda.getUserById(this.userDmId);
				gui.hiddenInit(jda, u, this.gui.bonziReference);
			}
		}
		this.gui = gui;
		redrawMessage(jda);
		//resetAllReactions(jda);
	}
	/*
	public void onReaction(ReactionEmote emote, User executor) {
		if(!this.enabled)
			return;
		if(this.ownerId != executor.getIdLong())
			return;
		gui.receiveReaction(emote);
	}*/
	public void onAction(ButtonClickEvent event) {
		if(!this.enabled) {
			event.reply(":x: `This GUI has been disabled.`").setEphemeral(true).queue();
			return;
		}
		
		nevermind:
		if(this.ownerId != event.getUser().getIdLong()) {
			// Check whitelist
			for(Long wl: this.ownerWhitelist)
				if(event.getUser().getIdLong() == wl.longValue())
					break nevermind;
			event.reply(":x: `This GUI was opened by someone else.`").setEphemeral(true).queue();
			return;
		}
		
		event.deferEdit().queue();
		this.gui.receiveActionButton(event.getComponentId(), event.getUser().getIdLong(), event.getJDA());
	}
	public void onAction(SelectionMenuEvent event) {
		if(!this.enabled) {
			event.reply(":x: `This GUI has been disabled.`").setEphemeral(true).queue();
			return;
		}
		
		nevermind:
		if(this.ownerId != event.getUser().getIdLong()) {
			// Check whitelist
			for(Long wl: this.ownerWhitelist)
				if(event.getUser().getIdLong() == wl.longValue())
					break nevermind;
			event.reply(":x: `This GUI was opened by someone else.`").setEphemeral(true).queue();
			return;
		}
		event.deferEdit().queue();
		this.gui.receiveActionSelect(event.getComponentId(), event.getSelectedOptions(), event.getUser().getIdLong(), event.getJDA());
	}
}