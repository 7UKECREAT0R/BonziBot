package com.lukecreator.BonziBot;

import javax.security.auth.login.LoginException;

public class App {
	public static void main(String[] args) throws LoginException, InterruptedException {
		new BonziBot(true).start();
	}
}