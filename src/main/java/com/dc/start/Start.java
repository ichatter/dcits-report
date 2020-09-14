package com.dc.start;

import static com.dc.session.Session.MAX_TRY_TIMES;
import static com.dc.session.Session.tried_times;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dc.service.OcrService;
import com.dc.service.UserService;
import com.dc.session.SessionUtil;
import com.dc.util.HttpUtil;

public class Start {
	private static final Logger logger = LogManager.getLogger(Start.class);

	private static HttpClient client;
	private static boolean isFailed = true;

	public static void main(String[] args) {
		// 获取start.sh存入系统环境变量的账户信息
		SessionUtil.setUsernamePassword(System.getenv("username"), System.getenv("password"));

		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				String dateStr = new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
				logger.info("报工开始了，" + SessionUtil.getUsername() + "，今天是：" + dateStr);
				isFailed = true;
				while (isFailed) {
					if (tried_times >= MAX_TRY_TIMES) {
						try {
							logger.info("连续尝试次数超过" + MAX_TRY_TIMES + "次，先休息1小时...");
							Thread.sleep(1000 * 3600);
							tried_times = 0;
						} catch (InterruptedException e) {
							logger.error("线程休息出错：", e);
						}
					}

					tried_times++;
					try {
						new Start().start();
					} finally {
						HttpClientUtils.closeQuietly(client);
					}
				}
			}
		}, 0, 1, TimeUnit.DAYS);// 每24小时报工一次
	}

	private void start() {
		client = HttpUtil.getHttpClient();// 每次报工启用新的client
		OcrService ocrService = new OcrService(client);
		while (true) {
			logger.info("正在获取验证码图片...");
			if (ocrService.getOcrInputStream() == null) {
				logger.info("获取验证码失败，重新运行...");
				return;
			}

			logger.info("正在识别验证码图片...");
			if (StringUtils.isBlank(ocrService.recognizeCode())) {
				logger.info("识别失败，重新运行...");
				continue;
			}

			logger.info("正在验证验证码...");
			if (!ocrService.verifyingCode()) {
				logger.info("验证失败，重新运行...");
				return;
			}
			break;
		}

		UserService userService = new UserService(client);
		logger.info("正在登陆...");
		boolean isLogined = userService.login();
		if (!isLogined) {
			logger.info("登陆失败，重新运行...");
			return;
		}

		logger.info("登陆成功！正在查询并组装报工数据...");
		userService.getReportData();
		logger.info("组装报工数据成功！正在提交报工...");
		boolean isSuccess = userService.report();
		if (isSuccess) {
			logger.info("恭喜，报工成功！");
			isFailed = false;// 程序成功结束时，修改执行状态
			tried_times = 0;// 重置为0
		} else {
			logger.warn("-_-!报工失败！");
		}
	}

}
