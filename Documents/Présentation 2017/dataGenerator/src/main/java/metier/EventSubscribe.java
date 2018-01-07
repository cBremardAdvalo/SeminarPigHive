package metier;

import java.util.Calendar;

import metier.lists.Cities;
import metier.lists.FirstnameFeminin;
import metier.lists.FirstnameMasculin;
import metier.lists.FirstnameUnknown;
import metier.lists.Lastname;

public class EventSubscribe extends Event {

	public EventSubscribe(String userId, Calendar ts) {
		super(userId,ts);
	}

	@Override
	public String toString(String seed) {
		String[] city = Cities.get(userId);
		int sexe = Factory.buildSexe(userId);
		StringBuilder s = new StringBuilder();
		s.append("{\"age\":")
		.append(Factory.buildAge(userId))
		.append(",\"firstname\":\"")
		.append(sexe==1?FirstnameMasculin.get(userId):sexe==2?FirstnameFeminin.get(userId):FirstnameUnknown.get(userId))
		.append("\",\"lastname\":\"")
		.append(Lastname.get(userId))
		.append("\",\"sexe\":")
		.append(sexe)
		.append(",\"city\":\"")
		.append(city[0])
		.append("\",\"geopoint\":\"")
		.append(city[1])
		.append("\"}");
		return s.toString();
	}

	@Override
	protected String getEventName() {
		return "subcription";
	}
}
