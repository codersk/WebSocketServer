package com.java.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
		while (true) {
			try {
				Thread.sleep(1 * 1000);
				for (String key : sMap.keySet()) {
					Session s = sMap.get(key);
					if (s.isOpen()) {
						Date d = new Date(System.currentTimeMillis());
						s.getBasicRemote().sendText(d.toString());
					} else {
						sMap.remove(key);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}