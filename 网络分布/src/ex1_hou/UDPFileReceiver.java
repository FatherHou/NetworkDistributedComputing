package hou;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Scanner;
 
public class UDPFileReceiver {
	private int port = 2021;
	private String remoteIp = "127.0.0.1"; // 服务器IP
	private DatagramSocket socket;
	String filepath = "D:/hby/";
	
	public UDPFileReceiver() throws SocketException{
//		socket = new DatagramSocket(port);
		socket = new DatagramSocket(port);
		socket.setSoTimeout(4000);
	}
	public void reciveData() throws IOException{
		//hou
		Scanner in = new Scanner(System.in);
		SocketAddress socketAddres = new InetSocketAddress(remoteIp, port+1);
		String s = in.next()+in.nextLine(); // 获取用户输入
		if(s.contains("get")){
			s=s.replace("get ", "");
			byte[] info = s.getBytes();
			DatagramPacket dp = new DatagramPacket(info, info.length,
					socketAddres);
			socket.send(dp); // 向服务器端发送数据包
			filepath=filepath+s;
			}
		File newfile = new File(filepath);
		FileOutputStream fos = new FileOutputStream(newfile);
		//hou
//		File newfile = new File("D://hby.jpg");
//		byte[] buf = new byte[1024];
//		FileOutputStream fos = new FileOutputStream(newfile);
		while(true){
			DatagramPacket packet = new DatagramPacket(new byte[8192], 8192);
			try{
//				System.out.println("hby");
				socket.receive(packet);
//				System.out.println("hby2");
				System.out.println("client"+packet.getData());
				fos.write(packet.getData(), 0, packet.getLength());
			}catch(Exception e){
				try {
					System.out.println("传输结束");
					socket.close();
					fos.flush();
					fos.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			}
		}
	}
	public static void main(String[] args) throws FileNotFoundException, SocketException, IOException {
		new UDPFileReceiver().reciveData();
	}
 
}