package metier;

import java.util.Calendar;

public class EventDiscussionStart extends Event {
	private String targetUserId;

	public EventDiscussionStart(String userId, Calendar ts, String targetUserId) {
		super(userId, ts);
		this.targetUserId=targetUserId;
	}

	@Override
	public String toString(String seed) {
		// TODO Auto-generated method stub
		StringBuilder s = new StringBuilder();
		s.append("{\"askedBy\":\"")
		.append(userId)
		.append("\",\"target\":\"")
		.append(targetUserId)
		.append("\",\"discussionId\":\"")
		.append(getDiscussionId())
		.append("\"}");
		return s.toString();
	}

	public String getDiscussionId() {
		return Util.md5(userId+targetUserId);
	}

	@Override
	protected String getEventName() {
		return "discussion_start";
	}

}
