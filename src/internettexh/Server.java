package internettexh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static ServerSocket serverSocket;
	private static final int GATE_NUMBER = 9004;
	protected static final boolean ACTIEVE = true;

	public static void main(String[] args) throws IOException {
		serverSocket = new ServerSocket(GATE_NUMBER);

		System.out.println("waiting...");

		Server server = new Server();

		while (ACTIEVE) {
			Socket socket = serverSocket.accept();

			ClientThread thread = server.new ClientThread(socket);

			thread.start();

		}
	}

	private class ClientThread extends Thread {
		private Socket socket;	
		private User activeUser;

		public ClientThread(Socket socket) {
			this.socket = socket;
		}

		public void run() {

			// Ophalen van de username
			try {
				InputStream inputStream = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String newUserName = reader.readLine();
				activeUser = new User(newUserName, socket);

				// Username wordt toegevoegd aan lijst met users
				addConnectedUsers(activeUser);

				System.out.println(newUserName + " connected");
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}

			while (ACTIEVE) {
				try {
					
					InputStream inputStream = socket.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					String text = reader.readLine();
					
					String textWithoutWisper = "";
					String userWisperTo = "";
					
					if (text.startsWith("w:/")) {
						textWithoutWisper = text.substring(3);
						for (int i = 0; i < textWithoutWisper.length(); i++) {
							if (textWithoutWisper.charAt(i) != ' ') {
								userWisperTo += textWithoutWisper.charAt(i);
							} else {
								break;
							}
						}
						sendWisper(textWithoutWisper.substring(userWisperTo.length()), userWisperTo);
					} else {
						sendPublicMessage(text);
					}

				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		}

		private void sendPublicMessage(String text) {
			for (User user : ServerModel.getUserList()) {
				Socket userSocket = user.getSocket();
				OutputStream outputStream;
				try {
					outputStream = userSocket.getOutputStream();
					PrintWriter writer = new PrintWriter(outputStream);
					
					System.out.println("test publicMessage: "+activeUser.getUserName() + " says: " + text);
				
					writer.println(activeUser.getUserName() + " says: " + text);
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		private void sendWisper(String text, String userName) {
			for (User user : ServerModel.getUserList()) {
				if (user.getUserName().equals(userName)) {
					// found
					Socket userSocket = user.getSocket();
					OutputStream outputStream;
					try {
						outputStream = userSocket.getOutputStream();
						PrintWriter writer = new PrintWriter(outputStream);
						
						System.out.println("test wisperMessage: "+ activeUser.getUserName() + " Whispers to you: " + text);
						
						writer.println(activeUser.getUserName() + " Whispers to you: " + text);
						writer.flush();
						return;
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			}
			// not found
			Socket userSocket = activeUser.getSocket();
			OutputStream outputStream;
			try {
				outputStream = userSocket.getOutputStream();
				PrintWriter writer = new PrintWriter(outputStream);
				writer.println("Message not send, user not found or user not online");
				writer.flush();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		//werkt
		private void addConnectedUsers(User u) {
			// user wordt toegevoegd aan het model
			ServerModel.addUser(u);
			Socket userSocket = activeUser.getSocket();
			OutputStream outputStream;
			try {
				outputStream = userSocket.getOutputStream();
				PrintWriter writer = new PrintWriter(outputStream);
				writer.println(u.getUserName() + " has connected");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
