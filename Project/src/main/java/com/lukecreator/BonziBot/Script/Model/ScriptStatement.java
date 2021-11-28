package com.lukecreator.BonziBot.Script.Model;

import java.io.Serializable;

import com.lukecreator.BonziBot.GuiAPI.GuiEditEntry;
import com.lukecreator.BonziBot.Script.Editor.StatementCategory;

import net.dv8tion.jda.api.entities.Guild;

/**
 * A statement in the script waiting to be executed.
 * @author Lukec
 */
public interface ScriptStatement extends Serializable {
	
	public static final long serialVersionUID = 1L;
	
	/**
	 * Get the keyword used in the scripting language to invoke this statement.
	 * @return
	 */
	public String getKeyword();
	/**
	 * "Decompile" this into a high level language statement so that users can copy+paste it to other places.
	 * @return
	 */
	public String getAsCode();
	/**
	 * Get GUI entries of the args to be passed into this statement.
	 * @param caller The calling script.
	 * @param server TODO
	 * @return
	 */
	public GuiEditEntry[] getArgs(Script caller, Guild server);
	/**
	 * Get the name of a variable which this statement could potentially create. <code>null</code> if none.
	 * @return
	 */
	public String getNewVariable();
	
	/**
	 * Get the category this statement type resides in.
	 * @return
	 */
	public StatementCategory getCategory();
	
	/**
	 * Given a set of inputs guaranteed to be the length of the requested args, parse that information.
	 * @param inputs
	 */
	public void parse(Object[] inputs);
	/**
	 * Execute this statement in the context given.
	 * @param info
	 */
	public void execute(ScriptContextInfo info, ScriptExecutor context);
}
