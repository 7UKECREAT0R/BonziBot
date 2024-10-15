package com.lukecreator.BonziBot.Managers;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.Script.Model.InvocationMethod;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptPackage;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages/provides results for autocomplete arguments.
 */
public class AutocompleteManager {
    
    private final BonziBot parent;
    public AutocompleteManager(BonziBot parent) {
        this.parent = parent;
    }
    
    public String[] provideResults(Guild guild, String key, String currentInput) {
        if(key.startsWith("scriptbutton"))
            return provideScriptButtonResults(guild);
        
        return new String[0];
    }
    
    public String[] provideScriptButtonResults(Guild guild) {
        if(guild == null)
            return new String[0];
        
        // all packages for this server. now, collect script button names.
        List<ScriptPackage> packageList = parent.scripts.getPackages(guild);
        List<String> scriptButtonNames = new ArrayList<>();
        
        for(ScriptPackage pack: packageList) {
            List<Script> scripts = pack.getScripts();
            for(Script script: scripts) {
                if(script.method.getImplementation() != InvocationMethod.Implementation.BUTTON)
                    continue;
                scriptButtonNames.add(pack.packageName + ":" + script.name);
            }
        }
        
        return scriptButtonNames.toArray(new String[0]);
    }
}
