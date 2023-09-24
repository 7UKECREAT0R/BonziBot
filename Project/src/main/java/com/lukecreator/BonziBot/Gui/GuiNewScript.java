package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.TimeSpan;
import com.lukecreator.BonziBot.CommandAPI.EnumArg;
import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.CommandAPI.TimeSpanArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.DropdownItem;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiButton.ButtonColor;
import com.lukecreator.BonziBot.GuiAPI.GuiDropdown;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;
import com.lukecreator.BonziBot.Managers.ScriptCache;
import com.lukecreator.BonziBot.Script.Editor.EditorCategories;
import com.lukecreator.BonziBot.Script.Model.InvocationButton;
import com.lukecreator.BonziBot.Script.Model.InvocationCommand;
import com.lukecreator.BonziBot.Script.Model.InvocationMethod;
import com.lukecreator.BonziBot.Script.Model.InvocationPhrase;
import com.lukecreator.BonziBot.Script.Model.InvocationTimed;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptStatementCollection;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class GuiNewScript extends Gui {
	
	class SetterField {
		String value;
		String name;
		boolean set;
		
		SetterField(String name) {
			this.name = name;
			this.set = false;
		}
		public void set(String value) {
			this.value = value;
			this.set = true;
		}
		@Override
		public String toString() {
			if(this.set)
				return '`' + this.value + "` " + this.name;
			else
				return "`❌` " + this.name;
		}
	}
	
	public static final int MAX_ARGS = 5;
	
	GuiScriptChooser previous;
	GuiDropdown methodDropdown = new GuiDropdown("Choose a run method...", "method", false);
	
	boolean cancelled;
	SetterField[] setFields;
	Script script;
	
	public boolean wasCancelled() {
		return this.cancelled;
	}
	String buildSetFields() {
		return BonziUtils.stringJoinArbitrary("\n", this.setFields);
	}
	boolean canFinish() {
		if(this.methodDropdown == null)
			return false;
		if(!this.methodDropdown.anySelected())
			return false;
		if(this.setFields == null)
			return false;
		
		boolean prop = true;
		for(SetterField b: this.setFields)
			prop &= b.set;
		
		return prop;
	}
	public GuiNewScript(Script current, GuiScriptChooser previous) {
		this.script = current;
		this.previous = previous;
	}
	
	@Override
	public void initialize(JDA jda) {
		this.methodDropdown.addItemsTransform(EditorCategories.getInvocationDescriptors(), descriptor -> {
			try {
				return new DropdownItem(descriptor.createNew(), descriptor.name).withDescription(descriptor.desc);
			} catch (InstantiationException e) {
				InternalLogger.printError(e);
				return null;
			} catch (IllegalAccessException e) {
				InternalLogger.printError(e);
				return null;
			} catch (IllegalArgumentException e) {
				InternalLogger.printError(e);
				return null;
			} catch (InvocationTargetException e) {
				InternalLogger.printError(e);
				return null;
			} catch (NoSuchMethodException e) {
				InternalLogger.printError(e);
				return null;
			} catch (SecurityException e) {
				InternalLogger.printError(e);
				return null;
			}
		});
		this.reinitialize();
	}
	public void reinitialize() {
		this.elements.clear();
		this.elements.add(new GuiButton("Cancel", ButtonColor.RED, "cancel"));
		this.elements.add(new GuiButton("Create", ButtonColor.GREEN, "create").asEnabled(this.canFinish()));
		this.elements.add(this.methodDropdown);
		
		if(!this.methodDropdown.anySelected())
			return;
		
		Object _selected = this.methodDropdown.getSelectedObject();
		InvocationMethod selected = (InvocationMethod)_selected;
		
		// Setup the needed GUI elements for each different invocation implementation.
		// COMMAND is a special one in that it has a dynamic element count (being the args).
		
		switch(selected.getImplementation()) {
		case BUTTON:
			this.elements.add(new GuiButton("Set Text", ButtonColor.BLUE, "button_text"));
			this.elements.add(new GuiButton("Set Color", ButtonColor.BLUE, "button_color"));
			break;
		case COMMAND:
			InvocationCommand cmd = (InvocationCommand)selected;
			this.elements.add(new GuiButton("Set Name", ButtonColor.BLUE, "command_name"));
			this.elements.add(new GuiButton("Set Description", ButtonColor.BLUE, "command_desc"));
			int size = cmd.argNames.size();
			if(size > 0) {
				for(int i = 0; i < size; i++)
					this.elements.add(new GuiButton("Set Arg" + (i + 1), ButtonColor.BLUE, "command_arg_" + i));
			}
			this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("➖"), "command_dec").asEnabled(size > 0));
			this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("➕"), "command_inc").asEnabled(size < MAX_ARGS));
			break;
		case PHRASE:
			this.elements.add(new GuiButton("Phrase", ButtonColor.BLUE, "phrase"));
			break;
		case TIMED:
			this.elements.add(new GuiButton("Interval", ButtonColor.BLUE, "timed_interval"));
			break;
		case JOIN:
		case LEAVE:
		default:
			break;
		}
	}
	
	@Override
	public Object draw(JDA jda) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(BonziUtils.COLOR_BONZI_PURPLE);
		eb.setTitle(this.script.name);
		eb.setDescription("Choose how this script gets run.");
		if(this.canFinish())
			eb.setFooter("Hit 'Create' to create your script!");
		else {
			if(this.methodDropdown.anySelected())
				eb.setFooter("Finish filling out the fields next.");
			else
				eb.setFooter("Choose a method first.");
		}
		
		if(this.setFields != null && this.setFields.length > 0)
			eb.addField("Fields to Set:", this.buildSetFields(), false);
		
		return eb.build();
	}
	
	@Override
	public void onButtonClick(String buttonId, long clickerId, JDA jda) {
		
		if(buttonId.equals("cancel")) {
			this.cancelled = true;
			this.parent.setActiveGui(this.previous, jda);
			return;
		}
		
		if(buttonId.equals("create")) {
			if(!this.canFinish())
				return; // if u did this u a hacker
			
			if(this.script.method instanceof InvocationCommand) {
				// Add to guild commands before registering into the cache.
				// This ensures it obtains a valid id from discord before being applied.
				Guild guild = jda.getGuildById(this.parent.guildId);
				CommandData upload = ((InvocationCommand)this.script.method).toDiscord();
				guild.upsertCommand(upload).queue(command -> {
					((InvocationCommand)this.script.method)._commandId = command.getIdLong();
					ScriptCache.register(this.parent.guildId, this.previous.thePackage, this.script);
				});
			} else {
				ScriptCache.register(this.parent.guildId, this.previous.thePackage, this.script);
			}
			
			this.script.code = new ScriptStatementCollection(this.script);
			this.previous.thePackage.addScript(this.script);
			this.previous.scriptChooser.addItem(new DropdownItem(this.script, this.script.name));
			
			this.previous.reinitialize();
			this.parent.setActiveGui(this.previous, jda);
			return;
		}
		
		MessageChannelUnion channel = this.parent.getChannel(jda);
		EventWaiterManager ewm = this.bonziReference.eventWaiter;
		long id = this.parent.ownerId;
		
		// Method Specific
		
		// BUTTON
		if(buttonId.equals("button_text")) {
			MessageEmbed me = BonziUtils.quickEmbed("Button Text",
				"Type the text that you want to go on the button.", Color.orange).build();
			channel.sendMessageEmbeds(me).queue(msg -> {
				ewm.waitForArgument(id, new StringArg(""), _str -> {
					String str = BonziUtils.stripText((String)_str);
					str = BonziUtils.cutOffString(str, Button.LABEL_MAX_LENGTH);
					msg.delete().queue();
					
					((InvocationButton)this.script.method).buttonText = str;
					
					this.setFields[0].set(str);
					
					this.reinitialize();
					this.parent.redrawMessage(jda);
				});
			});
			return;
		}
		if(buttonId.equals("button_color")) {
			MessageEmbed me = BonziUtils.quickEmbed("Button Color",
				"Enter the color you want this button to be in: `Blue`, `Green`, `Red`, or `Gray`", Color.orange).build();
			channel.sendMessageEmbeds(me).queue(msg -> {
				EnumArg arg = new EnumArg("", ButtonColor.class);
				ewm.waitForArgument(id, arg, _color -> {
					ButtonColor color = (ButtonColor)_color;
					msg.delete().queue();
					
					((InvocationButton)this.script.method).buttonColor = color;
					
					this.setFields[1].set(BonziUtils.titleString(color.toString()));
					
					this.reinitialize();
					this.parent.redrawMessage(jda);
				});
			});
			return;
		}
		
		// COMMAND
		if(buttonId.equals("command_name")) {
			MessageEmbed me = BonziUtils.quickEmbed("Command Name",
				"Type the name of the command to be input. /<command>", Color.orange).build();
			channel.sendMessageEmbeds(me).queue(msg -> {
				ewm.waitForArgument(id, new StringArg(""), _str -> {
					String str = BonziUtils.stripText((String)_str)
						.toLowerCase().replaceAll("\\s+", "-");
					if(str.length() > 24)
						str = str.substring(0, 24);
					msg.delete().queue();
					
					((InvocationCommand)this.script.method).commandName = str;
					
					this.setFields[0].set(str);
					
					this.reinitialize();
					this.parent.redrawMessage(jda);
				});
			});
		}
		if(buttonId.equals("command_desc")) {
			MessageEmbed me = BonziUtils.quickEmbed("Command Description",
				"Type the description that will show up for this command.", Color.orange).build();
			channel.sendMessageEmbeds(me).queue(msg -> {
				ewm.waitForArgument(id, new StringArg(""), _str -> {
					String str = ((String)_str).replaceAll("\\s+", " ");
					if(str.length() > 100)
						str = str.substring(0, 100);
					msg.delete().queue();
					
					((InvocationCommand)this.script.method).commandDescription = str;
					
					this.setFields[1].set(str);
					
					this.reinitialize();
					this.parent.redrawMessage(jda);
				});
			});
		}
		if(buttonId.startsWith("command_arg_")) {
			String _num = buttonId.substring(12);
			int num = Integer.parseInt(_num); // this is 0-based
			
			MessageEmbed me = BonziUtils.quickEmbed("Command Argument " + (num + 1),
				"Type the name of this argument.", Color.orange).build();
			channel.sendMessageEmbeds(me).queue(msg -> {
				ewm.waitForArgument(id, new StringArg(""), _str -> {
					String str = BonziUtils.stripText((String)_str)
						.toLowerCase().replaceAll("\\s+", "-");
					if(str.length() > 24)
						str = str.substring(0, 24);
					msg.delete().queue();
					
					((InvocationCommand)this.script.method).argNames.set(num, str);
					
					this.setFields[num + 2].set(str);
					
					this.reinitialize();
					this.parent.redrawMessage(jda);
				});
			});
		}
		if(buttonId.equals("command_inc")) {
			InvocationCommand cmd = (InvocationCommand)this.script.method;
			
			if(cmd.argNames.size() > 5)
				return;
			
			cmd.argNames.add("unset");
			
			// Resize setFields.
			int size = cmd.argNames.size();
			SetterField[] newFields = new SetterField[2 + size];
			newFields[0] = this.setFields[0];
			newFields[1] = this.setFields[1];
			for(int i = 0; i < size; i++) {
				if(i + 2 < this.setFields.length)
					newFields[i + 2] = this.setFields[i + 2];
				else
					newFields[i + 2] = new SetterField("Arg" + (i + 1));
			}
			
			this.setFields = newFields;
			this.reinitialize();
			this.parent.redrawMessage(jda);
		}
		if(buttonId.equals("command_dec")) {
			InvocationCommand cmd = (InvocationCommand)this.script.method;
			
			if(cmd.argNames.size() < 1)
				return;
			cmd.argNames.remove(cmd.argNames.size() - 1);
			
			// Resize setFields.
			int size = cmd.argNames.size();
			SetterField[] newFields = new SetterField[2 + size];
			newFields[0] = this.setFields[0];
			newFields[1] = this.setFields[1];
			for(int i = 0; i < size; i++) {
				newFields[i + 2] = this.setFields[i + 2];
			}
			
			this.setFields = newFields;
			this.reinitialize();
			this.parent.redrawMessage(jda);
		}
		
		// PHRASE
		if(buttonId.equals("phrase")) {
			MessageEmbed me = BonziUtils.quickEmbed("Phrase",
				"Type the phrase or word that you want to trigger this script.", Color.orange).build();
			channel.sendMessageEmbeds(me).queue(msg -> {
				ewm.waitForArgument(id, new StringArg(""), _str -> {
					String str = BonziUtils.stripText((String)_str);
					msg.delete().queue();
					
					((InvocationPhrase)this.script.method).setPhrase(str);
					
					this.setFields[0].set(str);
					
					this.reinitialize();
					this.parent.redrawMessage(jda);
				});
			});
			return;
		}
		
		// TIMED
		if(buttonId.equals("timed_interval")) {
			MessageEmbed me = BonziUtils.quickEmbed("Interval",
				"Type the phrase or word that you want to trigger this script.", Color.orange).build();
			channel.sendMessageEmbeds(me).queue(msg -> {
				ewm.waitForArgument(id, new TimeSpanArg(""), _timespan -> {
					TimeSpan timespan = (TimeSpan)_timespan;
					msg.delete().queue();
					
					if(timespan.ms < ScriptCache.DELAY_MINIMUM) {
						BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("Interval must be an hour or more.", "Cancelled operation."), 5);
						return;
					}
					
					((InvocationTimed)this.script.method).time = timespan;
					
					this.setFields[0].set(timespan.toLongString());
					
					this.reinitialize();
					this.parent.redrawMessage(jda);
				});
			});
			return;
		}
	}
	@Override
	public void onDropdownChanged(GuiDropdown dropdown, long clickerId, JDA jda) {
		Object _selected = dropdown.getSelectedObject();
		InvocationMethod selected = (InvocationMethod)_selected;
		this.script.method = selected;
		
		switch(selected.getImplementation()) {
		case BUTTON:
			this.setFields = new SetterField[2];
			this.setFields[0] = new SetterField("Button Text");
			this.setFields[1] = new SetterField("Button Color");
			break;
		case COMMAND:
			InvocationCommand cmd = (InvocationCommand)selected;
			int size = cmd.argNames.size();
			this.setFields = new SetterField[2 + size];
			this.setFields[0] = new SetterField("Name");
			this.setFields[1] = new SetterField("Description");
			for(int i = 0; i < size; i++)
				this.setFields[i + 2] = new SetterField("Arg" + (i + 1));
			break;
		case PHRASE:
			this.setFields = new SetterField[1];
			this.setFields[0] = new SetterField("Phrase");
			break;
		case TIMED:
			this.setFields = new SetterField[1];
			this.setFields[0] = new SetterField("Interval");
			break;
		case JOIN:
		case LEAVE:
			this.setFields = new SetterField[0];
			break;
		default:
			break;
		}
		
		this.reinitialize();
		this.parent.redrawMessage(jda);
	}
}
