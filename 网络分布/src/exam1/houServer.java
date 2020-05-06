package exam1;

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
	
	public houServer() throws IOException {
		serverSocket = new ServerSocket(PORT);
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().
					availableProcessors() * POOLSIZE);
		System.out.println("服务器启动");
	}

	public static void main(String[] args) throws IOException {
		new houServer().servic();
	}

	public void servic() {
		Socket socket = null;
		while (true) {
			try {
				socket = serverSocket.accept();
				executorService.execute(new Handler(socket));	//将serverSocket和服务器根目录传递给每个子线程
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
	