package projekat;

import java.util.LinkedList;
import java.util.List;

public class UserProvider {

	private List<User> allUsers = new LinkedList<>();
	
	private static String currentUser; // preko ovoga treba da se dohvata objekat korisnika da bi u generateRSAKeys
	// mogli da dohvatimo njegove podatke pri imenovanju fajlova

	public static String getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(String currentUser) {
		UserProvider.currentUser = currentUser;
	}

	private static UserProvider instance = new UserProvider();

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
		allUsers.add(new User(username, password));
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
