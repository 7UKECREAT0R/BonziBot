package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.requests.ErrorResponse;

/**
 * Manages GuildSettings objects.
 */
public class GuildSettingsManager implements IStorableData {
	
	public HashMap<Long, GuildSettings> settings = new HashMap<Long, GuildSettings>();
	
	public GuildSettings getSettings(Guild g) {
		if(g == null) return null;
		return getSettings(g.getIdLong());
	}
	public void setSettings(Guild g, GuildSettings gs) {
		setSettings(g.getIdLong(), gs);
	}
	public GuildSettings getSettings(long gId) {
		if(settings.containsKey(gId))
			return settings.get(gId);
		GuildSettings gs = new GuildSettings();
		settings.put(gId, gs);
		return gs;
	}
	public void setSettings(long gId, GuildSettings gs) {
		settings.put(gId, gs);
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(settings, "guildSettings");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject("guildSettings");
		if(o == null) return;
		settings = (HashMap<Long, GuildSettings>)o;
	}
	
	public void memberJoined(BonziBot bb, GuildMemberJoinEvent e) {
		Guild guild = e.getGuild();
		long guildId = guild.getIdLong();
		User user = e.getUser();
		long userId = user.getIdLong();
		GuildSettings settings = this.getSettings(guildId);
		if(settings.joinRole) {
			Role role = guild.getRoleById(settings.joinRoleId);
			TextChannel tc = guild.getDefaultChannel();
			if(role != null) {
				try {
					guild.addRoleToMember(userId, role).queue(null, new ErrorHandler()
					.ignore(ErrorResponse.UNKNOWN_MEMBER)
					.handle(ErrorResponse.MISSING_PERMISSIONS, err -> {
						if(tc != null)
							tc.sendMessage(BonziUtils.failureEmbed("I need some help here!",
								"Somebody joined, but I don't have the permissions to set anyone's roles...")).queue();
					}).handle(ErrorResponse.UNKNOWN_ROLE, err -> {
						if(tc != null)
							tc.sendMessage(BonziUtils.failureEmbed("I need some help here!",
								"Somebody joined, but it seems like my join role was deleted. Please disable it or set a new one!")).queue();
					}));
				} catch(Exception exc) {
					if(exc instanceof HierarchyException) {
						if(tc != null)
							tc.sendMessage(BonziUtils.failureEmbed("I need some help here!",
									"Somebody joined, but I'm not able to give the role because it's higher than me!")).queue();
					} else {
						if(tc != null)
							tc.sendMessage(BonziUtils.failureEmbed("something went pretty wrong",
								"someone joined the server and i can't figure out what happened... but this error message might help:\n\n" +
								exc.getMessage())).queue();
					}
				}
			}
		}
		
		if(settings.joinMessages) {
			if(settings.joinMessage == null ||
			settings.joinMessageChannel == 0l)
				return;
			String message = settings.joinMessage;
			long channelId = settings.joinMessageChannel;
			TextChannel tc = guild.getTextChannelById(channelId);
			String send = BonziUtils.joinLeaveVariables
				(message, user, guild);
			if(tc != null) {
				if(settings.joinMessageIsEmbed) {
					EmbedBuilder eb = new EmbedBuilder();
					List<String> allUrls = new ArrayList<String>();
					Matcher matcher = Constants.IMAGE_URL_REGEX_COMPILED.matcher(send);
					while(matcher.find())
						allUrls.add(matcher.group());
					if(!allUrls.isEmpty()) {
						send = matcher.replaceAll("");
						String firstImage = allUrls.get(0);
						eb.setImage(firstImage);
					}
					eb.setDescription(send);
					tc.sendMessage(eb.build()).queue();
				} else {
					tc.sendMessage(send).queue();
				}
			}
		}
		// probably dead code below this point
	}
	public void memberLeft(BonziBot bb, GuildMemberRemoveEvent e) {
		Guild guild = e.getGuild();
		long guildId = guild.getIdLong();
		User user = e.getUser();
		GuildSettings settings = this.getSettings(guildId);
		
		if(settings.leaveMessages) {
			if(settings.leaveMessage == null ||
			settings.leaveMessageChannel == 0l)
				return;
			String message = settings.leaveMessage;
			long channelId = settings.leaveMessageChannel;
			TextChannel tc = guild.getTextChannelById(channelId);
			String send = BonziUtils.joinLeaveVariables
				(message, user, guild);
			if(tc == null)
				return;
			if(settings.leaveMessageIsEmbed) {
				EmbedBuilder eb = new EmbedBuilder();
				String regex = Constants.IMAGE_URL_REGEX;
				List<String> allUrls = new ArrayList<String>();
				Matcher matcher = Pattern.compile(regex).matcher(send);
				while(matcher.find())
					allUrls.add(matcher.group());
				if(!allUrls.isEmpty()) {
					send = send.replaceAll(regex, "");
					String firstImage = allUrls.get(0);
					eb.setImage(firstImage);
				}
				eb.setDescription(send);
				tc.sendMessage(eb.build()).queue();
			} else {
				tc.sendMessage(send).queue();
			}
		}
		// probably dead code below this point
	}
}
