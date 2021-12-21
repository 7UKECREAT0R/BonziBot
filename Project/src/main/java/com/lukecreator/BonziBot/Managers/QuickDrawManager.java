package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.QuickDraw;
import com.lukecreator.BonziBot.Data.UserAccount;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

/**
 * Passively watch a channel and run quick draws if enabled.
 * The presence of a Guild in QuickDrawProfile is synced with the server settings actively.
 * @author Lukec
 */
public class QuickDrawManager implements IStorableData {
	
	HashMap<Long, QuickDrawProfile> profiles = new HashMap<Long, QuickDrawProfile>();
	public void tryOptGuild(long guildId) {
		if(profiles.containsKey(guildId))
			return;
		profiles.put(guildId, new QuickDrawProfile());
	}
	public void tryUnoptGuild(long guildId) {
		if(!profiles.containsKey(guildId))
			return;
		profiles.remove(guildId);
	}
	
	QuickDrawProfile winGame(User winner, TextChannel tc, QuickDrawProfile profile, BonziBot bb) {
		UserAccountManager uam = bb.accounts;
		UserAccount account = uam.getUserAccount(winner);
		int reward = profile.game.reward;
		account.addCoins(reward);
		uam.setUserAccount(winner, account);
		
		long sent = profile.game.sentMessageId;
		if(sent != 0l)
			tc.deleteMessageById(sent).queue();
		
		profile.game.constructWinnerMessage(winner, reward, tc).queue();
		
		profile.game = null;
		return profile;
	}
	public void messageReceived(User sender, Message message, BonziBot bb) {
		TextChannel channel = message.getTextChannel();
		long gid = channel.getGuild().getIdLong();
		if(!profiles.containsKey(gid))
			return;
		
		QuickDrawProfile profile = profiles.get(gid);
		if(profile.shouldSend(sender.getIdLong())) {
			QuickDraw game = QuickDraw.create(bb);
			MessageAction action = game.constructMessage(channel);
			action.queue(msg -> {
				game.sentMessageId = msg.getIdLong();
				game.postConstructMessage(msg);
				QuickDrawProfile profile2 = profiles.get(gid);
				profile2.game = game;
				profiles.put(gid, profile2);
			});
		} else if(profile.game != null) {
			if(profile.game.tryInput(message)) {
				profile = this.winGame(sender, channel, profile, bb);
			}
		}
		profiles.put(gid, profile);
	}
	public void reactionReceived(User sender, GuildMessageReactionAddEvent event, BonziBot bb) {
		TextChannel channel = event.getChannel();
		long gid = channel.getGuild().getIdLong();
		if(!profiles.containsKey(gid))
			return;
		
		QuickDrawProfile profile = profiles.get(gid);
		if(profile.game == null)
			return;
		
		if(event.getMessageIdLong() != profile.game.sentMessageId)
			return;
		
		if(profile.game.tryInput(event.getReactionEmote())) {
			profile = this.winGame(sender, channel, profile, bb);
			profiles.put(gid, profile);
		}
	}
	
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(profiles.keySet().stream().collect(Collectors.toList()), "qdp.ser"); // quick draw presence
	}
	@Override
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject("qdp.ser"); // quick draw presence
		if(o != null) {
			@SuppressWarnings("unchecked")
			List<Long> ids = (List<Long>)o;
			for(long id: ids)
				this.profiles.put(id, new QuickDrawProfile());
		}
	}
}
/**
 * One per-guild with quick draw enabled. States are not saved/loaded.
 * @author Lukec
 */
class QuickDrawProfile {
	
	public static final int QUICK_DRAW_DELAY_LOW = 30; // Lowest message count that can trigger a quick draw.
	public static final int QUICK_DRAW_DELAY_CHANCE = 7; // 1/X chance when messages is higher than QUICK_DRAW_DELAY_LOW
	
	public QuickDraw game = null;
	public Random random = new Random();
	
	public long lastSenderId = 0l;
	public int totalMessages = 0;
	
	public boolean shouldSend(long newSenderId) {
		if(newSenderId == this.lastSenderId)
			return false;
		
		this.lastSenderId = newSenderId;
		
		if(this.totalMessages++ < QUICK_DRAW_DELAY_LOW)
			return false;
		
		int pick = random.nextInt(QUICK_DRAW_DELAY_CHANCE);
		if(pick != 0)
			return false;
		
		this.totalMessages = 0;
		return true;
	}
}