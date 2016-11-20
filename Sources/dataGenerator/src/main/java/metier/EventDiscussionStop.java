package metier;

import java.util.Calendar;

public class EventDiscussionStop extends Event {
	private String discussionId;

	public EventDiscussionStop(String userId, Calendar ts, EventDiscussionStart discussion) {
		super(userId,ts);
		this.discussionId = discussion.getDiscussionId();
	}

	@Override
	public String toString(String seed) {
		// TODO Auto-generated method stub
		StringBuilder s = new StringBuilder();
		s.append("{\"closedBy\":\"")
		.append(userId)
		.append("\",\"discussionId\":\"")
		.append(discussionId)
		.append("\"}");
		return s.toString();
	}

	@Override
	protected String getEventName() {
		return "discussion_end";
	}

}
