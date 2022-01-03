package com.lukecreator.BonziBot.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.TimeSpan;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.StringRemainderArg;
import com.lukecreator.BonziBot.CommandAPI.TimeSpanArg;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Logging.LogEntryTempBan;
import com.lukecreator.BonziBot.Logging.LogEntryTempBan.TempBanDataPacket;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class BanCommand extends Command {

	public BanCommand() {
		this.subCategory = 0;
		this.name = "Ban";
		this.unicodeIcon = "🔨";
		this.description = "Ban a user. This allows the user to appeal as well (if enabled).";
		this.args = new CommandArgCollection(new UserArg("target"),
			new TimeSpanArg("time").optional(),
			new StringRemainderArg("reason").optional());
		this.neededPermissions = new Permission[] { Permission.BAN_MEMBERS };
		this.userRequiredPermissions = new Permission[] { Permission.BAN_MEMBERS };
		this.setCooldown(3000);
		this.worksInDms = false;
		this.category = CommandCategory.MODERATION;
		this.subCategory = 0;
	}

	@Override
	public void run(CommandExecutionInfo e) {
		User _target = e.args.getUser("target");
		Member target = e.guild.getMember(_target);
		Member self = e.member;
		Member bonzi = e.guild.getSelfMember();
		
		String reason = e.args.getString("reason");
		TimeSpan time = e.args.getTimeSpan("time");
		boolean temporary = time != null;
		String guildName = e.guild.getName();
		long guildId = e.guild.getIdLong();
		
		if(!self.canInteract(target)) {
			MessageEmbed send = BonziUtils.failureEmbed("You can't ban this user!",
				"This user is either an administrator, owner, or higher up than you.");
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(send).queue();
			else
				e.channel.sendMessageEmbeds(send).queue();
			return;
		}
		if(!bonzi.canInteract(target)) {
			MessageEmbed send = BonziUtils.failureEmbed("help",
				"This person is either an administrator, owner, or higher up than me so I can't ban them.");
			if(e.isSlashCommand)
				e.slashCommand.replyEmbeds(send).queue();
			else
				e.channel.sendMessageEmbeds(send).queue();
			return;
		}
		
		UserAccount profile = e.bonzi
			.accounts.getUserAccount(_target);
		GuildSettings settings = e.settings;
		
		Consumer<? super Object> readyToBan = obj -> {
			long tId = _target.getIdLong();
			if(settings.banAppeals)
				e.bonzi.appeals.mercy(guildId, tId);
			if(temporary) {
				long ends = System.currentTimeMillis() + time.ms;
				e.bonzi.bans.ban(guildId, tId, ends);
				
				long bannerId = e.executor.getIdLong();
				LogEntryTempBan tempBan = new LogEntryTempBan();
				
				TempBanDataPacket packet = tempBan.new TempBanDataPacket(bannerId, tId, time);
				tempBan.loadData(packet, e.bonzi, entry -> {
					e.bonzi.logging.tryLog(e.guild, e.bonzi, entry);
				}, null);
			}
			try {
				e.guild.ban(target, 1, reason).queue(null, fail -> {
					e.bonzi.bans.unban(target);
					if(e.isSlashCommand)
						e.slashCommand.getHook().editOriginal("Something went seriously wrong and I couldn't ban the user... Maybe this will help:\n```" + fail.toString() + "```").queue();
					else
						e.channel.sendMessage("Something went seriously wrong and I couldn't ban the user... Maybe this will help:\n```" + fail.toString() + "```").queue();
					return;
				});
			} catch(HierarchyException exc) {
				if(e.isSlashCommand)
					e.slashCommand.getHook().editOriginalEmbeds(BonziUtils.failureEmbed("Hierarchy Error", "This user is too high for me to ban!")).queue();
				else
					e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("Hierarchy Error", "This user is too high for me to ban!")).queue();
			}
			
			String desc = "For: `" + (temporary?time.toLongString():"forever") +
				"`\nReason: `" + (reason==null?"unspecified":reason) + "`";
			
			MessageEmbed send = BonziUtils.successEmbed("Banned!", desc);
			
			if(e.isSlashCommand)
				e.slashCommand.getHook().editOriginalEmbeds(send).queue();
			else
				e.channel.sendMessageEmbeds(send).queue();
		};
		
		if(!profile.optOutDms && (settings.banAppeals | settings.banMessage)) {
			PrivateChannel _pc = BonziUtils.getCachedPrivateChannel(_target);
			
			Consumer<? super PrivateChannel> dm = pc -> {
				BonziUtils.userPrivateChannels.put(pc.getUser().getIdLong(), pc.getIdLong());
				
				String desc;
				EmbedBuilder eb = new EmbedBuilder()
						.setColor(BonziUtils.COLOR_BONZI_PURPLE)
						.setTitle("You've been banned from " + guildName);
						
				if(settings.banMessage) {
					String banMsg = settings.banMessageString;
					desc = banMsg
						.replace("{name}", _target.getName())
						.replace("{server}", guildName)
						.replace("{reason}", reason==null?"unspecified":reason)
						.replace("{time}", temporary?time.toLongString():"forever");
					
					List<String> allUrls = new ArrayList<String>();
					Matcher matcher = Constants.IMAGE_URL_REGEX_COMPILED.matcher(banMsg);
					while(matcher.find())
						allUrls.add(matcher.group());
					if(!allUrls.isEmpty()) {
						String firstImage = allUrls.get(0);
						eb.setImage(firstImage);
					}
				} else {
					if(temporary) {
						desc = settings.banAppeals ? "This ban is temporary and also appeal-able." : "This ban is temporary and not appeal-able.";
						desc += "\nTime: `" + time.toLongString() + "`";
					} else
						desc = settings.banAppeals ? "This ban is appeal-able." : "This ban is not appeal-able.";
				}
				
				if(!settings.banMessage && reason != null)
					desc += "\nReason: `" + reason + "`";
				
				eb.setDescription(desc);
				
				GuiButton[] buttons = {
					(GuiButton)new GuiButton("Appeal Ban", GuiButton.ButtonColor.BLUE,
						"_appeal:request:" + guildId).asEnabled(settings.banAppeals)
				};
				
				if(e.isSlashCommand)
					e.slashCommand.deferReply().queue();
				
				BonziUtils.appendComponents(pc.sendMessageEmbeds(eb.build()), buttons, false).queue(readyToBan);
			};
			
			if(_pc == null)
				_target.openPrivateChannel().queue(dm);
			else
				dm.accept(_pc);
		} else {
			if(e.isSlashCommand)
				e.slashCommand.deferReply().queue();
			// User is not accepting DMs so the ban can commence immediately.
			readyToBan.accept(null);
		}
	}
}