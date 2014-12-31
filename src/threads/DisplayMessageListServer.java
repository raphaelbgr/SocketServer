package threads;

@Deprecated
public class DisplayMessageListServer extends Thread {
	
	@SuppressWarnings("static-access")
	public void run () {
		while (true) {
			try {
				this.sleep(10000);
				if (servermain.ServerMain.mc.getMessageList().isEmpty()) {
					System.err.println("No messages to display.");
				} else {
					servermain.ServerMain.mc.printList();
				}		
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
}
