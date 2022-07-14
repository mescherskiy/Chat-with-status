package academy.prog;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "UsersServlet", value = "/users")
public class UsersServlet extends HttpServlet {
    private UsersList usersList = UsersList.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String users = checkOutUsers();
        if (users != null) {
            OutputStream os = response.getOutputStream();
            byte[] buf = users.getBytes(StandardCharsets.UTF_8);
            os.write(buf);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        byte[] buf = requestBodyToArray(request);
        String bufStr = new String(buf, StandardCharsets.UTF_8);

        User user = User.fromJSON(bufStr);
        if (user != null && !usersList.getUsers().contains(user)) {
            usersList.add(user);
        } else if (user != null && usersList.getUsers().contains(user)) {
            usersList.getUsers().set(usersList.getUsers().indexOf(user), user);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private byte[] requestBodyToArray(HttpServletRequest req) throws IOException {
        InputStream is = req.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }

    private synchronized String checkOutUsers() {
        String result = "";
        for (User user : usersList.getUsers()) {
            if (System.currentTimeMillis() - user.getLastActivity() >= 120000) {
                result += user.getLogin() + " => offline" + System.lineSeparator();
            } else if (System.currentTimeMillis() - user.getLastActivity() >= 60000) {
                result += user.getLogin() + " => away" + System.lineSeparator();
            } else {
                result += user.getLogin() + " => online" + System.lineSeparator();
            }
        }
        return result;
    }
}
