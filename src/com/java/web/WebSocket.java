package com.java.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/service")

public class WebSocket {
	@OnOpen
	public void onOpen(Session session) {
		System.out.println("onOpen::" + session.getId());
		Map<String, List<String>> params = session.getRequestParameterMap();

		if (params.get("push") != null && (params.get("push").get(0).equals("TIME"))) {
			PushService.initialize();
			PushService.add(session);
		}
		if (params.get("push") != null && (params.get("push").get(0).equals("Date"))) {
			PushService2.initialize();
			PushService2.add(session);
		}
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("onClose::" + session.getId());
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("onMessage::From=" + session.getId() + " Message=" + message);

		try {
			session.getBasicRemote().sendText("Hello Client " + session.getId() + "!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@OnError
	public void onError(Throwable t) {
		System.out.println("onError::" + t.getMessage());
	}
}
