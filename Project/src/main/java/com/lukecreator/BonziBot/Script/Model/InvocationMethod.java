package com.lukecreator.BonziBot.Script.Model;

/**
 * A method of invoking a script. Can be implemented.
 * @author Lukec
 */
public interface InvocationMethod {
	
	public enum Implementation {
		COMMAND("Run through a command."),
		BUTTON("Run through a button press."),
		PHRASE("Run when a word/phrase is said."),
		TIMED("Run on a timed basis."),
		
		JOIN("Run when a member joins."),
		LEAVE("Run when a member leaves.");
		
		public final String desc;
		private Implementation(String desc) {
			this.desc = desc;
		}
	}
	
	public Implementation getImplementation();
	public String[] getEventVariables();
}
