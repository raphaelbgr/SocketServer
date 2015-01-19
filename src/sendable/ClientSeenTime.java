package sendable;


public class ClientSeenTime extends Client {
	
	private Message message;
	private long seenTime;
	private boolean received = false;
	
	
	public boolean isReceived() {
		return received;
	}
	
	public void setReceived(boolean received) {
		this.received = received;
	}
	public boolean isSeen() {
		return seen;
	}
	public void setSeen(boolean seen) {
		this.seen = seen;
	}
	private boolean seen = false;
	
	public Message getM() {
		return message;
	}
	public void setM(Message m) {
		this.message = m;
	}
	public long getSeenTime() {
		return seenTime;
	}
	public void setSeenTime(long seenTime) {
		this.seenTime = seenTime;
	}
	
}
