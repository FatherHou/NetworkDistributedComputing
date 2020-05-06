package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;






public class houServer {
	static ServerSocket serverSocket;
	private final static int PORT = 80; //端口
	private static Path path;
	private OutputStream outputStream;
	private RequestReader request;

	public static void main(String[] args) throws IOException {
		
		if (args.length == 0) {
            System.out.println("请设置启动参数");
            return;
        }
        File file = new File(args[0]);
        if (!file.exists()) {
            System.err.println("路径不存在");
            return;
        }
        if (!file.isDirectory()) {
            System.err.println("该位置不是文件夹");
            return;
        }
        path = Paths.get(file.getPath());

        try {
            serverSocket = new ServerSocket(PORT, 2);
            System.out.println("服务器启动。");
        } catch (IOException e) {
            e.printStackTrace();
            // 在进程终止的时候两个 Socket 会被释放，不需要显式释放
            return;
        }
        new houServer().servic(); // 启动服务
	}
	
	/**
	 * service implements
	 * @throws IOException 
	 */
	public void servic() throws IOException {
		Socket socket = null;
		while (true) {
			try {
				socket = serverSocket.accept(); //等待并取出用户连接，并创建套接字
//		        request = new RequestReader(socket.getInputStream());
		        outputStream = socket.getOutputStream();
				System.out.println("新连接，连接地址：" + socket.getInetAddress() + "："
						+ socket.getPort()); //客户端信息
				//输入流，读取客户端信息
				BufferedReader br = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				//输出流，向客户端写信息
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						socket.getOutputStream()));
				PrintWriter pw = new PrintWriter(bw, true); //装饰输出流，true,每写一行就刷新输出缓冲区，不用flush
				String info = null; //接收用户输入的信息
//				while ((info = br.readLine()) != null) {
					info = br.readLine();
//					System.out.println(info); //输出用户发送的消息
//					pw.println("you said:" + info); //向客户端返回用户发送的消息，println输出完后会自动刷新缓冲区
//					if (info.equals("quit")) { //如果用户输入“quit”就退出
//						break;
//					}
					System.out.println(info);
					if(info.contains("GET")){
						String temp = info.replace("GET ", "");
		                File file = new File(path+"//"+temp);
		                if (file == null || !file.isFile()) {
		                	processPlainTextResponse(404, "404 Not Found. Please check your Url.");
		                	continue;
		                }
		                processResponse(
		                        new ResponseBuilder(200)
		                                .addHeader("Content-Type",
		                                        resolveMimeTypeFromFileName(file.getName()))
		                );
						int length = 0;
				        byte[] sendBytes = null;
				        DataOutputStream dos = null;
				        FileInputStream fis = null;
				        try {
				            try {
				                dos = new DataOutputStream(socket.getOutputStream());
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
				        
					}else if(info.contains("PUT")){
						String temp = info.replaceAll("PUT ", "");
				        File file = new File("D://"+temp);
//				        if (file.isDirectory()) {
//				            processPlainTextResponse(400, "400 Bad Requests. The url must be a file.");
//				            return;
//				        }
//
//				        boolean newFile = file.createNewFile();
//				        if (!newFile) {
//				            // Bug1： 没有遵循 put 的定义。 put 操作是如果资源存在，则更新资源。
//				            processPlainTextResponse(400, "400 Bad Requests. The file is already exist.");
//				            return;
//				        }
						byte[] inputByte = null;
				        int length = 0;
				        DataInputStream dis = null;
						FileOutputStream fos = null;;
						try {
					        dis = new DataInputStream(socket.getInputStream());
					        fos = new FileOutputStream(new File(path+"//"+temp));
					        inputByte = new byte[1024];
					        System.out.println("服务器开始接收数据");
					        while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
					            System.out.println("长度为"+length);
					            fos.write(inputByte, 0, length);
					            fos.flush();
					        }
					        System.out.println("完成接收");
					        processResponse(new ResponseBuilder(200));
					    } finally {
					        if (fos != null)
					            fos.close();
					        if (dis != null)
					            dis.close();
					    }
						
					}
//				}
			} //如果客户端断开连接，则应捕获该异常，但不应中断整个while循环，使得服务器能继续与其他客户端通信 
			catch (IOException e) {
				e.printStackTrace();
				processUnhandledException(e);
//			} finally {
//				if (null != socket) {
//					try {
//						socket.close(); //断开连接
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
			}
		}
	}
	
	
	  /**
     * 从文件名获取 mime 类型
     *
     * @param fileName
     * @return
     */
    private String resolveMimeTypeFromFileName(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".jpg")) {
            return "image/jpg";
        } else {
            // 默认返回值
            return "application/octet-stream";
        }
    }
	
    /**
     * 处理其他 handle 函数未捕获的异常，本函数用来向用户返回 500 响应，防止处理函数
     * 抛出异常导致的 socket 无应答关闭。
     *
     * @param e 未捕获的异常
     * @throws IOException
     */
    private void processUnhandledException(Exception e) throws IOException {
        processPlainTextResponse(500,
                "500 Server Error. \n" +
                        e.toString());
    }
	
    /**
     * 向用户以文本格式进行应答
     *
     * @param code    Http Status Code
     * @param content 应答的内容，这里必须提供内容，如果不需要内容，可以直接使用 processResponse 进行应答
     * @throws IOException
     */
    private void processPlainTextResponse(int code, String content) throws IOException {
        processResponse(
                new ResponseBuilder(code)
                        .addHeader("Content-Type", "text/plain")
                        .setContent(content.getBytes())
        );
    }
    
    /**
     * 向客户端发送相应，根据 {@link ResponseBuilder} 来生成相应
     *
     * @param rb
     * @throws IOException
     */
    private void processResponse(ResponseBuilder rb) throws IOException {
        // PrintStream outputStream = System.out; // DEBUG
        outputStream.write(rb.build());
        outputStream.flush();
    }
	
}
