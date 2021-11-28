package com.lukecreator.BonziBot.Script.Editor;

/**
 * A category that a statement would fit in.
 * @author Lukec
 */
public enum StatementCategory {
	
	SYSTEM("<:script_system:901183464123039754>", "System"),
	DATA("<:script_data:903021825230000188>", "Data"),
	STORAGE("<:script_storage:903021825204846632>", "Storage"),
	LIMITING("<:script_limiting:901186623541243925>", "Limiting"),
	MESSAGES("<:script_messaging:901186623360888864>", "Messages"),
	ROLES("<:script_roles:901186623604162600>", "Roles"),
	ACTIONS("<:script_action:901186623532830790>", "Actions");
	
	public final String emoji;
	public final String name;
	
	private StatementCategory(String emoji, String name) {
		this.emoji = emoji;
		this.name = name;
	}
	
}
