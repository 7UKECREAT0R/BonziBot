package com.lukecreator.BonziBot.Commands;

import java.util.function.Consumer;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Tuple;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.UserArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Gui.GuiTicTacToe;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class TicTacToeCommand extends Command {

	public TicTacToeCommand() {
		this.subCategory = 0;
		this.name = "Tic Tac Toe";
		this.unicodeIcon = "❌⭕";
		this.description = "Play Tic-Tac-Toe with someone!";
		this.args = new CommandArgCollection(new UserArg("opponent").optional());
		this.category = CommandCategory.FUN;
		this.setCooldown(20000);
	}

	@Override
	public void executeCommand(CommandExecutionInfo e) {
		boolean chosen = e.args.argSpecified("opponent");

		EventWaiterManager ewm = e.bonzi.eventWaiter;
		
		if(chosen) {
			User opponent = chosen ? e.args.getUser("opponent") : null;
			EmbedBuilder eb = new EmbedBuilder()
				.setColor(BonziUtils.COLOR_BONZI_PURPLE)
				.setTitle("❌ TIC-TAC-TOE DUEL ⭕")
				.setDescription(e.executor.getAsMention() +
					" has requested to duel with " + opponent.getAsMention()
					+ " in a game of tic-tac-toe!");
			Consumer<String> onAccept = (str -> {
				if(str.equals("tttaccept")){
					GuiTicTacToe gui = new GuiTicTacToe(e.executor, opponent);
					BonziUtils.sendGui(e, gui);
				} else {
					e.channel.sendMessageEmbeds(BonziUtils.failureEmbed(opponent.getName() + " rejected the tic-tac-toe duel.")).queue();
					return;
				}
			});
			if(e.isSlashCommand) {
				ewm.waitForAction(opponent, e.slashCommand.replyEmbeds(eb.build()).setEphemeral(false), onAccept,
					new GuiButton("ACCEPT", GuiButton.ButtonColor.GREEN, "tttaccept"),
					new GuiButton("REJECT", GuiButton.ButtonColor.RED, "tttreject")).queue();
			} else {
				ewm.waitForAction(opponent, e.channel.sendMessageEmbeds(eb.build()), onAccept,
					new GuiButton("ACCEPT", GuiButton.ButtonColor.GREEN, "tttaccept"),
					new GuiButton("REJECT", GuiButton.ButtonColor.RED, "tttreject")).queue();
			}
		} else {
			EmbedBuilder eb = new EmbedBuilder()
				.setColor(BonziUtils.COLOR_BONZI_PURPLE)
				.setTitle("❌ TIC-TAC-TOE CHALLENGE ⭕")
				.setDescription(e.executor.getAsMention() +
					" has opened a tic-tac-toe challenge!");
			Consumer<Tuple<User, String>> onAccept = (tuple -> {
				User user = tuple.getA();
				GuiTicTacToe gui = new GuiTicTacToe(e.executor, user);
				BonziUtils.sendGui(e, gui);
			});
			if(e.isSlashCommand) {
				ewm.waitForGlobalAction(e.slashCommand.replyEmbeds(eb.build()).setEphemeral(false), onAccept,
					new GuiButton(GenericEmoji.fromEmoji("⚔️"), "JOIN", GuiButton.ButtonColor.GREEN, "jointtt"));
			} else {
				ewm.waitForGlobalAction(e.channel.sendMessageEmbeds(eb.build()), onAccept,
					new GuiButton(GenericEmoji.fromEmoji("⚔️"), "JOIN", GuiButton.ButtonColor.GREEN, "jointtt"));
			}
		}
	}
}