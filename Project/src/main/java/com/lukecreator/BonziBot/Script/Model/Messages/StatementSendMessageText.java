package com.lukecreator.BonziBot.Script.Model.Messages;

import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public class StatementSendMessageText implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;
	
	String channelVariable;
	String text;
	
	@Override
	public String getKeyword() {
		return "send_text";
	}

	@Override
	public String getAsCode() {
		return "send_text " + Script.asArgument(this.channelVariable) + ' ' + Script.asArgument(this.text);
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			caller.createVariableChoice("#️⃣", "Channel", "The channel to send the message in."),
			new GuiEditEntryText(new StringArg("text"), "🖊", "Text to Send", "To include variables, surround in curly brackets: {variable name}")
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
		this.channelVariable = (String)inputs[0];
		this.text = (String)inputs[1];
	}

	@Override
	public void execute(ScriptContextInfo info, ScriptExecutor context) {
		Object object = context.memory.readObjectRef(this.channelVariable);
		if(object == null || !(object instanceof GuildChannel)) {
			ScriptExecutor.raiseError(new ScriptError("Variable \"" + this.channelVariable + "\" is not a valid channel.", this));
			return;
		}
		GuildChannel _channel = (GuildChannel)object;
		if(_channel.getType() != ChannelType.TEXT) {
			ScriptExecutor.raiseError(new ScriptError("You can only send messages in a text channel.", this));
			return;
		}
		TextChannel channel = (TextChannel)_channel;
		
		String send = context.memory.replaceVariables(this.text);
		
		if(info.hasSlashCommand && !info.slashCommand.isAcknowledged()) {
			if(channel.getIdLong() == info.slashCommand.getChannel().getIdLong())
				info.slashCommand.reply(send).complete();
			else {
				info.slashCommand.reply(channel.getAsMention()).setEphemeral(true).queue(null, fail -> {});
				channel.sendMessage(send).complete();
			}
		} else
			channel.sendMessage(send).queue(null, fail -> {});
	}
}
