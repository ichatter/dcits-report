package com.dc.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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

import com.alibaba.fastjson.JSONObject;
import com.dc.common.Api;
import com.dc.session.SessionUtil;
import com.dc.util.ConfigUtil;
import com.dc.util.HttpUtil;
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
		params.add(new BasicNameValuePair("username", SessionUtil.getUsername()));
		params.add(new BasicNameValuePair("password", SessionUtil.getPassword()));
		try {
			post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
			HttpResponse resp = client.execute(post);// 登陆
			String charset = HttpUtil.getResponseCharset(resp);
			String respHtml = StringUtil.removeEmptyLine(resp.getEntity().getContent(),
					charset == null ? "utf-8" : charset);

			Document doc = Jsoup.parse(respHtml);
			Elements titles = doc.getElementsByTag("TITLE");
			for (Element title : titles) {
				if (title.hasText() && title.text().contains("Success")) {
					return true;// 登陆成功
				}
			}
		} catch (Exception e) {
			logger.error("登陆失败：", e);
		} finally {
			post.abort();
		}
		return false;
	}

	/**
	 * 需要调用一次这个接口，使该httpClient对象获得响应头Set-Cookie:JSESSIONID=.......
	 */
	private void getJsessionid() {
		HttpGet get = new HttpGet(Api.jsessionidUrl);
		get.setConfig(HttpUtil.getCircularRedirectsConfig());
		HttpResponse resp;
		try {
			resp = client.execute(get);
			EntityUtils.consume(resp.getEntity());

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			get.abort();
		}
	}

	/**
	 * 查询报工表单，并填充报工数据
	 * 
	 * @return
	 */
	public List<NameValuePair> getReportData() {
		getJsessionid();
		HttpGet get = new HttpGet(Api.reportListUrl);
		get.setConfig(HttpUtil.getCircularRedirectsConfig());
		try {
			HttpResponse resp = client.execute(get);
			Document doc = Jsoup.parse(EntityUtils.toString(resp.getEntity()));
			Elements es = doc.select("form#form1 tbody");
			Elements all = es.select("tr");

			String isBizTrip = ConfigUtil.getProp("isBizTrip").toLowerCase();
			String reportType = ConfigUtil.getProp("reportType");
			for (int i = 0; i < all.size() - 1; i++) {// 去掉最末一行
				Element theDay = all.get(i);
				Elements reportTypes = theDay.select("select[name*=bgmold] > option");// 工作类型
				reportTypes.removeAttr("selected");// 先清空默认已经选择的值
				for (Element e : reportTypes) {
					if (e.val().equalsIgnoreCase(reportType)) {
						e.attr("selected", true);
						break;
					}
				}
				if (isBizTrip.equals("true") || isBizTrip.equals("1")) {
					theDay.select("input[name$=businesstrip]").get(0).attr("checked", true).val("1");// 出差
				}
				Element workHour = theDay.select("input[name$=workhours]").get(0);
				if (!workHour.hasAttr("readonly")) {
					workHour.val("8.0");// 非节假日就要报工
				}
				// 将每条记录中的vacationhours字段input属性中的disabled去掉，否则FormElement无法提取该字段值
				theDay.select("input[name$=vacationhours]").get(0).removeAttr("disabled");
			}

			Element f = doc.select("form#form1").get(0);
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
		} finally {
			get.abort();
		}
		return new ArrayList<NameValuePair>();
	}

	/**
	 * 报工
	 * 
	 */
	public boolean report() {
		HttpPost post = new HttpPost(Api.reportUrl);
		try {
			post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
			HttpResponse resp = client.execute(post);
			JSONObject jo = JSONObject.parseObject(EntityUtils.toString(resp.getEntity()));
			// 报工成功，返回json结构的报文{"data" : [ {},{}...],"success" : true}
			if (jo.getBooleanValue("success")) {
				return true;
			}
			logger.warn(jo.getString("error"));
		} catch (Exception e) {
			logger.error("报工异常:", e);
		} finally {
			post.abort();
		}
		return false;
	}
}
