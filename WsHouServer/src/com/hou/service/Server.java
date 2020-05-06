package com.hou.service;

import javax.xml.ws.Endpoint;

/**
 * @author hou
 * 服务器
 */
public class Server {
	public static void main(String[] args) {
        Endpoint.publish("http://localhost:8000/HouService", new MyService());
    }
}
