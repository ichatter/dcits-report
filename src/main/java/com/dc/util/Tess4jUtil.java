package com.dc.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sourceforge.tess4j.ITessAPI.TessPageSegMode;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

public class Tess4jUtil {
	private static final Logger logger = LogManager.getLogger(Tess4jUtil.class);

	private static ITesseract instance;

	static {
		// In case you don't have your own tessdata, let it also be
		// extracted for you
		// 这样就能使用classpath目录下的训练库了
		File tessDataFolder = LoadLibs.extractTessResources("tessdata");
		instance = new Tesseract(); // JNA Interface Mapping
		// ITesseract instance = new Tesseract1(); // JNA Direct Mapping
		// instance.setDatapath("<parentPath>"); // replace <parentPath>
		// with
		// path to parent directory of tessdata
		// instance.setLanguage("eng");

		instance.setDatapath(tessDataFolder.getAbsolutePath());
		instance.setPageSegMode(TessPageSegMode.PSM_SINGLE_WORD);// 可以避免识别结果中出现空格
		instance.setConfigs(Arrays.asList(new String[] { "chars" }));
		// instance.setTessVariable("tessedit_char_whitelist",
		// "0123456789abcd");
		// instance.setTessVariable("tessedit_char_blacklist", "123344");
	}

	public static String recognize(InputStream is) {
		try {
//			String filePath = FileUtils.getTempDirectoryPath() + File.separatorChar + "ocrtmp.jpg";
//			File tmpFile = new File(filePath);
//			FileUtils.copyInputStreamToFile(is, tmpFile);
			BufferedImage image = ImageIO.read(is);

			// 识别验证码图片(去掉返回结果的换行符和空格)
			String ocrCode = instance.doOCR(image).trim();
			logger.info("验证码图片识别结果：" + ocrCode);
			return ocrCode;
		} catch (IOException e) {
			logger.error("验证码图片流转换异常：", e);
		} catch (TesseractException e) {
			logger.error("验证码图片识别异常：", e);
		}
		return "";
	}

	public static void main(String[] args) {
		try {
			// test print the "tessdata" directory path
			Enumeration<URL> resources = LoadLibs.class.getClassLoader().getResources("tessdata");
			while (resources.hasMoreElements()) {
				URL resourceUrl = resources.nextElement();
				System.out.println(resourceUrl);
			}
			File tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "ocrtmp.jpg");

			// 识别验证码图片
			String ocrCode = instance.doOCR(tmpFile);// 返回结果有换行符
			logger.info("验证码图片识别结果：" + ocrCode);
		} catch (Exception e) {
			logger.error("验证码图片识别异常：", e);
		}
	}

}
