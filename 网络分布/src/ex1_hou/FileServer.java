package hou;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @function 线程池
 * @version 3.0
 * @author hou
 *
 */
public class FileServer {
	private final int TCPPORT = 2021; //端口
	private final int UDPPORT = 2020;
	static final String HOST = "127.0.0.1"; //连接地址
	//UDP
	DatagramSocket udpSocket;
	//TCP
	ServerSocket tcpSocket;
	File tfile = new File("");//参数为空 
	String courseFile = tfile.getCanonicalPath() ; 
	ExecutorService executorService; // 线程池
	final int POOL_SIZE = 6; // 单个处理器线程池工作线程数目

	public FileServer() throws IOException {
		//TCP
		tcpSocket = new ServerSocket(TCPPORT); // 创建服务器端套接字
		//UDP
		udpSocket = new DatagramSocket(UDPPORT);
		// 创建线程池
		// Runtime的availableProcessors()方法返回当前系统可用处理器的数目
		// 由JVM根据系统的情况来决定线程的数量
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors() * POOL_SIZE);
		System.out.println("服务器启动。");
	}

	public static void main(String[] args) throws IOException {
		new FileServer().servic(); // 启动服务
	}

	/**
	 * service implements
	 */
	public void servic() {
		Socket socket = null;
		while (true) {
			try {
				socket = tcpSocket.accept(); // 等待用户连接
				PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
				pw.println(courseFile); //将文件根目录输出给客户端
				DatagramPacket dataPacket = new DatagramPacket(new byte[1024],1024);
				udpSocket.receive(dataPacket); //接受一个空文件来获取客户端UDP的地址和端口
				executorService.execute(new Handler(socket,dataPacket)); // 把执行交给线程池来维护
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
