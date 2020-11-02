package com.lukecreator.BonziBot.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.lukecreator.BonziBot.InternalLogger;

public class DataSerializer {
	
	public static final String baseDataPath = "/home/pi/";
	public static String currentPath = baseDataPath;
	
	static String ensureSuffix(String path) {
		if(path.endsWith(".ser")) return path;
		else return path + ".ser";
	}
	static void safelyCloseStreams(FileInputStream a, ObjectInputStream b) {
		try {
			if(a != null)
				a.close();
		} catch(IOException e) {
			InternalLogger.printError(e);
		}
		try {
			if(b != null)
				b.close();
		} catch(IOException e) {
			InternalLogger.printError(e);
		}
	}
	static void safelyCloseStreams(FileOutputStream a, ObjectOutputStream b) {
		try {
			if(a != null)
				a.close();
		} catch(IOException e) {
			InternalLogger.printError(e);
		}
		try {
			if(b != null)
				b.close();
		} catch(IOException e) {
			InternalLogger.printError(e);
		}
	}
	
	/*
	 * Write an object to a file.
	 */
	public static void writeObject(Object obj, String fileName) {
		FileOutputStream fileOut = null;
		ObjectOutputStream objectOut = null;
    	try {
    		fileName = ensureSuffix(fileName);
    		fileOut = new FileOutputStream(currentPath + fileName);
    		objectOut = new ObjectOutputStream(fileOut);
    		objectOut.writeObject(obj);
    	} catch(IOException exc) {
    		InternalLogger.printError(exc);
    	} finally {
    		safelyCloseStreams(fileOut, objectOut);
    	}
	}
	/*
	 * Retrieve a written file.
	 */
	public static Object retrieveObject(String fileName) {
		
		fileName = ensureSuffix(fileName);
		String fullPath = currentPath + fileName;
		File f = new File(fullPath);
		if(!f.exists()) return null;
		
		Object toReturn = null;
		FileInputStream fileIn = null;
		ObjectInputStream objectIn = null;
    	try {
    		fileIn = new FileInputStream(currentPath + fileName);
    		objectIn = new ObjectInputStream(fileIn);
    		toReturn = objectIn.readObject();
    	} catch(IOException exc) {
    		InternalLogger.printError(exc);
    	} catch (ClassNotFoundException exc) {
    		InternalLogger.printError(exc);
		} finally {
			safelyCloseStreams(fileIn, objectIn);
		}
    	return toReturn;
	}
}
