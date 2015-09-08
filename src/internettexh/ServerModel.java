package internettexh;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Model houdt de lijst met users bij
 */
public class ServerModel {

	private static List<User> userList = new ArrayList<>();
	
	
	public static List<User> getUserList(){
		return new ArrayList<>(userList);
	}
	
	public static void addUser(User user){
		userList.add(user);
	}
	
	public static User getUserByName(String name){
		
		for(User user: userList){
			if(user.getUserName().equals(name)){
				return user;
			}
		}
		
		return null;
	}
	
	public static User getUserBySocket(Socket socket){
		
		for(User user: userList){
			if(user.getUserName().equals(socket)){
				return user;
			}
		}
		
		return null;
	}
	
	
}
