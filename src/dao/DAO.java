package dao;

import sendable.Message;

public interface DAO {
	public boolean send(Message m) throws Throwable;
	public Message receive() throws Throwable;
	public Message AssembleNormalMessage();
}
