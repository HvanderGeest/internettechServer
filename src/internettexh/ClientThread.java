package internettexh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread extends Thread {
	private Socket socket;
	private ArrayList<User> userList;
	private User thisUser;

	public ClientThread(Socket socket, User thisUser) {
		this.socket = socket;
		this.userList = new ArrayList<>();
		this.thisUser = thisUser;
	}

	public User getThisUser() {
		return thisUser;
	}

	public void run() {

		while (true) {
			try {
				InputStream inputStream = socket.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
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
					sendWisper(
							textWithoutWisper.substring(userWisperTo.length()),
							userWisperTo);
				} else {
					sendPublicMessage(text);
				}

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	private void sendPublicMessage(String text) {
		for (User user : userList) {
			Socket userSocket = user.getSocket();
			OutputStream outputStream;
			try {
				outputStream = userSocket.getOutputStream();
				PrintWriter writer = new PrintWriter(outputStream);
				writer.println(thisUser.getUserName() + " says: " + text);
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void sendWisper(String text, String userName) {
		for (User user : userList) {
			if (user.getUserName().equals(userName)) {
				// found
				Socket userSocket = user.getSocket();
				OutputStream outputStream;
				try {
					outputStream = userSocket.getOutputStream();
					PrintWriter writer = new PrintWriter(outputStream);
					writer.println(thisUser.getUserName()
							+ " Whispers to you: " + text);
					writer.flush();
					return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		// not found
		Socket userSocket = thisUser.getSocket();
		OutputStream outputStream;
		try {
			outputStream = userSocket.getOutputStream();
			PrintWriter writer = new PrintWriter(outputStream);
			writer.println("Message not send, user not found or user not online");
			writer.flush();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	public void addConnectedUsers(User u) {
		userList.add(u);
		Socket userSocket = thisUser.getSocket();
		OutputStream outputStream;
		try {
			outputStream = userSocket.getOutputStream();
			PrintWriter writer = new PrintWriter(outputStream);
			writer.println(u.getUserName() + " has connected");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
