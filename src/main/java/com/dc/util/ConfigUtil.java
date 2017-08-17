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
	private static final Logger logger = LogManager.getLogger(OcrKingUtil.class);

	private static Properties props = new Properties();

	private static void loadConfig() {
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

	/**
	 * 每次获取属性时，都重新从config.properties中读取最新数据，而不在内存中缓存
	 * 
	 * @param key
	 * @return
	 */
	public static String getProp(String key) {
		loadConfig();
		return props.getProperty(key);
	}

	public static void main(String[] args) throws InterruptedException {
		while (true) {
			String p = ConfigUtil.getProp("isBizTrip");
			System.out.println(p);
			Thread.sleep(3000);
		}
	}
}
