package projekat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserProvider {

	private List<User> allUsers = new LinkedList<>();

	private static UserProvider instance;
	static {
		instance = new UserProvider();
		try {
			File myObj = new File(System.getProperty("user.dir") + "/users/users.txt");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String[] data = myReader.nextLine().split(";");
				User u = new User(data[0], data[1]);
				u.setPassword(data[1]);
				instance.allUsers.add(u);
			}
			if (instance.allUsers.size() > 0)
				instance.currentUser = instance.allUsers.get(0);
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	private User currentUser;

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	private UserProvider() {
	}

	public static UserProvider getInstance() {
		return instance;
	}

	public List<User> getAllUsers() {
		return allUsers;
	}

	public boolean createUser(String username, String password) {
		if (allUsers.parallelStream().anyMatch((User user) -> {
			return user.getUsername().equals(username);
		}))
			return false;
		User u = new User(username, password);
		allUsers.add(u);
		try {
			File myObj = new File(System.getProperty("user.dir") + "/users/users.txt");
			FileWriter fw = new FileWriter(myObj, true);
			fw.write(String.format("%s;%s\n", u.getUsername(), u.getPassword()).toCharArray());
			fw.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		return true;
	}

	public User getUserByUsername(String username) {
		Optional ret = allUsers.parallelStream().filter((User user) -> {
			return user.getUsername().equals(username);
		}).findFirst();
		return ret.isPresent() ? (User) ret.get() : null;
	}

	public String getAllUsersAsString() {
		StringBuilder sb = new StringBuilder();
		allUsers.forEach(user -> {
			sb.append(user.toString() + "\n");
		});
		return sb.toString();
	}

}
