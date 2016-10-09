package metier;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class GlobalStat {
	private static GlobalStat instance;
	private static boolean debugMode = false;
	private int nbInscrit;
	private double ca = 0;
	private Calendar ts;
	private int nbInscriptionInMonth = 0;
	
	private final int stepIntervalInHour = 1;
	private final double sessionDurationInHour = 5d / Double.valueOf(stepIntervalInHour);
	private final double[] departuresprobs;
	private final double[] incrementalHourArrivals = new double[]{0.05,0.04,0.03,0.02,0.0075,0.005,0.0075,0.01,0.01,0.02,0.03,0.03,0.04,0.03,0.04,0.04,0.04,0.05,0.06,0.08,0.1,0.1,0.09,0.07};
	private final double[] incrementalDayArrivals =new double[]{0.18,0.02,0.08,0.11,0.08,0.2,0.33};

	private String appName;
	private String backgroundPath;
	private double monthPrice;
	private double monthCost = 23456.99 / Double.valueOf(stepIntervalInHour);
	private double kissPrice;
	private int popularity;
	private int churn;
	private File staging;

	private Map<Long,List<Event>> events = new HashMap<Long,List<Event>>();
	private Set<String> connectedUsers = new HashSet<String>();
	private int[] departuresEffective;
	private SimpleDateFormat fDate = new SimpleDateFormat("EEE d MMM yyyy 'à' HH'h'", new Locale("FR","fr"));
	private NumberFormat fDecimal = new DecimalFormat("###,###,###,###,##0.00");
	private NumberFormat fInteger= new DecimalFormat("###,###,###,###,##0");

	public static GlobalStat getInstance(String appName, String backgroundPath, double monthPrice, double kissPrice, double popularity, double churn, File staging) {
		if(instance == null) {
			instance = new GlobalStat(appName, backgroundPath, monthPrice, kissPrice, popularity, churn, staging);
		}
		return instance;
	}

	private GlobalStat(String appName, String backgroundPath, double monthPrice, double kissPrice, double popularity, double churn, File staging) {
		super();
		int maxSessionDuration = Double.valueOf(Math.ceil(2*sessionDurationInHour)).intValue();
		departuresprobs = new double[maxSessionDuration];
		departuresEffective = new int[maxSessionDuration];
    	double cumulative = 0d;
    	for (int i = 0; i < maxSessionDuration; i++) {
    		double value =  1.0 - Math.abs(sessionDurationInHour-i)/sessionDurationInHour;
    		cumulative += value;
    		departuresprobs[i] = value;
    		departuresEffective[i] = 0;
		}
    	for (int i = 0; i < maxSessionDuration; i++) departuresprobs[i] = departuresprobs[i] /cumulative;
		if(debugMode){System.out.println(
		"GlobalStat :"
		+"\n  appName        = "+appName
		+"\n  backgroundPath = "+backgroundPath
		+"\n  monthPrice     = "+monthPrice
		+"\n  kissPrice      = "+kissPrice
		+"\n  popularity     = "+Double.valueOf(popularity).intValue()
		+"\n  churn          = "+Double.valueOf(churn).intValue()
		+"\n  staging        = "+staging
		+"\n  departuresprobs= "+Arrays.toString(departuresprobs));}
		this.appName = appName;
		this.backgroundPath = backgroundPath;
		this.monthPrice = monthPrice;
		this.kissPrice = kissPrice;
		this.popularity = Double.valueOf(popularity).intValue();
		this.churn = Double.valueOf(churn).intValue();
		this.staging = staging;
		
		Date date = new Date(1451602800000l);
		this.ts = new GregorianCalendar(new Locale("FR","fr"));
		ts.setTime(date);
		this.nbInscrit = 0;
		this.ca = 0d;
	}
	
	public void nextStep(){
		Map<String,Calendar> lastActionTS = new HashMap<String, Calendar>();
		
		// Increment Time 
		int oldMonth = ts.get(Calendar.MONTH);
		ts.add(Calendar.HOUR_OF_DAY, stepIntervalInHour);
		if(oldMonth!=ts.get(Calendar.MONTH)){
			becomeNewMonth();
		}
		List<Event> currentEvents = (events.containsKey(ts.getTimeInMillis()) ? events.remove(ts.getTimeInMillis()) : new ArrayList<Event>());
		for (Long eventsTS : events.keySet()) {
			if(eventsTS < ts.getTimeInMillis()){
				System.err.println("missing ts "+fDate.format(new Date(eventsTS)));
			}
		}
		
		
		// Get new users
		int newUser = countArrivals(ts.get(Calendar.DAY_OF_WEEK),ts.get(Calendar.HOUR_OF_DAY));
		Set<String> arrivalsNew = new HashSet<String>(newUser);
		for (int i = 0; i < newUser; i++) {
			String id = generateId(ts,i);
			arrivalsNew.add(id);
			generateFutureEvents(id);
		}
		connectedUsers.addAll(arrivalsNew);
		
		// Get known users
		Set<String> arrivalsOld = new HashSet<String>();
		for (Event e : currentEvents) {
			arrivalsOld.add(e.getUserId());
			lastActionTS.put(e.getUserId(), e.getTs());
		}
		int oldUser = arrivalsOld.size();
		connectedUsers.addAll(arrivalsOld);
		if(debugMode) System.out.println("\tprevious="+(connectedUsers.size()-oldUser-newUser)+", returning="+oldUser+", new="+newUser+" -> "+connectedUsers.size());
		
		// Generate subscription
		for (String id : arrivalsNew) {
			Calendar arrivalTS = randomizeTS(ts, stepIntervalInHour*3600000l);
			currentEvents.add(new EventSubscribe(id, arrivalTS));
			currentEvents.add(new EventConnection(id, arrivalTS));
			lastActionTS.put(id, arrivalTS);
		}
		if(debugMode) System.out.println("\tAdd "+arrivalsNew.size()+" subcription(s)");
		
		// Generate all discussions
		Set<String> arrivals = arrivalsOld;
		Set<String> outcast = new HashSet<String>();
		arrivals.addAll(arrivalsNew);
		int numDiscussion = 0;
		for (String id1 : arrivals) {
			if(!outcast.contains(id1)){
				for (String id2 : connectedUsers) {
					if(Math.random()<0.8 && !id1.equals(id2)){
						Calendar discussionTS = null;
						if(lastActionTS.containsKey(id1)){
							discussionTS = lastActionTS.get(id1);
						}
						if(lastActionTS.containsKey(id2)){
							if(discussionTS==null){
								discussionTS = lastActionTS.get(id2);
							}else if(discussionTS.before(lastActionTS.get(id2))){
								discussionTS = lastActionTS.get(id2);
							}
						}
						if(discussionTS==null){
							discussionTS = randomizeTS(ts, stepIntervalInHour*3600000l);
						}
						EventDiscussionStart discussion = new EventDiscussionStart(id2, discussionTS, id1);
						currentEvents.add(discussion);
						Calendar discussionEndTS = randomizeTS(discussionTS, stepIntervalInHour*3600000l);
						currentEvents.add(new EventDiscussionStop(id1.compareTo(id2)<0?id1:id2, discussionEndTS, discussion));
						lastActionTS.put(id1, discussionEndTS);
						lastActionTS.put(id2, discussionEndTS);
						outcast.add(id2);
						numDiscussion++;
					}
				}
			}
		}
		if(debugMode) System.out.println("\tAdd "+numDiscussion+" discussions");
		
		// Get unsubcribes
		int departureForever = 0;
		for (Event event : currentEvents) {
			if(event.isUnsubscribe()){
				String id = event.getUserId();
				connectedUsers.remove(id);
				departureForever++;
				event.setTs(randomizeTS(lastActionTS.containsKey(id)?lastActionTS.get(id):ts, stepIntervalInHour*3600000l));
			}
		}
		if(debugMode) System.out.println("\tLose "+departureForever+" churnners");

		// Get natural departures
		int departureWillComeBack = countDepatures(oldUser+newUser-departureForever);
		Set<String> departures = new HashSet<String>();
		Iterator<String> it = connectedUsers.iterator();
		int i=0;
		while (it.hasNext() && i < departureWillComeBack) {
			String id = (String) it.next();
			departures.add(id);
			i++;
		}
		if(debugMode) System.out.println("\tLose "+i+" natural departures");
		connectedUsers.removeAll(departures);
		
		// Update statistics
		nbInscrit = nbInscrit + newUser - departureForever;
		nbInscriptionInMonth += newUser;
		ca += newUser * monthPrice;

		storeEvents(currentEvents);
	}

	public String getNbConnected() {
		return fInteger.format(connectedUsers.size()).trim();
	}
	public String getNbInscrit() {
		return fInteger.format(nbInscrit).trim();
	}
	public String getNbInscriptionInMonth() {
		return fInteger.format(nbInscriptionInMonth).trim();
	}
	public String getCa() {
		return fDecimal.format(ca).trim() + " €";
	}
	public String getTs() {
		return fDate.format(ts.getTime());
	}
	public String getAppName() {
		return appName;
	}
	public String getBackgroundPath(){
		return backgroundPath;
	}
	@Override
	public String toString() {
		String string = getTs()+" : "+getNbInscriptionInMonth()+"/"+getNbInscrit()+" inscrits (dont "+getNbConnected()+" connectés) pour un CA de "+getCa();
		if(debugMode) string += " (A venir "+events.size()+" steps and departures :"+Arrays.toString(departuresEffective)+")";
		string += " Free memory = "+new DecimalFormat("0.00'%'").format(100.0 * Runtime.getRuntime().freeMemory() / Runtime.getRuntime().maxMemory());
		return string;
	}

	
	private int countArrivals(int day, int hour) {
		double incrementtalOfDay = incrementalDayArrivals[day-1] * 7 * popularity * stepIntervalInHour;
		int incrementalHour = Double.valueOf(Math.ceil(incrementalHourArrivals[hour] * incrementtalOfDay)).intValue();
		return incrementalHour;
	}

	private int countDepatures(int arrivals) {
		int leavingPeoples = departuresEffective[0];
		int totalChurn = 0;
		for (int i = 1; i < departuresprobs.length; i++) {
			int incrementalChurn = Double.valueOf(Math.round(arrivals * departuresprobs[i-1])).intValue();
			totalChurn += incrementalChurn;
			departuresEffective[i-1] = departuresEffective[i] + incrementalChurn;
		}
		departuresEffective[departuresprobs.length-1] = Double.valueOf(Math.round(arrivals * departuresprobs[departuresprobs.length-1])).intValue();
		totalChurn += departuresEffective[departuresprobs.length-1];
		departuresEffective[Double.valueOf(sessionDurationInHour).intValue()] +=  (arrivals-totalChurn);
		return leavingPeoples;
	}

	private void becomeNewMonth() {
		ca -= monthCost;
		ca += Math.max(0, nbInscrit - nbInscriptionInMonth) * monthPrice;
		nbInscriptionInMonth = 0;
		
	}

	private void generateFutureEvents(String id) {
		long lifeTimeHours = (1+Double.valueOf(Math.random()*7200l*churn).intValue())*stepIntervalInHour;
		Calendar unsubcribeTime = new GregorianCalendar();
		unsubcribeTime.setTimeInMillis(ts.getTimeInMillis()+lifeTimeHours*3600000l);
		Calendar movingTime =  new GregorianCalendar();
		movingTime.setTimeInMillis(ts.getTimeInMillis());
		movingTime.add(Calendar.HOUR_OF_DAY, (1+Double.valueOf(Math.random()*168).intValue())*stepIntervalInHour);
		while(movingTime.before(unsubcribeTime)){
			long key = movingTime.getTimeInMillis();
			List<Event> futurEvents;
			if(events.containsKey(key)){
				futurEvents = events.get(key);
			}else{
				futurEvents = new ArrayList<Event>();
			}
			futurEvents.add(new EventConnection(id, randomizeTS(movingTime, stepIntervalInHour*3600000)));
			movingTime.add(Calendar.HOUR_OF_DAY, (1+Double.valueOf(Math.random()*168).intValue())*stepIntervalInHour);
		}
		long key = unsubcribeTime.getTimeInMillis();
		List<Event> futurEvents;
		if(events.containsKey(key)){
			futurEvents = events.get(key);
		}else{
			futurEvents = new ArrayList<Event>();
		}
		futurEvents.add(new EventUnsubscribe(id, unsubcribeTime));
		events.put(key, futurEvents);
	}
	
	private void storeEvents(List<Event> currentEvents) {
		// TODO Auto-generated method stub
//		for (Event event : currentEvents) {
//			System.out.println(event.generateEvent());
//		}
	}
	
	
	private static Calendar randomizeTS(Calendar ts, long maximalAdditionnalMillisecondes){
		Calendar randomTS = new GregorianCalendar(new Locale("FR","fr"));
		randomTS.setTime(ts.getTime());
		randomTS.add(Calendar.MILLISECOND, Double.valueOf(Math.random()*maximalAdditionnalMillisecondes).intValue());
		return randomTS;
	}

	private static String generateId(Calendar ts, int index){
		return Util.md5(Long.toString(ts.getTimeInMillis()+index));
	}

}
