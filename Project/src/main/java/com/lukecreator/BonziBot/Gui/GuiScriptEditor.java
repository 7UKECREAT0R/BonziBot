package com.lukecreator.BonziBot.Gui;

import java.awt.Color;

import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.DropdownItem;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiButton.ButtonColor;
import com.lukecreator.BonziBot.GuiAPI.GuiDropdown;
import com.lukecreator.BonziBot.GuiAPI.GuiEditDialog;
import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.GuiAPI.GuiNewline;
import com.lukecreator.BonziBot.Script.Editor.EditorCategories;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;
import com.lukecreator.BonziBot.Script.Editor.StatementDescriptor;
import com.lukecreator.BonziBot.Script.Model.Script;
import com.lukecreator.BonziBot.Script.Model.ScriptStatement;
import com.lukecreator.BonziBot.Script.Model.ScriptStatementCollection;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class GuiScriptEditor extends Gui {
	
	public static final String ARROW = "➜";
	
	GuiScriptChooser previous;
	Script script;
	int selection;
	
	GuiDropdown categoryDropdown;
	GuiDropdown statementDropdown;
	
	public GuiScriptEditor(GuiScriptChooser previous, Script script) {
		this.previous = previous;
		this.script = script;
		this.selection = script.code.size() - 1;
	}
	
	@Override
	public void initialize(JDA jda) {
		this.categoryDropdown = new GuiDropdown("Category", "category", false);
		this.statementDropdown = (GuiDropdown)new GuiDropdown("Statement", "statement", false).asEnabled(false);
		
		for(StatementCategory category: StatementCategory.values()) {
			this.categoryDropdown.addItem(new DropdownItem(category,
				category.name).withEmoji(GenericEmoji.parseEmote(category.emoji)));
		}
		
		this.reinitialize();
	}
	public void reinitialize() {
		this.elements.clear();
		int count = this.script.code.size();
		
		boolean canAdd = count < Script.MAX_STATEMENTS;
		canAdd &= this.categoryDropdown.anySelected();
		canAdd &= this.statementDropdown.anySelected();
		
		this.elements.add(new GuiButton(GenericEmoji.fromEmoji("📂"), "Return", ButtonColor.GRAY, "return"));
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬆️"), "up"));
		this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬇️"), "down"));
		//this.elements.add(GuiButton.singleEmoji(GenericEmoji.fromEmoji("✒️"), "paste"));
		this.elements.add(new GuiNewline());
		this.elements.add(new GuiButton("Add Statement", ButtonColor.BLUE, "add").asEnabled(canAdd));
		this.elements.add(new GuiButton("Remove", ButtonColor.RED, "remove").asEnabled(count > 0));
		this.elements.add(this.categoryDropdown);
		this.elements.add(this.statementDropdown);
	}
	
	@Override
	public Object draw(JDA jda) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.cyan);
		eb.setTitle("Editing Script " + this.script.name);
		eb.appendDescription("```\n");
		
		ScriptStatementCollection ssc = this.script.code;
		
		ssc.seek(0);
		int i = 0;
		if(!ssc.isEmpty()) {
			while(ssc.hasNext()) {
				ScriptStatement statement = ssc.next();
				if(i++ == this.selection)
					eb.appendDescription(ARROW + " ");
				eb.appendDescription(statement.getAsCode() + '\n');
			}
		}
		eb.appendDescription("```");
		return eb;
	}
	
	@Override
	public void onButtonClick(String buttonId, long clickerId, JDA jda) {
		
		if(buttonId.equals("return")) {
			this.parent.setActiveGui(previous, jda);
			return;
		}
		
		if(buttonId.equals("up")) {
			this.selection--;
			if(this.selection < 0)
				this.selection = this.script.code.size() - 1;
			this.reinitialize();
			this.parent.redrawMessage(jda);
			return;
		}
		if(buttonId.equals("down")) {
			this.selection++;
			if(this.selection >= this.script.code.size())
				this.selection = 0;
			this.reinitialize();
			this.parent.redrawMessage(jda);
			return;
		}
		/*if(buttonId.equals("paste")) {
			MessageEmbed me = BonziUtils.quickEmbed("Pasting Code",
				"Paste your new code here.",
				Color.orange).build();
			MessageChannel channel = this.parent.getChannel(jda);
			EventWaiterManager ewm = this.bonziReference.eventWaiter;
			
			channel.sendMessageEmbeds(me).queue(del -> {
				ewm.waitForResponse(clickerId, response -> {
					del.delete().queue();
					response.delete().queue();
					
					// get content and trim codeblock markdown
					String content = response.getContentRaw();
					content = content.replace(GuiScriptEditor.ARROW + " ", "");
					if(content.startsWith("```\n"))
						content = content.substring(4);
					if(content.endsWith("\n```"))
						content = content.substring(0, content.length() - 4);
					
					String[] lines = content.split("\n");
					
				});
			});
		}*/
		
		if(buttonId.equals("add")) {
			if(!this.categoryDropdown.anySelected())
				return;
			if(!this.statementDropdown.anySelected())
				return;
			
			try {
				StatementDescriptor _statement = (StatementDescriptor)
					this.statementDropdown.getSelectedObject();
				ScriptStatement statement = _statement.createNewFromClass();
				Guild theGuild = jda.getGuildById(this.parent.guildId);
				GuiEditEntry[] entries = statement.getArgs(this.script, theGuild);
				if(entries == null || entries.length < 1) {
					this.script.code.add(this.selection, statement);
					this.selection++;
					this.reinitialize();
					this.parent.redrawMessage(jda);
					return;
				}
				GuiEditDialog dialog = new GuiEditDialog(this, _statement.name, _statement.desc, entries);
				dialog.after(output -> {
					if(output.wasCancelled)
						return;
					statement.parse(output.values);
					this.script.code.add(this.selection, statement);
					this.selection++;
					this.reinitialize();
					this.parent.redrawMessage(jda);
				});
				this.parent.setActiveGui(dialog, jda);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
			return;
		}
		
		if(buttonId.equals("remove")) {
			if(this.script.code.size() < 1)
				return;
			this.script.code.remove(this.selection--);
			if(this.selection < 0)
				this.selection = 0;
			this.reinitialize();
			this.parent.redrawMessage(jda);
			return;
		}
	}
	
	@Override
	public void onDropdownChanged(GuiDropdown dropdown, long clickerId, JDA jda) {
		if(dropdown.idEqual("category")) {
			StatementCategory newCategory = (StatementCategory)dropdown.getSelectedObject();
			StatementDescriptor[] statements = EditorCategories.getStatementsForCategory(newCategory);
			this.statementDropdown.setItems(); // Clear
			this.statementDropdown.addItemsTransform(statements, s -> {
				return new DropdownItem(s, s.name).withDescription(s.desc);
			});
			this.statementDropdown.asEnabled(true);
		}
		
		this.reinitialize();
		this.parent.redrawMessage(jda);
	}
}
