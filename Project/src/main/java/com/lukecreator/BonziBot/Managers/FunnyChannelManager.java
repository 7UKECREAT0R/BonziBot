package com.lukecreator.BonziBot.Managers;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Data.UsernameGenerator;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;

/**
 * shhh youre not supposed to see this!
 * @author Lukec
 */
public class FunnyChannelManager {
	
	private static Consumer<? super Throwable> fail = (thing) -> {};
	private static Random random = new Random(System.currentTimeMillis());
	
	/*
	 * THIS IS A SECRET FEATURE NOBODY KNOWS ABOUT! USE IT FOR WHATEVER I WAS JUST BORED
	 * BTW you can put whatever you want after this and itll still work!
	 * 
	 * ~trapdoor 		- makes you fall to the next channel down
	 * ~pitfall <x> 	- makes you fall down x channels (like trapdoor)
	 * ~ladder 			- moves you up one channel
	 * ~elevator <x> 	- go up x channels (like ladder)
	 * ~transformer 	- gives you a random funny username every time you join
	 * ~nojoin 			- users just cannot join it lmao
	 * ~houseof <name> 	- people can only join it if someone named <name> is in the channel.
	 * ~drivethru 		- pings the user and lets them order... food? moves them down a channel after.
	 * ~goto <channel>  - moves this user to a certain channel by name. no spaces allowed
	 * ~tryharder 		- there's only a 1/25 chance you'll actually be allowed into this channel!
	 * ~ballersonly		- only people with 10k+ coins can join
	 */
	
	public static void funnyChannels(BonziBot bb, GuildVoiceUpdateEvent e) {
		if(e.getEntity().getUser().isBot())
			return;
		
		VoiceChannel vc = e.getChannelJoined();
		
		if(vc == null || e instanceof GuildVoiceLeaveEvent)
			return;
		
		String chName = vc.getName();
		if(!chName.startsWith("~"))
			return;
		if(chName.length() < 2)
			return;
		
		String name = chName.substring(1);
		String[] parts = name.split(Constants.WHITESPACE_REGEX);
		String[] args;
		if(parts.length > 1) {
			args = new String[parts.length - 1];
			for(int i = 1; i < parts.length; i++)
				args[i - 1] = parts[i];
		} else args = new String[0];
		
		switch(parts[0].toLowerCase()) {
		case "trapdoor":
			trapdoor(bb, (GenericGuildVoiceEvent)e, args);
			return;
		case "pitfall":
			pitfall(bb, (GenericGuildVoiceEvent)e, args);
			return;
		case "ladder":
			ladder(bb, (GenericGuildVoiceEvent)e, args);
			return;
		case "elevator":
			elevator(bb, (GenericGuildVoiceEvent)e, args);
			return;
		case "transformer":
			transformer(bb, (GenericGuildVoiceEvent)e, args);
			return;
		case "nojoin":
			nojoin(bb, (GenericGuildVoiceEvent)e, args);
			return;
		case "houseof":
			houseof(bb, (GenericGuildVoiceEvent)e, args);
			return;
		case "drivethru":
			drivethru(bb, (GenericGuildVoiceEvent)e, args);
			return;
		case "goto":
			$goto(bb, (GenericGuildVoiceEvent)e, args);
			return;
		case "tryharder":
			tryharder(bb, (GenericGuildVoiceEvent)e, args);
			return;
		case "ballersonly":
			ballersonly(bb, (GenericGuildVoiceEvent)e, args);
			return;
		}
	}
	
