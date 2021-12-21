package com.lukecreator.BonziBot.Gui;

import java.awt.Color;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.EmojiArg;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.Data.TodoFolder;
import com.lukecreator.BonziBot.Data.TodoItem;
import com.lukecreator.BonziBot.Data.TodoList;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiNewline;
import com.lukecreator.BonziBot.GuiAPI.GuiPaging;
import com.lukecreator.BonziBot.Managers.EventWaiterManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * Add/remove items from a to-do list.
 * @author Lukec
 */
public class GuiTodoList extends GuiPaging {
	
	public static final int PER_PAGE = 8;
	
	boolean empty;
	TodoFolder folder;
	
	public GuiTodoList(TodoFolder folder) {
		this.folder = folder;
		
		int count = folder.size();
		this.empty = count < 1;
		this.maxPage = (count / PER_PAGE);
		if(count % PER_PAGE != 0)
			this.maxPage++;
	}
	
	@Override
	public void initialize(JDA jda) {
		this.reinitialize(jda);
	}
	public void reinitialize(JDA jda) {
		this.elements.clear();
		super.initialize(jda);
		this.elements.add(new GuiNewline());
		this.elements.add(new GuiButton("Finished", GuiButton.ButtonColor.GREEN, "done").asEnabled(folder.size() > 0 && this.currentPage == 1));
		this.elements.add(new GuiButton("Push Down", GuiButton.ButtonColor.GRAY, "push").asEnabled(folder.size() > 0));
		this.elements.add(new GuiNewline());
		
		this.elements.add(new GuiButton("Add Item", GuiButton.ButtonColor.BLUE, "add").asEnabled(folder.size() < TodoFolder.MAX_ITEMS));
		this.elements.add(new GuiButton(GenericEmoji.fromEmoji("🖼️"), "Set Icon", GuiButton.ButtonColor.BLUE, "icon"));
	}
	
	@Override
	public Object draw(JDA jda) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(BonziUtils.COLOR_BONZI_PURPLE);
		eb.setTitle("Todo-List: " + folder.toString());
		
		if(this.empty) {
			eb.setDescription("No to-do items are in this folder yet! Press 'Add Item' to add some stuff to your list!");
			eb.setFooter("No items... yet");
		} else {
			int count = folder.size();
			int shown = count > PER_PAGE ? PER_PAGE : count;
			eb.setDescription("Showing " + shown + "/" + count);
			TodoItem[] _items = folder.getItems();
			TodoItem[] items = new TodoItem[PER_PAGE];
			int page = this.currentPage - 1;
			int startIndex = page * PER_PAGE;
			int endIndex = startIndex + PER_PAGE;
			if(endIndex > count)
				endIndex = count;
			for(int i = startIndex; i < endIndex; i++) {
				items[i - startIndex] = _items[i];
			}
			
			for(int i = 0; i < PER_PAGE; i++) {
				TodoItem item = items[i];
				if(item == null)
					break;
				String comment = item.comment;
				if(comment == null)
					comment = "";
				eb.addField(item.name, item.comment, false);
			}
			if(count > PER_PAGE)
				eb.setFooter("Page " + this.currentPage + "/" + this.maxPage);
		}
		
