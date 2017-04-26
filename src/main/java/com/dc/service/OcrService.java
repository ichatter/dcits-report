package com.dc.service;

import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.dc.common.Api;
import com.dc.util.ConfigUtil;
import com.dc.util.HttpHeaderUtil;
import com.dc.util.OcrUtil;

public class OcrService {
	private static final Logger logger = LogManager.getLogger(OcrService.class);

	private HttpClient client;
	private InputStream ocrStream;
	private String orcCode;

	public OcrService(HttpClient client) {
		this.client = client;
	}

	/**
	 * 获取验证码图片，直接返回图片流
	 */
	public InputStream getOcrInputStream() {
		HttpGet get = new HttpGet(Api.verifyCodeUrl + System.currentTimeMillis());
		get.setHeader(new BasicHeader("Referer", "https://c.dcits.com/mydcitslogin.html"));// 这个头必需
		try {
			HttpResponse resp = client.execute(get);
			ocrStream = resp.getEntity().getContent();
			logger.info("获取验证码图片成功！");
			return ocrStream;
		} catch (Exception e) {
			logger.error("获取验证码图片异常：", e);
		}
		return null;
	}

	/**
	 * 识别验证码
	 * 
	 * @return
	 */
	public String recognizeCode() {
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("service", "OcrKingForCaptcha");
		dataMap.put("language", "eng");
		dataMap.put("charset", "4");
		dataMap.put("type", "https://c.dcits.com/dcAuthCode/Image3Servlet?t=" + System.currentTimeMillis());
		dataMap.put("apiKey", ConfigUtil.getProp("ocrApiKey"));

		// String ret = OcrUtil.postMultipart(ConfigUtil.getProp("ocrApiUrl"),
		// dataMap, fileMap);
		String ret = OcrUtil.postMultipart2(ConfigUtil.getProp("ocrApiUrl"), dataMap, ocrStream);
		// logger.info("验证码识别响应XML:\n" + ret);

		Node node = null;
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(ret)));
			node = doc.getElementsByTagName("Result").item(0);
			logger.info("验证码图片识别结果：" + node.getTextContent());
		} catch (Exception e) {
			logger.error("验证码图片识别异常：", e);
			return "";
		}

		orcCode = node.getTextContent();
		return orcCode;
	}

	/**
	 * 验证验证码
	 * 
	 * @param valcode
	 */
	public boolean verifyingCode() {
		HttpGet get = new HttpGet(Api.verifyUrl + "&checkcode=" + orcCode);
		try {
			HttpResponse resp = client.execute(get);
			String result = EntityUtils.toString(resp.getEntity());
			boolean b = Pattern.matches(".*\\{\"ifauth\":\"true\"\\}.*", result);
			logger.info("验证码验证结果：" + b);
			return b;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("验证码验证异常：", e);
		} finally {
			get.abort();
		}
		return false;

	}

	public static void main(String[] args) {
		String s = "random({\"ifauth\":\"true\"})";
		boolean b = Pattern.matches(".*\\{\"ifauth\":\"true\"\\}.*", s);
		System.out.println(b);
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setDefaultHeaders(HttpHeaderUtil.defaultHeaders());
		new OcrService(builder.build()).recognizeCode();
	}
}
