package riikka.mapmyfamily;

public interface ServiceObserver {
	
	public void socketConnectionStatusChanged( NetworkStatus status );

	public void messageReceived(String payload);

}
