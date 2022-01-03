package com.lukecreator.BonziBot.Async;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.GuiAPI.Gui;

import net.dv8tion.jda.api.JDA;

/**
 * Run every second to poll animated GUIs.
 * @author Lukec
 */
public class GuiAnimationService extends AutoRepeat {

	public GuiAnimationService() {
		this.unit = TimeUnit.SECONDS;
		this.initialDelay = 1;
		this.delay = 1;
	}
	
	@Override
	public void run(BonziBot bb, JDA jda) {
		Collection<Gui> entries = animatedGuis.values();
		entries.removeIf(Gui::isDisabled);
		
		for(Gui gui: entries) {
			if(!gui.pollAnimation())
				return;
			gui.onAnimationTick();
			gui.parent.redrawMessage(jda);
		}
	}
	
	static ConcurrentHashMap<Long, Gui> animatedGuis = new ConcurrentHashMap<Long, Gui>();
	
	/**
	 * Sets the GUI which will receive animation events for this guild.
	 * @param guildId
	 * @param gui
	 */
	public static void setEventTarget(long guildId, Gui gui) {
		animatedGuis.put(guildId, gui);
	}
	/**
	 * Stop this guild from receiving animation events.
	 * @param guildId
	 */
	public static void removeEventTarget(long guildId) {
		animatedGuis.remove(guildId);
	}
}
