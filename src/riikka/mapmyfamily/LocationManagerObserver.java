package riikka.mapmyfamily;

import org.json.JSONObject;

public interface LocationManagerObserver {
	
	public void notifyLocationChanged( JSONObject messageToServer );
	public void notifyLocationProviderChanged( String event );

}
