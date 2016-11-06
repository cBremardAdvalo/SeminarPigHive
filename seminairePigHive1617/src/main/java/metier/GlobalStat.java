package metier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	private final double[] arrivalsHourDensity = new double[]{0.05,0.04,0.03,0.02,0.0075,0.005,0.0075,0.01,0.01,0.02,0.03,0.03,0.04,0.03,0.04,0.04,0.04,0.05,0.06,0.08,0.1,0.1,0.09,0.07};
	private final double[] arrivalsDayDEnsity =new double[]{0.18,0.02,0.08,0.11,0.08,0.2,0.33};

	private String appName;
	private String backgroundPath;
	private double monthPrice;
	private double monthCost = 4999.99;
	private double flowerPrice;
	private int popularity;
	private int churn;
	private File stagingDirectory;

	private Map<Long,List<Event>> events = new HashMap<Long,List<Event>>();
	private Set<String> connectedUsers = new HashSet<String>();
	private int[] departuresEffective;
	private SimpleDateFormat fDate = new SimpleDateFormat("EEE d MMM yyyy 'à' HH'h'", new Locale("FR","fr"));
	private SimpleDateFormat fDateShort = new SimpleDateFormat("yyyyMMDDHHmmss");
	private NumberFormat fDecimal = new DecimalFormat("###,###,###,###,##0.00");
	private NumberFormat fInteger= new DecimalFormat("###,###,###,###,##0");
	private boolean overheating = false;


	public static GlobalStat getInstance(String appName, String backgroundPath, double monthPrice, double kissPrice, double popularity, double churn, File staging) {
		if(instance == null) {
			instance = new GlobalStat(appName, backgroundPath, monthPrice, kissPrice, popularity, churn, staging);
		}
		return instance;
	}

	private GlobalStat(String appName, String backgroundPath, double monthPrice, double flowerPrice, double popularity, double churn, File stagingDirectory) {
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
		+"\n  kissPrice      = "+flowerPrice
		+"\n  popularity     = "+Double.valueOf(popularity).intValue()
		+"\n  churn          = "+Double.valueOf(churn).intValue()
		+"\n  staging        = "+stagingDirectory
		+"\n  departuresprobs= "+Arrays.toString(departuresprobs));}
		this.appName = appName;
		this.backgroundPath = backgroundPath;
		this.monthPrice = monthPrice;
		this.flowerPrice = flowerPrice;
		this.popularity = Double.valueOf(popularity).intValue();
		this.churn = Double.valueOf(churn).intValue();
		this.stagingDirectory = new File(stagingDirectory,"staging");
		this.stagingDirectory.mkdirs();
		
		Date date = new Date(1451602800000l);
		this.ts = new GregorianCalendar(new Locale("FR","fr"));
		ts.setTime(date);
		this.nbInscrit = 0;
		this.ca = 0d;
	}

	public String getFlowerPrice() {
		return fDecimal.format(flowerPrice).trim() + " €";
	}
	public String getMonthPrice() {
		return fDecimal.format(monthPrice).trim() + " €";
	}
	public String getMonthCost() {
		return fDecimal.format(monthCost).trim() + " €";
	}
	public String getPopularity() {
		return fInteger.format(popularity).trim();
	}
	public String getChurn() {
		return fInteger.format(churn).trim()+" mois";
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
		return fInteger.format(ca).trim() + " €";
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
	public boolean isOverheating() {
		return overheating;
	}
	@Override
	public String toString() {
		String string = getTs()+" : "+getNbInscriptionInMonth()+"/"+getNbInscrit()+" inscrits (dont "+getNbConnected()+" connectés) pour un CA de "+getCa();
		if(debugMode) string += " (Incoming "+events.size()+" steps and departures :"+Arrays.toString(departuresEffective)+")";
		string += " Free memory = "+new DecimalFormat("0.00'%'").format(100.0 * getFreeMemory());
		return string;
	}
	
	private double getFreeMemory() {
		//return Double.valueOf(Runtime.getRuntime().freeMemory()) / Double.valueOf(Runtime.getRuntime().maxMemory());
		return Double.valueOf(Runtime.getRuntime().freeMemory()) / Double.valueOf(Runtime.getRuntime().totalMemory());
	}

	public void nextStep(){
		Map<String,Calendar> lastActionTS = new HashMap<String, Calendar>();
		
		// Increment Time 
		incrementTime();
		
		// Get new users and their events
		int newUser=0;
		Set<String> newUserConnected = new HashSet<String>();
		if(getFreeMemory()<0.25){
			overheating=true;
		}else{
			overheating=false;
			newUser = countArrivals(ts.get(Calendar.DAY_OF_WEEK),ts.get(Calendar.HOUR_OF_DAY));
			for (int i = 0; i < newUser; i++) {
				String id = generateId(ts,i);
				newUserConnected.add(id);
				generateFuturEvents(events, id);
			}
		}
		List<Event> currentEvents = (events.containsKey(ts.getTimeInMillis()) ? events.remove(ts.getTimeInMillis()) : new ArrayList<Event>());
		connectedUsers.addAll(newUserConnected);
		
		// Get known users
		Set<String> oldUserConnected = new HashSet<String>();
		for (Event e : currentEvents) {
			oldUserConnected.add(e.getUserId());
			lastActionTS.put(e.getUserId(), e.getTs());
		}
		connectedUsers.addAll(oldUserConnected);
		if(debugMode) System.out.println("\tprevious="+(connectedUsers.size()-oldUserConnected.size()-newUser)+", returning="+oldUserConnected.size()+", new="+newUser+" -> "+connectedUsers.size());
		
		// Generate subscription
		for (String id : newUserConnected) {
			Calendar arrivalTS = randomizeTS(ts, stepIntervalInHour*3600000l);
			currentEvents.add(new EventSubscribe(id, arrivalTS));
			currentEvents.add(new EventConnection(id, arrivalTS));
			lastActionTS.put(id, arrivalTS);
		}
		if(debugMode) System.out.println("\tAdd "+newUserConnected.size()+" subcription(s)");
		
		// Generate all discussions
		/*
		Set<String> arrivals = oldUserConnected;
		Set<String> outcast = new HashSet<String>();
		arrivals.addAll(newUserConnected);
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
		*/
		
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

		// Get disconnecteds
		int departureWillComeBack = countDepatures(oldUserConnected.size()+newUser-departureForever);
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

		storeCurrentEvents(ts, currentEvents);
	}

	private void incrementTime() {
		int oldMonth = ts.get(Calendar.MONTH);
		ts.add(Calendar.HOUR_OF_DAY, stepIntervalInHour);
		if(oldMonth!=ts.get(Calendar.MONTH)){
			becomeNewMonth();
		}
	}

	/**
	 * For given densities "arrivalsHourDensity" and "arrivalsDayDEnsity",
	 * returns numbers of connection during stepIntervalInHour in oder to respect
	 * an average of popularity connection per day over the week.
	 * @param day : day of week
	 * @param hour : hour of day
	 * @return number of connection during next stepIntervalInHour hours
	 */
	private int countArrivals(int day, int hour) {
		double numberOfConnectionDuringAllDay = arrivalsDayDEnsity[day-1] * 7 * popularity;
		int numberOfConnectionDuringStepIntervalHour = 0;
		for (int i = 0; i < stepIntervalInHour; i++) {
			int hourIndex = (hour+i>=arrivalsHourDensity.length ? (hour+i-arrivalsHourDensity.length) : (hour+i) );
			numberOfConnectionDuringStepIntervalHour += Double.valueOf(Math.ceil(arrivalsHourDensity[hourIndex] * numberOfConnectionDuringAllDay)).intValue();
		}
		return numberOfConnectionDuringStepIntervalHour;
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


	/**
	 * Add all futur events of given user in "events"
	 * @param events
	 * @param id
	 */
	private void generateFuturEvents(Map<Long, List<Event>> events, String id) {
		// get churning time
		long lifeTimeHours = generateRandomLifeTimeInMilliseconds();
		Calendar unsubcribeTime = new GregorianCalendar();
		unsubcribeTime.setTimeInMillis(ts.getTimeInMillis()+lifeTimeHours);
		// get futur connections
		generateFuturEventsConnection(events, id, unsubcribeTime);
		// get unsubscribe event
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

	private void generateFuturEventsConnection(Map<Long, List<Event>> events, String id, Calendar unsubcribeTime) {
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
			events.put(key,futurEvents);
			movingTime.add(Calendar.HOUR_OF_DAY, (1+Double.valueOf(Math.random()*168).intValue())*stepIntervalInHour);
		}
	}

	private long generateRandomLifeTimeInMilliseconds() {
		return(1+Double.valueOf(Math.random()*7200l*churn).intValue())*stepIntervalInHour*3600000l;
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
	


//	private void storeFuturEvents(Map<String, List<Event>> futurEvents) {
//		FileWriter fw = null;
//		BufferedWriter bw = null;
//		for (Entry<String, List<Event>> content : futurEvents.entrySet()) {
//			try {
//				File currentFile = new File(temporaryDirectory, content.getKey());
//				if (!currentFile.exists()) {
//					currentFile.createNewFile();
//				}
//				try {
//					fw = new FileWriter(currentFile.getAbsoluteFile(),true);
//					bw = new BufferedWriter(fw);
//					boolean isFirst=true;
//					for (Event event : content.getValue()) {
//						if(isFirst){
//							isFirst=false;
//						}else{
//							bw.write(String.format("%n"));
//						}
//						bw.write(event.generateEvent());
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}finally{
//					try {bw.close();} catch (IOException e) {e.printStackTrace();}
//					try {fw.close();} catch (IOException e) {e.printStackTrace();}
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private List<Event> relaodEvents(String stagingFile) {
//		List<Event> reloadedEvents = new ArrayList<Event>();
//		File currentFile = new File(stagingFile);
//		if (currentFile.exists()) {
//			FileReader fr = null;
//			BufferedReader br = null;
//			try {
//				fr = new FileReader(currentFile);
//				br = new BufferedReader(fr);
//				String line = br.readLine();
//				while (line != null) {
//					reloadedEvents.add(Event.fromString(line));
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}finally {
//				try {br.close();} catch (IOException e) {e.printStackTrace();}
//				try {fr.close();} catch (IOException e) {e.printStackTrace();}
//			}
//		}
//		return reloadedEvents;
//	}
	
	private void storeCurrentEvents(Calendar ts2, List<Event> currentEvents) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			File currentFile = new File(stagingDirectory, fDateShort.format(ts2.getTime())+".json");
			if (!currentFile.exists()) {
				currentFile.createNewFile();
			}
			fw = new FileWriter(currentFile.getAbsoluteFile(),false);
			bw = new BufferedWriter(fw);
			boolean isFirst=true;
			for (Event event : currentEvents) {
				if(isFirst){
					isFirst=false;
				}else{
					bw.write(String.format("%n"));
				}
				bw.write(event.generateEvent());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {bw.close();} catch (IOException e) {e.printStackTrace();}
			try {fw.close();} catch (IOException e) {e.printStackTrace();}
		}
	}

}
