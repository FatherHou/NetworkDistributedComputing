package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Class <em>HttpClient</em> is a class representing a simple HTTP client.
 *
 * @author wben
 */

public class HttpClient {

	/**
	 * default HTTP port is port 80
	 */
	private static int port = 80;

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

	/**
	 * HttpClient constructor;
	 */
	public HttpClient() {
		buffer = new byte[buffer_size];
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
		//客户端输出流，向服务器发消息
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		//客户端输入流，接收服务器消息
		BufferedReader br = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		PrintWriter pw = new PrintWriter(bw, true); //装饰输出流，及时刷新
		pw.println(request); //发送给服务器端
		byte[] inputByte = null;
        int length = 0;
        DataInputStream dis = null;
        FileOutputStream fos = null;
        String temRequest = request.replace("GET ", "");
//        temRequest = temRequest.replace(" HTTP/1.0", "");
	    try {
	        dis = new DataInputStream(socket.getInputStream());
	        fos = new FileOutputStream(new File("D://"+temRequest));
	        inputByte = new byte[1024];
	        System.out.println("开始接收数据...");
	        while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
	            System.out.println(length);
	            fos.write(inputByte, 0, length);
	            fos.flush();
	        }
	        System.out.println("完成接收");
	    } finally {
	        if (fos != null)
	            fos.close();
	        if (dis != null)
	            dis.close();
	    }
		request += CRLF + CRLF;
		buffer = request.getBytes();
		System.out.println("123"+request);
		ostream.write(buffer, 0, request.length());
//		ostream.flush();
		/**
		 * waiting for the response.
		 */
		System.out.println("1111111"+istream.read());
		processResponse();
	}
	
//	/**
//	 * <em>processGetRequest</em> process the input GET request.
//	 */
//	public void processGetRequest(String request) throws Exception {
//		/**
//		 * Send the request to the server.
//		 */
//		request += CRLF + CRLF;
//		buffer = request.getBytes();
//		ostream.write(buffer, 0, request.length());
//		ostream.flush();
//		/**
//		 * waiting for the response.
//		 */
//		processResponse();
//	}
	
	/**
	 * <em>processPutRequest</em> process the input PUT request.
	 */
	public void processPutRequest(String request) throws Exception {
		//=======start your job here============//
		 // 发送文件
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		//客户端输入流，接收服务器消息
		BufferedReader br = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		PrintWriter pw = new PrintWriter(bw, true); //装饰输出流，及时刷新
		pw.println(request);
		String temRequest = request.replace("PUT ", "");
		int length = 0;
        byte[] sendBytes = null;
        DataOutputStream dos = null;
        FileInputStream fis = null;
        try {
            try {
                dos = new DataOutputStream(socket.getOutputStream());
                File file = new File("D://"+temRequest);
                fis = new FileInputStream(file);
                System.out.println(file.getPath());
                sendBytes = new byte[1024];
                while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                    dos.write(sendBytes, 0, length);
                    dos.flush();
                }
            } finally {
                if (dos != null)
                    dos.close();
                if (fis != null)
                    fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		request += CRLF + CRLF;
		buffer = request.getBytes();
		ostream.write(buffer, 0, request.length());
		processResponse();
//        try (
//        	FileInputStream inPut = new FileInputStream(new File("D://"+temRequest));
//        	FileOutputStream fos = new FileOutputStream(new File("D://hby//"+temRequest));
//            ByteArrayOutputStream outPut = new ByteArrayOutputStream()) {
//            byte[] buf = new byte[1024];
//            while (true) {
//                int r = inPut.read(buf);
//                if (r == -1) {
//                    break;
//                }
//                fos.write(buf, 0, r);
//                fos.flush();
//                
//            }
//            outPut.flush();
//            System.out.println(outPut.size());
//            request += CRLF;
//            request += "Content-Length: " + outPut.size();
//            request += CRLF + CRLF;
//            buffer = request.getBytes();
//            ostream.write(buffer, 0, request.length());
//            outPut.writeTo(ostream);
//        }

//		processResponse();
		
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
	        	System.out.println((char)c);
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
	            response.append(new String(buffer, "iso-8859-1"));
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
	 * @throws IOException 
	 */
	public String getResponse() throws IOException {
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
