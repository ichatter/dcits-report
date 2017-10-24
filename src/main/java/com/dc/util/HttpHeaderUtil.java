package com.dc.util;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;

public class HttpHeaderUtil {

	public static String getResponseCharset(HttpResponse resp) {
		ContentType ctype = ContentType.getOrDefault(resp.getEntity());
		if (ctype.getCharset() != null)
			return ctype.getCharset().name();
		return null;
	}
}
