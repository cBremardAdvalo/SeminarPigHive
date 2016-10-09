package metier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public abstract class Event {
	private static DateFormat df = null;
	protected Calendar ts;
	protected String userId;
	
	public Event(String userId, Calendar ts) {
		super();
		if(df ==null){
			df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));
		}
		this.ts = ts;
		this.userId = userId;
	}

	
	public String getUserId() {
		return userId;
	}
	public Calendar getTs(){
		return ts;
	}
	public void setTs(Calendar ts) {
		this.ts = ts;
	}


	public String generateEvent(){
		StringBuilder s = new StringBuilder();
		s.append("{\"userId\":\"")
		.append(userId)
		.append("\",\"ts\":\"")
		.append(df.format(ts.getTime()))
		.append("\",\"eventName\":\"")
		.append(getEventName())
		.append("\",\"event\":")
		.append(toString(userId))
		.append("}");
		return s.toString();
	}
	
	protected abstract String getEventName();

	protected abstract String toString(String seed);
	
	public boolean isUnsubscribe(){
		return this instanceof EventUnsubscribe;
	}
	
	public boolean isKiss(){
		return this instanceof EventKiss;
	}
}
