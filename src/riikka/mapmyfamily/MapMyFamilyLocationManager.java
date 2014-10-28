package riikka.mapmyfamily;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


/**
 * This class requires periodically GPS position from Android::LocationManager 
 * Notifies MapMyFamilyService when location has changed or GPS has been disabled.
 * 
 * TODO: 
 * - keep track from the current location provider
 * - get minTime for requests from settings
 * - set client name, phonenumber?
 * 
 */
 
 public class MapMyFamilyLocationManager implements LocationListener{
 
	 LocationManager locManager;
	 LocationManagerObserver observer;
	 Context myContext;
	 public static final String LOG_TAG = "MapMyFamilyLocationManager";
	 
	 /**
	 * Constructor
	 */
	 public MapMyFamilyLocationManager(Context context){
	 	 
		 myContext = context;
		 // get location manager
		 locManager = (LocationManager)myContext.getSystemService(Context.LOCATION_SERVICE);
	 }
	 
	 public void setObserver( LocationManagerObserver observer ){
		 this.observer = observer;
	 }

	 /**
	  * Starts tracking by requesting location updates from location manager.
	  * Both GPS and network are followed as GPS might not be enabled or even
	  * it is enabled it might not provide location data.
	  */
	 public void StartTracking(){
		 locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
		 locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, this);
	 }

	/**
	* From LocationListener
	* Called when the location has changed
	*/
	@Override
	public void onLocationChanged(Location location){
		if( null != observer ){
			JSONObject messageToServer = createJSON( location );
			
			if ( null != messageToServer ){
				observer.notifyLocationChanged( messageToServer );
			}
			
		}
	}

	/**
	 * Creates JSON from location information
	 * TODO: add to utils package
	 */
	private JSONObject createJSON(Location location) {
		JSONObject object = new JSONObject();
		
		  try {
			    object.put("latitude", location.getLatitude());
			    object.put("longitude", location.getLongitude());
			    object.put("accuracy", location.getAccuracy());
			    object.put("event", "location");
			    object.put("time", location.getTime());
			    object.put("client", "mapmyfamily");
			    object.put("locationProvider", location.getProvider());
			  } catch (JSONException e) {
				object = null;
			    Log.i(LOG_TAG, "JSON error");
			  }
		

		return object;
	}

	/**
	* Called when the provider is disabled by the user.
	*/
	@Override
	public void onProviderDisabled(String provider){
		if( null != observer ){
			observer.notifyLocationProviderChanged(provider + " disabled ");
		}
	}
	
	/**
	* Called when the provider is enabled by the user.
	*/
	@Override
	public void onProviderEnabled(String provider){
		if( null != observer ){
			observer.notifyLocationProviderChanged(provider + " enabled ");
		}
	}
	
	/**
	* Called when the provider status changes
	*/
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras){
		if( null != observer ){
			observer.notifyLocationProviderChanged(provider + " status changed: " + status );
		}
	}
	
 }