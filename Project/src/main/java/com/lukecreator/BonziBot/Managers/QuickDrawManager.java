package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.QuickDraw;
import com.lukecreator.BonziBot.Data.UserAccount;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

/**
 * Passively watch a channel and run quick draws if enabled.
 * The presence of a Guild in QuickDrawProfile is synced with the server settings actively.
 * @author Lukec
 */
public class QuickDrawManager implements IStorableData {
	
	HashMap<Long, QuickDrawProfile> profiles = new HashMap<Long, QuickDrawProfile>();
	
	public void tryOptGuild(long guildId) {
		if(this.profiles.containsKey(guildId))
			return;
		this.profiles.put(guildId, new QuickDrawProfile());
	}
	public void tryUnoptGuild(long guildId) {
		if(!this.profiles.containsKey(guildId))
			return;
		this.profiles.remove(guildId);
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
	public void spawnQuickDraw(long guildId, TextChannel channel, BonziBot bb) {
		QuickDraw game = QuickDraw.create(bb);
		MessageCreateAction action = game.constructMessage(channel);
		action.queue(msg -> {
			game.sentMessageId = msg.getIdLong();
			game.postConstructMessage(msg);
			
			// because async state
			QuickDrawProfile profile2 = this.profiles.get(guildId);
			profile2.game = game;
			this.profiles.put(guildId, profile2);
		}, fail -> {
			InternalLogger.print("Failed to send.\n" + fail.toString() + "\n\n" + action.toString());
		});
	}
	
	public void messageReceived(User sender, Message message, BonziBot bb) {
		if(!message.isFromGuild())
			return;
		
		TextChannel channel = message.getChannel().asTextChannel();
		
		long gid = channel.getGuild().getIdLong();
		if(!this.profiles.containsKey(gid))
			return;
		
		QuickDrawProfile profile = this.profiles.get(gid);
		if(profile.shouldSend(sender.getIdLong())) {
			this.spawnQuickDraw(gid, channel, bb);
		} else if(profile.game != null) {
			if(profile.game.tryInput(message)) {
				profile = this.winGame(sender, channel, profile, bb);
			}
		}
		this.profiles.put(gid, profile);
	}
	public void reactionReceived(User sender, MessageReactionAddEvent event, BonziBot bb) {
		if(!event.isFromGuild())
			return;
		
		TextChannel channel = event.getChannel().asTextChannel();
		long gid = channel.getGuild().getIdLong();
		
		if(!this.profiles.containsKey(gid))
			return;
		
		QuickDrawProfile profile = this.profiles.get(gid);
		
		if(profile.game == null)
			return;
		
		if(event.getMessageIdLong() != profile.game.sentMessageId)
			return;
		
		if(profile.game.tryInput(event.getEmoji())) {
			profile = this.winGame(sender, channel, profile, bb);
			this.profiles.put(gid, profile);
		}
	}
	public void buttonReceived(User sender, String data, ButtonInteractionEvent event, BonziBot bb) {
		if(!event.isFromGuild())
			return;
		
		TextChannel channel = event.getChannel().asTextChannel();
		long gid = event.getGuild().getIdLong();
		
		if(!this.profiles.containsKey(gid))
			return;
		
		QuickDrawProfile profile = this.profiles.get(gid);
		
		if(profile.game == null)
			return;
		
		if(event.getMessageIdLong() != profile.game.sentMessageId) {
			event.replyEmbeds(BonziUtils.failureEmbed("This quick draw has expired!")).setEphemeral(true).queue();
			return;
		}
		
		if(profile.game.tryInput(event.getButton(), data)) {
			profile = this.winGame(sender, channel, profile, bb);
			this.profiles.put(gid, profile);
		} else {
			event.deferEdit().queue();
		}
	}
	
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(this.profiles.keySet().stream().collect(Collectors.toList()), "qdp.ser"); // quick draw presence
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
		
		int pick = this.random.nextInt(QUICK_DRAW_DELAY_CHANCE);
		if(pick != 0)
			return false;
		
		this.totalMessages = 0;
		return true;
	}
}