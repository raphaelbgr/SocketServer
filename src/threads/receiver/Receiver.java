package threads.receiver;

import java.io.IOException;

import exceptions.ServerException;

public interface Receiver {

	public void receive(Object o) throws IOException, ServerException;
	
}
