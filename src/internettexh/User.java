package internettexh;

import java.net.Socket;

public class User {
	private String userName;
	private Socket socket;
	
	public User(String userName, Socket socket){
		this.userName = userName;
		this.socket = socket;
	}

	public String getUserName() {
		return userName;
	}

	public Socket getSocket() {
		return socket;
	}

}
