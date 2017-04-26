package com.dc.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 读取配置文件
 * 
 * @author yangzhenyu
 * 
 */
public class ConfigUtil {
	private static final Logger logger = LogManager.getLogger(OcrUtil.class);

	private static Properties props = new Properties();

	static {
		InputStream is = ConfigUtil.class.getResourceAsStream("/config.properties");
		try {
			props.load(is);
		} catch (IOException e) {
			logger.fatal("配置文件路径有误：", e);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error("配置文件读取出错：", e);
				}
			}
		}
	}

	public static String getProp(String key) {
		return props.getProperty(key);
	}

	public static void main(String[] args) {
		String p = ConfigUtil.getProp("isBizTrip");
		System.out.println(p);
	}
}
