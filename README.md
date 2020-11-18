# BonziBot
The most epicest epic bot for epic gamers! Now with open source code for you guys!
Note this is not the current build that is being run. This is a complete re-code<br />
from scratch because the old codebase sucked major candy bar

## Where can I get it?
https://discordapp.com/oauth2/authorize?client_id=545806922209558537&permissions=8&scope=bot

## But can I steal the code...?
yeah go for it but make sure you adhere to these terms:<br />
- Include a notice or note which states where the snippets of code/code came from.<br />This must be visible somewhere in the bot itself.
- Note what parts of the code you changed. This could be changing some of the text,<br />the color of an embed, etc...
- Your bot must be open source. Self explanitory, release your source code.
- You must have the same license on the bot source code. (GNU-GPL 3.0)

## yoo how the commands work
So essentially this project does rely on `org.reflections` but<br />
to sum it up, I just pull all the classes in the project that<br />
extend `Command` and I just smack em into a list. From there all<br />
you gotta do it loop through and check which one the user wants.<br />
*See: `CommandSystem.java`, `Command.java`, `SayCommand.java`*

## what about those guis
Kinda not so similar idea but it does use abstraction to get<br />
things done! The bot keeps a limited list of `Gui`s according<br />
to each server they're in. Then when you react it just loops<br />
and sends the reaction through crazy pipes and into your eyes!<br />
*See: `Gui.java`, `GuiManager.java`, `GuiTestMenu.java`, `GuiContainer.java`*
