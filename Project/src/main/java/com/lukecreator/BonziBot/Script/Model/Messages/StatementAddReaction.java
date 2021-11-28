package com.lukecreator.BonziBot.Script.Model.Messages;

import com.lukecreator.BonziBot.CommandAPI.EmojiArg;
import com.lukecreator.BonziBot.Data.EmoteCache;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class StatementAddReaction implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;
	
	GenericEmoji reaction;
	
	@Override
	public String getKeyword() {
		return "add_reaction";
	}

	@Override
	public String getAsCode() {
		if(this.reaction.getIsGeneric())
			return "add_reaction " + this.reaction.getGenericEmoji();
		else
			return "add_reaction " + EmoteCache.getEmoteById(this.reaction.getGuildEmojiId()).getAsMention();
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			new GuiEditEntryText(new EmojiArg("emoji"), "🖊", "Emoji", "The emoji to add to the message.")
		};
	}

	@Override
	public String getNewVariable() {
		return null;
	}

	@Override
	public StatementCategory getCategory() {
		return StatementCategory.MESSAGES;
	}

	@Override
	public void parse(Object[] inputs) {
		this.reaction = (GenericEmoji)inputs[0];
	}

	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		
		if(!info.hasMessage) {
			ScriptExecutor.raiseError(new ScriptError("No message is present.", this));
			return;
		}
		
		Message message = info.msg;
		
		if(this.reaction.getIsGeneric())
			message.addReaction(this.reaction.getGenericEmoji()).queue(null, fail -> {});
		else
			message.addReaction(EmoteCache.getEmoteById(this.reaction.getGuildEmojiId())).queue(null, fail -> {});
	}
}
