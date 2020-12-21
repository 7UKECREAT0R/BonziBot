package com.lukecreator.BonziBot.Data;

import java.io.EOFException;

public interface IStorableData {

	public void saveData();
	public void loadData() throws EOFException;
	
}
