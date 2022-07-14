package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class UsersList {
    private static final UsersList usersList = new UsersList();
    private final Gson gson;
    private final List<User> users = new ArrayList<>();

    public List<User> getUsers() {
        return users;
    }

    public static UsersList getInstance() {return usersList; }

    private UsersList() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public synchronized void add(User u) {
        users.add(u);
    }

    public synchronized String toJSON() {
        return gson.toJson(usersList);
    }
}
