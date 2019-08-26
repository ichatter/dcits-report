package com.dc.session;


public class Session {

	private String username;
	private String password;
	public static final int MAX_TRY_TIMES = 5;// 报工过程中遇到各种异常时，持续尝试报工的允许最大次数
	/** 当前尝试报工次数，不能超过{@code Session.MAX_TRY_TIMES} */
	public static int tried_times = 0;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
