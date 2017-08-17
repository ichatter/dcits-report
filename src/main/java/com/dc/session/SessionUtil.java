package com.dc.session;

public class SessionUtil {
	private static Session session = new Session();

	public static Session getSession() {
		return session;
	}

	public static void setUsernamePassword(String username, String password) {
		session.setUsername(username);
		session.setPassword(password);
	}

	public static String getUsername() {
		return session.getUsername();
	}

	public static String getPassword() {
		return session.getPassword();
	}

}
