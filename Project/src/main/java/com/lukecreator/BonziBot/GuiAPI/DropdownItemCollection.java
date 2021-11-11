package com.lukecreator.BonziBot.GuiAPI;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

/**
 * ArrayList of {@link DropdownItem}s with extended methods for interacting with its items.
 * @author Lukec
 */
public class DropdownItemCollection extends ArrayList<DropdownItem> {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Resolve an array of element IDs to their indexes.
	 * @param ids
	 * @return
	 */
	public int[] resolveIndexesById(String...ids) {
		if(ids == null || ids.length == 0)
			return new int[0];
		
		int[] ret = new int[ids.length];
		for(int i = 0; i < ret.length; i++)
			ret[i] = -1;
		
		for(int i = 0; i < ids.length; i++) {
			String cid = ids[i];
			for(int test = 0; test < this.size(); test++)
				if(this.get(test).id.equals(cid)) {
					ret[i] = test;
					break;
				}
		}
		
		return ret;
	}
	/**
	 * Resolve an index from a chosen id.
	 * @param ids
	 * @return
	 */
	public int resolveIndexById(String id) {
		if(id == null)
			return -1;
		
		for(int i = 0; i < this.size(); i++) {
			if(this.get(i).id.equals(id))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Convert to a JDA supported version of {@link DropdownItem}s.
	 * @return
	 */
	public List<SelectOption> toDiscord() {
		List<SelectOption> ret = new ArrayList<SelectOption>();
		
		for(DropdownItem dd: this)
			ret.add(dd.toDiscord());
		
		return ret;
	}
}
