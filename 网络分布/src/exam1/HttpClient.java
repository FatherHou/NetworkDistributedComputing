package exam1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

/**
 * Class <em>HttpClient</em> is a class representing a simple HTTP client.
 *
 * @author hou
 */

public class HttpClient {


	/**
	 * Allow a maximum buffer size of 8192 bytes
	 */
	private static int buffer_size = 8192;

	/**
	 * Response is stored in a byte array.
	 */
	private byte[] buffer;

	/**
	 * My socket to the world.
	 */
	Socket socket = null;

	/**
	 * Default port is 80.
	 */
	private static final int PORT = 80;

	/**
	 * Output stream to the socket.
	 */
	BufferedOutputStream ostream = null;

	/**
	 * Input stream from the socket.
	 */
	BufferedInputStream istream = null;

	/**
	 * StringBuffer storing the header
	 */
	private StringBuffer header = null;

	/**
	 * StringBuffer storing the response.
	 */
	private StringBuffer response = null;
	
	/**
	 * String to represent the Carriage Return and Line Feed character sequence.
	 */
	static private String CRLF = "\r\n";
	static private String pathname = "D:";
	/**
	 * HttpClient constructor;
	 */
	public HttpClient() {
		buffer = new byte[8192];
		header = new StringBuffer();
		response = new StringBuffer();
	}

	/**
	 * <em>connect</em> connects to the input host on the default http port --
	 * port 80. This function opens the socket and creates the input and output
	 * streams used for communication.
	 */
	public void connect(String host) throws Exception {

		/**
		 * Open my socket to the specified host at the default port.
		 */
		socket = new Socket(host, PORT);

		/**
		 * Create the output stream.
		 */
		ostream = new BufferedOutputStream(socket.getOutputStream());

		/**
		 * Create the input stream.
		 */
		istream = new BufferedInputStream(socket.getInputStream());
	}

	/**
	 * <em>processGetRequest</em> process the input GET request.
	 */
	public void processGetRequest(String request) throws Exception {
		/**
		 * Send the request to the server.
		 */
		request += CRLF + CRLF;
		buffer = request.getBytes();
		ostream.write(buffer, 0, request.length());
		ostream.flush();
		/**
		 * waiting for the response.
		 */
		processResponse();
	}
	
	/**
	 * <em>processPutRequest</em> process the input PUT request.
	 */
	public void processPutRequest(String request) throws Exception {
		//=======start your job here============//
		String[] temReq = request.split(" ");	//将请求以空格分到数组中
		File file = null;						
		request += CRLF ;//报文只有结尾才加两个CRLF		//符合报文非结尾格式
		if(temReq.length == 3){
			String filePath = temReq[1];		//数组第2位为文件路径
			file = new File(pathname+filePath); //以用户根路径加请求路径来提取文件
			if(file.exists()){	
				request += "Content-length: " + file.length() + CRLF + CRLF;	//文件存在用于服务器中提取文件长度
																				//两个CRLF符合报文结尾格式
			}else{
				request += "Content-length: 0" + CRLF + CRLF; 
			}
		}else{
			request += "Content-length: 0" + CRLF + CRLF; 
		}
		buffer = request.getBytes();	//将request写入buffer
		ostream.write(buffer, 0, request.length());	//将request输出给服务器
		
		if(file.exists()){	//判断此文件是否存在
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					new FileInputStream(file));	//创建输入到文件流
			while ((bufferedInputStream.read(buffer)) > 0) {	//按buffer大小读取文件并传输给服务器根目录
				ostream.write(buffer, 0, buffer.length);		//将buffer中的文件内容输出给服务器
				buffer = new byte[8192];	//一个buffer传输完成后刷新继续下一次传输
			}
			bufferedInputStream.close();	//关闭输入流
		}
		ostream.flush();
		/**
		 * waiting for the response.
		 */
		processResponse();
		//=======end of your job============//
	}

	
	/**
	 * <em>processResponse</em> process the server response.
	 * 
	 */
	public void processResponse() throws Exception {
		int last = 0, c = 0;
		/**
		 * Process the header and add it to the header StringBuffer.
		 */
		boolean inHeader = true; // loop control
		while (inHeader && ((c = istream.read()) != -1)) {
			switch (c) {
			case '\r':
				break;
			case '\n':
				if (c == last) {
					inHeader = false;
					break;
				}
				last = c;
				header.append("\n");
				break;
			default:
				last = c;
				header.append((char) c);
			}
		}

		/**
		 * Read the contents and add it to the response StringBuffer.
		 */
		while (istream.read(buffer) != -1) {
			response.append(new String(buffer,"iso-8859-1"));
		}
	}

	/**
	 * Get the response header.
	 */
	public String getHeader() {
		return header.toString();
	}

	/**
	 * Get the server's response.
	 */
	public String getResponse() {
		return response.toString();
	}

	/**
	 * Close all open connections -- sockets and streams.
	 */
	public void close() throws Exception {
		socket.close();
		istream.close();
		ostream.close();
	}
}
