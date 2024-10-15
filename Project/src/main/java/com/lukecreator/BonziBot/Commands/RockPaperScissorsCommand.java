package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.*;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Gui.GuiRockPaperScissors;
import com.lukecreator.BonziBot.InternalLogger;
import net.dv8tion.jda.api.entities.User;

public class RockPaperScissorsCommand extends Command {
    
    public RockPaperScissorsCommand() {
        this.subCategory = 0;
        this.name = "Rock Paper Scissors";
        this.icon = GenericEmoji.fromEmoji("��✂");
        this.description = "Open a Rock Paper Scissors event! If you choose a user, they will be the only ones allowed to input.";
        this.args = new CommandArgCollection(new UserArg("duel").optional());
        this.category = CommandCategory.FUN;
        this.setCooldown(BonziUtils.getMsForSeconds(2));
    }

    @Override
    public void run(CommandExecutionInfo e) {
        long duel = 0L;
        if(e.args.argSpecified("duel")) {
            User duelUser = e.args.getUser("duel");
            duel = duelUser.getIdLong();
            
            if(duel == e.executor.getIdLong()) {
                e.reply(3, BonziUtils.failureEmbed("You can't duel yourself!"));
                return;
            }
        }
        
        GuiRockPaperScissors guiRockPaperScissors = new GuiRockPaperScissors(duel);
        BonziUtils.sendGui(e, guiRockPaperScissors);
    }
}
