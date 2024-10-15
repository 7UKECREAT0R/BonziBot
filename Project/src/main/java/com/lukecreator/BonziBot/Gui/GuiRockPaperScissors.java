package com.lukecreator.BonziBot.Gui;

import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiElement;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Objects;

public class GuiRockPaperScissors extends Gui {
    
    private enum Vote {
        Rock(GenericEmoji.fromEmoji("🪨"), "Rock"),
        Paper(GenericEmoji.fromEmoji("📃"), "Paper"),
        Scissors(GenericEmoji.fromEmoji("✂️"), "Scissors");
        
        public final GenericEmoji emoji;
        public final String name;
        public final String actionID;

        Vote(GenericEmoji emoji, String name) {
            this.emoji = emoji;
            this.name = name;
            this.actionID = "press" + this.name.toLowerCase();
        }
        public GuiButton makeButton() {
            return new GuiButton(this.emoji, this.name, GuiButton.ButtonColor.GRAY, this.actionID);
        }
    }
    
    private final long duelistID;

    public GuiRockPaperScissors(long duelistID) {
        this.duelistID = duelistID;
    }

    private long playerA;
    private long playerB;
    
    private Vote voteA;
    private Vote voteB;

    private void calculateResults() {
        isDraw = voteA == voteB;
        
        // cancel if it's a draw, the next value is unused.
        if(isDraw)
            return;

        if (Objects.requireNonNull(voteA) == Vote.Rock)
            aWins = voteB == Vote.Scissors;
        else if (voteA == Vote.Paper)
            aWins = voteB == Vote.Rock;
        else if (voteA == Vote.Scissors)
            aWins = voteB == Vote.Paper;
    }
    private boolean aWins;
    private boolean isDraw;
    
    @Override
    public void initialize(JDA jda) {
        if(duelistID == 0L)
            this.parent.globalWhitelist = true;
        else
            this.parent.ownerWhitelist.add(duelistID);
        
        playerA = 0L;
        playerB = 0L;
        
        for(Vote vote: Vote.values())
            this.elements.add(vote.makeButton().asEnabled(true));
    }
    public void reinitialize() {
        boolean needsVotes = playerA == 0L || playerB == 0L;
        
        // enable/disable the buttons based on if votes are needed.
        for(int i = 0; i < this.elements.size(); i++) {
            GuiElement element = this.elements.get(i);
            element = element.asEnabled(needsVotes);
            this.elements.set(i, element);
        }
    }
    public void onVotingComplete(JDA jda) {
        this.calculateResults();
        
        this.reinitialize();
        this.parent.redrawMessage(jda);
        
        if(this.isDraw) {
            this.playerA = 0L;
            this.playerB = 0L;
        }
    }

    @Override
    public void onButtonClick(String buttonId, long clickerId, JDA jda) {
        for(Vote vote: Vote.values()) {
            if(!buttonId.equals(vote.actionID))
                continue;
            
            if(playerA == 0L) {
                playerA = clickerId;
                voteA = vote;
                this.parent.redrawMessage(jda);
                return;
            }
            if(playerB == 0L) {
                if(clickerId == playerA)
                    return;
                playerB = clickerId;
                voteB = vote;
                this.onVotingComplete(jda);
                return;
            }
        }
    }

    @Override
    public Object draw(JDA jda) {
        EmbedBuilder eb = new EmbedBuilder();
        final String WAITING = "Waiting for entries...";
        
        if(isDraw) {
            eb.setColor(Color.orange);
            eb.setTitle(this.voteA.emoji + "" + this.voteB.emoji + " DRAW! Try again.");
            eb.setDescription(WAITING);
            return eb.build();
        }
        
        if(playerA == 0L || playerB == 0L) {
            eb.setColor(Color.gray);
            eb.setTitle("Rock Paper Scissors");
            eb.setDescription(WAITING);
            return eb.build();
        }
        
        eb.setColor(Color.green);
        User playerA = jda.getUserById(this.playerA);
        User playerB = jda.getUserById(this.playerB);

        User winner = aWins ? playerA : playerB;
        User loser = aWins ? playerB : playerA;
        Vote winningVote = aWins ? voteA : voteB;
        Vote losingVote = aWins ? voteB : voteA;

        eb.setColor(Color.green);
        eb.setTitle("Rock Paper Scissors");
        eb.setDescription(winningVote.emoji + " -> " + losingVote.emoji);
        eb.setAuthor(winner.getEffectiveName() + " won!", null, winner.getEffectiveAvatarUrl());
        eb.setFooter(loser.getEffectiveName() + " lost.", loser.getEffectiveAvatarUrl());
        return eb.build();
    }
}
