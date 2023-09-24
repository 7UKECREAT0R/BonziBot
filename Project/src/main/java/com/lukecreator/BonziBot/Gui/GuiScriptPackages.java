package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.List;
import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.DropdownItem;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiButton.ButtonColor;
import com.lukecreator.BonziBot.GuiAPI.GuiDropdown;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.ScriptCache;
import com.lukecreator.BonziBot.Script.Model.InvocationCommand;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptPackage;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class GuiScriptPackages extends Gui {
	
	List<ScriptPackage> packages;
	GuiDropdown packageChooser;
	
	public GuiScriptPackages(List<ScriptPackage> packages) {
		this.packages = packages;
	}
	
	DropdownItem fromPackage(ScriptPackage pkg) {
		return new DropdownItem(pkg, pkg.getName())
			.withEmoji(GenericEmoji.fromEmoji(pkg.isEnabled() ? "☑️" : "🔳"));
	}
	
	@Override
	public void initialize(JDA jda) {
		this.packageChooser = new GuiDropdown("Choose Package...", "pkg", false);
		for(ScriptPackage pkg: this.packages) {
			this.packageChooser.addItem(this.fromPackage(pkg));
		}
		this.reinitialize();
	}
	public void reinitialize() {
		this.elements.clear();
		boolean enable = this.packageChooser.anySelected();
		this.elements.add(this.packageChooser);
		this.elements.add(new GuiButton("Create", ButtonColor.BLUE, "create"));
		this.elements.add(new GuiButton("Open", ButtonColor.BLUE, "open").asEnabled(enable));
		this.elements.add(new GuiButton("Delete", ButtonColor.RED, "delete").asEnabled(enable));
		this.elements.add(new GuiButton("Rename", ButtonColor.GRAY, "rename").asEnabled(enable));
	}
	
	@Override
	public Object draw(JDA jda) {
		boolean anySelected = this.packageChooser.anySelected();
		
		if(anySelected) {
			ScriptPackage pkg = (ScriptPackage)this.packageChooser.getSelectedObject();
			String name = pkg.getName();
			String s_name = BonziUtils.plural(" Script", pkg.size());
			String desc = pkg.isEnabled() ?
				"☑️ This package is `ENABLED` and contains " + pkg.size() + s_name:
				"🔳 This package is `DISABLED`, but contains " + pkg.size() + s_name;
			String footer = "To begin editing scripts, click 'Open'!";
			return new EmbedBuilder()
				.setTitle(name)
				.setDescription(desc)
				.setFooter(footer)
				.build();
		} else {
			return new EmbedBuilder()
				.setTitle("Select a Package")
				.setDescription("...or create one!")
				.build();
		}
	}
	
	@Override
	public void onButtonClick(String buttonId, long clickerId, JDA jda) {
		
		MessageChannelUnion channel = this.parent.getChannel(jda);
		
		if(buttonId.equals("create")) {
			if(this.packages.size() >= ScriptPackage.MAX_PACKAGES) {
				BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("Max number of packages reached."), 3);
				return;
			}
			MessageEmbed me = new EmbedBuilder()
				.setTitle("Enter a name for your package!")
				.setColor(Color.orange)
				.build();
			channel.sendMessageEmbeds(me).queue(msg -> {
				EventWaiterManager ewm = this.bonziReference.eventWaiter;
				ewm.waitForArgument(this.parent.ownerId, new StringArg(""), _str -> {
					msg.delete().queue();
					String str = (String)_str;
					if(str.length() > ScriptPackage.MAX_LENGTH_PACKAGE_NAME)
						str = str.substring(0, ScriptPackage.MAX_LENGTH_PACKAGE_NAME);
					if(!ScriptPackage.checkPackageName(str)) {
						BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed(
							"Invalid Package Name", "Make sure to avoid non-standard characters!"), 5);
						return;
					}
					ScriptPackage newPkg = new ScriptPackage(str);
					this.packages.add(newPkg);
					this.packageChooser.addItem(this.fromPackage(newPkg));
					this.parent.redrawMessage(jda);
					return;
				});
			});
			return;
		}
		
		if(!this.packageChooser.anySelected()) {
			channel.sendMessageEmbeds(BonziUtils.failureEmbed("No package selected...")).queue();
			return;
		}
		
		ScriptPackage pkg = (ScriptPackage)this.packageChooser.getSelectedObject();
		
		if(buttonId.equals("open")) {
			GuiScriptChooser chooser = new GuiScriptChooser(pkg, this);
			this.parent.setActiveGui(chooser, jda);
			return;
		}
		
		if(buttonId.equals("delete")) {
			Consumer<Boolean> afterConfirmation = b -> {
				if(b) {
					// delete slash commands and unregister from cache
					for(Script script: pkg.getScripts()) {
						ScriptCache.unregister(this.parent.guildId, pkg, script);
						if(script.method instanceof InvocationCommand) {
							Guild guild = jda.getGuildById(this.parent.guildId);
							guild.deleteCommandById(((InvocationCommand)script.method)._commandId).queue();
						}
					}
					
					// delete package (execution by gc lol)
					int index = this.packageChooser.getSelectedIndexes()[0];
					this.packages.remove(index);
					this.packageChooser.removeItemAtIndex(index);
					BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("Deleted package '" + pkg.getName() + "'!"), 4);
					
					this.reinitialize();
					this.parent.redrawMessage(jda);
				} else {
					BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("Cancelled deletion."), 3);
				}
			};
			
			if(pkg.size() > 0) {
				MessageEmbed embed = BonziUtils.quickEmbed("Are you really sure you want to do this?",
					"This package will be deleted with all of its scripts, and cannot be recovered.", Color.orange).build();
				this.bonziReference.eventWaiter.getConfirmation(this.parent.ownerId, channel, embed, afterConfirmation);
			} else {
				afterConfirmation.accept(true);
			}
		}
		
		if(buttonId.equals("rename")) {
			MessageEmbed me = new EmbedBuilder()
				.setTitle("Enter new package name...")
				.setColor(Color.orange)
				.build();
			channel.sendMessageEmbeds(me).queue(msg -> {
				EventWaiterManager ewm = this.bonziReference.eventWaiter;
				ewm.waitForArgument(this.parent.ownerId, new StringArg(""), _str -> {
					msg.delete().queue();
					String str = (String)_str;
					if(str.length() > ScriptPackage.MAX_LENGTH_PACKAGE_NAME)
						str = str.substring(0, ScriptPackage.MAX_LENGTH_PACKAGE_NAME);
					if(!ScriptPackage.checkPackageName(str)) {
						BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed(
							"Invalid Package Name", "Make sure to avoid non-standard characters!"), 5);
						return;
					}
					pkg.rename(str);
					this.initialize(jda);
					this.parent.redrawMessage(jda);
					return;
				});
			});
		}
	}
	
	@Override
	public void onDropdownChanged(GuiDropdown dropdown, long clickerId, JDA jda) {
		this.reinitialize();
		this.parent.redrawMessage(jda);
	}
}
