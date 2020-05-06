package ex2_hou;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
	private String path;
	
	public Handler(Socket socket, String path) {
		buffer = new byte[8192];
		this.socket = socket;	//接收socket
		this.path = path;	//接收根目录
		header = new StringBuffer();
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

			String dirname = "";	//用于从请求中提取文件的名字
			boolean myGeshi = true;	//判断是否为正确的格式
			long fileSize = 0;		//用于检测文件状态以及输出报文中的Content_length
            File recvFile = null;	//用于创建服务器接受到的文件
			for (String info : headers) {	//一行一行读取Header内容
				if (info.startsWith("GET")) {	//执行GET方法
					myGeshi = false;
					 String[] reqs = info.split(" ");	//分解请求的格式
					    if (reqs.length != 3) {	//异常格式请求
					        String response = "HTTP/1.1 400 Bad Request" + CRLF + CRLF;
					        buffer = response.getBytes();
					        ostream.write(buffer, 0, response.length());
					        ostream.flush();
				        }else {
				            dirname = reqs[1];	//读取出数组中第2个值为文件名
				            recvFile = new File(path + dirname);	//将服务器根目录和文件名结合读取文件
				            if (!recvFile.exists()){	//如果文件不存在
				                String response = "HTTP/1.1 404 NOT FOUND" + CRLF + CRLF;
				                buffer = response.getBytes();
				                ostream.write(buffer, 0, response.length());
				            }else {
				                String fileType = fileType(dirname);	//改善文件的类型
				                fileSize = recvFile.length();	//读取文件长度
				                String response = "HTTP/1.1 200 OK" + CRLF;
				                response += "Server: hou/1.0" + CRLF;
				                response += "Content-type: " + fileType + CRLF;
				                response += "Content-length: " + fileSize + CRLF + CRLF;

				                buffer = response.getBytes();
				                ostream.write(buffer, 0, response.length());
				                try {
				            	    buffer = new byte[8192];
				            		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				            				new FileInputStream(recvFile));	//创建输入文件流
				            		long temSize = recvFile.length();
				            		while ((bufferedInputStream.read(buffer)) > 0) {//将文件内容写入buffer
				            			if(temSize <= 8192){
				            				ostream.write(buffer, 0, (int)temSize);
				            				break;
				            			}
				            			temSize = temSize - buffer.length;
				            			ostream.write(buffer, 0, buffer.length);	//将buffer内容输出给用户
				            			buffer = new byte[8192];					//刷新buffer
				            		}
				            		bufferedInputStream.close();	//关闭文件输入流
				                } catch (Exception e) {
				                    e.printStackTrace();
				                }
				                ostream.flush();
				            }
				        }
					break;
				} else if (info.startsWith("PUT")) {
					String[] reqs = info.split(" ");//分解请求的格式
					if (reqs.length != 3) {	//异常格式请求
						myGeshi = false;
						String response = "HTTP/1.1 400 Bad Request" + CRLF
								+ CRLF;
						buffer = response.getBytes();
						ostream.write(buffer, 0, response.length());
						ostream.flush();
						break;
					} else {
						dirname = reqs[1];	//读取出数组中第2个值为文件名
						recvFile = new File(path + dirname);	
					}
				} else if (info.startsWith("Content-length")) {	//根据Content-length提取内容长度
					fileSize = Long.parseLong(info.split(" ")[1].trim(), 10);	//trim()去掉多余的空格
				}
			}

			if (myGeshi) {	//进行PUT的处理
				if (fileSize == 0) {	// 内容长度为0，无需读取body
					if (recvFile.exists()) {	//如果文件存在
						FileWriter fileWriter = new FileWriter(recvFile);
						fileWriter.write("");	// 清空文件
						fileWriter.close();
						String response = "HTTP/1.1 204 No Content" + CRLF;
						response += "Content-Location: " + path + dirname + CRLF
								+ CRLF;
						buffer = response.getBytes();
						ostream.write(buffer, 0, response.length());
						ostream.flush();
					} else {
						if (createFile(path + dirname)) {	//创建新文件
							String response = "HTTP/1.1 201 Created" + CRLF;
							response += "Content-Location: " + path + dirname + CRLF
									+ CRLF;
							buffer = response.getBytes();
							ostream.write(buffer, 0, response.length());
							ostream.flush();
						} else {	//不能创建，服务器出错
							String response = "HTTP/1.1 500 Internal ProxyServer Error"
									+ CRLF + CRLF;
							buffer = response.getBytes();
							ostream.write(buffer, 0, response.length());
							ostream.flush();
						}
					}
				} else {	//如果已有内容
					boolean myFile = recvFile.exists();
					if (!myFile) {	//文件不存在但读取到内容，服务器出错
						if (!createFile(path + dirname)) {
							String response = "HTTP/1.1 500 Internal ProxyServer Error"
									+ CRLF + CRLF;
							buffer = response.getBytes();
							ostream.write(buffer, 0, response.length());
							ostream.flush();
						}
					}

					if (myFile) {	//如果文件存在
						ArrayList<Byte> recMes = new ArrayList<Byte>();	//创建arrayList接收内容
						int body = 0;
						while ((body = istream.read()) != -1) {
							recMes.add(new Byte((byte) body));	//读取文件内容,存储到arrayList中
							if (recMes.size() == fileSize) {	//读取到指定大小以结束读取
								break;
							}
						}
						byte[] recMes2 = new byte[recMes.size()];	
						for (int i = 0; i < recMes.size(); i++) {	// 转化成数组
							recMes2[i] = recMes.get(i).byteValue();
						}

						BufferedOutputStream outputStream = new BufferedOutputStream(
								new FileOutputStream(recvFile));	//开启输出到文件流
						outputStream.write(recMes2);	//将recMes2写入到文件
						outputStream.flush();
						outputStream.close();
						String fileType = fileType(dirname);	//改善文件的类型

						if (myFile) {	// 若文件已存在修改文件
							String response = "HTTP/1.1 200 OK" + CRLF;
							response += "Server: " + "hou/1.0" + CRLF;
							response += "Content-type: " + fileType + CRLF;
							response += "Content-Location: " + path + dirname + CRLF
									+ CRLF;
							buffer = response.getBytes();
							ostream.write(buffer, 0, response.length());
							ostream.flush();
						} else {	// 创建新文件
							String response = "HTTP/1.1 201 Created" + CRLF;
							response += "Server: " + "hou/1.0" + CRLF;
							response += "Content-type: " + fileType + CRLF;
							response += "Content-Location: " + path + dirname + CRLF
									+ CRLF;
							buffer = response.getBytes();
							ostream.write(buffer, 0, response.length());
							ostream.flush();
						}
					}
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
