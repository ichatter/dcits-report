package com.dc.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection.KeyVal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import com.dc.common.Api;
import com.dc.session.SessionUtil;
import com.dc.util.ConfigUtil;
import com.dc.util.HttpHeaderUtil;
import com.dc.util.StringUtil;

public class UserService {
	private static final Logger logger = LogManager.getLogger(UserService.class);
	private HttpClient client;
	private List<NameValuePair> params = new ArrayList<NameValuePair>();// 报工表单数据

	public UserService(HttpClient client) {
		this.client = client;
	}

	/**
	 * 登陆报工系统
	 */
	public boolean login() {
		HttpPost post = new HttpPost(Api.loginUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", SessionUtil.getSession().getUsername()));
		params.add(new BasicNameValuePair("password", SessionUtil.getSession().getPassword()));
		try {
			post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
			HttpResponse resp = client.execute(post);// 登陆
			String charset = HttpHeaderUtil.getResponseCharset(resp);
			String respHtml = StringUtil.removeEmptyLine(resp.getEntity().getContent(), charset == null ? "utf-8" : charset);

			// logger.info(respHtml);
			Document doc = Jsoup.parse(respHtml);
			Elements titles = doc.getElementsByTag("TITLE");
			for (Element title : titles) {
				if (title.hasText() && title.text().contains("Success")) {
					HttpGet get = new HttpGet(Api.jsessionidUrl);
					resp = client.execute(get);// 获取JSESSIONID
					StringBuffer sb = new StringBuffer();
					for (Header h : resp.getHeaders("Set-Cookie")) {
						// logger.info(h.getName() + ":" + h.getValue());
						sb.append(h.getValue() + ";");
					}
					SessionUtil.setCookie(new BasicHeader("Cookie", sb.toString()));
					EntityUtils.consume(resp.getEntity());// 释放资源
					return true;// 登陆成功
				}
			}
		} catch (Exception e) {
			logger.error("登陆失败：", e);
		}
		return false;
	}

	/**
	 * 查询报工表单，并填充报工数据
	 * 
	 * @return
	 */
	public List<NameValuePair> getReportData() {
		HttpGet get = new HttpGet(Api.reportListUrl);
		get.setHeader(SessionUtil.getCookie());
		try {
			HttpResponse resp = client.execute(get);
			Document doc = Jsoup.parse(EntityUtils.toString(resp.getEntity()));
			Elements es = doc.select("form[name=formnew] table.tab");
			Elements all = es.select("tr");
			String isBizTrip = ConfigUtil.getProp("isBizTrip").toLowerCase();
			for (int i = 1; i < all.size() - 1; i++) {// 去掉第一行和最末一行
				Element theDay = all.get(i);
				if (isBizTrip.equals("true") || isBizTrip.equals("1")) {
					theDay.select("input[name^=WA_CATALOG_CODE]").get(0).val("1");// 出差
				}
				Element e = theDay.select("input[type=checkbox]").get(0).parent().parent();
				Element workHour = e.siblingElements().select("td input[name^=WRR_NMLTIME]").get(0);
				if (!workHour.hasAttr("readonly")) {
					workHour.val("8.0");// 非节假日就要报工
				}
			}

			Element f = doc.select("form[name=formnew]").get(0);
			if (f instanceof FormElement) {
				FormElement form = (FormElement) f;
				List<KeyVal> list = form.formData();
				for (KeyVal kv : list) {
					// System.out.println(kv.key() + ":" + kv.value());
					params.add(new BasicNameValuePair(kv.key(), kv.value()));
				}
				return params;
			}
		} catch (Exception e) {
			logger.error("查询报工数据异常：", e);
		}
		return new ArrayList<NameValuePair>();
	}

	/**
	 * 报工
	 * 
	 */
	public boolean report() {
		HttpPost post = new HttpPost(Api.reportUrl);
		post.setHeader(SessionUtil.getCookie());
		post.setHeader("Referer", "https://c.dcits.com/bg/bg/baog/weekrep-paper.jsp");
		try {
			post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
			HttpResponse resp = client.execute(post);
			EntityUtils.consume(resp.getEntity());// 释放资源
			// 报工成功，响应头中会有重定向的location属性
			return resp.getFirstHeader("location").getValue().equals("https://c.dcits.com/bg/bg/baog/weekrep-view.jsp");
		} catch (Exception e) {
			logger.error("报工异常:", e);
		}
		return false;
	}
}
