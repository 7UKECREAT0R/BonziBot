package com.lukecreator.BonziBot.Data;

import java.util.List;
import java.util.Set;

import com.lukecreator.BonziBot.BonziBot;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;


public class QuickDrawIdentify extends QuickDraw {
	
	public String correctData;
	
	QuickDrawIdentify(BonziBot bb) {
		// done in constructMessage
		this.reward = BonziUtils.randomInt(20, 40);
	} 
	
	@Override
	public MessageCreateAction constructMessage(TextChannel channel) {
		Guild guild = channel.getGuild();
		List<RichCustomEmoji> customEmoji = guild.getEmojiCache().asList();
		Set<String> regularEmoji = GenericEmoji.getShortcodeNames();
		
		int picksRemaining = 5;
		int customPicksRemaining = customEmoji.size();

		int correctIndex = BonziUtils.randomInt(5);
		GenericEmoji[] picks = new GenericEmoji[5];
		String[] picksNames = new String[5];
		
		retry:
		while(picksRemaining > 0) {
			GenericEmoji pick = null;
			String pickName = null;
			picksRemaining--;
			boolean correct = correctIndex == picksRemaining;
			
			if(customPicksRemaining > 0 && BonziUtils.randomBoolean()) {
				customPicksRemaining--;
				RichCustomEmoji rich = customEmoji.get(BonziUtils.randomInt(customEmoji.size()));
				pick = GenericEmoji.fromEmote(rich);
				pickName = rich.getName();
				
				// if any duplicates, try again.
				for(String name: picksNames) {
					if(name == null)
						continue;
					if(name.equals(pickName)) {
						picksRemaining++;
						continue retry;
					}
				}
				
				if(correct)
					this.correctData = rich.getName();
			} else {
				int pickIndex = BonziUtils.randomInt(regularEmoji.size());
				for(String stringEmoji: regularEmoji) {
					pickIndex--;
					if(pickIndex == 0) {
						String unicode = GenericEmoji.getCharacterFromShortcode(stringEmoji);
						pick = GenericEmoji.fromEmoji(unicode);
						pickName = stringEmoji;
						
						// if any duplicates, try again.
						for(String name: picksNames) {
							if(name == null)
								continue;
							if(name.equals(pickName)) {
								picksRemaining++;
								continue retry;
							}
						}
						
						if(correct)
							this.correctData = stringEmoji;
					}
				}
			}
			
			picks[picksRemaining] = pick;
			picksNames[picksRemaining] = pickName;
		}
		
		ItemComponent[] buttons = new ItemComponent[5];
		for(int i = 0; i < 5; i++) {
			GenericEmoji emoji = picks[i];
			String emojiName = picksNames[i];
			buttons[i] = GuiButton.singleEmoji(emoji, encodeProtocol(emojiName)).toDiscord();
		}
		
		return channel
			.sendMessage("` Quick Draw! ` Press the ` :" + this.correctData + ": `")
			.addActionRow(buttons);
	}
	@Override
	public MessageCreateAction constructWinnerMessage(User winner, int coinsGained, TextChannel channel) {
		return channel.sendMessage(winner.getAsMention() + "` won the Quick Draw! ` ` +" + coinsGained + " coins! `");
	}
	
	@Override
	public boolean tryInput(Button button, String data) {
		return data.equals(this.correctData);
	}
	
}
