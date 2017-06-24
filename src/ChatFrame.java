import org.apache.http.client.ClientProtocolException;
import org.pmw.tinylog.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;

class ChatFrame extends JFrame implements ActionListener, KeyListener {
	
	private JTextArea output;
	private JTextField input;
	private JTextField vzdevek;

	private String recipient_name;

	public ChatFrame() {
		super();
		setTitle("Glavno okno");
		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());

		JPanel vzdevekpanel = new JPanel();
		FlowLayout vzdevekpanelLayout = new FlowLayout(FlowLayout.LEFT);
		vzdevekpanel.setLayout(vzdevekpanelLayout);
		GridBagConstraints vzdevekpanelConstraint = new GridBagConstraints();
		vzdevekpanelConstraint.gridx = 0;
		vzdevekpanelConstraint.gridy = 0;
		vzdevekpanelConstraint.fill = GridBagConstraints.HORIZONTAL;
		pane.add(vzdevekpanel, vzdevekpanelConstraint);

		JButton gumbPodajUporabnike = new JButton("Prijavljeni");
		JButton gumbOdjaviVse = new JButton("Odjavi vse");
		pane.add(gumbPodajUporabnike);
		pane.add(gumbOdjaviVse);


		JLabel vzdeveknapis = new JLabel();
		vzdeveknapis.setText("Vzdevek (uporabniško ime): ");
		vzdevekpanel.add(vzdeveknapis);

		// širina vnosnega polja za vzdevek
		this.vzdevek = new JTextField(40);
		// prednastavitev: sistemsko uporabniško ime
		this.vzdevek.setText("");
		Logger.info("ChatFrame.ChatFrame.Vzdevek: predefined value");
		vzdevekpanel.add(vzdevek);
		// prebere novo stanje vnosnega polja
		vzdevek.addKeyListener(this);

		this.output = new JTextArea(20, 40);
		JScrollPane sp = new JScrollPane(output);
		this.output.setEditable(false);
		GridBagConstraints outputConstraint = new GridBagConstraints();
		outputConstraint.gridx = 0;
		outputConstraint.gridy = 1;
		outputConstraint.weighty = 1;
		outputConstraint.weightx = 1;
		outputConstraint.fill = GridBagConstraints.BOTH;
		pane.add(sp, outputConstraint);

		this.input = new JTextField(40);
		GridBagConstraints inputConstraint = new GridBagConstraints();
		inputConstraint.gridx = 0;
		inputConstraint.gridy = 2;
		inputConstraint.fill = GridBagConstraints.HORIZONTAL;
		pane.add(input, inputConstraint);
		// prebere novo stanje vnosne vrstice
		input.addKeyListener(this);

		addWindowListener( new WindowAdapter() {
		    public void windowOpened( WindowEvent e ){
		        input.requestFocus();
		    }
		});

		// tu poslušamo ali sta pritisnjena gumba
		gumbPodajUporabnike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Da se lahko odjavimo,
				// je potrebno za metodo Klinet.odjavi() uloviiti vse dogodke
				// Če prijava ni uspešna, potem se vrnemo iz metode (return).
				try {
					Klient.izpisi_vse();
				} catch (URISyntaxException e1){
					// tu lahko obdelamo dogodek URISyntaxException
					Logger.info("ChatFrame.odjavi.URISyntaxException:" + e1.getMessage());
				} catch (ClientProtocolException e2){
					// tu lahko obdelamo dogodek ClientProtocolException
					Logger.info("ChatFrame.odjavi.ClientProtocolException:" + e2.getMessage());
				} catch (IOException e3){
					// tu lahko obdelamo dogodek IOException
					Logger.info("ChatFrame.odjavi.IOException:" + e3.getMessage());
				}

			}
		});
		gumbOdjaviVse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Logger.info("Gumb: Odjavi vse");
				// Da se lahko odjavimo,
				// je potrebno za metodo Klinet.odjavi() uloviiti vse dogodke
				// Če prijava ni uspešna, potem se vrnemo iz metode (return).
				try {
					Klient.odjavi_vse(recipient_name);
				} catch (URISyntaxException e1){
					// tu lahko obdelamo dogodek URISyntaxException
					Logger.info("ChatFrame.odjavi.URISyntaxException:" + e1.getMessage());
				} catch (ClientProtocolException e2){
					// tu lahko obdelamo dogodek ClientProtocolException
					Logger.info("ChatFrame.odjavi.ClientProtocolException:" + e2.getMessage());
				} catch (IOException e3){
					// tu lahko obdelamo dogodek IOException
					Logger.info("ChatFrame.odjavi.IOException:" + e3.getMessage());
				}
			}
		});
	}



	/**
	 * @param person - the person sending the message
	 * @param message - the message content
	 */
	public void addMessage(String person, String message) {
		String chat = this.output.getText();
//		this.output.setForeground(Color.black);
		this.output.setText(chat + person + ": " + message + "\n");
	}

	/**
	 * @param person - the person sending the message
	 * @param message - the message content
	 * @param color - the message text color
	 */
	public void addMessage(String person, String message, Color color) {
		String chat = this.output.getText();
		//
		// metoda setForeground spremeni barvo za celem oknu
		//
		this.output.setForeground(color);
		// this.output.setSelectedTextColor(color);
		this.output.setText(chat + person + ": " + message + "\n");
	}

	public JTextArea getOutput() {
		return output;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
/*
		Logger.info("Key typed:" + e.getKeyChar());
		Logger.info("Novo ime:" + this.vzdevek.getText());
*/
	}

	@Override
	public void keyPressed(KeyEvent e) {
		String staro_ime = recipient_name;

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			recipient_name = this.vzdevek.getText();
			Logger.info("Komu pišem:" + recipient_name);

			// če je polje prazno, potem se sporočilo pošlje vsem (global = true)
			Boolean send_global;
			if (vzdevek.getText().equals("")){
				// this.vzdevek.setText(LoginFrame.vzdevek.getText());
				Logger.info("ChatFrame.keyPressed: new value for login_name:"+LoginFrame.login_name.getText());
				send_global = true;
			} else
				send_global = false;

			// pošljemo sporočilo
			try {
				Klient.poslji_sporocilo(this.input.getText(), recipient_name, send_global);
			} catch (URISyntaxException e1){
				// tu lahko obdelamo dogodek URISyntaxException
				Logger.info("ChatFrame.addMassage.keyPressed.URISyntaxException:" + e1.getMessage());
			} catch (ClientProtocolException e2){
				// tu lahko obdelamo dogodek ClientProtocolException
				Logger.info("ChatFrame.keyPressed.addMassage.ClientProtocolException:" + e2.getMessage());
			} catch (IOException e3){
				// tu lahko obdelamo dogodek IOException
				Logger.info("ChatFrame.keyPressed.addMassage.IOException:" + e3.getMessage());
			}

			//
			// Prikazovanje lastnih sporočil: ČRNO
			//
			this.addMessage(this.vzdevek.getText(), this.input.getText(), Color.black);

			this.input.setText("");

		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
