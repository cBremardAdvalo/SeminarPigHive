package metier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class GlobalStat {
	private static GlobalStat instance;
	private static boolean debugMode = false;
	private int nbInscrit;
	private double ca = 0;
	private Calendar ts;
	private int nbInscriptionInMonth = 0;
	
	private final int stepIntervalInHour = 6;
	private final double sessionDurationInHour = 5d / Double.valueOf(stepIntervalInHour);
	private final double[] departuresProbs;
	private final double[] arrivalsHourDensity = new double[]{0.05,0.04,0.03,0.02,0.0075,0.005,0.0075,0.01,0.01,0.02,0.03,0.03,0.04,0.03,0.04,0.04,0.04,0.05,0.06,0.08,0.1,0.1,0.09,0.07};
	private final double[] arrivalsDayDEnsity =new double[]{0.18,0.02,0.08,0.11,0.08,0.2,0.33};

	private String appName;
	private String backgroundPath;
	private double monthPrice;
	private double monthCost = 4999.99;
	private double flowerPrice;
	private int popularity;
	public static int churn;
	private File stagingDirectory;
	private final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
	private final boolean manageCPU = bean.isCurrentThreadCpuTimeSupported();

	//private Map<Long,List<Event>> events = new HashMap<Long,List<Event>>();
	private Users[] incomingUsers;
	private List<User> connectedUsers = new ArrayList<User>();
	private int[] departuresEffective;
	public final static SimpleDateFormat fDate = new SimpleDateFormat("EEE d MMM yyyy 'à' HH'h'", new Locale("FR","fr"));
	public final static SimpleDateFormat fDateShort = new SimpleDateFormat("yyyyMMDDHHmmss");
	public final static  NumberFormat fDecimal = new DecimalFormat("###,###,###,###,##0.00");
	public final static  NumberFormat fInteger= new DecimalFormat("###,###,###,###,##0");
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
		departuresProbs = new double[maxSessionDuration];
		departuresEffective = new int[maxSessionDuration];
    	double cumulative = 0d;
    	for (int i = 0; i < maxSessionDuration; i++) {
    		double value =  1.0 - Math.abs(sessionDurationInHour-i)/sessionDurationInHour;
    		cumulative += value;
    		departuresProbs[i] = value;
    		departuresEffective[i] = 0;
		}
    	for (int i = 0; i < maxSessionDuration; i++) departuresProbs[i] = departuresProbs[i] /cumulative;
		if(debugMode){System.out.println(
		"GlobalStat :"
		+"\n  appName        = "+appName
		+"\n  backgroundPath = "+backgroundPath
		+"\n  monthPrice     = "+monthPrice
		+"\n  kissPrice      = "+flowerPrice
		+"\n  popularity     = "+Double.valueOf(popularity).intValue()
		+"\n  churn          = "+Double.valueOf(churn).intValue()
		+"\n  staging        = "+stagingDirectory
		+"\n  departuresprobs= "+Arrays.toString(departuresProbs));}
		this.appName = appName;
		this.backgroundPath = backgroundPath;
		this.monthPrice = monthPrice;
		this.flowerPrice = flowerPrice;
		this.popularity = Double.valueOf(popularity).intValue();
		GlobalStat.churn = Math.max(1, Double.valueOf(churn).intValue());
		this.stagingDirectory = new File(stagingDirectory,"staging");
		this.stagingDirectory.mkdirs();
		this.incomingUsers = new Users[31*24*GlobalStat.churn/this.stepIntervalInHour];
		
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
		if(debugMode) string += " (Incoming departures :"+Arrays.toString(departuresEffective)+")";
		return string;
	}

	public void nextStep() throws Exception{
		chekMemory();
		Events events = new Events();
		// Increment Time 
		incrementTime();
		// Get already connected users
		Users userDisconnected = generateDisconnectedUsers();
		connectedUsers.removeAll(userDisconnected);
		// Get new users
		Users newUserConnected = generateNewSubcribers();
		// Get known users
		Users recomingUserConnected = generateReturningUsers();

		// Generate events subscription, connection and disconnection
		int numberOfUnsubscription = 0;
		for (User user : newUserConnected) {
			events.add(user.getConnectionEvent());
			events.add(user.getSubscriptionEvent());
		}
		for (User user : recomingUserConnected) {
			events.add(user.getConnectionEvent());
		}
		for (User user : userDisconnected) {
			events.add(user.getDisconnectionEvent());
			if(user.isUnsubcribe(ts)){
				events.add(user.getUnsubcribeEvent());
				numberOfUnsubscription++;
			}else{
				int idx = getNextConnexionIndex();
				if(incomingUsers[idx] == null){
					incomingUsers[idx] = new Users();
				}
				incomingUsers[idx].add(user);
			}
		}
		
		
		
		// Generate all discussions
		if (!overheating){
			for (User user1 : connectedUsers) {
				long zeroUser = manageCPU ? bean.getCurrentThreadUserTime() : 0l;
				long zeroCPU = manageCPU ? bean.getCurrentThreadCpuTime() : 0l;
				for (User user2 : newUserConnected) {
					Events dualEvents = user1.generateDualEvents(user2);
					if(dualEvents!=null && ! dualEvents.isEmpty()){
						events.addAll(dualEvents);
						for (Event event : dualEvents) {
							if("flower".equals(event.getEventName())){
								ca += flowerPrice;
							} else if("discussion_start".equals(event.getEventName())){
							} else if("discussion_end".equals(event.getEventName())){
							} else {
								if(Math.random()<0.01) System.err.println("Dual event "+event.getEventName()+" doesn't increment recettes. Add it here " + Thread.currentThread().getStackTrace()[1].toString());
							}
						}
					}
				}
				for (User user2 : recomingUserConnected) {
					Events dualEvents = user1.generateDualEvents(user2);
					if(dualEvents!=null && ! dualEvents.isEmpty()){
						events.addAll(dualEvents);
					}
				}
				checkCPU(zeroUser, zeroCPU);
			}
		}
		
		// Update statistics
		connectedUsers.addAll(newUserConnected);
		connectedUsers.addAll(recomingUserConnected);
		
		nbInscrit = nbInscrit + newUserConnected.size() - numberOfUnsubscription;
		nbInscriptionInMonth += newUserConnected.size();
		ca += newUserConnected.size() * monthPrice;

		storeCurrentEvents(ts, events);
	}


	
	private void chekMemory() {
		double memoryFree = Math.max(
				Double.valueOf(Runtime.getRuntime().freeMemory()) / Double.valueOf(Runtime.getRuntime().maxMemory())
				,
				Double.valueOf(Runtime.getRuntime().freeMemory()) / Double.valueOf(Runtime.getRuntime().totalMemory())
				);
		if(memoryFree<0.2){
			System.out.println("Overheat memory : "+(1d-memoryFree));
			//overheating=true;
		}else{
			//overheating=false;
		}
	}
	private void checkCPU(long zeroUser, long zeroCPU) {
		if(getCpuUsage(zeroUser, zeroCPU)>0.6){
			System.out.println("Overheat CPU : "+getCpuUsage(zeroUser, zeroCPU));
			try {Thread.sleep(10);} catch (InterruptedException e) {}
		}
	}

	private double getCpuUsage(long zeroUser, long zeroCPU) {
		if(manageCPU){
			double cpu = Double.valueOf(bean.getCurrentThreadCpuTime()-zeroCPU);
			double user = Double.valueOf(bean.getCurrentThreadUserTime()-zeroUser);
			return (cpu-user)/Math.max(1d,cpu);
		}
		else{
			return 1d;
		}
	}

	private int getNextConnexionIndex() {
		double maxTimeToReconnection = Double.valueOf(744.0/stepIntervalInHour);
		double minTimeToReconnection = Double.valueOf(10.0/stepIntervalInHour);
		return Math.max(0,Math.min(incomingUsers.length, Double.valueOf(maxTimeToReconnection / ( 1.0 + (maxTimeToReconnection-minTimeToReconnection)*Math.random()/minTimeToReconnection)).intValue()));
	}

	private Users generateDisconnectedUsers() {
		Users disconnectedUsers = new Users();
		for (User user : connectedUsers)
			if(user.isDisconnected(ts))
				disconnectedUsers.add(user);
		return disconnectedUsers ;
	}

	private Users generateReturningUsers() {
		Users returningUserConnected = incomingUsers[0]== null ? new Users(0) : new Users(incomingUsers[0]) ;
		for (int i = 1; i < incomingUsers.length; i++) {
			incomingUsers[i-1] = incomingUsers[i];
		}
		incomingUsers[incomingUsers.length-1]=null;
		for (User user : returningUserConnected) {
			user.setSessionStart(generateNewSubcriptionTs(ts,stepIntervalInHour));
		}
		return returningUserConnected;
	}

	private Users generateNewSubcribers() {
		Users newUserConnected=new Users(); 
		if(!overheating){
			int numberOfNewSubcription = countArrivals(ts.get(Calendar.DAY_OF_WEEK),ts.get(Calendar.HOUR_OF_DAY));
			for (int i = 0; i < numberOfNewSubcription; i++) {
				String id = generateNewUserId(ts,i);
				Calendar subcriptionTs = generateNewSubcriptionTs(ts,stepIntervalInHour);
				newUserConnected.add(new User(id, subcriptionTs));
			}
		}
		return newUserConnected;
	}



	private void incrementTime() {
		int oldMonth = ts.get(Calendar.MONTH);
		ts.add(Calendar.HOUR_OF_DAY, stepIntervalInHour);
		if(oldMonth!=ts.get(Calendar.MONTH)){
			becomeNewMonth();
		}
	}

	private String generateNewUserId(Calendar ts2, int index){
		return Util.md5(Long.toString(ts2.getTimeInMillis()+index));
	}
	
	private Calendar generateNewSubcriptionTs(Calendar ts2, int stepIntervalInHour2) {
		Calendar subcriptionTs = new GregorianCalendar(new Locale("FR","fr"));
		subcriptionTs.setTime(ts2.getTime());
		subcriptionTs.add(Calendar.MILLISECOND, -1 * Double.valueOf(Math.random()*stepIntervalInHour2*3600000.0).intValue());
		return subcriptionTs;
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


	private void becomeNewMonth() {
		ca -= monthCost;
		ca += Math.max(0, nbInscrit - nbInscriptionInMonth) * monthPrice;
		nbInscriptionInMonth = 0;
		
	}

	private void storeCurrentEvents(Calendar ts2, Events currentEvents) {
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
