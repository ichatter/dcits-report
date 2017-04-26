package com.dc.session;

import org.apache.http.Header;

public class SessionUtil {
	private static Session session = new Session();

	public static Session getSession() {
		return session;
	}

	public static void setCookie(Header cookie) {
		session.setCookie(cookie);
	}

	public static Header getCookie() {
		return session.getCookie();
	}

	public static void setUsernamePassword(String username, String password) {
		session.setUsername(username);
		session.setPassword(password);
	}
}
