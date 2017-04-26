package com.dc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

	public static String removeEmptyLine(InputStream htmlStr) throws IOException {
		return removeEmptyLine(htmlStr, "utf-8");
	}

	public static String removeEmptyLine(InputStream io, String charset) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(io, charset));
		String line = "";
		StringBuffer sb = new StringBuffer();
		while ((line = br.readLine()) != null) {
			if (StringUtils.isNotBlank(line.trim())) {
				sb.append(line + "\n");
			}
		}
		br.close();
		return sb.toString();
	}
}
