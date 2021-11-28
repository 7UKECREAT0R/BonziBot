package com.lukecreator.BonziBot.Gui;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.DropdownItem;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiButton.ButtonColor;
import com.lukecreator.BonziBot.GuiAPI.GuiDropdown;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Script.Model.InvocationCommand;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptPackage;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Allows you to choose a script inside a package to edit.
 * @author Lukec
 */
public class GuiScriptChooser extends Gui {
	
	public ScriptPackage thePackage;
	public GuiDropdown scriptChooser;
	GuiScriptPackages previous;
	
	public GuiScriptChooser(ScriptPackage pkg, GuiScriptPackages previous) {
		this.scriptChooser = new GuiDropdown("Choose a Script...", "script", false);
		this.previous = previous;
		this.thePackage = pkg;
	}
	public void resetDropdown() {
		int size = this.thePackage.size();
		DropdownItem[] items = new DropdownItem[size];
		
		for(int i = 0; i < size; i++) {
			Script script = this.thePackage.get(i);
			items[i] = new DropdownItem(script, script.name);
		}
		
		this.scriptChooser.setItems(items);
	}
	
	@Override
	public void initialize(JDA jda) {
		this.resetDropdown();
		this.reinitialize();
	}
	public void reinitialize() {
		this.elements.clear();
		
		this.elements.add(this.scriptChooser);
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("📂"), "back"));
		this.elements.add(new GuiButton("New Script", ButtonColor.GREEN, "new"));
		boolean allowAction = this.scriptChooser.anySelected();
		this.elements.add(new GuiButton("Open", ButtonColor.BLUE, "open").asEnabled(allowAction));
		this.elements.add(new GuiButton("Delete", ButtonColor.RED, "delete").asEnabled(allowAction));
		
		if(this.thePackage.isEnabled())
			this.elements.add(new GuiButton("Disable Package", ButtonColor.RED, "toggle"));
		else
			this.elements.add(new GuiButton("Enable Package", ButtonColor.GREEN, "toggle"));
	}
	
	@Override
	public Object draw(JDA jda) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(BonziUtils.COLOR_BONZI_PURPLE);
		eb.setTitle("All Scripts in " + this.thePackage.getName());
		if(this.thePackage.isEnabled())
			eb.setDescription("☑️ This package is enabled and all scripts are listening!");
		else
			eb.setDescription("🔳 This package is disabled.");
		
		int size = this.thePackage.size();
		for(int i = 0; i < size; i++) {
			Script script = this.thePackage.get(i);
			String desc = '`' + script.method.getAsExplanation() + "`\n*Created by " + script.author.getAsMention() + '*';
			if(this.scriptChooser.anySelected() && this.scriptChooser.getSelectedIndexes()[0] == i)
				desc = "➜ " + desc;
			eb.addField("📜 " + script.name, desc, false);
		}
		
		return eb.build();
	}
	
	@Override
	public void onButtonClick(String buttonId, long clickerId, JDA jda) {
		
		MessageChannel channel = this.parent.getChannel(jda);
		
		if(buttonId.equals("back")) {
			previous.initialize(jda); // reset selection box
			this.parent.setActiveGui(previous, jda);
			return;
		}
		if(buttonId.equals("new")) {
			StringArg nameArg = new StringArg("");
			MessageEmbed nameEmbed = BonziUtils.quickEmbed("Enter a name for the script...", "Max length is 32 characters!", Color.orange).build();
			final EventWaiterManager ewm = this.bonziReference.eventWaiter;
			
			channel.sendMessageEmbeds(nameEmbed).queue(msg -> {
				ewm.waitForArgument(this.parent.ownerId, nameArg, _str -> {
					String str = (String)_str;
					if(str.length() > 32)
						str = str.substring(0, 32);
					msg.delete().queue();
					Script script = new Script(jda.getUserById(this.parent.ownerId), str);
					GuiNewScript next = new GuiNewScript(script, this);
					this.parent.setActiveGui(next, jda);
					return;
				});
			});
			return;
		}
		if(buttonId.equals("toggle")) {
			this.thePackage.toggleEnabled();
			this.reinitialize();
			this.parent.redrawMessage(jda);
			return;
		}
		
		int selectedIndex = this.scriptChooser.getSelectedIndexes()[0];
		Script selected = (Script)this.scriptChooser.getSelectedObject();
		
		if(buttonId.equals("open")) {
			GuiScriptEditor editor = new GuiScriptEditor(this, selected);
			this.parent.setActiveGui(editor, jda);
			return;
		}
		
		if(buttonId.equals("delete")) {
			EventWaiterManager ewm = this.bonziReference.eventWaiter;
			ewm.getConfirmation(this.parent.ownerId, channel, "Are you sure you want to delete this?", b -> {
				if(!b)
					return;
				if(selected.method instanceof InvocationCommand) {
					Guild guild = jda.getGuildById(this.parent.guildId);
					guild.deleteCommandById(((InvocationCommand)selected.method)._commandId).queue();
				}
				this.thePackage.removeScript(selectedIndex);
				this.scriptChooser.removeItems();
				this.reinitialize();
				this.parent.redrawMessage(jda);
			});
			return;
		}
	}
	
	@Override
	public void onDropdownChanged(GuiDropdown dropdown, long clickerId, JDA jda) {
		this.reinitialize();
		this.parent.redrawMessage(jda);
	}
}
