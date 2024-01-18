package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.TagData;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TagManager implements IStorableData {
	
	HashMap<Long, String> responseQueue;
	HashMap<Long, HashMap<Long, String>> privateResponseQueue;
	
	List<TagData> tags;
	HashMap<Long, List<TagData>> privateTags;
	
	public TagManager() {
		this.tags = new ArrayList<TagData>();
		this.privateTags = new HashMap<Long, List<TagData>>();
		this.responseQueue = new HashMap<Long, String>();
		this.privateResponseQueue = new HashMap<Long, HashMap<Long, String>>();
	}
	public void addToPublicQueue(User user, String tagName) {
		this.responseQueue.put(user.getIdLong(), tagName);
	}
	public boolean userInPublicQueue(User user) {
		return this.responseQueue.containsKey(user.getIdLong());
	}
	public String getEditingCommandPublic(User user) {
		long id = user.getIdLong();
		if(this.responseQueue.containsKey(id))
			return this.responseQueue.get(id);
		else return null;
	}
	public String removeFromPublicQueue(User user) {
		long id = user.getIdLong();
		if(this.responseQueue.containsKey(id))
			return this.responseQueue.remove(id);
		return null;
	}
	
	public void addToPrivateQueue(Member m, String tagName) {
		HashMap<Long, String> queue = this.getQueueForGuild(m.getGuild());
		queue.put(m.getUser().getIdLong(), tagName);
		this.setQueueForGuild(m.getGuild(), queue);
	}
	public boolean userInPrivateQueue(Member m) {
		HashMap<Long, String> queue = this.getQueueForGuild(m.getGuild());
		return queue.containsKey(m.getUser().getIdLong());
	}
	public String getEditingCommandPrivate(Member m) {
		HashMap<Long, String> queue = this.getQueueForGuild(m.getGuild());
		long id = m.getUser().getIdLong();
		if(queue.containsKey(id))
			return queue.get(id);
		else return null;
	}
	public String getEditingCommandPrivate(long userId, long guildId) {
		HashMap<Long, String> queue = this.getQueueForGuild(guildId);
		if(queue.containsKey(userId))
			return queue.get(userId);
		else return null;
	}
	public String removeFromPrivateQueue(Member m) {
		HashMap<Long, String> queue = this.getQueueForGuild(m.getGuild());
		long id = m.getUser().getIdLong();
		if(queue.containsKey(id)) {
			String out = queue.remove(id);
			this.setQueueForGuild(m.getGuild(), queue);
			return out;
		}
		return null;
	}
	public String removeFromPrivateQueue(long userId, long guildId) {
		HashMap<Long, String> queue = this.getQueueForGuild(guildId);
		if(queue.containsKey(userId)) {
			String out = queue.remove(userId);
			this.setQueueForGuild(guildId, queue);
			return out;
		}
		return null;
	}
	private HashMap<Long, String> getQueueForGuild(Guild g) {
		long id = g.getIdLong();
		if(!this.privateResponseQueue.containsKey(id)) {
			HashMap<Long, String> n = new HashMap<Long, String>();
			this.privateResponseQueue.put(id, n);
			return n;
		}
		return this.privateResponseQueue.get(id);
	}
	private HashMap<Long, String> getQueueForGuild(long id) {
		if(!this.privateResponseQueue.containsKey(id)) {
			HashMap<Long, String> n = new HashMap<Long, String>();
			this.privateResponseQueue.put(id, n);
			return n;
		}
		return this.privateResponseQueue.get(id);
	}
	private HashMap<Long, String> setQueueForGuild(Guild g, HashMap<Long, String> queue) {
		long id = g.getIdLong();
		return this.privateResponseQueue.put(id, queue);
	}
	private HashMap<Long, String> setQueueForGuild(long id, HashMap<Long, String> queue) {
		return this.privateResponseQueue.put(id, queue);
	}
	
	public List<TagData> getPublicTags() {
		return this.tags;
	}
	public List<TagData> getPrivateTags(Guild g) {
		long id = g.getIdLong();
		if(this.privateTags.containsKey(id)) {
			return this.privateTags.get(id);
		} else {
			this.privateTags.put(id, new ArrayList<TagData>());
			return this.privateTags.get(id);
		}
	}
	public List<TagData> getPrivateTags(long gId) {
		if(this.privateTags.containsKey(gId)) {
			return this.privateTags.get(gId);
		} else {
			this.privateTags.put(gId, new ArrayList<TagData>());
			return this.privateTags.get(gId);
		}
	}
	public void setPublicTags(List<TagData> data) {
		this.tags = data;
	}
	public void setPrivateTags(List<TagData> data, Guild g) {
		long id = g.getIdLong();
		this.privateTags.put(id, data);
	}
	public void setPrivateTags(List<TagData> data, long gId) {
		this.privateTags.put(gId, data);
	}
	public TagData getTagByName(String name) {
		for(TagData data: this.tags) {
			if(data.name.equalsIgnoreCase(name))
				return data;
		}
		return null;
	}
	public void setTagByName(TagData tag) {
		for(int i = 0; i < this.tags.size(); i++) {
			TagData check = this.tags.get(i);
			if(check.name.equalsIgnoreCase(tag.name)) {
				this.tags.set(i, tag);
				return;
			}
		}
		this.tags.add(tag);
	}
	public void removeTagByName(String name) {
		for(int i = 0; i < this.tags.size(); i++) {
			TagData check = this.tags.get(i);
			if(check == null || check.name == null) {
				this.tags.remove(i);
			}
			if(check.name.equalsIgnoreCase(name)) {
				this.tags.remove(i);
				return;
			}
		}
	}
	public String useTagByName(String name) {
		for(int i = 0; i < this.tags.size(); i++) {
			TagData tag = this.tags.get(i);
			if(tag == null || tag.name == null) continue;
			if(tag.name.equalsIgnoreCase(name)) {
				tag.uses++;
				this.tags.set(i, tag);
				return tag.response;
			}
		}
		return null;
	}
	
	public TagData getPrivateTagByName(String name, Guild g) {
		List<TagData> tags = this.getPrivateTags(g);
		for(TagData data: tags) {
			if(data.name.equalsIgnoreCase(name))
				return data;
		}
		return null;
	}
	public void setPrivateTagByName(TagData tag, Guild g) {
		List<TagData> tags = this.getPrivateTags(g);
		for(int i = 0; i < tags.size(); i++) {
			TagData check = tags.get(i);
			if(check.name.equalsIgnoreCase(tag.name)) {
				tags.set(i, tag);
				this.setPrivateTags(tags, g);
				return;
			}
		}
		tags.add(tag);
		this.setPrivateTags(tags, g);
	}
	public void setPrivateTagByName(TagData tag, long guildId) {
		List<TagData> tags = this.getPrivateTags(guildId);
		for(int i = 0; i < tags.size(); i++) {
			TagData check = tags.get(i);
			if(check.name.equalsIgnoreCase(tag.name)) {
				tags.set(i, tag);
				this.setPrivateTags(tags, guildId);
				return;
			}
		}
		tags.add(tag);
		this.setPrivateTags(tags, guildId);
	}
	public void removePrivateTagByName(String name, Guild g) {
		List<TagData> tags = this.getPrivateTags(g);
		for(int i = 0; i < tags.size(); i++) {
			TagData check = tags.get(i);
			if(check == null || check.name == null) {
				tags.remove(i);
			}
			if(check.name.equalsIgnoreCase(name)) {
				tags.remove(i);
			}
		}
		this.setPrivateTags(tags, g);
	}
	public void removePrivateTagByName(String name, long guildId) {
		List<TagData> tags = this.getPrivateTags(guildId);
		for(int i = 0; i < tags.size(); i++) {
			TagData check = tags.get(i);
			if(check == null || check.name == null) {
				tags.remove(i);
			}
			if(check.name.equalsIgnoreCase(name)) {
				tags.remove(i);
			}
		}
		this.setPrivateTags(tags, guildId);
	}
	public String usePrivateTagByName(String name, Guild g) {
		List<TagData> tags = this.getPrivateTags(g);
		for(int i = 0; i < tags.size(); i++) {
			TagData tag = tags.get(i);
			if(tag.name.equalsIgnoreCase(name)) {
				tag.uses++;
				tags.set(i, tag);
				this.setPrivateTags(tags, g);
				return tag.response;
			}
		}
		return null;
	}
	
	public List<TagData> getPublicTagsOfUser(User u) {
		return this.getPublicTagsOfUser(u.getIdLong());
	}
	public List<TagData> getPublicTagsOfUser(long id) {
		List<TagData> ret = new ArrayList<TagData>();
		for(TagData td: this.tags) {
			if(td.creatorId == id)
				ret.add(td);
		}
		return ret;
	}
	public List<TagData> getPrivateTagsOfUser(User u, Guild g) {
		return this.getPrivateTagsOfUser(u.getIdLong(), g.getIdLong());
	}
	public List<TagData> getPrivateTagsOfUser(long id, long gId) {
		List<TagData> base = this.getPrivateTags(gId);
		List<TagData> ret = new ArrayList<TagData>();
		for(TagData td: base) {
			if(td.creatorId == id)
				ret.add(td);
		}
		return ret;
	}
	
	// returns if it should block command execution
	public boolean receiveMessage(MessageReceivedEvent e, BonziBot b) {
		if(e.isFromType(ChannelType.TEXT)) {
			MessageChannelUnion channel = e.getChannel();
			Member m = e.getMember();
			User u = e.getAuthor();
			
			String prefix = BonziUtils.getPrefixOrDefault(e, b);
			
			if(this.userInPublicQueue(u)) {
				Message msg = e.getMessage();
				String tName = this.getEditingCommandPublic(u);
				TagData tag = TagData.constructFromMessage(tName, msg);
				this.removeFromPublicQueue(u);
				this.setTagByName(tag);
				
				MessageEmbed me = BonziUtils.successEmbedIncomplete
					("Successfully created tag!")
					.setDescription("Use [" + prefix + "taginfo] to modify/delete your tag.").build();
				channel.sendMessageEmbeds(me).queue();
				return true;
			}
			if(this.userInPrivateQueue(m)) {
				Message msg = e.getMessage();
				String tName = this.getEditingCommandPrivate(m);
				TagData tag = TagData.constructFromMessage(tName, msg);
				this.removeFromPrivateQueue(m);
				this.setPrivateTagByName(tag, e.getGuild());
				
				MessageEmbed me = BonziUtils.successEmbedIncomplete
						("Successfully created private tag!")
						.setDescription("Use [" + prefix + "taginfo] to modify/delete your tag.").build();
				channel.sendMessageEmbeds(me).queue();
				return true;
			}
			
			return false;
		} else if(e.isFromType(ChannelType.PRIVATE)) {
			MessageChannelUnion channel = e.getChannel();
			User u = e.getAuthor();
			
			if(this.userInPublicQueue(u)) {
				Message msg = e.getMessage();
				String tName = this.getEditingCommandPublic(u);
				TagData tag = TagData.constructFromMessage(tName, msg);
				this.removeFromPublicQueue(u);
				this.setTagByName(tag);
				
				String prefix = Constants.DEFAULT_PREFIX;
				
				MessageEmbed me = BonziUtils.successEmbedIncomplete
					("Successfully created tag!")
					.setDescription("Use [" + prefix + "taginfo] to modify/delete your tag.").build();
				channel.sendMessageEmbeds(me).queue();
				return true;
			}

			return false;
		}
		
		return false;
	}
	public boolean receiveMessage(BonziBot instance, Message msg, String tagName, long privateId, boolean edit) {
		
		// privateID can be -1 which indicates its public.
		boolean isPrivate = privateId != -1;
		MessageChannelUnion channel = msg.getChannel();
		User u = msg.getAuthor();
		
		boolean fromGuild = msg.isFromGuild();
		String prefix = fromGuild ? instance.guildSettings.getSettings
			(msg.getGuild()).getPrefix() : Constants.DEFAULT_PREFIX;
		
		if(isPrivate) {
			TagData tag = TagData.constructFromMessage(tagName, msg);
			this.removeFromPrivateQueue(msg.getMember());
			this.setPrivateTagByName(tag, privateId);
			
			MessageEmbed me;
			if(edit) {
				me = BonziUtils.successEmbedIncomplete("Successfully edited your private tag.").build();
			} else me = BonziUtils.successEmbedIncomplete("Successfully created private tag!").setDescription("Use [" + prefix + "taginfo] to edit/delete your tag.").build();
			channel.sendMessageEmbeds(me).queue();
			return true;
		} else {
			TagData tag = TagData.constructFromMessage(tagName, msg);
			this.removeFromPublicQueue(u);
			this.setTagByName(tag);
			
			MessageEmbed me;
			if(edit) {
				me = BonziUtils.successEmbedIncomplete("Successfully edited your tag.").build();
			} else me = BonziUtils.successEmbedIncomplete("Successfully created tag!").setDescription("Use [" + prefix + "taginfo] to edit/delete your tag.").build();
			channel.sendMessageEmbeds(me).queue();
			return true;
		}
	}
	
	public void verifyTagValidities() {
		for(int i = 0; i < this.tags.size(); i++) {
			TagData td = this.tags.get(i);
			if(td == null || td.name == null) {
				this.tags.remove(i);
				i--;
			}
		}
	}
	@Override
	public void saveData() {
		DataSerializer.writeObject(this.tags, "tags");
		DataSerializer.writeObject(this.privateTags, "privateTags");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o1 = DataSerializer.retrieveObject("tags");
		Object o2 = DataSerializer.retrieveObject("privateTags");
		if(o1 != null)
			this.tags = (List<TagData>)o1;
		if(o2 != null)
			this.privateTags = (HashMap<Long, List<TagData>>)o2;
	}
}
