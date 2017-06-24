import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

//
// List of current active users:
// http://chitchat.andrej.com/users
//
class ChitChat {
	private static LoginFrame loginFrame = new LoginFrame("Okno za prijavo");
	public static ChatFrame chatFrame = new ChatFrame();
	static IzpisiPrejetoRobot izpis_robot = new IzpisiPrejetoRobot(chatFrame);

	public static void main(String[] args) throws IOException, URISyntaxException {
		// uporaba GlasnejeRobot.java
		//GlasnejeRobot robot2 = new GlasnejeRobot(chatFrame);

		// okno za prijavo na stražnik
		ChitChat.loginFrame.pack();
		ChitChat.loginFrame.setVisible(true);
		ChitChat.loginFrame.setAlwaysOnTop(true);




		Logger.info("Main-zadnja-vrstica");
	}
}
