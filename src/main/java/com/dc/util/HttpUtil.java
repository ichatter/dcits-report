package com.dc.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpUtil {

	public static String getResponseCharset(HttpResponse resp) {
		ContentType ctype = ContentType.getOrDefault(resp.getEntity());
		if (ctype.getCharset() != null)
			return ctype.getCharset().name();
		return null;
	}

	public static HttpClient getHttpClient() {
		// Locale.setDefault(new Locale("zh","CN"));
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setDefaultRequestConfig(getRequestConfig());
		return builder.build();
	}

	public static RequestConfig getRequestConfig() {
		return RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(60000).build();
	}

	public static RequestConfig getCircularRedirectsConfig() {
		return RequestConfig.custom().setCircularRedirectsAllowed(true).setConnectTimeout(30000).setSocketTimeout(60000)
				.build();
	}
}
