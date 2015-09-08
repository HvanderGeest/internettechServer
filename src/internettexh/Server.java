package internettexh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private static ServerSocket serverSocket;
	

	public static void main(String[] args) throws IOException {
		serverSocket = new ServerSocket(9004);
		ArrayList<ClientThread> users = new ArrayList<>();
		System.out.println("waiting...");
		while(true){
			Socket socket = serverSocket.accept();
			InputStream inputStream = socket.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(inputStream));
			String newUserName = reader.readLine();
			User u = new User(newUserName, socket);
			
			ClientThread thread = new ClientThread(socket, u);
			for(ClientThread t : users){
				t.addConnectedUsers(u);
				thread.addConnectedUsers(t.getThisUser());
			}
			users.add(thread);
			thread.addConnectedUsers(u);
			
			
			thread.start();
			System.out.println(newUserName+" connected");
		}
	}

}
