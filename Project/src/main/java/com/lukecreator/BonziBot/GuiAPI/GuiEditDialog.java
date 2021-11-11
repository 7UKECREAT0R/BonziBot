package com.lukecreator.BonziBot.GuiAPI;

import java.awt.Color;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * GUI which allows dynamic building of a set of editable fields, all wrapping the arg parsing API.
 * @author Lukec
 */
public class GuiEditDialog extends Gui {
	
	public class Output {
		public final boolean wasCancelled;	// If this dialog was cancelled.
		public final boolean[] wasSet;		// Array describing which fields were set.
		public final String[] stringValues;	// Implementation-specific string representations of values.
		public final Object[] values;		// Output values.
		
		public Output(boolean cancelled, GuiEditDialog dialog) {
			this.wasCancelled = cancelled;
			this.wasSet = dialog.setFields;
			
			int len = dialog.fields.length;
			this.stringValues = new String[len];
			this.values = new Object[len];
			for(int i = 0; i < len; i++) {
				GuiEditEntry entry = dialog.fields[i];
				this.values[i] = entry.getValue();
				this.stringValues[i] = entry.getStringValue();
			}
		}
	}
	private Consumer<Output> onClosed = null;
	private Gui returnTo;
	
	final boolean[] setFields;
	final GuiEditEntry[] fields;
	
	final String title;
	final String description;
	
	public GuiEditDialog(@Nullable Gui returnTo, String title, String description, @Nonnull GuiEditEntry...entries) {
		this.returnTo = returnTo;
		this.title = title;
		this.description = description;
		this.fields = entries;
		this.setFields = new boolean[entries.length];
		for(int i = 0; i < entries.length; i++)
			setFields[i] = false;
	}
	public GuiEditDialog(@Nullable Gui returnTo, String title, @Nonnull GuiEditEntry...entries) {
		this.returnTo = returnTo;
		this.title = title;
		this.description = null;
		this.fields = entries;
		this.setFields = new boolean[entries.length];
		for(int i = 0; i < entries.length; i++)
			setFields[i] = false;
	}
	public GuiEditDialog after(Consumer<Output> action) {
		this.onClosed = action;
		return this;
	}
	
	@Override
	public void initialize(JDA jda) {
		this.reinitialize();
	}
	public void reinitialize() {
		this.elements.clear();
		
		boolean submittable = true;
		int i = 0;
		for(boolean b: this.setFields)
			if(!this.fields[i++].optional && !b) {
				submittable = false;
				break;
			}
		
		for(GuiEditEntry entry: this.fields) {
			String text = entry.title;
			String actionId = entry.getActionID();
			
			if(entry instanceof GuiEditEntryText) {
				GuiButton.ButtonColor color = GuiButton.ButtonColor.GRAY;
				if(entry.emoji != null)
					this.elements.add(new GuiButton(GenericEmoji.fromEmoji(entry.emoji), text, color, actionId));
				else
					this.elements.add(new GuiButton(text, color, actionId));
			} else if(entry instanceof GuiEditEntryChoice){
				GuiDropdown toAdd = ((GuiEditEntryChoice)entry).getDropdown();
				this.elements.add(toAdd);
			}
		}
		
		this.elements.add(new GuiNewline());
		if(submittable)
			this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("✔️"), "submit").withColor(GuiButton.ButtonColor.GREEN).asEnabled(submittable));
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("✖️"), "cancel").withColor(GuiButton.ButtonColor.RED));
	}
	
	@Override
	public Object draw(JDA jda) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.GRAY);
		eb.setTitle(this.title);
		if(this.description != null)
			eb.setDescription(this.description);
		
		int i = 0;
		for(boolean b: this.setFields)
			if(!this.fields[i++].optional && !b) {
				eb.setDescription("`❌ Some fields still need to be set.`");
				break;
			}
		
		for(GuiEditEntry entry: this.fields) {
			String name = entry.title;
			if(!entry.optional)
				name += '*';
			if(entry.emoji != null)
				name = entry.emoji + " " + name;
			
			String desc = entry.description;
			
			String value;
			if(entry.getValue() == null)
				value = "`unset`";
			else
				value = entry.getStringValue();
			
			eb.addField(name, desc + '\n' + value, false);
		}
		
		eb.setFooter("*required");
		return eb.build();
	}
	
	@Override
	public void onDropdownChanged(GuiDropdown dropdown, long clickerId, JDA jda) {
		
		for(int i = 0; i < this.fields.length; i++) {
			GuiEditEntry entry = this.fields[i];
			if(!(entry instanceof GuiEditEntryChoice))
				continue;
			if(!entry.getActionID().equals(dropdown.id))
				continue;
			this.setFields[i] = true;
			break;
		}
		
		this.parent.redrawMessage(jda);
		return;
	}
	
	@Override
	public void onButtonClick(String actionId, long executorId, JDA jda) {
		
		MessageChannel channel = this.parent.getChannel(jda);
		
		if(actionId.equals("submit")) {
			// Cancel if all fields haven't been set.
			int i = 0;
			for(boolean b: this.setFields)
				if(!this.fields[i++].optional && !b) {
					BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("All required fields must be set."), 3);
					return;
				}
			Output output = new Output(false, this);
			selfClose(output, jda);
		} else if(actionId.equals("cancel")) {
			Output output = new Output(true, this);
			selfClose(output, jda);
		} else {
			// This is a request to set/edit a field.
			// Search for it and signal the event waiter.
			for(int i = 0; i < this.fields.length; i++) {
				GuiEditEntry entry = this.fields[i];
				String test = entry.getActionID();
				if(actionId.equals(test) && entry instanceof GuiEditEntryText) {
					final int finalIndex = i;
					EventWaiterManager waiter = this.bonziReference.eventWaiter;
					channel.sendMessageEmbeds(BonziUtils.quickEmbed("Setting " + entry.title + "...",
							"Enter a new value for this field!", Color.orange).build()).queue(msg -> {
						waiter.waitForArgument(executorId, ((GuiEditEntryText)entry).getParser(), result -> {
							msg.delete().queue();
							if(result == null)
								return;
							this.setFields[finalIndex] = true;
							this.reinitialize();
							this.parent.redrawMessage(jda);
						});
					});
				}
			}
		}
	}
	
	public void selfClose(Output output, JDA jda) {
		if(returnTo != null)
			this.parent.setActiveGui(returnTo, jda);
		else {
			this.parent.delete(jda);
		}
		
		if(this.onClosed != null)
			this.onClosed.accept(output);
	}
}
