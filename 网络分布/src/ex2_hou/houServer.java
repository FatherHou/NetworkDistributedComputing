package ex2_hou;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author hou
 * 
 */
public class houServer {
	ServerSocket serverSocket;

	private final int PORT = 80;
	ExecutorService executorService;
	final int POOLSIZE = 4;
	private static String path;
	
	public houServer() throws IOException {
		serverSocket = new ServerSocket(PORT);
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().
					availableProcessors() * POOLSIZE);
		System.out.println("服务器启动");
	}

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {	//检测是否有启动参数
            System.out.println("usage: java HttpServer <dir>");	
            return;
        }
        File file = new File(args[0]);	//根据启动参数配置服务器根目录
        if (!file.exists()) {	//检测目录是否存在
            System.err.println("路径不存在");
            return;
        }
        if (!file.isDirectory()) {	//检测该目录是否为文件夹
            System.err.println("该位置不是文件夹");
            return;
        }
        path = args[0];	//将目录字符串赋值给path并传递到每个子线程中
		new houServer().servic();
	}

	public void servic() {
		Socket socket = null;
		while (true) {
			try {
				socket = serverSocket.accept();
				executorService.execute(new Handler(socket,path));	//将serverSocket和服务器根目录传递给每个子线程
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
	