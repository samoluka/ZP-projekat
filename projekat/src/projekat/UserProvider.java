package projekat;

import java.io.File;
import java.io.FileNotFoundException;
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
				instance.allUsers.add(new User(data[0], data[1], data[2]));
			}
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

	public boolean createUser(String username, String mail, String password) {
		if (allUsers.parallelStream().anyMatch((User user) -> {
			return user.getUsername().equals(username);
		}))
			return false;
		allUsers.add(new User(username, mail, password));
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
