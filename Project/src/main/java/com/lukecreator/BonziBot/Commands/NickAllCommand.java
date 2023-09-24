package com.lukecreator.BonziBot.Commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.TimeSpan;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArg;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.RoleArg;
import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.PremiumItem;
import com.lukecreator.BonziBot.Data.UsernameGenerator;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class NickAllCommand extends Command {
	
	// List of guilds that are in the process of nicknaming users.
	protected static List<Long> STILL_NICKNAMING = new ArrayList<Long>();
	
	class Condition {
		protected CommandArg arg;
		
		CommandArg getArg() {
			return this.arg;
		}
		void setArg(CommandArg arg) {
			this.arg = arg;
		}
		boolean memberFits(Member m) {
			return false;
		}
	}
	class AllCondition extends Condition {
		
		AllCondition() {
			this.arg = new StringArg("");
		}
		
		@Override
		boolean memberFits(Member m) {
			return true;
		}
	}
	class RoleCondition extends Condition {
		
		RoleCondition() {
			this.arg = new RoleArg("");
		}
		
		@Override
		boolean memberFits(Member m) {
			Role baseRole = (Role)this.arg.object;
			for(Role r: m.getRoles()) {
				if(r.getIdLong() == baseRole.getIdLong()) {
					return true;
				}
			}
			return false;
		}
	}
	class UnpingableCondition extends Condition {
		
		UnpingableCondition() {
			this.arg = new StringArg("");
		}
		
		@Override
		boolean memberFits(Member m) {
			String name = m.getEffectiveName();
			return BonziUtils.isUnpingable(name);
		}
	}
	class ContainsCondition extends Condition {
		
		ContainsCondition() {
			this.arg = new StringArg("");
		}
		
		@Override
		boolean memberFits(Member m) {
			String text = (String)this.arg.object;
			String name = m.getEffectiveName();
			return name.toLowerCase().contains(text.toLowerCase());
		}
	}
	
	public NickAllCommand() {
		this.subCategory = 0;
		this.name = "Nick All";
		this.icon = GenericEmoji.fromEmoji("🖋️");
		this.description = "Rename everyone in the server! You can also specify a condition to select the users to rename.";
		this.args = null;
		this.userRequiredPermissions = new Permission[] { Permission.NICKNAME_MANAGE };
		this.category = CommandCategory._SHOP_COMMAND;
		this.worksInDms = false;
		this.neededPermissions = new Permission[] { Permission.NICKNAME_MANAGE, Permission.NICKNAME_CHANGE };
		this.setCooldown(30000);
		this.setPremiumItem(PremiumItem.NICK_ALL);
	}

	@Override
	public void run(CommandExecutionInfo e) {
		
		if(STILL_NICKNAMING.contains(e.guild.getIdLong())) {
			e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("Please wait until I'm finished nicknaming my current batch!")).queue();
			this.resetCooldown(e);
			return;
		}
		
		String prefix = BonziUtils.getPrefixOrDefault(e);
		
		EmbedBuilder eb = BonziUtils.quickEmbed("NickAll Conditions",
			"You can use conditions to select which users to nickname. Seperate with a comma!", BonziUtils.COLOR_BONZI_PURPLE);
		eb.addField("all", "Select all users.", true);
		eb.addField("role: <id>", "Select every user with a certain role. Check `" + prefix + "howtoid`!", true);
		eb.addField("unpingable", "Select users with unpingable names.", true);
		eb.addField("contains: <text>", "Select all users where their names contain certain text.", true);
		
		if(e.isSlashCommand) {
			e.slashCommand.replyEmbeds(eb.build()).setEphemeral(true).queue();
		} else {
			e.channel.sendMessageEmbeds(eb.build()).queue();
		}
		e.channel.sendMessageEmbeds(BonziUtils.successEmbedIncomplete("Send the condition to apply.").setFooter("Type 'all' to pick all members.\nType 'cancel' to cancel this action.").build()).queue();
		
		EventWaiterManager ewm = e.bonzi.eventWaiter;
		ewm.waitForResponse(e.executor, msg -> {
			String conditionString = msg.getContentRaw();
			
			if(conditionString.equalsIgnoreCase("cancel")) {
				e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("Cancelled.")).queue();
				this.resetCooldown(e);
				return;
			}
			
			String[] parts = conditionString.split(",");
			Condition[] conditions = new Condition[parts.length];
			
			for(int i = 0; i < parts.length; i++) {
				String part = parts[i].trim();
				Condition c = null;
				if(!part.contains(":")) {
					if(part.equalsIgnoreCase("all"))
						c = new AllCondition();
					if(part.equalsIgnoreCase("unpingable"))
						c = new UnpingableCondition();
				} else {
					String[] halves = part.split(":");
					String conditionName = halves[0].trim();
					String conditionData = halves[1].trim();
					if(conditionName.equalsIgnoreCase("role")) {
						c = new RoleCondition();
						Guild g = e.isGuildMessage ? e.guild : null;
						if(!c.getArg().isWordParsable(conditionData, g)) {
							e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("Invalid role specified.")).queue();
							this.resetCooldown(e);
							return;
						}
						CommandArg a = c.getArg();
						a.parseWord(conditionData, msg.getJDA(), e.executor, null);
						c.setArg(a);
					} else if(conditionName.equalsIgnoreCase("contains")) {
						c = new ContainsCondition();
						CommandArg a = c.getArg();
						a.object = conditionData;
					}
				}
				
				if(c == null) {
					e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("No valid conditions were specified.")).queue();
					this.resetCooldown(e);
					return;
				}
				
				conditions[i] = c;
			}
			
			List<Member> members = msg.getGuild().getMembers();
			List<Member> matchedMembers = new ArrayList<Member>();
			
			for(Member m: members) {
				if(m.getUser().isBot())
					continue;
				boolean good = true;
				for(Condition c: conditions) {
					if(!c.memberFits(m)) {
						good = false;
						break;
					}
				}
				if(good)
					matchedMembers.add(m);
			}
			
			if(matchedMembers.isEmpty()) {
				e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("Couldn't find anyone that matches that condition!", "Try narrowing your search a bit.")).queue();
				this.resetCooldown(e);
				return;
			}
			
			EmbedBuilder eb1 = new EmbedBuilder();
			int matches = matchedMembers.size();
			String memberString = BonziUtils.plural("member", matches);
			eb1.setColor(Color.cyan)
				.setTitle("Found " + matches + " " + memberString + " to rename.")
				.setDescription("Type what you want to rename them to. `You can mix and match with the variables listed here to change the nickname between members!`")
				.setFooter("You'll have a chance to confirm.");
			
			eb1.addField("{username}", "The user's actual username.", true);
			eb1.addField("{username.uwu}", "The user's username in uwu style...?", true);
			eb1.addField("{username.upper}", "The user's username in uppercase.", true);
			eb1.addField("{nickname}", "The user's old nickname.", true);
			eb1.addField("{index}", "The current index of the user. Starts at 1, goes up each user modified.", true);
			eb1.addField("{membercount}", "The amount of members in the server.", true);
			eb1.addField("{highestrole}", "The name of the highest role this user has.", true);
			eb1.addField("{nothing}", "No nickname.", true);
			eb1.addField("{random}", "A randomly generated nickname.", true);
			e.channel.sendMessageEmbeds(eb1.build()).queue();
			
			ewm.waitForResponse(msg.getAuthor(), nicknameMsg -> {
				
				String nicknameRaw = nicknameMsg.getContentRaw();
				long millis = matches * 1000l;
				TimeSpan _time = TimeSpan.fromMillis(millis);
				String time = _time.toLongString();
				
				EmbedBuilder eb2 = BonziUtils.quickEmbed("About to rename " + matches + " " + memberString + "...",
					"This will take about " + time + ".\n"
				  + "Nickname: `" + BonziUtils.cutOffString(nicknameRaw, 128) + "`\n"
				  + "\nReact to commence the nicknaming!", Color.orange);
				
				ewm.getConfirmation(nicknameMsg.getAuthor(), nicknameMsg.getChannel(), eb2.build(), confirm -> {
					
					if(!confirm) {
						e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("Cancelled Nickall.")).queue();
						this.resetCooldown(e);
						return;
					}
					
					e.channel.sendMessageEmbeds(BonziUtils.successEmbed("Starting to nickname!", "Please wait about " + time + "...")).queue();
					
					long guildId = e.guild.getIdLong();
					STILL_NICKNAMING.add(guildId);
					
					int i = 0;
					int totalMembers = e.guild.getMemberCount();
					String memberCount = BonziUtils.comma(totalMembers);
					for(Member iteration: matchedMembers) {
						i++;
						String username = iteration.getUser().getName();
						String oldNick = iteration.getEffectiveName();
						String index = String.valueOf(i);
						
						String highestRole = "NONE";
						if(!iteration.getRoles().isEmpty())
							highestRole = iteration.getRoles().get(0).getName();
						
						String vars = nicknameRaw
							.replace("{username}", username)
							.replace("{username.uwu}", BonziUtils.uwu(username))
							.replace("{username.upper}", username.toUpperCase())
							.replace("{nickname}", oldNick)
							.replace("{index}", index)
							.replace("{membercount}", memberCount)
							.replace("{highestrole}", highestRole)
							.replace("{nothing}", "");
						
						if(vars.contains("{random}"))
							vars = vars.replace("{random}", UsernameGenerator.generate());
						
						if(vars.length() > 32)
							vars = vars.substring(0, 32);
						
						if (i == matches) {
							try {
								iteration.modifyNickname(vars).queue(_v -> {
									NickAllCommand.STILL_NICKNAMING.remove(guildId);
									e.channel.sendMessageEmbeds(BonziUtils.successEmbed("Finished nicknaming!")).queue();
								}, f -> {});
							} catch(HierarchyException exc) {
								NickAllCommand.STILL_NICKNAMING.remove(guildId);
								e.channel.sendMessageEmbeds(BonziUtils.successEmbed("Finished nicknaming!")).queue();
							}
						} else {
							try {
								iteration.modifyNickname(vars).queue();
							} catch(HierarchyException exc) {}
						}
					}
				});
			});
		});
	}
}