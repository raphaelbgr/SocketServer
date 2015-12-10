package app.control.services.interfaces;

import net.sytes.surfael.api.model.messages.Message;

public interface ConnectionSpecification {
	public boolean send(Message m) throws Throwable;
	public Message receive() throws Throwable;
	public Message AssembleNormalMessage();
}
