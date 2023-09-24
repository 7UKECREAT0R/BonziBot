package com.lukecreator.BonziBot.Gui;

import java.awt.Point;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.GuiAPI.Gui;
import com.lukecreator.BonziBot.GuiAPI.GuiButton;
import com.lukecreator.BonziBot.GuiAPI.GuiButton.ButtonColor;
import com.lukecreator.BonziBot.GuiAPI.GuiNewline;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class GuiTicTacToe extends Gui {
	enum Turn {
		OWNER,		// 	X
		OPPONENT	//	O
	}
	enum Tile {
		NONE,
		X,	// Owner
		O	// Opponent
	}
	enum WinScheme {
		TIE,
		HORIZONTAL,
		VERTICAL,
		DIAGONAL_DSC,
		DIAGONAL_ASC
	}
	
	Tile[][] grid;
	Turn turn;
	long userId;
	long opponentId;
	String user;
	String opponent;
	
	boolean ended = false;
	boolean tie = false;
	String winner = null;
	
	// used for drawing buttons
	WinScheme scheme;
	int winOffset;
	
	public GuiTicTacToe(User owner, User opponent) {
		this.userId = owner.getIdLong();
		this.user = owner.getAsMention();
		this.opponentId = opponent.getIdLong();
		this.opponent = opponent.getAsMention();
		this.turn = BonziUtils.randomBoolean() ? Turn.OPPONENT : Turn.OWNER;
		this.grid = new Tile[3][3];
		for(int x = 0; x < 3; x++)
		for(int y = 0; y < 3; y++)
			this.grid[x][y] = Tile.NONE;
	}
	
	@Override
	public void initialize(JDA jda) {
		this.parent.ownerWhitelist.add(this.opponentId);
		this.reinitialize();
	}
	public void reinitialize() {
		this.elements.clear();
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++) {
				this.elements.add(this.buttonForTile(x, y));
			}
			if(y != 2)
				this.elements.add(new GuiNewline());
		}
	}
	GuiButton buttonForTile(int x, int y) {
		Tile tile = this.grid[x][y];
		String actionId = this.encode(x, y);
		
		GuiButton.ButtonColor color = GuiButton.ButtonColor.GRAY;
		
		if(this.ended && this.scheme != WinScheme.TIE) {
			if(this.scheme == WinScheme.HORIZONTAL) {
				if(y == this.winOffset)
					color = GuiButton.ButtonColor.GREEN;
			} else if(this.scheme == WinScheme.VERTICAL) {
				if(x == this.winOffset)
					color = GuiButton.ButtonColor.GREEN;
			} else if(this.scheme == WinScheme.DIAGONAL_DSC) {
				if(x == y)
					color = GuiButton.ButtonColor.GREEN;
			} else if(this.scheme == WinScheme.DIAGONAL_ASC) {
				if(y == 2 - x)
					color = GuiButton.ButtonColor.GREEN;
			}
		}
		
		switch(tile) {
			case NONE:
				return GuiButton.singleEmoji(GenericEmoji.fromEmoji("⬛"), actionId).withColor(ButtonColor.GRAY);
			case O:
				return GuiButton.singleEmoji(GenericEmoji.fromEmoji("⭕"), actionId).withColor(color); 
			case X:
				return GuiButton.singleEmoji(GenericEmoji.fromEmoji("❌"), actionId).withColor(color); 
			default:
				return new GuiButton("error", GuiButton.ButtonColor.RED, actionId);
		}
	}
	
	@Override
	public Object draw(JDA jda) {
		if(this.ended) {
			if(this.tie) {
				return "`The tic-tac-toe ended in a tie.`";
			} else {
				return this.winner + "` won the tic-tac-toe!`";
			}
		} else {
			String a = this.turn == Turn.OWNER ? this.user : this.opponent;
			String b = this.turn == Turn.OWNER ? "❌" : "⭕";
			return "" + a + "`'s turn! (" + b + ")`";
		}
	}
	
	@Override
	public void onButtonClick(String actionId, long executorId, JDA jda) {
		if(this.ended) return;
		Point clicked = this.decode(actionId);
		if(clicked == null)
			return;
		int x = clicked.x;
		int y = clicked.y;
		if(x > 2) x = 2;
		if(x < 0) x = 0;
		if(y > 2) y = 2;
		if(y < 0) y = 0;
		
		if(this.grid[x][y] != Tile.NONE)
			return;
		
		long allowed = this.turn == Turn.OWNER ? this.userId : this.opponentId;
		if(executorId != allowed)
			return;
		
		this.grid[x][y] = this.turn == Turn.OWNER ? Tile.X : Tile.O;
		this.turn = this.turn == Turn.OWNER ? Turn.OPPONENT : Turn.OWNER;
		
		this.tryEndGame();
		this.reinitialize();
		this.parent.redrawMessage(jda);
	}
	String encode(int x, int y) {
		return "loc" + x + "" + y;
	}
	Point decode(String in) {
		if(in.length() != 5)
			return null;
		String part = in.substring(3);
		char a = part.charAt(0);
		char b = part.charAt(1);
		return new Point(this.charToInt(a), this.charToInt(b));
	}
	int charToInt(char c) {
		return (int)(c - '0');
	}
	
	void tryEndGame() {
		// Horizontal
		for(int y = 0; y < 3; y++) {
			if(this.grid[0][y] == this.grid[1][y]
			&& this.grid[1][y] == this.grid[2][y]
			&& this.grid[0][y] != Tile.NONE) {
				this.winner = this.grid[0][y] == Tile.X ? this.user : this.opponent;
				this.ended = true;
				this.tie = false;
				this.scheme = WinScheme.HORIZONTAL;
				this.winOffset = y;
				return;
			}
		}
		
		// Vertical
		for(int x = 0; x < 3; x++) {
			if(this.grid[x][0] == this.grid[x][1]
			&& this.grid[x][1] == this.grid[x][2]
			&& this.grid[x][0] != Tile.NONE) {
				this.winner = this.grid[x][0] == Tile.X ? this.user : this.opponent;
				this.ended = true;
				this.tie = false;
				this.scheme = WinScheme.VERTICAL;
				this.winOffset = x;
				return;
			}
		}
		
		// Diagonals
		if(this.grid[0][0] == this.grid[1][1]
		&& this.grid[1][1] == this.grid[2][2]
		&& this.grid[0][0] != Tile.NONE) {
			this.winner = this.grid[0][0] == Tile.X ? this.user : this.opponent;
			this.ended = true;
			this.tie = false;
			this.scheme = WinScheme.DIAGONAL_DSC;
			this.winOffset = 0;
			return;
		}
		if(this.grid[0][2] == this.grid[1][1]
		&& this.grid[1][1] == this.grid[2][0]
		&& this.grid[0][2] != Tile.NONE) {
			this.winner = this.grid[0][2] == Tile.X ? this.user : this.opponent;
			this.ended = true;
			this.tie = false;
			this.scheme = WinScheme.DIAGONAL_ASC;
			this.winOffset = 0;
			return;
		}
		
		// Tie?
		boolean allUsed = true;
		esc_tie:
		for(int x = 0; x < 3; x++) {
			for(int y = 0; y < 3; y++) {
				if(this.grid[x][y] == Tile.NONE) {
					allUsed = false;
					break esc_tie;
				}
			}
		}
		if(allUsed) {
			this.ended = true;
			this.tie = true;
			this.scheme = WinScheme.TIE;
			this.winOffset = 0;
			return;
		}
	}
}