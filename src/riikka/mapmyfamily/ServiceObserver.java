package riikka.mapmyfamily;

public interface ServiceObserver {
	
	public void socketConnectionStatusChanged( String status );

	public void networkConnectionDisabled();

	public void locationSentToServer();

}
