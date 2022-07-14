package academy.prog;

import java.io.IOException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		User user = null;
		try {
			System.out.println("Enter your login: ");
			String login = scanner.nextLine();
			user = new User(login);
			int resp = user.logIn(Utils.getURL() + "/users");

			if (resp != 200) { // 200 OK
				System.out.println("HTTP error ocurred: " + resp);
				return;
			}
	
			Thread th = new Thread(new GetThread(login));
			th.setDaemon(true);
			th.start();

            System.out.println("Enter your message: ");
			while (true) {
				String text = scanner.nextLine();
				if (text.isEmpty()) break;

				int res;

				if ("/users".equals(text)) {
					user.update();
					user.checkOutUsers();
				} else if (text.startsWith("@")) {
					Message m = new Message(login, text.substring(1, text.indexOf(" ")), text.substring(text.indexOf(" ") + 1));
					res = m.send(Utils.getURL() + "/add");
					user.update();

					if (res != 200) { // 200 OK
						System.out.println("HTTP error ocurred: " + res);
						return;
					}
				} else {
					Message m = new Message(login, text);
					res = m.send(Utils.getURL() + "/add");
					user.update();
					if (res != 200) { // 200 OK
						System.out.println("HTTP error ocurred: " + res);
						return;
					}
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();
			if (user != null) {
				user.logOut();
			}
		}
	}
}
