package projekat;

import java.util.LinkedList;
import java.util.List;

public class UserProvider {

	private List<User> allUsers = new LinkedList<>();

	private static UserProvider instance = new UserProvider();

	private User currentUser = new User("kalus", "kurcina@kurcina.com", "superSifra");

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
		return allUsers.parallelStream().filter((User user) -> {
			return user.getUsername().equals(username);
		}).findFirst().get();
	}

	public String getAllUsersAsString() {
		StringBuilder sb = new StringBuilder();
		allUsers.forEach(user -> {
			sb.append(user.toString() + "\n");
		});
		return sb.toString();
	}

}
