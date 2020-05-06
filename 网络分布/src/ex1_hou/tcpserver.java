package hou;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;


public class tcpserver {
	private final int TCPPORT = 2021; //端口
	private final int UDPPORT = 2020;
	static final String HOST = "127.0.0.1"; //连接地址
	//UDP
	DatagramSocket udpSocket;
	//TCP
	ServerSocket serverSocket;
	File tfile = new File("");//参数为空 
	String courseFile = tfile.getCanonicalPath() ; 

	public tcpserver() throws IOException {
		//TCP
		serverSocket = new ServerSocket(TCPPORT, 2); // 创建服务器端套接字
		//UDP
		udpSocket = new DatagramSocket(UDPPORT-1);
//		serverSocket.setSoTimeout(3000);
		System.out.println("服务器启动。");
	}

	public static void main(String[] args) throws IOException {
		new tcpserver().servic(); // 启动服务
	}
	
	/**
	 * service implements
	 * @throws IOException 
	 */
	public void servic() throws IOException {
		Socket tcpsocket = null;
		while (true) {
			try {
				//TCP
				tcpsocket = serverSocket.accept(); //等待并取出用户连接，并创建套接字
				System.out.println("新TCP连接,连接地址:" + tcpsocket.getInetAddress() + "："
						+ tcpsocket.getPort()); //客户端信息
				//输入流，读取客户端信息
				BufferedReader br = new BufferedReader(new InputStreamReader(
						tcpsocket.getInputStream()));
				//输出流，向客户端写信息
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						tcpsocket.getOutputStream()));
				PrintWriter pw = new PrintWriter(bw, true); //装饰输出流，true,每写一行就刷新输出缓冲区，不用flush
//				pw.println(socket.getInetAddress()+":<"+socket.getPort()+">连接成功");
				String info = null; //接收用户输入的信息
				while ((info = br.readLine()) != null) {
					System.out.println(info); //输出用户发送的消息
					if(info.equals("ls")){
				    	File file = new File(courseFile);  
				        File[] array = file.listFiles();   
				        for(int i=0;i<array.length;i++)
				        {   
				        	if(array[i].isDirectory()){
				        		pw.println("<dir> " + array[i].getName() + " " + array[i].length());
				        	}else if(array[i].isFile()){
				        		pw.println("<file> " + array[i].getName() + " " + array[i].length());
				        	}
				        	if(i==array.length-1){
				        		pw.println("/n");
				        	}
				        } 
					}else if(info.contains("cd")){
						if(info.length()>2){
							if(info.contains("..")){
//								String tmsg="";
//								char[] msgg=courseFile.toCharArray();
//								for(int i=0;i<courseFile.length();i++){
//									tmsg += msgg[courseFile.length()-1-i];
//								}
//								System.out.println(tmsg);
								File file = new File(courseFile); 
								courseFile = file.getParent();
								pw.println("courseEnv > OK");
								pw.println("/n");
							}else{
//								System.out.println(msg);
								info=info.replace("cd ", "");
//								System.out.println(msg);
//								String tmsg=tcpMsg.substring(3, tcpMsg.length());
//								System.out.println(tmsg);
						    	String newcourseFile="";
						    	newcourseFile=courseFile+"\\"+info;
						    	File file = new File(newcourseFile);
						    	if(file.isDirectory()){
						    		pw.println(info+" > OK");
						    		pw.println("/n");
							    	courseFile=newcourseFile;
						    	}else{
						    		pw.println("unknown dir");
						    		pw.println("/n");
						    	}
							}
							}else{
								pw.println("unknown cmd");
							}
					}else if (info.equals("bye")) {
						break; //退出
					}else if(info.contains("get")){
						if(info.length()>3){
						info = info.replace("get ", "") ;
//							byte[] udpmsg=info.getBytes();
//							DatagramPacket dp = new DatagramPacket(new byte[512], 512,new InetSocketAddress(HOST, UDPPORT));
						File file = new File(courseFile+"//"+info);
						if(file.isFile()){
							FileInputStream fis = new FileInputStream(file);
	//							InputStream is = this.getClass().getResourceAsStream("D://hby2.jpg");
							byte[] buffer = new byte[8192];
							int len = 0;
							while((len = fis.read(buffer, 0, buffer.length)) > 0){
								System.out.println(len);
								DatagramPacket packet = new DatagramPacket(buffer, len,new InetSocketAddress(HOST, UDPPORT));
								udpSocket.send(packet);
						}
						}
						continue;
//							System.out.println("udp:"+dp.getAddress() + ":" + dp.getPort() + ">" + info);
//							dp.setData(("udp you said:" + info).getBytes());
//							System.out.println("hby2");
//							udpSocket.send(dp); // 回复数据
//							System.out.println("hby3");
						}else{
							pw.println("unknown cmd");
						}
					}
					else{
//						pw.println(msg); //发送给服务器端
//						System.out.println(br.readLine()); //输出服务器返回的消息
						pw.println("unknown cmd");
					}
					
//					else{
//					pw.println("you said:" + info); //向客户端返回用户发送的消息，println输出完后会自动刷新缓冲区
//					if (info.equals("quit")) { //如果用户输入“quit”就退出
//						break;
//					}
//					}
				}
			} //如果客户端断开连接，则应捕获该异常，但不应中断整个while循环，使得服务器能继续与其他客户端通信 
			catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != tcpsocket) {
					try {
						tcpsocket.close(); //断开连接
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}
	

}
