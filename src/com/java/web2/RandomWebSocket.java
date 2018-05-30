package com.java.web2;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/random")
public class RandomWebSocket {

	private static Queue<Session> sessions = new ConcurrentLinkedQueue<Session>();

	private static Thread randomNumThread;

	static {
		randomNumThread = new Thread() {
			@Override
			public void run() {
				Random r = new Random();
				while (true) {
					int randomNum = r.nextInt(11); // integer between 0 and 10
													// inclusive
					for (Session session : sessions) {
						if (session.isOpen()) {
							try {
								// // Date d = new
								// Date(System.currentTimeMillis());
								String pattern = "dd-MM-yyyy";
								SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

								String d = simpleDateFormat.format(new Date());
								session.getBasicRemote().sendText(
										"[{\"date\":\"" + d + "\",\"value\":" + Integer.toString(randomNum) + "}]");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		};
		randomNumThread.start();
	}

	@OnOpen
	public void onOpen(Session session) {
		sessions.add(session);
	}

	@OnError
	public void onError(Session session, Throwable t) {
		t.printStackTrace();
		sessions.remove(session);
	}

	@OnClose
	public void onClose(Session session) {
		sessions.remove(session);
	}

}
