package hou;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
 
public class UDPFileServer {
	private int port = 2021;
	private String filePath = "";
	private DatagramSocket socket;
	File hfile = new File("");//参数为空 
	String courseFile = hfile.getCanonicalPath() ; 
	public UDPFileServer() throws SocketException,IOException {
//		socket = new DatagramSocket();
		socket = new DatagramSocket(port+1);
		//System.out.println("服务器启动成功");
	}
	public void service() throws IOException{
		while(true){
			//hou
			DatagramPacket dp = new DatagramPacket(new byte[512], 512);
			socket.receive(dp); // 接收客户端信息
			String msg = new String(dp.getData(), 0, dp.getLength());
			System.out.println(dp.getAddress() + ":" + dp.getPort() + ">" + msg);
			//hou
			File file = new File(courseFile+"//"+msg);
//			File file = new File(courseFile+"//hby2.jpg");
			FileInputStream fis = new FileInputStream(file);
//			InputStream is = this.getClass().getResourceAsStream("D://hby2.jpg");
			byte[] buffer = new byte[8192];
			int len = 0;
			while((len = fis.read(buffer, 0, buffer.length)) > 0){
				System.out.println(len);
				DatagramPacket packet = new DatagramPacket(buffer, len,InetAddress.getByName("localhost"),port);
				socket.send(packet);
			}
			break;
		}
	}
	public static void main(String[] args) throws UnknownHostException, SocketException, IOException {
		new UDPFileServer().service();
	}
}
