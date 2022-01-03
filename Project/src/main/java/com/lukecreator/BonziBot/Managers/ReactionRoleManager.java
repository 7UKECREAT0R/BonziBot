package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.ReactionRole;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;

/**
 * Ties a set of reaction roles to a message ID.
 */
public class ReactionRoleManager implements IStorableData {

	public static final String FILE = "newReactionRoles";
	
	HashMap<Long, ReactionRole[]> messages = new HashMap<Long, ReactionRole[]>();
	
	public void setReactionRoles(Message msg, ReactionRole[] roles) {
		this.messages.put(msg.getIdLong(), roles);
	}
	public void setReactionRoles(long messageId, ReactionRole[] roles) {
		this.messages.put(messageId, roles);
	}
	
	/**
	 * Gets the role ID to give in the case of a reaction on a message.
	 * @param msg
	 * @param emote
	 * @return -1 if no role could be found.
	 */
	public long getRoleForReaction(Message msg, ReactionEmote emote) {
		return this.getRoleForReaction(msg.getIdLong(), emote);
	}
	/**
	 * Gets the role ID to give in the case of a reaction on a message.
	 * @param messageId
	 * @param emote
	 * @return -1 if no role could be found.
	 */
	public long getRoleForReaction(long messageId, ReactionEmote emote) {
		if(!this.messages.containsKey(messageId))
			return -1;
		
		ReactionRole[] roles = this.messages.get(messageId);
		
		for(ReactionRole rr: roles) {
			if(rr.emoji.isEqual(emote))
				return rr.roleId;
		}
		
		return -1;
	}
	
	public void handleEvent(GuildMessageReactionAddEvent e, BonziBot bb) {
		Member m = e.getMember();
		if(m != null && m.getUser().isBot())
			return;
		
		this.handleEvent(e, bb, true);
	}
	public void handleEvent(GuildMessageReactionRemoveEvent e, BonziBot bb) {
		Member m = e.getMember();
		if(m != null && m.getUser().isBot())
			return;
		
		this.handleEvent(e, bb, false);
	}
	
	void handleEvent(GenericGuildMessageReactionEvent e, BonziBot bb, boolean add) {
		long messageId = e.getMessageIdLong();
		ReactionEmote re = e.getReactionEmote();
		
		long roleId = this.getRoleForReaction(messageId, re);
		if(roleId == -1)
			return;
		
		Guild guild = e.getGuild();
		Role role = guild.getRoleById(roleId);
		long userId = e.getUserIdLong();
		
		TextChannel channel = e.getChannel();
		
		if(role == null) {
			// fix this catastrophe
			channel.sendMessage("UH OH the role you selected was deleted... tryna fix this").queue();
			
			e.retrieveMessage().queue(msg -> {
				ReactionRole[] oldReactionRoles = this.messages.get(messageId);
				ReactionRole[] newReactionRoles = new ReactionRole[oldReactionRoles.length - 1];
				
				if(newReactionRoles.length < 1) {
					this.messages.remove(messageId);
					msg.delete().queue();
					return;
				}
				
				int write = 0;
				for(ReactionRole rr: oldReactionRoles)
					if(rr.roleId != roleId)
						newReactionRoles[write++] = rr;
				
				this.messages.put(messageId, newReactionRoles);
				
				e.getReaction().clearReactions().queue(success -> {
					for(ReactionRole rr: newReactionRoles)
						rr.emoji.react(msg);
				}, fail -> {});
			});
			return;
		}
		
		MessageEmbed failEmbed = BonziUtils.failureEmbed("help", "i cannot give the role to this person");
		Member self = guild.getSelfMember();
		
		guild.retrieveMemberById(userId).queue(member -> {
			if(!self.canInteract(member)) {
				channel.sendMessageEmbeds(failEmbed).queue(del -> {
					del.delete().queueAfter(5, TimeUnit.SECONDS);
				});
				return;
			}
			
			boolean hasRole = member.getRoles().stream().anyMatch(r -> r.getIdLong() == roleId);
			
			if(add && !hasRole)
				guild.addRoleToMember(userId, role).queue(null, fail -> {
					channel.sendMessageEmbeds(failEmbed).queue(del -> {
						del.delete().queueAfter(5, TimeUnit.SECONDS);
					});
				});
			else if(!add && hasRole)
				guild.removeRoleFromMember(userId, role).queue(null, fail -> {
					channel.sendMessageEmbeds(failEmbed).queue(del -> {
						del.delete().queueAfter(5, TimeUnit.SECONDS);
					});
				});
		});
	}
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(this.messages, FILE);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject(FILE);
		if(o != null)
			this.messages = (HashMap<Long, ReactionRole[]>)o;
	}
}
