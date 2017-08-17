package com.dc.common;

public class Api {

	// 验证码接口
	public static final String verifyCodeUrl = "https://c.dcits.com/dcAuthCode/Image3Servlet?t=";
	// 验证码验证接口
	public static final String verifyUrl = "https://c.dcits.com/dcAuthCode/ValServlet?callback=random";
	// 登陆接口
	public static final String loginUrl = "https://c.dcits.com/dcAuthCode/AuthCodeUser";
	// 免验证码登陆接口 (暂未研究)
	public static final String loginUrl2 = "https://c.dcits.com/bg/bg/main/index.jsp";
	// 重定向url,登陆之后必须调一下这个接口，以获取JSESSIONID
	public static final String jsessionidUrl="https://c.dcits.com/itswk/";
	// 查询每周报工列表
	public static final String reportListUrl = "https://c.dcits.com/itswk/forward/L215d29yay90aW1lU2hlZXQvdGltZXNoZWV0aW5kZXg=";
	// 提交报工数据
	public static final String reportUrl = "https://c.dcits.com/itswk/mywork/timeSheet/savetimesheet";
}