		return eb.build();
	}
	
	@Override
	public void onButtonClick(String actionId, long executorId, JDA jda) {
		
		if(actionId.equals("pageleft") && pagingEnabled) {
			if(--currentPage < minPage)
				currentPage = minPage;
			else {
				this.reinitialize(jda);
				parent.redrawMessage(jda);
			}
		}
		if(actionId.equals("pageright") && pagingEnabled) {
			if(++currentPage > maxPage)
				currentPage = maxPage;
			else {
				this.reinitialize(jda);
				parent.redrawMessage(jda);
			}
		}
		
		MessageChannel channel = this.parent.getChannel(jda);
		
		if(actionId.equals("done")) {
			if(folder.size() < 1) {
				BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("No items to be done with."), 3);
				return;
			}
			
			folder.complete();
			
			int count = folder.size();
			if(count < 1)
				this.empty = true;
			
			this.maxPage = (count / PER_PAGE);
			if(count % PER_PAGE != 0)
				this.maxPage++;
			if(this.currentPage > this.maxPage)
				this.currentPage = this.maxPage;
			
			TodoList list = this.bonziReference
				.todolists.getTodoList(executorId);
			list.setFolder(folder);
			this.bonziReference.todolists
				.setTodoList(executorId, list);
			
			this.reinitialize(jda);
			this.parent.redrawMessage(jda);
			return;
		}
		if(actionId.equals("push")) {
			if(folder.size() < 1) {
				BonziUtils.sendTempMessage(channel, BonziUtils.failureEmbed("No items to push down."), 3);
				return;
			}
			
			folder.shiftUp();
			
			TodoList list = this.bonziReference
				.todolists.getTodoList(executorId);
			list.setFolder(folder);
			this.bonziReference.todolists
				.setTodoList(executorId, list);
			
			this.reinitialize(jda);
			this.parent.redrawMessage(jda);
		}
		
		if(actionId.equals("add")) {
			if(this.folder.size() + 1 >= TodoFolder.MAX_ITEMS) {
				this.parent.getChannel(jda).sendMessageEmbeds(BonziUtils.failureEmbed("Max number of to-do items reached.",
					"Make another folder to add some more items into!")).queue();
				return;
			}
			
			EventWaiterManager ewm = this.bonziReference.eventWaiter;
			channel.sendMessageEmbeds(BonziUtils.quickEmbed("Adding item to todo list...",
				"What will its title be?", Color.orange).build()).queue(del -> {
				ewm.waitForResponse(executorId, response -> {
					del.delete().queue();
					response.delete().queue(null, fail -> {});
					String name = response.getContentRaw();
					if(name.length() > TodoItem.MAX_NAME_LEN)
						name = name.substring(0, TodoItem.MAX_NAME_LEN);
					if(name.length() < 1) {
						response.getChannel().sendMessageEmbeds(BonziUtils.failureEmbed("You need to specify a name.", "Cancelled operation.")).queue();
						return;
					}
					final String nameModified = name;
					channel.sendMessageEmbeds(BonziUtils.quickEmbed("Now, describe your task!",
						"Type 'none' to not include a description.", Color.orange).build()).queue(del2 -> {
							ewm.waitForResponse(executorId, descResponse -> {
								del2.delete().queue();
								descResponse.delete().queue(null, fail -> {});
								String desc = descResponse.getContentRaw();
								if(desc.length() > TodoItem.MAX_DESC_LEN)
									desc = desc.substring(0, TodoItem.MAX_DESC_LEN);
								if(desc.length() < 1 || desc.equalsIgnoreCase("none"))
									desc = "";
								
								this.folder.addItem(new TodoItem(nameModified, desc));
								int count = this.folder.size();
								this.empty = count < 1;
								
								this.maxPage = (count / PER_PAGE);
								if(count % PER_PAGE != 0)
									this.maxPage++;
								this.currentPage = this.maxPage;
								
								TodoList todoList = this.bonziReference
									.todolists.getTodoList(executorId);
								todoList.setFolder(this.folder);
								this.bonziReference.todolists
									.setTodoList(executorId, todoList);
								
								this.reinitialize(jda);
								this.parent.redrawMessage(jda);
							});
					});
				});
			});
		}
		if(actionId.equals("icon")) {
			EventWaiterManager ewm = this.bonziReference.eventWaiter;
			channel.sendMessageEmbeds(BonziUtils.quickEmbed("Change icon!",
					"Send an emoji in chat.", Color.orange).build()).queue(del -> {
					ewm.waitForArgument(executorId, new EmojiArg(""), obj -> {
						del.delete().queue();
						
						GenericEmoji _emoji = (GenericEmoji)obj;
						this.folder.folderIcon = _emoji.toString();
						
						TodoList todoList = this.bonziReference
							.todolists.getTodoList(executorId);
						todoList.setFolder(this.folder);
						this.bonziReference.todolists
							.setTodoList(executorId, todoList);
						
						this.parent.redrawMessage(jda);
					});
			});
		}
	}
}
