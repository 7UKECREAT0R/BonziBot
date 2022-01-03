package com.lukecreator.BonziBot.Script.Model.Messages;

import java.awt.Color;

import com.lukecreator.BonziBot.CommandAPI.ColorArg;
import com.lukecreator.BonziBot.CommandAPI.StringArg;
import com.lukecreator.BonziBot.GuiAPI.DropdownItem;
import com.lukecreator.BonziBot.GuiAPI.GuiDropdown;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryChoice;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntryText;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptContextInfo;
import com.lukecreator.BonziBot.Script.Model.ScriptError;
import com.lukecreator.BonziBot.Script.Model.ScriptExecutor;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class StatementSendMessageEmbed implements ScriptStatement {
	
	private static final long serialVersionUID = 1L;
	
	String channelVariable;
	
	String title;
	String description;
	Color color;
	String imageUrl;
	int imageMode;
	
	@Override
	public String getKeyword() {
		return "send_embed";
	}
	
	@Override
	public String getAsCode() {
		StringBuilder sb = new StringBuilder();
		sb.append("send_embed " + Script.asArgument(channelVariable) + ' ' +
			Script.asArgument(this.title) + ' ' +
			Script.asArgument(this.description) + ' ' +
			Script.asArgument(this.color));
		
		if(this.imageUrl != null) {
			sb.append(' ');
			sb.append(Script.asArgument(this.imageUrl));
			if(this.imageMode != -1) {
				sb.append(' ');
				sb.append(imageMode);
			}
		}
		
		return sb.toString();
	}

	@Override
	public GuiEditEntry[] getArgs(Script caller, Guild server) {
		return new GuiEditEntry[] {
			new GuiEditEntryText(new StringArg("title"), null, "Title", "The title that will go on the embed. To include variables, surround in curly brackets: {variable name}"),
			new GuiEditEntryText(new StringArg("desc"), null, "Subtitle", "The main text of the embed. To include variables, surround in curly brackets: {variable name}"),
			new GuiEditEntryText(new ColorArg("color"), "🖌", "Color", "The color of the embed."),
			new GuiEditEntryText(new StringArg("imageurl"), "🖼", "Image", "The image on the embed.").optional(),
			new GuiEditEntryChoice(new GuiDropdown("Select an image mode...", "imgmode", false).addItems(
				new DropdownItem(0, "Large Image"),
				new DropdownItem(1, "Small Image"),
				new DropdownItem(2, "Avatar")), null,
					"Image Size", "The size of the image, if given.").optional(),
			
			caller.getVariableChoice("#️⃣", "Channel", "The channel to send the message in.")
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
		this.title = (String)inputs[0];
		this.description = (String)inputs[1];
		this.color = (Color)inputs[2];
		this.imageUrl = (String)inputs[3];
		Integer imageMode = (Integer)inputs[4];
		this.channelVariable = (String)inputs[5];
		
		if(imageMode != null)
			this.imageMode = imageMode.intValue();
		else
			this.imageMode = -1;
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
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(context.memory.replaceVariables(this.title));
		eb.setDescription(context.memory.replaceVariables(this.description));
		eb.setColor(this.color);
		
		if(this.imageUrl != null) {
			if(this.imageMode < 1) {
				eb.setFooter(null, this.imageUrl);
			} else if(this.imageMode == 1) {
				eb.setAuthor(null, null, this.imageUrl);
			} else {
				eb.setThumbnail(this.imageUrl);
			}
		}
		
		if(info.hasSlashCommand && !info.slashCommand.isAcknowledged()) {
			if(channel.getIdLong() == info.slashCommand.getChannel().getIdLong())
				info.slashCommand.replyEmbeds(eb.build()).complete();
			else {
				info.slashCommand.reply(channel.getAsMention()).setEphemeral(true).queue(null, fail -> {});
				channel.sendMessageEmbeds(eb.build()).queue(null, fail -> {});
			}
		} else
			channel.sendMessageEmbeds(eb.build()).queue(null, fail -> {});
	}
}
