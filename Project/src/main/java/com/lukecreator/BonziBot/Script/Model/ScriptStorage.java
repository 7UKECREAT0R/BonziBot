package com.lukecreator.BonziBot.Script.Model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;

/**
 * Provides a means of letting scripts store basic primitives on disk for later.
 * @author Lukec
 */
public class ScriptStorage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Max bytes of memory a script can hold. A block is just a user-facing way of saying byte (kinda like 3ds lol)
	public static transient final int MAX_BLOCKS = 0x4000; // 16 KiB
	
	public enum StorageType {
		LONG,
		DOUBLE,
		BOOLEAN,
		STRING,
		
		// Discord Entity IDs (Long)
		ENTITYCHANNEL,
		ENTITYMEMBER,
		ENTITYROLE
	}
	
	/**
	 * An exception which is to be thrown when an invalid block of data is given to the storage entry constructor.
	 * @author Lukec
	 */
	public class StorageException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	/**
	 * An exception which is to be thrown when the user has run out of available storage blocks.
	 * @author Lukec
	 */
	public class OutOfBlocksException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	/**
	 * An entry in the storage map.
	 * @author Lukec
	 */
	public class StorageEntry implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		// Number of storage blocks this value takes.
		public final short blocks;
		
		public final long key;
		public final StorageType type;
		public final Object data;
		
		public StorageEntry(long key, Object data) throws StorageException {
			this.key = key;
			if(data instanceof ISnowflake) {
				this.blocks = 8;
				this.data = new Long(((ISnowflake)data).getIdLong());
				if(data instanceof MessageChannel)
					this.type = StorageType.ENTITYCHANNEL;
				else if(data instanceof Member)
					this.type = StorageType.ENTITYMEMBER;
				else if(data instanceof Role)
					this.type = StorageType.ENTITYROLE;
				else
					this.type = StorageType.LONG;
			} else {
				if(data instanceof Long) {
					this.blocks = 8;
					this.type = StorageType.LONG;
					this.data = (Long)data;
				} else if(data instanceof Double) {
					this.blocks = 8;
					this.type = StorageType.DOUBLE;
					this.data = (Double)data;
				} else if(data instanceof Boolean) {
					this.blocks = 1;
					this.type = StorageType.BOOLEAN;
					this.data = (Boolean)data;
				} else if(data instanceof String) {
					this.blocks = (short)((String)data).getBytes(StandardCharsets.UTF_8).length;
					this.type = StorageType.STRING;
					this.data = (String)data;
				} else {
					throw new StorageException();
				}
			}
		}
	}
	
	public ScriptStorage() {
		this.storage = new HashMap<Long, StorageEntry>();
		this.maxBlocks = MAX_BLOCKS;
	}
	
	int maxBlocks = 0;
	int blocks = 0;
	public final HashMap<Long, StorageEntry> storage;
	
	public static long toKey(Object obj) {
		if(obj instanceof Long)
			return ((Long)obj).longValue();
		if(obj instanceof ISnowflake)
			return ((ISnowflake)obj).getIdLong();
		if(obj instanceof Double)
			return ((Double)obj).longValue();
		if(obj instanceof String) {
			String string = (String)obj;
			long hash = 1125899906842597L;
			int len = string.length();
			for (int i = 0; i < len; i++) {
				hash = 31 * hash + string.charAt(i);
			}
			return hash;
		}
		if(obj instanceof Boolean) {
			return ((Boolean)obj).booleanValue() ? 1 : 0;
		}
		
		return obj.hashCode();
	}
	
	/**
	 * Put a key/value pair of data into this ScriptStorage.
	 * @param key
	 * @param data
	 * @throws StorageException If the object given is not supported in storage.
	 * @throws OutOfBlocksException If this script is out of storage blocks.
	 */
	public void putData(long key, Object data) throws StorageException, OutOfBlocksException {
		if(this.storage.containsKey(key)) {
			StorageEntry old = this.storage.get(key);
			this.blocks -= old.blocks;
		}
		
		StorageEntry entry = new StorageEntry(key, data);
		this.blocks += entry.blocks;
		
		if(this.blocks > maxBlocks)
			throw new OutOfBlocksException();
		
		this.storage.put(key, entry);
	}
	/**
	 * Get data from this storage
	 * @param key
	 * @return
	 */
	public StorageEntry getData(long key) {
		if(this.storage.containsKey(key)) {
			return this.storage.get(key);
		} else {
			return null;
		}
	}
	
	/**
	 * Remove a value from this ScriptStorage based off of its key.
	 * @param key
	 * @return The old value, `null` if it wasn't there in the first place.
	 */
	public StorageEntry removeData(long key) {
		StorageEntry entry = this.storage.remove(key);
		if(entry != null)
			this.blocks -= entry.blocks;
		return entry;
	}
	/**
	 * Clear all data from this ScriptStorage.
	 */
	public void clearData() {
		this.blocks = 0;
		this.storage.clear();
	}
	
}
