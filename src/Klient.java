import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.pmw.tinylog.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


class Klient {
	// skupni atributi za razred Klient
	private static String chitchat_web_address = "http://chitchat.andrej.com";
	public static String sender_value;

	public static void parse_JSON(String str_json_array) {
		// podatke v JSON polju zapišemo kot Java objekt (to naredi JSONValue.parse)
		Object jobj_json_array = JSONValue.parse(str_json_array);
		// JSON podatke zapisane v Java obkejtu pretvorimo v JSON (casting to JSONArray)
		JSONArray json_array=(JSONArray)jobj_json_array;
		// vsak element polja je JSON objekt, zato potrebujemo novo spremenljivko
		JSONObject json_obj;

		for (int i = 0; i < json_array.size(); i++) {
			// potrebno je še zagotoviti pravi tip za elemente polje (casting to JSONObject)
			json_obj=(JSONObject)json_array.get(i);
//			System.out.println(i);

			// global is Boolean and cannot be casted to String - use other way for casting
			String global_value     = "" + json_obj.get("global");
			// sender, text, recipients are "Strings" and can be casted to String
			sender_value     = (String) json_obj.get("sender");
			String text_value       = (String) json_obj.get("text");
			String recipient_value  = (String) json_obj.get("recipient");

//			System.out.println(" \"global\"   :"   + global_value);
//			System.out.println(" \"sender\"   :"   + sender_value);
//			System.out.println(" \"recipient\":"+ recipient_value);
//			System.out.println(" \"text\"     :"     + text_value);

			if (global_value.equals("true")) {
				ChitChat.chatFrame.addMessage(sender_value,text_value, Color.blue);
				System.out.println(" " + sender_value + " : " + text_value + " (GLOBAL)");
			} else {
				ChitChat.chatFrame.addMessage(sender_value,text_value, Color.orange);
				System.out.println(" " + sender_value + " : " + text_value);
			}
		}
	}


	public static String prejmi_sporocilo() {
		String current_sender = LoginFrame.login_name.getText();

		String responseBody;
//		String messageBody;
		// Logger.info("-> prejmi_sporočilo()");

		try {
			responseBody = Request
					.Get(chitchat_web_address + "/messages?username="+current_sender)
//					.bodyString(messageBody, ContentType.APPLICATION_JSON)
					.execute()
					.returnContent()
					.asString();


			//
			// responseBody je v obliki "ContentType.APPLICATION_JSON"
			//
			if(!responseBody.equals("[]")) {
				Logger.info("---> prejmi_sporočilo(responseBody): " + responseBody);
				//
				// na okno izpišem sporočilo drugih
				//
				//
				parse_JSON(responseBody);
			}
/*
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonObjekt = mapper.readTree(responseBody);
			String posiljatelj = jsonObjekt.get("a").textValue();
*/

		} catch (IOException e3){
			// tu lahko obdelamo dogodek IOException
			responseBody = "Klient.prejmi_sporočilo.IOException:" + e3.getMessage();
			Logger.error(responseBody);
			return "";
		}

		return responseBody;
	}

	public static void poslji_sporocilo(String sporocilo, String username, Boolean javno)
			throws IOException, URISyntaxException {
		Logger.info("-> poslji_sporočilo()");

		URI uri;
		String current_sender = LoginFrame.login_name.getText();
		String message;

		// Uporabimo "Uniform Resource Identifier (URI)"
		uri = new URIBuilder(chitchat_web_address + "/messages")
				.addParameter("username", current_sender)
				.build();

		if(javno) { // global = true
			message = "{ \"global\" : " + true + "," +
					" \"text\" : \"" + sporocilo + "\"  }";
		} else { // global = false
			message = "{ \"global\" : " + false + "," +
					" \"recipient\" : \"" + username + "\"," +
					" \"text\" : \"" + sporocilo + "\"  }";
		}

		Logger.info("poslji_sporocilo: URI:"+uri);
		Logger.info("poslji_sporocilo: message:"+message);

		String responseBody = Request
				.Post(uri)
				.bodyString(message, ContentType.APPLICATION_JSON)
				.execute()
				.returnContent()
				.asString();

		Logger.info("poslji_sporocilo: Request:" + responseBody);
	}

	public static void odjavi_vse(String username) throws IOException, URISyntaxException {
		String ime_uporabnika;
		// Preberi vse uporabnike in jih odjavi
		Logger.info("-> odjavi_vse()");

		List<Uporabnik> seznam = seznam_uporabnikov();
		Logger.info("Seznam uporabnikov:" + seznam);
		Logger.info("Imena uporabnikov:" + imena_uporabnikov(seznam));
		for (String uporabnik : imena_uporabnikov(seznam)) {
			ime_uporabnika = uporabnik;
			Logger.info("odjavi uporabnika \"" + ime_uporabnika+ "\" v zanki: " + odjavi(ime_uporabnika));
			ChitChat.chatFrame.addMessage("Odjavi uporabnika:", ime_uporabnika, Color.gray);
		}
	}

	public static void izpisi_vse() throws IOException, URISyntaxException {
		// Izpise vse uporabnike
		// ClientProtocolException, IOException, URISyntaxException
		String str_seznam;

		List<Uporabnik> seznam = seznam_uporabnikov();
		str_seznam = "" + seznam;
		Logger.info("Seznam uporabnikov:" + str_seznam);
		ChitChat.chatFrame.addMessage("Seznam uporabnikov:", str_seznam, Color.gray);
	}


	private static List<String> imena_uporabnikov(List<Uporabnik> seznam) {
		List<String> seznam_imen = new ArrayList<String>();
		Logger.info("-> imena_uporabnikov()");

		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new ISO8601DateFormat());

		for (Uporabnik uporabnik : seznam) {
			seznam_imen.add(uporabnik.getUsername());
		}

		return seznam_imen;
	}

	private static List<Uporabnik> seznam_uporabnikov()
			throws IOException {
		Logger.info("-> seznam_uporabnikov()");

		String responseBody = Request
				.Get(chitchat_web_address + "/users")
				.execute()
				.returnContent()
				.asString();

		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new ISO8601DateFormat());

		TypeReference<List<Uporabnik>> t = new TypeReference<List<Uporabnik>>() {
		};
		return mapper.readValue(responseBody, t);
	}


	private static String odjavi(String username)
			throws IOException, URISyntaxException {
		Logger.info("-> odjavi()");

		String time = Long.toString(new Date().getTime());
		URI uri = new URIBuilder(chitchat_web_address + "/users")
				.addParameter("username", username)
				.addParameter("stop_cache", time)
				.build();
		return Request
								.Delete(uri)
								.execute()
								.returnContent()
								.asString();
	}

	public static void javno_odjavi(String username) {
		try {
			odjavi(username);
		} catch (URISyntaxException e1){
			// tu lahko obdelamo dogodek URISyntaxException
			Logger.info("Klient.odjavi.URISyntaxException:" + e1.getMessage());
		} catch (IOException e3){
			// tu lahko obdelamo dogodek IOException
			Logger.info("Klient.odjavi.IOException:" + e3.getMessage());
		}
	}


	public static void prijavi(String username)
			throws URISyntaxException, IOException {
		Logger.info("-> prijavi()");

		String time = Long.toString(new Date().getTime());
		URI uri = new URIBuilder(chitchat_web_address + "/users")
				.addParameter("username", username)
				.addParameter("stop_cache", time)
				.build();
		Request
				.Post(uri)
				.execute()
				.returnContent()
				.asString();
	}
}
