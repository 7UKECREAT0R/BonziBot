package com.lukecreator.BonziBot;

import javax.security.auth.login.LoginException;

public class App {
	
	public static void main(String[] args) throws LoginException, InterruptedException {
		
		BonziBot bb = new BonziBot(true);
		bb.loadData();
		bb.start();
		
	}
	
}
