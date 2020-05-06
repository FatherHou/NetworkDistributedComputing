package exam1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * @author hou
 * 
 */
public class Handler implements Runnable {

	/**
	 * Allow a maximum buffer size of 8192 bytes
	 */
	private static int buffer_size = 8192;

	/**
	 * The end of line character sequence.
	 */
	private static String CRLF = "\r\n";
	
	/**
	 * Response is stored in a byte array.
	 */
	private byte[] buffer;

	/**
	 * StringBuffer storing the header
	 */
	private StringBuffer header = null;
	
	/**
	 * Output stream to the socket.
	 */
	BufferedOutputStream ostream = null;

	/**
	 * Input stream from the socket.
	 */
	BufferedInputStream istream = null;
	
	/**
	 * Create the socket
	 */
	private Socket socket;
	
	/**
	 * Give the server path
	 */
	private String path = "D://hby";
	
	public Handler(Socket socket) {
		buffer = new byte[8192];
		this.socket = socket;	//接收socket
		header = new StringBuffer();
		//若文件夹不存在创建文件夹
		File recDir = new File(path);
		if(!recDir.exists()){
			System.out.println("已创建服务器文件夹，用于接收html文件: "+path);
			recDir.mkdir();
		}
		
	}

	//初始化输入输出流
	public void initStream() throws IOException {
		istream = new BufferedInputStream(socket.getInputStream());
		ostream = new BufferedOutputStream(socket.getOutputStream());
	}
	
	@Override
	public void run() {
		try {
			initStream();
			
			int last = 0, c = 0;
			boolean myHeader = true;	// 读取请求的头部
			ArrayList<String> headers = new ArrayList<String>();//创建一个arrayList来存储header内容
			while (myHeader && ((c = istream.read()) != -1)) {	//从输入流读取用户传过来的header
				switch (c) {
				case '\r':
					break;
				case '\n':
					if (c == last) {	//通过内容来判断是否结束header的读取
						myHeader = false;
						break;
					}
					last = c;
					header.append('\n');
					headers.add(header.toString());	//将换行后的一行加入arrayList
					header = new StringBuffer();	//刷新header用于下一次加入
					break;
				default:
					last = c;
					header.append((char) c);	//将未有换行符的内容加入arrayList
					break;
				}
			}

			long fileSize = 0;		//用于检测文件状态以及输出报文中的Content_length
            //hou
        	BufferedReader bR = null;
        	OutputStreamWriter oSw = null;
			for (String info : headers) {	//一行一行读取Header内容
				if (info.startsWith("GET")) {	//执行GET方法
					 String[] reqs = info.split(" ");	//分解请求的格式
					    if (reqs.length != 3) {	//异常格式请求
					        String response = "HTTP/1.1 400 Bad Request" + CRLF + CRLF;
					        buffer = response.getBytes();
					        ostream.write(buffer, 0, response.length());
					        ostream.flush();
				        }else {
				        	URL myUrl = null;
				        	myUrl = new URL(reqs[1]);	//获取命令中的URL
				        	//获取URL的资源
				        	bR = new BufferedReader(new InputStreamReader(myUrl.openStream(),"UTF-8"));
				        	String temp = reqs[1].substring(11, 14);
				        	File houfile = new File(path+"//"+temp+".html");
				        	oSw = new OutputStreamWriter(new FileOutputStream(houfile),"utf-8");
				        	String myInfo = null;
				        	while((myInfo = bR.readLine()) != null){
				        		oSw.write(myInfo);
				        	}
				        	bR.close();
				        	oSw.close();
				        	System.out.println("下载到D://hby成功");
				        	String fileType = fileType(path+"//download.html");	//改善文件的类型
			                fileSize = houfile.length();	//读取文件长度
			                String response = "HTTP/1.1 200 OK" + CRLF;
			                response += "Request: " + info.trim() + CRLF;
			                response += "From: " + myUrl + CRLF;
			                response += "User-Agent: " + reqs[2].trim() + CRLF; 
			                response += "Content-type: " + fileType + CRLF;
			                response += "Content-length: " + fileSize + CRLF + CRLF;
			                buffer = response.getBytes();
			                ostream.write(buffer, 0, response.length());
			                ostream.flush();
//				            dirname = reqs[1];	//读取出数组中第2个值为文件名
//				            recvFile = new File(path + dirname);	//将服务器根目录和文件名结合读取文件
//				            if (!recvFile.exists()){	//如果文件不存在
//				                String response = "HTTP/1.1 404 NOT FOUND" + CRLF + CRLF;
//				                buffer = response.getBytes();
//				                ostream.write(buffer, 0, response.length());
//				            }else {
//				                String fileType = fileType(dirname);	//改善文件的类型
//				                fileSize = recvFile.length();	//读取文件长度
//				                String response = "HTTP/1.1 200 OK" + CRLF;
//				                response += "Server: hou/1.0" + CRLF;
//				                response += "Content-type: " + fileType + CRLF;
//				                response += "Content-length: " + fileSize + CRLF + CRLF;
//
//				                buffer = response.getBytes();
//				                ostream.write(buffer, 0, response.length());
				                /*try {
				            	    buffer = new byte[8192];
				            		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				            				new FileInputStream(houfile));	//创建输入文件流
				            		while ((bufferedInputStream.read(buffer)) > 0) {//将文件内容写入buffer
				            			ostream.write(buffer, 0, buffer.length);	//将buffer内容输出给用户
				            			buffer = new byte[8192];					//刷新buffer
				            		}
				            		bufferedInputStream.close();	//关闭文件输入流
				                } catch (Exception e) {
				                    e.printStackTrace();
				                }*/
//				                ostream.flush();
//				            }
				        }
					break;
				} 
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != socket) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
    
	/**
	 * 创建文件
	 */
	private boolean createFile(String filename) {
		File file = new File(filename);
		if (file.getParentFile().exists()!=true) {
			System.out.println("所需目录不存在，创建");
			if (file.getParentFile().mkdirs()!=true) {
				System.out.println("创建目录失败！");
				return false;
			}
		}
		try {
			if (file.createNewFile()) {
				System.out.println("创建文件成功");
				return true;
			} else {
				System.out.println("创建文件失败");
				return false;
			}
		} catch (IOException e) {
			System.out.println("创建文件失败");
			e.printStackTrace();
			return false;
		}
			
	}
	
    /**
     * 根据文件路径获取文件类型
     */
    private String fileType(String fileName){
	    String fileType = "Unknown Type";
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            fileType = "text/html";
        } else if (fileName.endsWith(".jpg")) {
        	fileType = "image/jpeg";
        } else {
            fileType = "application/octet-stream";	// 默认返回值
        }
        return fileType;
    }

}
