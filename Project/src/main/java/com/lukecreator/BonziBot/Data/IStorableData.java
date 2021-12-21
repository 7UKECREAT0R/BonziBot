package com.lukecreator.BonziBot.Data;

import java.io.EOFException;

/**
 * Acts as a template for a set of data which should be saved and loaded.
 * Part of Bonzi's shoddy "database" system which caches EVERYTHING in RAM
 * @author Lukec
 */
public interface IStorableData {
	public void saveData();
	public void loadData() throws EOFException;
}
