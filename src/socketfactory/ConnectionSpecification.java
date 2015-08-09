package socketfactory;

import sendable.messages.Message;

public interface ConnectionSpecification {
	public boolean send(Message m) throws Throwable;
	public Message receive() throws Throwable;
	public Message AssembleNormalMessage();
}
