package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.HashMap;

import com.lukecreator.BonziBot.InternalLogger;
import com.lukecreator.BonziBot.TimeSpan;
import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;

import net.dv8tion.jda.api.entities.User;

/**
 * Manages the global grid.
 * @author Lukec
 *
 */
public class GridManager implements IStorableData {
	
	public static final String FILE_GRID = "the_grid";
	public static final String FILE_GRID_TIMES = "the_grid_cooldowns";
	public static final int SIZE_X = 16;
	public static final int SIZE_Y = 12;
	public static final long COOLDOWN = TimeSpan.fromHours(1).ms;
	
	public enum TileType {
		BLACK("⬛"),
		WHITE("⬜"),
		RED("🟥"),
		ORANGE("🟧"),
		YELLOW("🟨"),
		GREEN("🟩"),
		BLUE("🟦"),
		PURPLE("🟪"),
		
		LAUGH("😂"),
		HEART("❤️"),
		BALL("🏀"),
		SALT("🧂"),
		CAR("🚗"),
		CONSTRUCTION("🚧"),
		APPLE("🍎"),
		BLOCK("🚫"),
		
		OK("🆗"),
		COOL("🆒"),
		FREE("🆓"),
		NEW("🆕");
		
		public final String emoji;
		private TileType(String emoji) {
			this.emoji = emoji;
		}
	}
	
	public GridManager() {
		this.cooldowns = new HashMap<Long, Long>();
		this.tiles = new TileType[SIZE_X][SIZE_Y];
		for(int x = 0; x < SIZE_X; x++) {
			this.tiles[x] = new TileType[SIZE_Y];
			for(int y = 0; y < SIZE_Y; y++)
				this.tiles[x][y] = TileType.BLACK;
		}
		
		InternalLogger.print("Initialized GridManager.");
	}
	
	HashMap<Long, Long> cooldowns;
	TileType[][] tiles;
	
	/**
	 * Return the message form of this grid.
	 * @return
	 */
	public String getString() {
		StringBuilder sb = new StringBuilder();
		for(int y = 0; y < SIZE_Y; y++) {
			for(int x = 0; x < SIZE_X; x++)
				sb.append(tiles[x][y].emoji);
			sb.append('\n');
		}
		return sb.toString();
	}
	public void resetCooldowns() {
		this.cooldowns.clear();
	}
	
	public long timeLeft(User user) {
		return this.timeLeft(user.getIdLong());
	}
	public long timeLeft(long userId) {
		if(this.cooldowns.containsKey(userId)) {
			long nextTime = this.cooldowns.get(userId);
			long diff = nextTime - System.currentTimeMillis();
			if(diff < 0)
				this.cooldowns.remove(userId);
			return diff;
		}
		return -1;
	}
	
	public void putTile(int x, int y, TileType tile, User user) throws Exception {
		this.putTile(x, y, tile, user.getIdLong());
	}
	public void putTile(int x, int y, TileType tile, long userId) throws Exception {
		if(x < 0 || y < 0 || x > SIZE_X || y > SIZE_Y)
			throw new Exception("Tile out of range.");
		this.tiles[x][y] = tile;
		this.cooldowns.put(userId, System.currentTimeMillis() + COOLDOWN);
	}
	
	
	
	@Override
	public void saveData() {
		DataSerializer.writeObject(this.tiles, FILE_GRID);
		DataSerializer.writeObject(this.cooldowns, FILE_GRID_TIMES);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object o = DataSerializer.retrieveObject(FILE_GRID);
		if(o != null) {
			TileType[][] temp = (TileType[][])o;
			// Check size is the same. Otherwise reset.
			if(temp.length == SIZE_X && temp[0].length == SIZE_Y)
				this.tiles = temp;
		}
		
		Object o2 = DataSerializer.retrieveObject(FILE_GRID_TIMES);
		if(o2 != null) {
			this.cooldowns = (HashMap<Long, Long>)o2;
		}
	}
}
