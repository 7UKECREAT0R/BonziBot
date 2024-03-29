package com.lukecreator.BonziBot.GuiAPI;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Async.GuiAnimationService;
import com.lukecreator.BonziBot.NoUpload.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;


public abstract class Gui {
	
	private boolean animation = false;
	private int animationInterval = 0;
	private int _untilRedraw = 0;
	private int animationExpires = 0;
	
	/**
	 * Makes this GUI get redrawn every <code>seconds</code> seconds.
	 * If the expiry is hit, then animation will be frozen until this method is called again.
	 * You can call this method every time a redraw or interaction is done to implement this functionality.
	 * @param seconds The interval in seconds of which to redraw this Gui.
	 * @param expiry The number of seconds until the animation pauses. 
	 */
	protected void enableAnimation(int seconds, int expiry) throws UnsupportedOperationException {
		if(this.parent.isDm)
			throw new UnsupportedOperationException("Cannot animate GUIs in user private messages.");
		this.animation = true;
		this.animationInterval = seconds;
		this._untilRedraw = seconds;
		
		this.animationExpires = expiry;
		GuiAnimationService.setEventTarget(this.parent.guildId, this);
	}
	protected void disableAnimation() {
		if(this.parent.isDm)
			return;
		GuiAnimationService.removeEventTarget(this.parent.guildId);
	}
	/**
	 * Called by the GuiAnimationService to check if a Gui needs a redraw.
	 */
	public boolean pollAnimation() {
		if(!this.animation)
			return false;
		if(this.animationExpires < 1)
			return false;
		
		this._untilRedraw--;
		this.animationExpires--;
		
		if(this._untilRedraw <= 0) {
			this._untilRedraw = this.animationInterval;
			return true;
		}
		
		return false;
	}
	
	// never override or modify these
	protected String prefixOfLocation;
	protected BonziBot bonziReference;
	boolean initialized = false;
	public boolean wasInitialized() {
		return this.initialized;
	}
	public boolean isDisabled() {
		if(this.parent == null)
			return true;
		return !this.parent.getEnabled();
	}
	public void hiddenInit(JDA jda, Guild g, BonziBot b) {
		if(this.elements == null) {
			this.elements = new ArrayList<GuiElement>();
		}
		this.prefixOfLocation = b.guildSettings.getSettings(g).getPrefix();
		this.bonziReference = b;
		this.initialize(jda);
		this.initialized = true;
	}
	public void hiddenInit(JDA jda, User u, BonziBot b) {
		if(this.elements == null) {
			this.elements = new ArrayList<GuiElement>();
		}
		this.prefixOfLocation = Constants.DEFAULT_PREFIX;
		this.bonziReference = b;
		this.initialize(jda);
		this.initialized = true;
	}
	
	public static final MessageEmbed EMPTY = new EmbedBuilder()
		.setTitle("No Content")
		.setDescription("i guess luke forgot to include a draw implemention...")
		.setColor(BonziUtils.COLOR_BONZI_PURPLE)
		.build();
	
	public Gui(GuiContainer parent, JDA jda, Guild g, BonziBot b) {
		this.elements = new ArrayList<GuiElement>();
		this.parent = parent;
		this.hiddenInit(jda, g, b);
	}
	public Gui(GuiContainer parent, JDA jda, User u, BonziBot b) {
		this.elements = new ArrayList<GuiElement>();
		this.parent = parent;
		this.hiddenInit(jda, u, b);
	}
	public Gui setParent(GuiContainer parent) {
		this.parent = parent;
		return this;
	}
	public Gui() {}
	
	public GuiContainer parent;
	public List<GuiElement> elements;
	
	/**
	 * Dispatch a button event to the right GUI.
	 * @param actionId
	 * @param executorId
	 * @param jda
	 */
	public void receiveActionButton(String actionId, long executorId, JDA jda) {
		for(int i = 0; i < this.elements.size(); i++) {
			GuiElement element = this.elements.get(i);
			if(element instanceof GuiNewline)
				continue;
			if(element.id.equals(actionId)) {
				this.onButtonClick(actionId, executorId, jda);
				break;
			}
		}
	}
	/**
	 * Dispatch a selection menu event to the right GUI event.
	 * @param actionId
	 * @param executorId
	 * @param jda
	 */
	public void receiveActionSelect(String actionId, List<SelectOption> selected, long executorId, JDA jda) {
		
		List<String> _selectedIds = selected
			.stream()
			.map(option -> option.getValue())
			.collect(Collectors.toList());
		
		String[] selectedIds = (String[])_selectedIds.toArray(new String[_selectedIds.size()]);
		
		// Find the correct GuiDropdown.
		GuiDropdown dropdown = null;
		for(GuiElement element: this.elements) {
			if(!(element instanceof GuiDropdown))
				continue;
			if(element.idEqual(actionId)) {
				dropdown = (GuiDropdown)element;
				break;
			}
		}
		
		if(dropdown == null)
			return; // what the heck happened?
		
		int[] indexes = dropdown._items.resolveIndexesById(selectedIds);
		dropdown.setSelectedIndexes(indexes);
		
		for(int i = 0; i < this.elements.size(); i++) {
			GuiElement element = this.elements.get(i);
			if(element instanceof GuiNewline)
				continue;
			if(element.id.equals(actionId)) {
				this.onDropdownChanged(dropdown, executorId, jda);
				break;
			}
		}
	}
	
	// Overridable
	
	/**
	 * Called when this GUI is initialized for the first time.
	 * You should add in any intial elements and setup state.
	 * @param jda
	 */
	public void initialize(JDA jda) {}
	/**
	 * Called before this GUI is routinely redrawn, granted that animation is enabled.
	 */
	public void onAnimationTick() {}
	/**
	 * Called when a button is clicked in this GUI.
	 * @param buttonId The ID of the button that was clicked.
	 * @param clickerId The ID of the user that clicked the button.
	 * @param jda 
	 */
	public void onButtonClick(String buttonId, long clickerId, JDA jda) {}
	/**
	 * Called when a select menu's value changes.
	 * @param dropdown The dropdown menu that was changed.
	 * @param clickerId The ID of the user that changed it.
	 * @param jda
	 */
	public void onDropdownChanged(GuiDropdown dropdown, long clickerId, JDA jda) {}
	/**
	 * Called when this GUI needs a redraw.
	 * @param jda
	 * @return A {@link String}, {@link File}, or {@link MessageEmbed}.
	 */
	public Object draw(JDA jda) { return EMPTY; }
	
}
