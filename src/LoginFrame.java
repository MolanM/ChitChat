import org.apache.http.client.ClientProtocolException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;

import org.pmw.tinylog.Logger;

class LoginFrame extends JFrame implements ActionListener, KeyListener {

	public static JTextField login_name;

	public LoginFrame(String ime_okna_LoginFrame) {
		super();
		setTitle(ime_okna_LoginFrame);
		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());

		JPanel vzdevekpanel = new JPanel();
		FlowLayout vzdevekpanelLayout = new FlowLayout(FlowLayout.LEFT);
		vzdevekpanel.setLayout(vzdevekpanelLayout);
		GridBagConstraints vzdevekpanelConstraint = new GridBagConstraints();
		pane.add(vzdevekpanel, vzdevekpanelConstraint);

		JLabel vzdeveknapis = new JLabel();
		vzdeveknapis.setText("Uporabniško ime: ");
		vzdevekpanel.add(vzdeveknapis);

		// širina vnosnega polja za login_name
		login_name = new JTextField(20);
		// prednastavitev: sistemsko uporabniško ime
		login_name.setText(System.getProperty("user.name"));
		vzdevekpanel.add(login_name);
		// prebere novo stanje vnosnega polja
		login_name.addKeyListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//
		// Upoštevamo samo dogodek, ko uporabnik pritisne tipko ENTER
		//
		if ((e.getKeyCode() == KeyEvent.VK_ENTER) && !(login_name.getText().equals(""))) {
			String login_ime = login_name.getText();
			Logger.info("LoginFrame.keyPressed.login_name:" + login_ime);
			// this.login_name.setText(login_ime);

			// Da se lahko prijavimo,
			// je potrebno za metodo Klinet.prijavi() uloviiti vse dogodke
			// Če prijava ni uspešna, potem se vrnemo iz metode (return).
			try {
				Klient.prijavi(login_ime);
			} catch (URISyntaxException e1){
				// tu lahko obdelamo dogodek URISyntaxException
				Logger.info("LoginFrame.keyPressed.URISyntaxException:" + e1.getMessage());
				return;
			} catch (ClientProtocolException e2){
				// tu lahko obdelamo dogodek ClientProtocolException
				Logger.info("LoginFrame.keyPressed.ClientProtocolException:" + e2.getMessage());
				return;
			} catch (IOException e3){
				// tu lahko obdelamo dogodek IOException
				Logger.info("LoginFrame.keyPressed.IOException:" + e3.getMessage());
				return;
			}

			// Pripravimo glavno okno
			// + aktiviramo objekt izpis_robot v rezredu ChitChat
			ChitChat.izpis_robot.activate();
			// iz prikažem glavno okno
			ChitChat.chatFrame.pack();
			ChitChat.chatFrame.setVisible(true);

			// Določimo, kaj se zgodi na koncu
			// Prednastavljena vrednost za konec za JFrame je JFrame.HIDE_ON_CLOSE
			// Da se zaprejo vse odprte niti, je potrebno glavno okno zapreti (JFrame.EXIT_ON_CLOSE)
			// Smo pa tukaj pametnejši. Najprej nastavimo JFrame.DO_NOTHING_ON_CLOSE,
			// da lahko ujamemo dogodek windowsClose(), kjer potem zapremo glavno okno.
			// Pred tem pa odjavimo prijavljenega uporabnika.
			ChitChat.chatFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			ChitChat.chatFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent event) {
					Klient.javno_odjavi(login_ime);
					Logger.info("Exit ChitChat JFrame. Odjavi: " + login_ime);
					ChitChat.chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			});

			// na koncu zapre okno, kot bi pritisnil Alt-F4
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
