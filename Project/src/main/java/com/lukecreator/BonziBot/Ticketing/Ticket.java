package com.lukecreator.BonziBot.Ticketing;

import java.io.Serializable;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.TimeSpan;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

/**
 * A ticket created by a user.
 * @author Lukec
 */
public class Ticket implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public transient TicketGuild guild;
	
	public long createdAt;	// Time this ticket was created, in milliseconds.
	public long closedAt;	// Time this ticket was closed, if at all.
	
	public long ticketId = 0l;	// ID of this ticket.
	public long ownerId = 0l;	// ID of owning user. (OP)
	public long closerId = 0l;	// ID of the staff that closed the ticket.
	public long claimId = 0l;	// ID of the staff that claimed, if any.
	public long channelId = 0l;	// ID of the channel this ticket takes place in.
	
	boolean isClaimed = false;	// Ticket is claimed by a staff member?
	boolean open = true;		// Ticket is currently open?
	
	public boolean isOpen() {
		return this.open;
	}
	public boolean isClaimed() {
		return this.isClaimed;
	}
	public void claim(User staff) {
		this.claim(staff.getIdLong());
	}
	public void claim(long staffId) {
		this.claimId = staffId;
		this.isClaimed = true;
	}
	
	static MessageEmbed closeTicketEmbedUser(TicketGuild tg, CloseReason reason, String message, boolean blacklist) {
		String ticketName = tg.getTicketName();
		String title = BonziUtils.titleString(ticketName) + " Closed";
		String status = "Reason: " + BonziUtils.titleString(reason.name());
		
		EmbedBuilder eb = BonziUtils.quickEmbed(title, status, BonziUtils.COLOR_BONZI_PURPLE);
		if(message != null && !BonziUtils.isWhitespace(message)) 
			eb.addField("Message from Staff", message, false);
		
		if(blacklist) {
			String ticketPlural = BonziUtils.pluralForm(ticketName.toLowerCase());
			eb.addField("", "```diff\n- You have been blacklisted from creating further " + ticketPlural + ".", false);
		}
		
		eb.setFooter(reason.description);
		return eb.build();
	}
	static MessageEmbed closeTicketEmbedStatistic(Ticket ticket, CloseReason reason, String message, boolean blacklist) {
		String ticketName = ticket.guild.getTicketName();
		String title = BonziUtils.titleString(ticketName) + " @" + ticket.ticketId;
		String staffMessage = message == null ? "<none specified>" : message;
		
		EmbedBuilder eb = BonziUtils.quickEmbed(title, "", BonziUtils.COLOR_BONZI_PURPLE);
		eb.addField("❔ Close Reason", BonziUtils.titleString(reason.name()), true);
		eb.addField("💬 Written Message", staffMessage, true);
		eb.addField("👥 Involved Users", "Creator: <@" + ticket.ownerId + 
				">\nClosed by: <@" + ticket.closerId + '>', true);
		
		long diff = ticket.closedAt - ticket.createdAt;
		TimeSpan diffSpan = TimeSpan.fromMillis(diff);
		eb.addField("⏰ Time Open", diffSpan.toLongString(), true);
		
		if(blacklist)
			eb.addField("❌ Blacklisted", "User has been blacklisted from creating further tickets.", true);
		
		return eb.build();
	}
	
	/**
	 * Send the dashboard message in this channel.
	 * @param channel Channel to be sent in.
	 * @param opener The user that opened this ticket.
	 * @return MessageAction to be queued.
	 */
	public MessageCreateAction sendDashboardMessage(TextChannel channel, Member opener) {
		String ticketName = BonziUtils.titleString(this.guild.getTicketName());
		String fullName = BonziUtils.fullName(opener.getUser());
		String title = fullName + "'s " + ticketName;
		
		String description = this.guild.getTicketDescription();
		EmbedBuilder eb = BonziUtils.quickEmbed(title, description, opener);
		return channel.sendMessageEmbeds(eb.build());
	}
	/**
	 * Close this ticket and perform all of the proper close actions. Manual invocation.
	 * @param guild The guild this ticket is located in.
	 * @param closer The user ID that closed the ticket.
	 * @param reason The generic reason this ticket was closed.
	 * @param message The message from the moderator given.
	 * @param blacklist Whether to blacklist this user from making tickets.
	 */
	public void close(Guild guild, long closer, CloseReason reason, String message, boolean blacklist) {
		
		this.open = false;
		this.closerId = closer;
		this.closedAt = System.currentTimeMillis();
		
		TextChannel ticketChannel = guild.getTextChannelById(this.channelId);
		if(ticketChannel != null)
			ticketChannel.delete().reason("Ticket closed.").queue();
		
		User creator = guild.getMemberById(this.ownerId).getUser();
		BonziUtils.messageUser(creator, closeTicketEmbedUser(this.guild, reason, message, blacklist));
		
		TextChannel transcriptsChannel = guild.getTextChannelById(this.guild.transcriptsChannel);
		if(transcriptsChannel != null) {
			MessageEmbed stat = closeTicketEmbedStatistic(this, reason, message, blacklist);
			String transcriptProtocol = TicketProtocol.createProtocol(TicketProtocol.Action.TRANSCRIPT, this.ticketId);
			Button transcriptButton = Button.secondary(transcriptProtocol, "Get Transcript");
			transcriptsChannel.sendMessageEmbeds(stat)
				.setActionRow(transcriptButton).queue();
		}
		
		
	}
}
