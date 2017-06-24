import org.pmw.tinylog.Logger;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

class IzpisiPrejetoRobot extends TimerTask {

	public IzpisiPrejetoRobot(ChatFrame chat) {
		//this.chat = chat;
	}

	/**
	 * Activate the robot!
	 */
	public void activate() {
		Timer izpis_timer = new Timer();
		izpis_timer.scheduleAtFixedRate(this, 5000, 7000);
	}
	
	@Override
	public void run() {
		String prejeto_sporocilo;

		prejeto_sporocilo = Klient.prejmi_sporocilo();
		if(!Objects.equals(prejeto_sporocilo,"") )
			// chat.addMessage("Berem", prejeto_sporocilo);
			if(prejeto_sporocilo.contentEquals("[]")) {
				Logger.trace("Čakam na sporočila.");
			} else {
				Logger.info("Prebral sem : " + prejeto_sporocilo);
			}

	}
}
