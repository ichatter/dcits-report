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
import com.dc.util.OcrKingUtil;
import com.dc.util.Tess4jUtil;

public class OcrService {
	private static final Logger logger = LogManager.getLogger(OcrService.class);

	private HttpClient client;
	private InputStream ocrStream;
	private String ocrCode;

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
		// return recognizeCodeByOcrKing();//备用识别方式
		return recognizeCodeByTess4j();
	}

	/**
	 * 通过OcrKing提供的远程api接口进行验证码识别
	 * 
	 * @return
	 */
	private String recognizeCodeByOcrKing() {
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("service", "OcrKingForCaptcha");
		dataMap.put("language", "eng");
		dataMap.put("charset", "4");
		dataMap.put("type", "https://c.dcits.com/dcAuthCode/Image3Servlet?t=" + System.currentTimeMillis());
		dataMap.put("apiKey", ConfigUtil.getProp("ocrApiKey"));

		// String ret =
		// OcrKingUtil.postMultipart(ConfigUtil.getProp("ocrApiUrl"),
		// dataMap, fileMap);
		String ret = OcrKingUtil.postMultipart2(ConfigUtil.getProp("ocrApiUrl"), dataMap, ocrStream);
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

		ocrCode = node.getTextContent();
		return ocrCode;
	}

	/**
	 * 通过本地Tess4j接口进行识别，不存在网络通讯异常的问题，优选识别方式
	 * 
	 * @return
	 */
	private String recognizeCodeByTess4j() {
		ocrCode = Tess4jUtil.recognize(ocrStream);// 使用本地tess4j API进行验证码识别
		return ocrCode;
	}

	/**
	 * 验证验证码
	 * 
	 * @param valcode
	 */
	public boolean verifyingCode() {
		HttpGet get = null;
		try {
			get = new HttpGet(Api.verifyUrl + "&checkcode=" + ocrCode);
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
		// builder.setDefaultHeaders(HttpHeaderUtil.defaultHeaders());
		new OcrService(builder.build()).recognizeCode();
	}
}
