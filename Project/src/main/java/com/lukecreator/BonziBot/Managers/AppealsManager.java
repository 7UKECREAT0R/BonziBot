package com.lukecreator.BonziBot.Managers;

import java.awt.Color;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.BanAppeal;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.IStorableData;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ComponentLayout;

/**
 * Manages which users have the ability to appeal a ban, referred to internally as mercy.
 * This also holds data on existing appeals waiting to be accepted/denied. Any garbage made
 * shouldn't be an issue for years to come as the data structures are quite small.
 * @author Lukec
 *
 */
public class AppealsManager implements IStorableData {
	
	// Users given a chance to appeal.
	HashMap<Long, List<Long>> mercy = new HashMap<Long, List<Long>>();
	HashMap<Long, List<BanAppeal>> appeals = new HashMap<Long, List<BanAppeal>>();
	
	public void processButtonEvent(String[] parts, BonziBot bb, ButtonClickEvent event) {
		if(parts[1].equals("request")) {
			final long gid = Long.parseLong(parts[2]);
			final long uid = event.getUser().getIdLong();
			
			if(!this.hasMercy(gid, uid)) {
				event.reply("You have already appealed.").setEphemeral(true).queue();
				return;
			}
			
			event.replyEmbeds(BonziUtils.quickEmbed("Appeal",
				"Send a message containing your ban appeal.").build()).queue();
			bb.eventWaiter.waitForResponse(uid, appealMessage -> {
				String content = appealMessage.getContentRaw();
				MessageChannel channel = appealMessage.getChannel();
				List<Attachment> attachments = appealMessage.getAttachments();
				
				for(Attachment file: attachments)
					content += "\n" + file.getUrl();
				
				Guild guild = appealMessage.getJDA().getGuildById(gid);
				GuildSettings settings = bb.guildSettings.getSettings(guild);
				if(settings == null)
					return; // what the hell
				
				if(!settings.banAppeals) {
					channel.sendMessageEmbeds(BonziUtils.failureEmbed("Ban appeals have been disabled. Sorry.")).queue();
					return;
				}
				
				long appealsChannelId = settings.banAppealsChannel;
				TextChannel appealsChannel = guild.getTextChannelById(appealsChannelId);
				if(appealsChannel == null) {
					channel.sendMessageEmbeds(BonziUtils.failureEmbed("Ban appeals have been disabled. Sorry.")).queue();
					return;
				}
				
				BanAppeal appeal = new BanAppeal(appealMessage.getAuthor(), content);
				appeal.sendMessage(appealsChannel, success -> {
					this.noMercy(gid, uid);
					this.addAppeal(gid, appeal);
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor(Color.pink);
					switch(BonziUtils.randomInt(7)) {
					case 0:
						eb.setTitle("Success!");
						eb.setDescription("Your appeal has been beamed up to HQ.");
						break;
					case 1:
						eb.setTitle("Completed!");
						eb.setDescription("Your appeal has been received by the staff team.");
						break;
					case 2:
						eb.setTitle("Appeal Sent!");
						eb.setDescription("The staff team are mid-debate right now. Good luck?");
						break;
					case 3:
						eb.setTitle("Success!");
						eb.setDescription("Your appeal has been received and is being considered by the greatest staff team on the planet *as we speak*.");
						break;
					case 4:
						eb.setTitle("Request Received!");
						eb.setDescription("The staff team is on lunch break right now, they'll get to you in a minute.");
						break;
					case 5:
						eb.setTitle("Unban Awaiting!");
						eb.setDescription("Or maybe not. We'll see.");
						break;
					case 6:
						eb.setTitle("Appeal Delivered!");
						eb.setDescription("via the fanciest pneumatic tubing you've ever seen.");
						break;
					}
					channel.sendMessageEmbeds(eb.build()).queue();
				}, failure -> {
					channel.sendMessageEmbeds(BonziUtils.failureEmbed("Something went wrong when processing your appeal.", failure.toString())).queue();
					return;
				});
			});
		}
		if(parts[1].equals("accept")) {
			final long bannedUser = Long.parseLong(parts[2]);
			Guild guild = event.getGuild();
			Member mod = event.getMember();
			
			if(!mod.hasPermission(Permission.BAN_MEMBERS)) {
				event.replyEmbeds(BonziUtils.failureEmbed("Insufficient permission.", "You must be able to manage bans to do this!")).setEphemeral(true).queue();
				return;
			}
			
			BanAppeal appeal = this.getAppeal(guild, bannedUser);
			String tag = mod.getUser().getAsTag();
			
			if(appeal == null) {
				event.replyEmbeds(BonziUtils.failureEmbed("Appeal has already been responded to.")).setEphemeral(true).queue();
				return;
			}
			
			guild.unban(String.valueOf(bannedUser)).reason
				("Appeal Accepted by Moderator " + mod.getUser().getAsTag()).queue();
			
			event.replyEmbeds(BonziUtils.successEmbed("Appeal Accepted", appeal.username + "'s appeal has been accepted by " + tag +
				"\nThis user will not be notified that their appeal was accepted. Reach out to them if you wish to invite them back.")).queue();
			event.getHook().editOriginalComponents(new ArrayList<ComponentLayout>()).queue();
			this.removeAppeal(guild, bannedUser);
			return;
		}
		if(parts[1].equals("reject")) {
			final long bannedUser = Long.parseLong(parts[2]);
			Guild guild = event.getGuild();
			Member mod = event.getMember();
			
			if(!mod.hasPermission(Permission.BAN_MEMBERS)) {
				event.replyEmbeds(BonziUtils.failureEmbed("You don't have permission to do that.")).setEphemeral(true).queue();
				return;
			}
			
			BanAppeal appeal = this.getAppeal(guild, bannedUser);
			String tag = mod.getUser().getAsTag();
			
			if(appeal == null) {
				event.replyEmbeds(BonziUtils.failureEmbed("Appeal has already been responded to.")).setEphemeral(true).queue();
				return;
			}
			
			event.replyEmbeds(BonziUtils.failureEmbed("Appeal Denied", appeal.username + "'s appeal has been denied by " + tag +
				"\nThis user will not be notified that their appeal was accepted. Reach out to them if you wish to invite them back.")).queue();
			event.getHook().editOriginalComponents(new ArrayList<ComponentLayout>()).queue();
			this.removeAppeal(guild, bannedUser);
			return;
		}
	}
	
