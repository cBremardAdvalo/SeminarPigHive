package metier;

import java.util.Calendar;

public class EventUnsubscribe extends Event {

	public EventUnsubscribe(String userId, Calendar ts) {
		super(userId,ts);
	}

	@Override
	public String toString(String seed) {
		// TODO Auto-generated method stub
		StringBuilder s = new StringBuilder();
		s.append("{}");
		return s.toString();
	}

	@Override
	protected String getEventName() {
		return "unsubscription";
	}

}
