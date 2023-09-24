package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.HashMap;
import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.Achievement;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.UserAccount;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;

/**
 * Manages the delays and using of the rep "command."
 * Rep is not actually a command, so this does it all.
 * @author Lukec
 */
public class RepManager implements IStorableData {
	
	public static final long ONE_HOUR = 3600000;
	public static final long ONE_DAY = ONE_HOUR * 24;
	public static final String FILE_TIMES = "rep_timestamps";
	public static final Emoji DONE = Emoji.fromUnicode("✅");
	
	// User ID, UTC Timestamp of last rep
	public HashMap<Long, Long> lastRep = new HashMap<Long, Long>();
	
	public void checkMessage(Message msg, BonziBot bb) {
		String content = msg.getContentRaw().toLowerCase();
		boolean positive = content.startsWith("+rep");
		if(!positive && !content.startsWith("-rep"))
			return;
		long executor = msg.getAuthor().getIdLong();
		if(!this.canRep(executor)) {
			long time = this.timeUntilRep(executor);
			String timeString = BonziUtils.getLongTimeStringMs(time);
			MessageChannelUnion ch = msg.getChannel();
			BonziUtils.sendTempMessage(ch, BonziUtils.failureEmbed(
				"You can't rep anyone yet!", timeString + " remaining"), 3);
			return;
		}
		List<User> mentioned = msg.getMentions().getUsers();
		if(mentioned.isEmpty())
			this.repByHistory(msg, bb, positive);
		else {
			User target = mentioned.get(0);
			if(target.getIdLong() == executor)
				return;
			this.repByExplicit(msg, bb, positive, target);
		}
	}
	public void repByHistory(Message sent, BonziBot bb, boolean add) {
		long sender = sent.getAuthor().getIdLong();
		this.lastRep.put(sender, System.currentTimeMillis());
		
		sent.getChannel().getHistoryBefore(sent, 3).queue(history -> {
			if(history.isEmpty())
				return;
			List<Message> msgs = history.getRetrievedHistory();
			for(Message iterate: msgs) {
				User author = iterate.getAuthor();
				if(author.getIdLong() == sender)
					continue;
				UserAccountManager uam = bb.accounts;
				UserAccount account = uam.getUserAccount(author);
				if(add)	account.addRep();
				else 	account.subRep();
				int rep = account.getRep();
				uam.setUserAccount(author, account);
				
				if(rep == 10)
					BonziUtils.tryAwardAchievement(sent.getChannel(), bb, author, Achievement.POPULAR);
				if(rep == 100)
					BonziUtils.tryAwardAchievement(sent.getChannel(), bb, author, Achievement.FAMOUS);
				
				sent.addReaction(DONE).queue();
				return;
			}
		});
	}
	public void repByExplicit(Message sent, BonziBot bb, boolean add, User target) {
		long sender = sent.getAuthor().getIdLong();
		this.lastRep.put(sender, System.currentTimeMillis());
		
		UserAccountManager uam = bb.accounts;
		UserAccount account = uam.getUserAccount(target);
		if(add)	account.addRep();
		else 	account.subRep();
		int rep = account.getRep();
		uam.setUserAccount(target, account);
		
		if(rep == 10)
			BonziUtils.tryAwardAchievement(sent.getChannel(), bb, target, Achievement.POPULAR);
		if(rep == 100)
			BonziUtils.tryAwardAchievement(sent.getChannel(), bb, target, Achievement.FAMOUS);
		
		sent.addReaction(DONE).queue();
		return;
	}
	
	public boolean canRep(long executor) {
		if(!this.lastRep.containsKey(executor))
			return true;
		long timestamp = this.lastRep.get(executor);
		timestamp += ONE_DAY;
		return System.currentTimeMillis() >= timestamp;
	}
	public long timeUntilRep(long executor) {
		if(!this.lastRep.containsKey(executor))
			return 0l;
		long timestamp = this.lastRep.get(executor);
		timestamp += ONE_DAY;
		long diff = timestamp - System.currentTimeMillis();
		if(diff < 0)
			return 0l;
		return diff;
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(this.lastRep, FILE_TIMES);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o1 = DataSerializer.retrieveObject(FILE_TIMES);
			
		if(o1 != null)
			this.lastRep = (HashMap<Long, Long>) o1;
	}
}
