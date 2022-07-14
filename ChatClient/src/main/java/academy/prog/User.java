package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class User {
    private String login;
    private long lastActivity;

    public User(String login) {
        this.login = login;
        lastActivity = System.currentTimeMillis();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String toJSON() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson.toJson(this);
    }

    public static User fromJSON(String s) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson.fromJson(s, User.class);
    }

    public int logIn(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            String json = toJSON();
            os.write(json.getBytes(StandardCharsets.UTF_8));
            return conn.getResponseCode(); // 200?
        }
    }

    public void update() {
        setLastActivity(System.currentTimeMillis());
        int resp = 0;
        try {
            resp = logIn(Utils.getURL() + "/users");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (resp != 200) { // 200 OK
            System.out.println("HTTP error ocurred: " + resp);
            return;
        }
    }

    public void logOut() {
        setLastActivity(6000000);
        int resp = 0;
        try {
            resp = logIn(Utils.getURL() + "/users");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (resp != 200) { // 200 OK
            System.out.println("HTTP error ocurred: " + resp);
            return;
        }
    }

    private byte[] responseBodyToArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }

    public void checkOutUsers() throws IOException {
        URL url = new URL(Utils.getURL() + "/users");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        InputStream is = http.getInputStream();
        try {
            byte[] buf = responseBodyToArray(is);
            String strBuf = new String(buf, StandardCharsets.UTF_8);
            System.out.println(strBuf);
        } finally {
            is.close();
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", lastActivity=" + lastActivity + '\'' +
                '}';
    }
}