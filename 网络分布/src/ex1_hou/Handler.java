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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

/**
 * @function 服务器池
 * @version 3.0
 * @author hou
 *
 */
public class Handler implements Runnable { // 负责与单个客户通信的线程
	private Socket tcpSocket;
	//UDP
	private DatagramSocket udpSocket;
	private DatagramPacket udpPacket;
	static final String HOST = "127.0.0.1"; //连接地址
	private final int UDPPORT = 2020;
	BufferedReader br;
	BufferedWriter bw;
	PrintWriter pw;
	File tfile = new File("");//参数为空 
	String courseFile = tfile.getCanonicalPath() ; 
	String userFile = courseFile;

	public Handler(Socket tcpsocket,DatagramPacket packet) throws SocketException,IOException {
		this.tcpSocket = tcpsocket;
		this.udpPacket = packet;
		udpSocket = new DatagramSocket();
	}

	public void initStream() throws IOException { // 初始化输入输出流对象方法
		br = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
		bw = new BufferedWriter(
				new OutputStreamWriter(tcpSocket.getOutputStream()));
		pw = new PrintWriter(bw, true);
	}

	public void run() { // 执行的内容
		try {
			System.out.println("新连接，连接地址：" + tcpSocket.getInetAddress() + "："
					+ tcpSocket.getPort()); //客户端信息
			initStream(); // 初始化输入输出流对象
			String info = null; //接收用户输入的信息
			while ((info = br.readLine()) != null) {
				System.out.println(info); //输出用户发送的消息
				if(info.equals("ls")){ 
			    	getFile(userFile); //输出指定文件路径的文件列表
			    	pw.println("/n");
				}else if(info.contains("cd")){
					if(info.length()>2){
						if(info.contains("..")){
							if(userFile.equals(courseFile)){
								pw.println("当前已经是根目录");
								pw.println("/n");
							}else{
								File file = new File(userFile); 
								userFile = file.getParent(); //将当前用户的文件路径返回上一层目录
								pw.println("courseEnv > OK");
								getFile(userFile);
								pw.println("/n");
							}
						}else{
							info=info.replace("cd ", "");
					    	String newcourseFile="";
					    	newcourseFile=userFile+"\\"+info;
					    	File file = new File(newcourseFile);
					    	if(file.isDirectory()){ //判断下一层是否是文件夹
					    		pw.println(info+" > OK");
					    		getFile(newcourseFile);
					    		pw.println("/n");
						    	userFile=newcourseFile; //修改当前用户的文件目录
					    	}else{
					    		pw.println("unknown dir");
					    		pw.println("/n");
					    	}
						}
						}else{
							pw.println("请输入文件夹名");
						}
				}else if (info.equals("bye")) {
					break; //退出
				}else if(info.contains("get")){
					if(info.length()>3){
						info = info.replace("get ", "") ;
						File file = new File(userFile+"//"+info);
						if(file.isFile()){ //判断是否是文件
							FileInputStream fis = new FileInputStream(file); //建立新的文件流读入
							byte[] buffer = new byte[8192];
							int len = 0;
							while((len = fis.read(buffer, 0, buffer.length)) > 0){ //将文件读取到Buffer数组中，当读完时结束循环
								System.out.println("数据包长度:"+len);
								//从之前传过来的数据包判断客户端的地址和端口号
								DatagramPacket packet = new DatagramPacket(buffer, len,new InetSocketAddress(udpPacket.getAddress(), udpPacket.getPort()));
								udpSocket.send(packet); //向客户端发送数据包
								TimeUnit.MICROSECONDS.sleep(1); //控制下载速度
							}
						}else{
							pw.println("这不是一个文件");
						}
						continue;
					}else{
						pw.println("请输入文件名");
					}
				}
				else{
					pw.println("unknown cmd");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != tcpSocket) {
				try {
					tcpSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/*
     * @Name：getFile
     * @Author: hou
     */
    private void getFile(String FilePath) throws IOException{   
    	// 获得指定文件对象
    	File file = new File(FilePath); 
        // 获得该文件夹内的所有文件   
        File[] array = file.listFiles();   
        for(int i=0;i<array.length;i++) //根据类型不同输出
        {   
        	if(array[i].isDirectory()){
        		pw.println("<dir> " + array[i].getName() + " " + array[i].length());
        	}else if(array[i].isFile()){
        		pw.println("<file> " + array[i].getName() + " " + array[i].length());
        	}
        }   
    }
}
