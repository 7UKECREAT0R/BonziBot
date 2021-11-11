package com.lukecreator.BonziBot.GuiAPI;

/**
 * An editable entry to go into a GuiEditDialog.
 * @author Lukec
 */
public abstract class GuiEditEntry {
	
	public boolean optional = false;
	public String emoji;
	public String title;
	public String description;
	
	/**
	 * Get the action ID of the element used to control this entry.
	 * @return
	 */
	public abstract String getActionID();
	
	/**
	 * Return this GuiEditEntry but as an optional value.
	 * @return
	 */
	public GuiEditEntry optional() {
		this.optional = true;
		return this;
	}
	
	/**
	 * Whether a value has been given to this entry or not.
	 * @return
	 */
	public abstract boolean valueGiven();
	/**
	 * Get the object this entry was set to.
	 * @return
	 */
	public abstract Object getValue();
	/**
	 * Get the formatting string version of the set object.
	 * @return
	 */
	public abstract String getStringValue();
	
}
