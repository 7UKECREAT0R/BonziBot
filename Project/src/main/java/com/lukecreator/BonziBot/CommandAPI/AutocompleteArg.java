package com.lukecreator.BonziBot.CommandAPI;

import net.dv8tion.jda.api.entities.Guild;

/**
 * Autocomplete the argument's contents dynamically using the argument name for lookup.
 * Make sure it's unique-ish!
 */
public class AutocompleteArg extends CommandArg {
    
    public AutocompleteArg(String name) {
        super(name);
        this.type = ArgType.StringAutocomplete;
    }

    @Override
    public boolean isWordParsable(String word, Guild theGuild) {
        return true; // it's up to the caller to process the result
    }
}
