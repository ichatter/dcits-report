package com.dc.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpHeaderUtil {
	private static final Logger logger = LogManager.getLogger(HttpHeaderUtil.class);

	public static List<Header> defaultHeaders() {
		Header h = new BasicHeader("Accept", "application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, */*");
		Header h2 = new BasicHeader("Accept-Encoding", "gzip, deflate, br");
		Header h3 = new BasicHeader("Accept-Language", "zh-CN");
		Header h4 = new BasicHeader("Cache-Control", "no-cache");
		Header h5 = new BasicHeader("Connection", "keep-alive");
		Header h6 = new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
//		Header h8 = new BasicHeader("Content-Type", "application/x-www-form-urlencoded");
		return Arrays.asList(h, h2, h3, h4, h5, h6);
	}

	public static Map<String, String> defaultHeadersMap() {
		List<Header> headers = defaultHeaders();
		Map<String, String> map = new HashMap<String, String>();
		for (Header h : headers) {
			map.put(h.getName(), h.getValue());
		}
		return map;
	}

	public static String getResponseCharset(HttpResponse resp) {
		ContentType ctype = ContentType.getOrDefault(resp.getEntity());
		if (ctype.getCharset() != null)
			return ctype.getCharset().name();
		return null;
	}
}
