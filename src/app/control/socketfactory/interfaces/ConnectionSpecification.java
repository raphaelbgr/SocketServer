package app.control.socketfactory.interfaces;

import app.model.messages.Message;

public interface ConnectionSpecification {
	public boolean send(Message m) throws Throwable;
	public Message receive() throws Throwable;
	public Message AssembleNormalMessage();
}