	public boolean hasMercy(Guild guild, User user) {
		return this.hasMercy(guild.getIdLong(), user.getIdLong());
	}
	public boolean hasMercy(long guild, long user) {
		List<Long> m = mercy.get(guild);
		if(m == null)
			return false;
		for(long test: m) {
			if(user == test)
				return true;
		}
		return false;
	}
	public List<Long> getMercy(Guild guild) {
		return this.getMercy(guild.getIdLong());
	}
	public List<Long> getMercy(long guild) {
		return mercy.get(guild);
	}
	public void mercy(Guild guild, User user) {
		this.mercy(guild.getIdLong(), user.getIdLong());
	}
	public void mercy(long guild, long user) {
		List<Long> m = mercy.get(guild);
		if(m == null)
			m = new ArrayList<Long>();
		m.add(user);
		mercy.put(guild, m);
	}
	public void noMercy(Guild guild, User user) {
		this.noMercy(guild.getIdLong(), user.getIdLong());
	}
	public void noMercy(long guild, long user) {
		List<Long> m = mercy.get(guild);
		if(m == null)
			return;
		for(int i = 0; i < m.size(); i++) {
			long test = m.get(i);
			if(user == test) {
				m.remove(i);
				break;
			}
		}
		mercy.put(guild, m);
	}
	
	public void addAppeal(Guild guild, BanAppeal appeal) {
		this.addAppeal(guild.getIdLong(), appeal);
	}
	public void addAppeal(long guild, BanAppeal appeal) {
		List<BanAppeal> existing = appeals.get(guild);
		if(existing == null)
			existing = new ArrayList<BanAppeal>();
		
		existing.add(appeal);
		appeals.put(guild, existing);
	}
	public BanAppeal getAppeal(Guild guild, long bannedUser) {
		return this.getAppeal(guild.getIdLong(), bannedUser);
	}
	public BanAppeal getAppeal(long guild, long bannedUser) {
		if(appeals.containsKey(guild)) {
			List<BanAppeal> existing = appeals.get(guild);
			for(int i = 0; i < existing.size(); i++) {
				BanAppeal test = existing.get(i);
				if(test.userId == bannedUser)
					return test;
			}
		}
		return null;
	}
	public BanAppeal removeAppeal(Guild guild, long bannedUser) {
		return this.removeAppeal(guild.getIdLong(), bannedUser);
	}
	public BanAppeal removeAppeal(long guild, long bannedUser) {
		if(appeals.containsKey(guild)) {
			List<BanAppeal> existing = appeals.get(guild);
			for(int i = 0; i < existing.size(); i++)
				if(existing.get(i).userId == bannedUser)
					return existing.remove(i);
		}
		return null;
	}
	
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(mercy, "mercy");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject("mercy");
		if(o == null)
			return;
		
		mercy = (HashMap<Long, List<Long>>)o;
	}
}
