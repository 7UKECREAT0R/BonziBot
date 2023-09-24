package com.lukecreator.BonziBot.Gui;

import java.awt.Color;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.TodoFolder;
import com.lukecreator.BonziBot.Data.TodoItem;
import com.lukecreator.BonziBot.Data.TodoList;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class GuiTodoFolders extends Gui {
	
	boolean slash;
	TodoList list;
	List<TodoFolder> folders;
	
	boolean deleteMode = false;
	int deleteCursor = 0;
	
	public GuiTodoFolders(TodoList list, boolean slash) {
		this.list = list;
		this.folders = list.getFolders();
		this.slash = slash;
	}
	
	@Override
	public void initialize(JDA jda) {
		this.reinitialize();
	}
	public void reinitialize() {
		
		this.elements.clear();
		if(this.deleteMode) {
			this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬅️"), "return"));
			this.elements.add(new GuiButton("Up", GuiButton.ButtonColor.BLUE, "up"));
			this.elements.add(new GuiButton("Down", GuiButton.ButtonColor.BLUE, "down"));
			this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("❌"), "delete").withColor(GuiButton.ButtonColor.RED));
		} else {
			this.elements.add(new GuiButton("Create Folder", GuiButton.ButtonColor.BLUE, "create").asEnabled(this.folders.size() < TodoList.MAX_FOLDERS));
			this.elements.add(new GuiButton("Remove Folder", GuiButton.ButtonColor.RED, "remove").asEnabled(this.folders.size() > 0));
		}
	}
	
	@Override
	public Object draw(JDA jda) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(BonziUtils.COLOR_BONZI_PURPLE);
		eb.setTitle("Todo-List Folders");
		
		if(this.deleteMode) {
			eb.setDescription("Removing folder(s)...");
		} else {
			if(this.slash)
				eb.setDescription("To open a folder: `/todolist <folder name>`");
			else
				eb.setDescription("To open a folder: `" + this.prefixOfLocation + "todolist <folder name>`");
		}
		
		int i = 0;
		for(TodoFolder folder: this.folders) {
			TodoItem[] items = folder.getItems();
			int count = items.length;
			String fString = folder.toString();
			String desc = count + " " + BonziUtils.plural("Item", count);
			
			if(this.deleteMode && i++ == this.deleteCursor)
				fString += " `< ❌`";
			
			eb.addField(fString, desc, false);
		}
		
		eb.setFooter(this.folders.size() + " / " + TodoList.MAX_FOLDERS + " Folders Created");
		return eb.build();
	}
	
	@Override
	public void onButtonClick(String actionId, long executorId, JDA jda) {
		if(this.deleteMode) {
			if(actionId.equals("return")) {
				this.deleteMode = false;
				this.reinitialize();
				this.parent.redrawMessage(jda);
				return;
			}
			if(actionId.equals("up")) {
				if(this.folders.size() > 0) {
					this.deleteCursor--;
					if(this.deleteCursor < 0)
						this.deleteCursor = this.folders.size() - 1;
					if(this.deleteCursor >= this.folders.size())
						this.deleteCursor = 0;
					this.parent.redrawMessage(jda);
				}
			}
			if(actionId.equals("down")) {
				if(this.folders.size() > 0) {
					this.deleteCursor++;
					if(this.deleteCursor < 0)
						this.deleteCursor = this.folders.size() - 1;
					if(this.deleteCursor >= this.folders.size())
						this.deleteCursor = 0;
					this.parent.redrawMessage(jda);
				}		
			}
			if(actionId.equals("delete")) {
				this.folders.remove(this.deleteCursor);
				
				TodoList todoList = this.bonziReference
					.todolists.getTodoList(executorId);
				todoList.setFolders(this.folders);
				this.bonziReference.todolists
					.setTodoList(executorId, todoList);
				
				if(this.folders.size() > 0) {
					if(this.deleteCursor > 0)
						this.deleteCursor--;
				} else {
					this.deleteMode = false;
					this.deleteCursor = 0;
					this.reinitialize();
				}
				this.parent.redrawMessage(jda);
			}
		} else {
			if(actionId.equals("create")) {
				TextChannel channel = (TextChannel)this.parent.getChannel(jda);
				
				if(this.folders.size() + 1 >= TodoList.MAX_FOLDERS) {
					this.parent.getChannel(jda).sendMessageEmbeds(BonziUtils.failureEmbed("Max number of folders reached.")).queue();
					return;
				}
				
				EventWaiterManager ewm = this.bonziReference.eventWaiter;
				channel.sendMessageEmbeds(BonziUtils.quickEmbed("Creating folder...",
					"Send the name of the folder you want to make here.", Color.orange).build()).queue(del -> {
					ewm.waitForResponse(executorId, response -> {
						del.delete().queue();
						response.delete().queue(null, fail -> {});
						String name = response.getContentRaw();
						if(name.length() > TodoFolder.MAX_NAME_LEN)
							name = name.substring(0, TodoFolder.MAX_NAME_LEN);
						if(name.length() < 1) {
							response.getChannel().sendMessageEmbeds(BonziUtils.failureEmbed("You need to specify a name.", "Cancelled operation.")).queue();
							return;
						}
						this.folders.add(new TodoFolder(name));
						TodoList todoList = this.bonziReference
							.todolists.getTodoList(executorId);
						
						todoList.setFolders(this.folders);
						this.bonziReference.todolists
							.setTodoList(executorId, todoList);
						
						this.reinitialize();
						this.parent.redrawMessage(jda);
					});
				});
			}
			if(actionId.equals("remove")) {
				MessageChannelUnion ch = this.parent.getChannel(jda);
				if(this.folders.size() < 1) {
					BonziUtils.sendTempMessage(ch, BonziUtils.failureEmbed("There are no folders to remove!"), 3);
					return;
				}
				this.deleteMode = true;
				this.deleteCursor = 0;
				this.reinitialize();
				this.parent.redrawMessage(jda);
			}
		}
	}
}
