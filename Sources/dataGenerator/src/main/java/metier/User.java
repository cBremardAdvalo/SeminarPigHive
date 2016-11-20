package metier;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class User {
	private final double maximalSessionDurationMillisecondes = 3600000.0+Double.valueOf(Math.random()*36000000).intValue();
	private final String id;
	private final Calendar subcriptionTs;
	private final Calendar unsubcriptionTs;
	private Calendar sessionStart;
	private Calendar sessionEnd;
	public User(String id, Calendar subcriptionTs) {
		super();
		this.id = id;
		this.subcriptionTs = subcriptionTs;
		this.unsubcriptionTs = new GregorianCalendar(new Locale("FR","fr"));
		long increment = Double.valueOf((10.0/((9.8)*Math.random()/0.2))*GlobalStat.churn*2678400000l).longValue();
		this.unsubcriptionTs.setTimeInMillis(subcriptionTs.getTimeInMillis() + increment);
		//System.out.println(id+" live from "+GlobalStat.fDateShort.format(subcriptionTs.getTime())+" + "+Double.valueOf(increment/86400000).intValue()+"d = "+GlobalStat.fDateShort.format(unsubcriptionTs.getTime()));
		setSessionStart(subcriptionTs);
	}
	public String getId() {
		return id;
	}
	public Calendar getSubcriptionTs() {
		return subcriptionTs;
	}
	public Calendar getSessionStart() {
		return sessionStart;
	}
	public Calendar getSessionEnd() {
		return sessionEnd;
	}
	public void setSessionStart(Calendar sessionStart) {
		this.sessionStart = sessionStart;
		this.sessionEnd = new GregorianCalendar(new Locale("FR","fr"));
		long increment = Double.valueOf(Math.random()*maximalSessionDurationMillisecondes).longValue();
		this.sessionEnd.setTimeInMillis(sessionStart.getTimeInMillis() + increment);
		//System.out.println(id+" connected from "+GlobalStat.fDateShort.format(sessionStart.getTime())+" + "+Double.valueOf(increment/60000).intValue()+"m = "+GlobalStat.fDateShort.format(sessionEnd.getTime()));
	}
	public boolean isDisconnected(Calendar ts) {
		return sessionEnd.before(ts);
	}
	public boolean isUnsubcribe(Calendar ts) {
		return unsubcriptionTs.before(ts);
	}
	public Event getDisconnectionEvent() {
		//System.out.println(id+" unsubcribe at "+GlobalStat.fDate.format(sessionEnd.getTime()));
		return new EventDisconnection(id, sessionEnd);
	}
	public Event getConnectionEvent() {
		//System.out.println(id+" unsubcribe at "+GlobalStat.fDate.format(sessionStart.getTime()));
		return new EventConnection(id, sessionStart);
	}
	public Event getSubscriptionEvent() {
		//System.out.println(id+" subcribe at "+GlobalStat.fDate.format(subcriptionTs.getTime()));
		return new EventSubscribe(id, subcriptionTs);
	}
	public Event getUnsubcribeEvent() {
		//System.out.println(id+" unsubcribe at "+GlobalStat.fDate.format(unsubcriptionTs.getTime()));
		return new EventUnsubscribe(id, unsubcriptionTs.after(sessionEnd) ? unsubcriptionTs : sessionEnd);
	}
	public Events generateDualEvents(User user) {
		Events events = new Events();
		long maxStart = Math.max(sessionStart.getTimeInMillis(), user.getSessionStart().getTimeInMillis());
		long minEnd = Math.min(sessionEnd.getTimeInMillis(), user.getSessionEnd().getTimeInMillis());
		if(minEnd>maxStart){
			double friendlyScore = getAttractivityWith(user);
			if(friendlyScore>0.3){
				boolean thisIsBigger = id.compareTo(user.getId())>0;
				boolean thisIsSmarter = id.substring(id.length()/2).compareTo(user.getId())>0;
				boolean thisIsLover = id.substring(id.length()/4).compareTo(user.getId())>0;
				Calendar discussionStart = new GregorianCalendar(new Locale("FR","fr"));
				discussionStart.setTimeInMillis(maxStart + Double.valueOf(Double.valueOf(minEnd-maxStart)*Math.random()).longValue());
				Calendar discussionEnd = new GregorianCalendar(new Locale("FR","fr"));
				discussionEnd.setTimeInMillis(discussionStart.getTimeInMillis() + Double.valueOf(Double.valueOf(minEnd-discussionStart.getTimeInMillis())*Math.random()).longValue());
				EventDiscussionStart discussion = new EventDiscussionStart(
						thisIsBigger ? id : user.getId(),
						discussionStart,
						thisIsBigger ? user.getId() : id
						);
				events.add(discussion);
				events.add(new EventDiscussionStop(thisIsSmarter ? id : user.getId(),discussionEnd,discussion));
				if(friendlyScore>0.75){
					Calendar flowerTs = new GregorianCalendar(new Locale("FR","fr"));
					flowerTs.setTimeInMillis(maxStart + Double.valueOf(Double.valueOf(minEnd-maxStart)*Math.random()).longValue());
					events.add(new EventFlower(
								thisIsLover ? id : user.getId(),
								flowerTs,
								thisIsLover ? user.getId() : id,
								Double.valueOf(1d+10d*(friendlyScore-0.75)).intValue()
								));
				}
			}
		}
		return events ;
	}
	private double getAttractivityWith(User user) {
		int age1 = Factory.buildAge(id);
		int age2 = Factory.buildAge(user.getId());
		double score1 = Math.abs(Double.valueOf(age1-age2)) / Double.valueOf( Math.max(age1, age2));
		int sexe1 = Factory.buildSexe(id);
		int sexe2 = Factory.buildSexe(user.getId());
		double score2 = sexe1==sexe2 ? 1d : 0d;
		int value1 = Util.stringToInt(id);
		int value2 = Util.stringToInt(user.getId());
		double score3 = Math.abs(Double.valueOf(value1-value2)) /Double.valueOf( Math.max(value1, value2));
		return 1d - ((9d*score1 + 10d*score2 + score3 ) / 30d);
	}
}
