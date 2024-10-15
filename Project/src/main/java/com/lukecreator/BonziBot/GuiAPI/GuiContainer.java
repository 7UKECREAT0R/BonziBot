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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.utils.FileUpload;

/**
 * Base Gui class. Represents a message containing a Gui.
 */
public class GuiContainer {
	
	private boolean enabled = true;

	/**
	 * The ID of the owner of this GUI. If globalWhitelist is off and the interacting user is not present in
	 * ownerWhitelist, the user will be blocked from using the GUI.
	 */
	public long ownerId;
	/**
	 * If enabled, all users will be able to use this GUI with no restrictions.
	 */
	public boolean globalWhitelist = false;
	/**
	 * The list of users who are allowed to interact with this GUI, on top of the ownerId. If globalWhitelist is
	 * enabled, this list does nothing.
	 */
	public List<Long> ownerWhitelist = new ArrayList<Long>();

	/**
	 * Is this GUI in a guild?
	 */
	public boolean isGuild = false;
	/**
	 * Is this GUI in a private message?
	 */
	public boolean isDm = false;
	
	// Message Stuff
	public boolean hasSentMessage = false;
	public long messageId = -1;
	private void setMessage(long id) {
		this.hasSentMessage = true;
		this.messageId = id;
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
	private MessageCreateAction components(MessageCreateAction in) {
		return BonziUtils.appendComponents(in, this.gui);
	}
	private MessageEditAction components(MessageEditAction in) {
		return BonziUtils.appendComponents(in, this.gui);
	}
	
	/**
	 * This is BY FAR the most PAINFUL function
	 *  I will ever write in BonziBot I swear
	 */
	public void sendMessage(JDA jda, long id, BonziBot bot, AllocGuiList agl) {
		
		Object drawn = this.gui.draw(jda);
		
		Consumer<? super Message> success = mmm -> {
			/*for(GuiButton gb: gui.buttons) {
				gb.icon.react(mmm);					Legacy; This is handled in the send method now.
			}*/
			this.setMessage(mmm.getIdLong());
			agl.addNew(this);
			if(this.isGuild)
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
			if(this.isGuild)
				bot.guis.guildGuis.put(id, agl);
			else bot.guis.userGuis.put(id, agl);
		};
		
		if(this.isGuild) {
			Guild g = jda.getGuildById(this.guildId);
			TextChannel tc = g.getTextChannelById(this.channelId);
			if(drawn instanceof MessageEmbed)
				this.components(tc.sendMessageEmbeds((MessageEmbed)drawn)).queue(success);
			if(drawn instanceof EmbedBuilder)
				this.components(tc.sendMessageEmbeds(((EmbedBuilder)drawn).build())).queue(success);
			if(drawn instanceof String)
				this.components(tc.sendMessage((String)drawn)).queue(success);
			if(drawn instanceof File) {
				File drawnFile = (File)drawn;
				FileUpload upload = FileUpload.fromData(drawnFile,
					drawnFile.toPath().getFileName().toString());
				this.components(tc.sendFiles(upload)).queue(fileSuccess);
			}
		} else {
			
			User user = jda.getUserById(this.userDmId);
			if(user == null) {
				// copy of BonziUtils::messageUser
				jda.retrieveUserById(this.userDmId).queue(u -> {
					if(u.hasPrivateChannel() && BonziUtils.userPrivateChannels.containsKey(this.userDmId)) {
						long cId = BonziUtils.userPrivateChannels.get(this.userDmId);
						PrivateChannel pc = u.getJDA().getPrivateChannelById(cId);
						if(drawn instanceof MessageEmbed)
							this.components(pc.sendMessageEmbeds((MessageEmbed)drawn)).queue(success);
						if(drawn instanceof EmbedBuilder)
							this.components(pc.sendMessageEmbeds(((EmbedBuilder)drawn).build())).queue(success);
						if(drawn instanceof String)
							this.components(pc.sendMessage((String)drawn)).queue(success);
						if(drawn instanceof File) {
							File drawnFile = (File)drawn;
							FileUpload upload = FileUpload.fromData(drawnFile,
								drawnFile.toPath().getFileName().toString());
							this.components(pc.sendFiles(upload)).queue(fileSuccess);
						}
					} else {
						u.openPrivateChannel().queue(p -> {
							long privateChannelId = p.getIdLong();
							BonziUtils.userPrivateChannels.put(this.userDmId, privateChannelId);
							if(drawn instanceof MessageEmbed)
								this.components(p.sendMessageEmbeds((MessageEmbed)drawn)).queue(success);
							if(drawn instanceof EmbedBuilder)
								this.components(p.sendMessageEmbeds(((EmbedBuilder)drawn).build())).queue(success);
							if(drawn instanceof String)
								this.components(p.sendMessage((String)drawn)).queue(success);
							if(drawn instanceof File) {
								File drawnFile = (File)drawn;
								FileUpload upload = FileUpload.fromData(drawnFile,
									drawnFile.toPath().getFileName().toString());
								this.components(p.sendFiles(upload)).queue(fileSuccess);
							}
						});
					}
				});
			} else {
				// copy of BonziUtils.messageUser
				if(user.hasPrivateChannel() && BonziUtils.userPrivateChannels.containsKey(this.userDmId)) {
					long cId = BonziUtils.userPrivateChannels.get(this.userDmId);
					PrivateChannel pc = user.getJDA().getPrivateChannelById(cId);
					if(drawn instanceof MessageEmbed)
						this.components(pc.sendMessageEmbeds((MessageEmbed)drawn)).queue(success);
					if(drawn instanceof EmbedBuilder)
						this.components(pc.sendMessageEmbeds(((EmbedBuilder)drawn).build())).queue(success);
					if(drawn instanceof String)
						this.components(pc.sendMessage((String)drawn)).queue(success);
					if(drawn instanceof File) {
						File drawnFile = (File)drawn;
						FileUpload upload = FileUpload.fromData(drawnFile,
							drawnFile.toPath().getFileName().toString());
						this.components(pc.sendFiles(upload)).queue(fileSuccess);
					}
				} else {
					user.openPrivateChannel().queue(p -> {
						long privateChannelId = p.getIdLong();
						BonziUtils.userPrivateChannels.put(this.userDmId, privateChannelId);
						if(drawn instanceof MessageEmbed)
							this.components(p.sendMessageEmbeds((MessageEmbed)drawn)).queue(success);
						if(drawn instanceof EmbedBuilder)
							this.components(p.sendMessageEmbeds(((EmbedBuilder)drawn).build())).queue(success);
						if(drawn instanceof String)
							this.components(p.sendMessage((String)drawn)).queue(success);
						if(drawn instanceof File) {
							File drawnFile = (File)drawn;
							FileUpload upload = FileUpload.fromData(drawnFile,
								drawnFile.toPath().getFileName().toString());
							this.components(p.sendFiles(upload)).queue(fileSuccess);
						}
					});
				}
			}
		}
	}
	public void redrawMessage(JDA jda) {
		if(!this.hasSentMessage) return;
		if(this.messageId == -1) return;
		
		Object drawn = this.gui.draw(jda);
		
		if(this.isGuild) {
			Guild g = jda.getGuildById(this.guildId);
			TextChannel tc = g.getTextChannelById(this.channelId);
			if(drawn instanceof MessageEmbed)
				this.components(tc.editMessageEmbedsById(this.messageId, (MessageEmbed)drawn)).queue();
			if(drawn instanceof EmbedBuilder)
				this.components(tc.editMessageEmbedsById(this.messageId, ((EmbedBuilder)drawn).build())).queue();
			if(drawn instanceof String)
				this.components(tc.editMessageById(this.messageId, (String)drawn)).queue();
		} else {
			User user = jda.getUserById(this.userDmId);
			if(user == null) {
				jda.retrieveUserById(this.userDmId).queue(u -> {
					PrivateChannel pc = BonziUtils.getCachedPrivateChannel(u);
					if(pc == null) {
						u.openPrivateChannel().queue(p -> {
							if(drawn instanceof MessageEmbed)
								this.components(p.editMessageEmbedsById(this.messageId, (MessageEmbed)drawn)).queue();
							if(drawn instanceof EmbedBuilder)
								this.components(p.editMessageEmbedsById(this.messageId, ((EmbedBuilder)drawn).build())).queue();
							if(drawn instanceof String)
								this.components(p.editMessageById(this.messageId, (String)drawn)).queue();
							BonziUtils.userPrivateChannels.put(u.getIdLong(), p.getIdLong());
						});
					} else {
						if(drawn instanceof MessageEmbed)
							this.components(pc.editMessageEmbedsById(this.messageId, (MessageEmbed)drawn)).queue();
						if(drawn instanceof EmbedBuilder)
							this.components(pc.editMessageEmbedsById(this.messageId, ((EmbedBuilder)drawn).build())).queue();
						if(drawn instanceof String)
							this.components(pc.editMessageById(this.messageId, (String)drawn)).queue();
					}
				});
			} else {
				// User exists.
				PrivateChannel pc = BonziUtils.getCachedPrivateChannel(user);
				if(pc == null) {
					user.openPrivateChannel().queue(p -> {
						if(drawn instanceof MessageEmbed)
							this.components(p.editMessageEmbedsById(this.messageId, (MessageEmbed)drawn)).queue();
						if(drawn instanceof EmbedBuilder)
							this.components(p.editMessageEmbedsById(this.messageId, ((EmbedBuilder)drawn).build())).queue();
						if(drawn instanceof String)
							this.components(p.editMessageById(this.messageId, (String)drawn)).queue();
						BonziUtils.userPrivateChannels.put(user.getIdLong(), p.getIdLong());
					});
				} else {
					if(drawn instanceof MessageEmbed)
						this.components(pc.editMessageEmbedsById(this.messageId, (MessageEmbed)drawn)).queue();
					if(drawn instanceof EmbedBuilder)
						this.components(pc.editMessageEmbedsById(this.messageId, ((EmbedBuilder)drawn).build())).queue();
					if(drawn instanceof String)
						this.components(pc.editMessageById(this.messageId, (String)drawn)).queue();
				}
			}
		}
	}
	
	public MessageChannelUnion getChannel(JDA jda) {
		if(this.isGuild) {
			Guild g = jda.getGuildById(this.guildId);
			return (MessageChannelUnion)g.getTextChannelById(this.channelId);
		}
		
		// dm and message has been sent there so the
		// channel is 99% probably cached (i hope)
		User u = jda.getUserById(this.userDmId);
		PrivateChannel cached = BonziUtils
			.getCachedPrivateChannel(u);
		return (MessageChannelUnion)cached; // rarely could be null
	}
	public void retrieveMessage(JDA jda, Consumer<Message> receive) {
		if(this.isGuild) {
			Guild g = jda.getGuildById(this.guildId);
			g.getTextChannelById(this.channelId).retrieveMessageById(this.messageId).queue(receive);
			return;
		}
		
		User u = jda.getUserById(this.userDmId);
		PrivateChannel cached = BonziUtils
			.getCachedPrivateChannel(u);
		cached.retrieveMessageById(this.messageId).queue(receive);
	}
	/**
	 * Delete this GUI from Discord.
	 */
	public void delete(JDA jda) {
		this.enabled = false;
		this.retrieveMessage(jda, msg -> {
			msg.delete().queue();
		});
	}
	public void disable(JDA jda) {
		//resetAllReactions(jda);
		this.enabled = false;
	}
	public void disableSilent() {
		this.enabled = false;
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
			if(this.isGuild) {
				TextChannel tc = jda
					.getGuildById(this.guildId)
					.getTextChannelById(this.channelId);
				gui.hiddenInit(jda, tc.getGuild(), this.gui.bonziReference);
			} else {
				User u = jda.getUserById(this.userDmId);
				gui.hiddenInit(jda, u, this.gui.bonziReference);
			}
		}
		this.gui = gui;
		this.redrawMessage(jda);
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
	public void onAction(ButtonInteractionEvent event) {
		if(!this.enabled) {
			event.reply(":x: `This GUI has been disabled.`").setEphemeral(true).queue();
			return;
		}
		
		nevermind:
		if(this.ownerId != event.getUser().getIdLong() && !this.globalWhitelist) {
			// check whitelist
			for(Long wl: this.ownerWhitelist)
				if(event.getUser().getIdLong() == wl)
					break nevermind;
			event.reply(":x: `This GUI was opened by someone else.`").setEphemeral(true).queue();
			return;
		}
		
		event.deferEdit().queue();
		this.gui.receiveActionButton(event.getComponentId(), event.getUser().getIdLong(), event.getJDA());
	}
	
	public void onAction(StringSelectInteractionEvent event) {
		if(!this.enabled) {
			event.reply(":x: `This GUI has been disabled.`").setEphemeral(true).queue();
			return;
		}
		
		nevermind:
		if(this.ownerId != event.getUser().getIdLong() && !this.globalWhitelist) {
			// check whitelist
			for(Long wl: this.ownerWhitelist)
				if(event.getUser().getIdLong() == wl)
					break nevermind;
			event.reply(":x: `This GUI was opened by someone else.`").setEphemeral(true).queue();
			return;
		}
		
		event.deferEdit().queue();
		
		List<SelectOption> selected = event.getSelectedOptions();
		this.gui.receiveActionSelect(event.getComponentId(), selected, event.getUser().getIdLong(), event.getJDA());
	}
}