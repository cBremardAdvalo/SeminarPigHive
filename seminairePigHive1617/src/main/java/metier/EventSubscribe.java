package metier;

import java.util.Calendar;

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
		.append("}");
		return s.toString();
	}

	@Override
	protected String getEventName() {
		return "subcription";
	}
}
