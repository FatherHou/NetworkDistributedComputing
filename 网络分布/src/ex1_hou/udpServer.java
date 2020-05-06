package hou;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class udpServer {
	int port=8080;
	DatagramSocket socket;
	public udpServer() throws SocketException {
		socket = new DatagramSocket(port); // 服务端DatagramSocket
		System.out.println("服务器启动。");
	}
	
	public void service() throws IOException {
		while (true) {
			DatagramPacket dp = new DatagramPacket(new byte[512], 512);
			socket.receive(dp); // 接收客户端信息
			String msg = new String(dp.getData(), 0, dp.getLength());
			// 获取客户端信息
			System.out.println(dp.getAddress() + ":" + dp.getPort() + ">" + msg);
			dp.setData(("you said:" + msg).getBytes());
			socket.send(dp); // 回复数据
		}
		
	}

	public static void main(String[] args) throws SocketException, IOException {
		new udpServer().service();
	}
}
