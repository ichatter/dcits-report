package com.dc.util;

/*
 * Copyright (c) 2009 - 2015, Aven's Lab. All rights reserved.
 *                   http://www.ocrking.com
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *  $Id: OcrKing.java,v 0.1 2014/10/29 23:50:18 $
 * The Java script for OcrKing Api
 * By Aven <Aven@OcrKing.Com>
 * Welcome to follow us 
 * http://weibo.com/OcrKing
 * http://t.qq.com/OcrKing
 * Warning! 
 * Before running this script , you should modify some parameter
 * within the post data according to what you wanna do
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class OcrKingUtil {
	
	private static final Logger logger = LogManager.getLogger(OcrKingUtil.class);

	/**
	 * post data with file uploading
	 * @param urlStr  the address to upload
	 * @param dataMap post data
	 * @param fileMap file to upload
	 * @return xml result
	 */
	public static String postMultipart(String urlStr, Map<String, String> dataMap, Map<String, String> fileMap) {
		String res = "";
		HttpURLConnection conn = null;
		String boundary = "----------------------------OcrKing_Client_Aven_s_Lab";
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Referer", "http://lab.ocrking.com/?javaclient0.1)");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; zh-CN; rv:1.9.1.3) Gecko/20100101 Firefox/8.0");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			OutputStream out = new DataOutputStream(conn.getOutputStream());
			// data   
			if (dataMap != null) {
				StringBuffer strBuf = new StringBuffer();
				Iterator<Map.Entry<String, String>> iter = dataMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, String> entry = iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
					strBuf.append(inputValue);
				}
				out.write(strBuf.toString().getBytes());
			}

			// file  
			if (fileMap != null) {
				Iterator<Map.Entry<String, String>> iter = fileMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, String> entry = iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					File file = new File(inputValue);
					String filename = file.getName();

					StringBuffer strBuf = new StringBuffer();
					strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");
					strBuf.append("Content-Type:application/octet-stream\r\n\r\n");

					out.write(strBuf.toString().getBytes());

					DataInputStream in = new DataInputStream(new FileInputStream(file));
					int bytes = 0;
					byte[] bufferOut = new byte[1024];
					while ((bytes = in.read(bufferOut)) != -1) {
						out.write(bufferOut, 0, bytes);
					}
					in.close();
				}
			}

			byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();

			// handle the response 
			StringBuffer strBuf = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuf.append(line).append("\n");
			}
			res = strBuf.toString();
			reader.close();
			reader = null;
		} catch (Exception e) {
			logger.error("异常：" + urlStr,e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return res;
	}
	
	/**
	 * post data with file uploading
	 * @param urlStr  the address to upload
	 * @param dataMap post data
	 * @param ocrStream ocr stream
	 * @return xml result
	 */
	public static String postMultipart2(String urlStr, Map<String, String> dataMap, InputStream ocrStream) {
		String res = "";
		HttpURLConnection conn = null;
		String boundary = "----------------------------OcrKing_Client_Aven_s_Lab";
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Referer", "http://lab.ocrking.com/?javaclient0.1)");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; zh-CN; rv:1.9.1.3) Gecko/20100101 Firefox/8.0");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			OutputStream out = new DataOutputStream(conn.getOutputStream());
			// data   
			if (dataMap != null) {
				StringBuffer strBuf = new StringBuffer();
				Iterator<Map.Entry<String, String>> iter = dataMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, String> entry = iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
					strBuf.append(inputValue);
				}
				out.write(strBuf.toString().getBytes());
			}

			// file  
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
			strBuf.append("Content-Disposition: form-data; name=\"\"; filename=\"\"\r\n");
			strBuf.append("Content-Type:application/octet-stream\r\n\r\n");

			out.write(strBuf.toString().getBytes());

//			DataInputStream ocrStream = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = ocrStream.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			ocrStream.close();

			byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();

			// handle the response 
			strBuf = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuf.append(line).append("\n");
			}
			res = strBuf.toString();
			reader.close();
			reader = null;
		} catch (Exception e) {
			logger.error("异常：" + urlStr,e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return res;
	}
	public static void main(String[] args) {

		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("service", "OcrKingForCaptcha");
		dataMap.put("language", "eng");
		dataMap.put("charset", "4");
		dataMap.put("type", "https://c.dcits.com/dcAuthCode/Image3Servlet?t=" + System.currentTimeMillis());
		dataMap.put("apiKey", ConfigUtil.getProp("ocrApiKey"));
		
		Map<String,String> f=new HashMap<String,String>();
		f.put("name", "C:/Users/yzy/Desktop/11.jpg");
		String ret = OcrKingUtil.postMultipart(ConfigUtil.getProp("ocrApiUrl"), dataMap, f);
		System.out.println(ret);
	}

}
