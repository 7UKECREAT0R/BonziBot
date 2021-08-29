package com.lukecreator.BonziBot.Commands;

import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.CommandAPI.StringRemainderArg;
import com.lukecreator.BonziBot.Data.PremiumItem;
import com.lukecreator.BonziBot.Data.TodoFolder;
import com.lukecreator.BonziBot.Data.TodoList;
import com.lukecreator.BonziBot.Gui.GuiTodoFolders;
import com.lukecreator.BonziBot.Gui.GuiTodoList;
import com.lukecreator.BonziBot.Managers.TodoListManager;

public class TodoListCommand extends Command {
	
	public TodoListCommand() {
		this.subCategory = 0;
		this.name = "Todo List";
		this.unicodeIcon = "🗒️";
		this.description = "Create or view your to-do lists!";
		this.args = new CommandArgCollection(new StringRemainderArg("folder").optional());
		this.setCooldown(2000);
		this.category = CommandCategory._SHOP_COMMAND;
		this.setPremiumItem(PremiumItem.TODO_LIST);
	}
	
	@Override
	public void executeCommand(CommandExecutionInfo e) {
		boolean openFolder = e.args.argSpecified("folder");
		String folder = e.args.getString("folder");
		
		TodoListManager tlm = e.bonzi.todolists;
		TodoList todoList = tlm.getTodoList(e.executor);
		List<TodoFolder> folders = todoList.getFolders();
		
		if(openFolder) {
			TodoFolder find = null;
			for(TodoFolder test: folders) {
				if(test.folderName.equalsIgnoreCase(folder)) {
					find = test;
					break;
				}
			}
			if(find == null) {
				if(e.isSlashCommand)
					e.slashCommand.replyEmbeds(BonziUtils.failureEmbed("Folder doesn't exist!",
						"Run the command without any arguments to view your folders.")).queue();
				else
					e.channel.sendMessageEmbeds(BonziUtils.failureEmbed("Folder doesn't exist!",
						"Run the command without any arguments to view your folders.")).queue();
				return;
			}
			GuiTodoList gui = new GuiTodoList(find);
			BonziUtils.sendGui(e, gui);
			return;
		} else {
			GuiTodoFolders gui = new GuiTodoFolders(todoList, e.isSlashCommand);
			BonziUtils.sendGui(e, gui);
			return;
		}
	}
}