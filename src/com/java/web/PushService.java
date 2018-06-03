package com.java.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.websocket.Session;

public class PushService implements Runnable {

	private static PushService instance;
	private static Map<String, Session> sMap = new HashMap<String, Session>();

	private PushService() {
	}

	public static void add(Session s) {
		sMap.put(s.getId(), s);
	}

	public static void initialize() {
		if (instance == null) {
			instance = new PushService();
			new Thread(instance).start();
		}
	}

	@Override
	public void run() {
		Random r = new Random();
		while (true) {
			int randomNum = r.nextInt(11);
			try {
				for (String key : sMap.keySet()) {
					Session s = sMap.get(key);
					if (s.isOpen()) {
						String pattern = "dd-MM-yyyy";
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
						String d = simpleDateFormat.format(new Date());
						s.getBasicRemote()
								.sendText("[{\"date\":\"" + d + "\",\"value\":" + Integer.toString(randomNum) + "}]");
					} else {
						sMap.remove(key);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}