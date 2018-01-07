package metier;

import java.util.Calendar;

public class EventFlower extends Event {
	private String targetUserId;
	private int quantity;

	public EventFlower(String userId, Calendar ts, String targetUserId, int quantity) {
		super(userId, ts);
		this.targetUserId=targetUserId;
		this.quantity=quantity;
	}

	@Override
	public String toString(String seed) {
		StringBuilder s = new StringBuilder();
		s.append("{\"sendBy\":\"")
		.append(userId)
		.append("\",\"sendTo\":\"")
		.append(targetUserId)
		.append("\",\"quantity\":")
		.append(quantity)
		.append("}");
		return s.toString();
	}

	@Override
	protected String getEventName() {
		return "flower";
	}
}
