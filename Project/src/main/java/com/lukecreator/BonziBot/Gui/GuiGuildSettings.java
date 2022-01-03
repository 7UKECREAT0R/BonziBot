package com.lukecreator.BonziBot.Gui;

import java.util.ArrayList;
import java.util.List;

import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.GuildSettings;
import com.lukecreator.BonziBot.Data.GuildSettings.FilterLevel;
import com.lukecreator.BonziBot.GuiAPI.DropdownItem;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiComplex;
import com.lukecreator.BonziBot.GuiAPI.GuiDropdown;

import net.dv8tion.jda.api.JDA;

/**
 * Better, more modular version of GuiGuildSettingsPageX. Switching to this because it will look nicer and 
 * @author Lukec
 */
public class GuiGuildSettings extends Gui {
	
	final List<GuiComplex<GuildSettings>> options;
	final GuiDropdown dropdown;
	
	static List<GuiComplex<GuildSettings>> createOptionList(GuildSettings data, long guildId) {
		
		List<GuiComplex<GuildSettings>> list = new ArrayList<GuiComplex<GuildSettings>>();
		
		// Filter Level
		/*
		FilterLevel filterLevel = data.filter;
		list.add(new GuiComplex<GuildSettings>(data, new GuiButton(GenericEmoji.fromEmoji("🤬"), "Filter Level", GuiButton.ButtonColor.BLUE, "filter"))
			.whenDrawn(eb -> {
				
			}));
		list.add(new GuiComplex<GuildSettings>(data, new GuiButton(GenericEmoji.fromEmoji("🗒️"), "Custom Filter", GuiButton.ButtonColor.BLUE, "filter")));*/
			
		// TODO Ill work on porting this some other day but for now I'm running out of time
		
		
		
		return list;
	}
	
	long guildId;
	GuildSettings settings;
	
	public GuiGuildSettings(GuildSettings settings, long guildId) {
		this.settings = settings;
		this.guildId = guildId;
		this.options = createOptionList(settings, guildId);
		
		this.dropdown = new GuiDropdown("Choose Setting...", "setting_choice", false);
		this.dropdown.addItem(new DropdownItem(options.get(0), "Filter Level", "0")
				.withEmoji(GenericEmoji.fromEmoji("🤬"))
				.withDescription("Set the swear/content filter for the server."));
		this.dropdown.addItem(new DropdownItem(options.get(1), "Custom Filter", "1")
				.withEmoji(GenericEmoji.fromEmoji("🗒️"))
				.withDescription("Manage your custom filtered words."));
		this.dropdown.addItem(new DropdownItem(options.get(2), "Tags", "2")
				.withEmoji(GenericEmoji.fromEmoji("📜"))
				.withDescription("Enable or disable tags in this server."));
		this.dropdown.addItem(new DropdownItem(options.get(3), "Tag Privacy", "3")
				.withEmoji(GenericEmoji.fromEmoji("🕵️"))
				.withDescription("Enable or disable your server's own private tags."));
		this.dropdown.addItem(new DropdownItem(options.get(4), "Logging", "4")
				.withEmoji(GenericEmoji.fromEmoji("📝"))
				.withDescription("Enable or disable advanced logging."));
		this.dropdown.addItem(new DropdownItem(options.get(5), "Bot Commands", "5")
				.withEmoji(GenericEmoji.fromEmoji("🤖"))
				.withDescription("Completely disable bot commands unless in a channel with the 'Bot Commands' modifier."));
		this.dropdown.addItem(new DropdownItem(options.get(6), "Join Messages", "6")
				.withEmoji(GenericEmoji.fromEmoji("👋"))
				.withDescription("Send a customizable message when a member joins."));
		this.dropdown.addItem(new DropdownItem(options.get(7), "Leave Messages", "7")
				.withEmoji(GenericEmoji.fromEmoji("🚪"))
				.withDescription("Send a customizable message when a member leaves."));
		this.dropdown.addItem(new DropdownItem(options.get(8), "Join Role", "8")
				.withEmoji(GenericEmoji.fromEmoji("💥"))
				.withDescription("Give members a role when they join the server."));
		this.dropdown.addItem(new DropdownItem(options.get(9), "Prefix", "9")
				.withEmoji(GenericEmoji.fromEmoji("↪️"))
				.withDescription("Set my prefix if you aren't using slash commands. (you should)"));
		this.dropdown.addItem(new DropdownItem(options.get(10), "Rules", "10")
				.withEmoji(GenericEmoji.fromEmoji("📖"))
				.withDescription("Customize the server's rules and send a customizable embed!"));
		this.dropdown.addItem(new DropdownItem(options.get(11), "Disable Commands", "11")
				.withEmoji(GenericEmoji.fromEmoji("🚫"))
				.withDescription("Disable certain commands from being used entirely."));
		this.dropdown.addItem(new DropdownItem(options.get(12), "Quick Draw", "12")
				.withEmoji(GenericEmoji.fromEmoji("🎲"))
				.withDescription("Send quick coin-earning challenges every few minutes!"));
		this.dropdown.addItem(new DropdownItem(options.get(13), "Ban Appeals", "13")
				.withEmoji(GenericEmoji.fromEmoji("📥"))
				.withDescription("Allow users to appeal their bans, if banned through /ban."));
		this.dropdown.addItem(new DropdownItem(options.get(14), "Ban Message", "14")
				.withEmoji(GenericEmoji.fromEmoji("📳"))
				.withDescription("Send users a friendly little message when banned through /ban."));
		this.dropdown.addItem(new DropdownItem(options.get(15), "Token Scanning", "15")
				.withEmoji(GenericEmoji.fromEmoji("🔬"))
				.withDescription("Scan for discord bot tokens and automatically invalidate them."));
		this.dropdown.addItem(new DropdownItem(options.get(16), "Starboard", "16")
				.withEmoji(GenericEmoji.fromEmoji("🌟"))
				.withDescription("Let users star messages, which are then placed in a hall of fame!"));
	}
	public static String emojiForFilter(FilterLevel level) {
		switch(level) {
		case NONE:
			return "❌";
		case SENSITIVE:
			return "👀";
		case SLURS:
			return "😤";
		case SWEARS:
			return "🤬";
		default:
			return "❔";
		}
	}
	
	int selectedIndex = 0;
	GuiComplex<GuildSettings> selected = null;
	
	@Override
	public void initialize(JDA jda) {
		this.reinitialize();
	}
	public void reinitialize() {
		this.elements.clear();
		
	}
	
}