	public static void trapdoor(BonziBot bb, GenericGuildVoiceEvent e, String[] args) {
		List<GuildChannel> channels = e.getGuild().getChannels();
		VoiceChannel vc = e.getVoiceState().getChannel();
		int currentPosition = 0;
		for(int i = 0; i < channels.size(); i++) {
			GuildChannel pTest = channels.get(i);
			if(pTest.getIdLong() == vc.getIdLong()) {
				currentPosition = i;
				break;
			}
		}
		for(int i = currentPosition + 1; i < channels.size(); i++) {
			GuildChannel channel = channels.get(i);
			if(channel.getType() == ChannelType.VOICE) {
				Guild guild = e.getGuild();
				Member member = e.getMember();
				String nameL = channel.getName().toLowerCase();
				if(nameL.contains("~elevator") || nameL.contains("~ladder"))
					continue;
				guild.moveVoiceMember(member, (VoiceChannel)channel).queueAfter(750, TimeUnit.MILLISECONDS, null, fail);
				return;
			}
		}
		return;
	}
	public static void pitfall(BonziBot bb, GenericGuildVoiceEvent e, String[] args) {
		if(args.length < 1)
			return;
		
		int amount;
		try {
			amount = Integer.parseInt(args[0]);
		} catch(NumberFormatException nfe) {
			return;
		}
		
		if(amount < 1)
			return;
		
		List<GuildChannel> channels = e.getGuild().getChannels();
		VoiceChannel vc = e.getVoiceState().getChannel();
		int currentPosition = 0;
		for(int i = 0; i < channels.size(); i++) {
			GuildChannel pTest = channels.get(i);
			if(pTest.getIdLong() == vc.getIdLong()) {
				currentPosition = i;
				break;
			}
		}
		
		for(int i = currentPosition + amount; i < channels.size(); i++) {
			GuildChannel channel = channels.get(i);
			if(channel.getType() == ChannelType.VOICE) {
				Guild guild = e.getGuild();
				Member member = e.getMember();
				String nameL = channel.getName().toLowerCase();
				if(nameL.contains("~elevator") || nameL.contains("~ladder"))
					continue;
				guild.moveVoiceMember(member, (VoiceChannel)channel).queueAfter(750, TimeUnit.MILLISECONDS, null, fail);
				return;
			}
		}
		return;
	}
	public static void ladder(BonziBot bb, GenericGuildVoiceEvent e, String[] args) {
		List<GuildChannel> channels = e.getGuild().getChannels();
		VoiceChannel vc = e.getVoiceState().getChannel();
		int currentPosition = 0;
		for(int i = 0; i < channels.size(); i++) {
			GuildChannel pTest = channels.get(i);
			if(pTest.getIdLong() == vc.getIdLong()) {
				currentPosition = i;
				break;
			}
		}
		for(int i = currentPosition - 1; i >= 0; i++) {
			GuildChannel channel = channels.get(i);
			if(channel.getType() == ChannelType.VOICE) {
				Guild guild = e.getGuild();
				Member member = e.getMember();
				String nameL = channel.getName().toLowerCase();
				if(nameL.contains("~trapdoor") || nameL.contains("~pitfall"))
					continue;
				guild.moveVoiceMember(member, (VoiceChannel)channel).queueAfter(750, TimeUnit.MILLISECONDS, null, fail);
				return;
			}
		}
		return;
	}
	public static void elevator(BonziBot bb, GenericGuildVoiceEvent e, String[] args) {
		if(args.length < 1)
			return;
		
		int amount;
		try {
			amount = Integer.parseInt(args[0]);
		} catch(NumberFormatException nfe) {
			return;
		}
		
		if(amount < 1)
			return;
		
		List<GuildChannel> channels = e.getGuild().getChannels();
		VoiceChannel vc = e.getVoiceState().getChannel();
		int currentPosition = 0;
		for(int i = 0; i < channels.size(); i++) {
			GuildChannel pTest = channels.get(i);
			if(pTest.getIdLong() == vc.getIdLong()) {
				currentPosition = i;
				break;
			}
		}
		for(int i = currentPosition - amount; i >= 0; i++) {
			GuildChannel channel = channels.get(i);
			if(channel.getType() == ChannelType.VOICE) {
				Guild guild = e.getGuild();
				Member member = e.getMember();
				String nameL = channel.getName().toLowerCase();
				if(nameL.contains("~trapdoor") || nameL.contains("~pitfall"))
					continue;
				guild.moveVoiceMember(member, (VoiceChannel)channel).queueAfter(750, TimeUnit.MILLISECONDS, null, fail);
				return;
			}
		}
		return;
	}
	public static void transformer(BonziBot bb, GenericGuildVoiceEvent e, String[] args) {
		String username = UsernameGenerator.generate();
		try {
			e.getMember().modifyNickname(username).queue(null, fail);
		} catch(HierarchyException exc) {}
		
	}
	public static void randomwarp(BonziBot bb, GenericGuildVoiceEvent e, String[] args) {
		// disabled for fear of someone using loops to get the bot ratelimited
		List<VoiceChannel> channels = e.getGuild().getVoiceChannels();
		VoiceChannel current = e.getVoiceState().getChannel();
		
		if(channels.size() <= 1)
			return;
		
		VoiceChannel pick = null;
		
		do {
			pick = channels.get(random.nextInt(channels.size()));
		} while(pick.getIdLong() == current.getIdLong());
		
		Member member = e.getMember();
		Guild guild = e.getGuild();
		guild.moveVoiceMember(member, pick).queueAfter(500, TimeUnit.MILLISECONDS, null, fail);
	}
	public static void nojoin(BonziBot bb, GenericGuildVoiceEvent e, String[] args) {
		Guild guild = e.getGuild();
		Member disconnect = e.getMember();
		guild.kickVoiceMember(disconnect).queueAfter(750, TimeUnit.MILLISECONDS, null, fail);
	}
	public static void houseof(BonziBot bb, GenericGuildVoiceEvent e, String[] args) {
		if(args.length < 1)
			return;
		String name = args[0];
		
		VoiceChannel channel = e.getVoiceState().getChannel();
		Guild guild = e.getGuild();
		Member member = e.getMember();
		
		if(member.getUser().getName().equalsIgnoreCase(name))
			return;
		
		List<Member> allMembers = channel.getMembers();
		for(Member test: allMembers) {
			if(test.getUser().getName().equalsIgnoreCase(name))
				return; // let them in
		}
		
		guild.kickVoiceMember(member).queueAfter(750, TimeUnit.MILLISECONDS, null, fail);
		
	}
	public static void drivethru(BonziBot bb, GenericGuildVoiceEvent e, String[] args) {
		Member member = e.getMember();
		Guild guild = e.getGuild();
		String guildName = guild.getName();
		TextChannel tc = guild.getDefaultChannel();
		String[] foodOptions = new String[] {
			"Burger Shack", "Milkshakes", "Burgers", "Fries", "Fast Food Truck", "Tendies"
		};
		String subtitle = foodOptions[random.nextInt(foodOptions.length)];
		tc.sendMessage("Hey, " + member.getAsMention() + "! Welcome to " + guildName + "'s " + subtitle + ". What can I get for ya?").queue();
		
		EventWaiterManager ewm = bb.eventWaiter;
		ewm.waitForResponse(member.getUser(), msg -> {
			tc.sendMessage("Alright, there you go! Have a great day!").queue();
			trapdoor(bb, e, args);
		});
	}
	public static void $goto(BonziBot bb, GenericGuildVoiceEvent e, String[] args) {
		if(args.length < 1)
			return;
		String channelName = args[0];
		Guild guild = e.getGuild();
		List<VoiceChannel> matches = guild.getVoiceChannelsByName(channelName, true);
		
		if(matches.isEmpty())
			return;
		
		VoiceChannel moveTo = matches.get(0);
		String nameL = moveTo.getName().toLowerCase();
		if(nameL.contains("~trapdoor") || nameL.contains("~pitfall")
		|| nameL.contains("~ladder") || nameL.contains("~elevator"))
			return;
		guild.moveVoiceMember(e.getMember(), moveTo).queueAfter(750, TimeUnit.MILLISECONDS, null, fail);
	}
	public static void tryharder(BonziBot bb, GenericGuildVoiceEvent e, String[] args) {
		
		int test = random.nextInt(25);
		if(test == 0)
			return;
		
		Guild guild = e.getGuild();
		Member disconnect = e.getMember();
		guild.kickVoiceMember(disconnect).queueAfter(500, TimeUnit.MILLISECONDS);
	}
	public static void ballersonly(BonziBot bb, GenericGuildVoiceEvent e, String[] args) {
		
		User user = e.getMember().getUser();
		UserAccount account = bb.accounts.getUserAccount(user);
		
		if(account.getCoins() >= 10000)
			return;
		
		Guild guild = e.getGuild();
		Member disconnect = e.getMember();
		guild.kickVoiceMember(disconnect).queueAfter(750, TimeUnit.MILLISECONDS, null, fail);
	}
}