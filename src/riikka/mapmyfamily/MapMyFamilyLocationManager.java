package riikka.mapmyfamily;


import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


/**
 * This class requires periodically GPS position from Android::LocationManager 
 * Notifies MapMyFamilyService when location has changed or GPS has been disabled.
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

	 public void StartTracking(){
		 Criteria criteria = new Criteria();
		 criteria.setAccuracy(Criteria.ACCURACY_FINE);
		 String provider = locManager.getBestProvider(criteria, true);
		 Log.i(LOG_TAG, "Best provider" + provider);
	 	 locManager.requestLocationUpdates(provider, 3000, 0, this);
	 	 
	 	 if( provider != LocationManager.GPS_PROVIDER ){
	 		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
	 	 }
	 }

	/**
	* From LocationListener
	* Called when the location has changed
	*/
	@Override
	public void onLocationChanged(Location location){
		if( null != observer ){
			String locationText = "XXXXXXXXXX" + String.valueOf(location.getLatitude())+ " , " + String.valueOf(location.getLongitude() + "," + String.valueOf(location.getTime()));
			observer.notifyLocationChanged( locationText );
		}
	}

	/**
	* Called when the provider is disabled by the user.
	*/
	@Override
	public void onProviderDisabled(String provider){
		Log.i(LOG_TAG, "ProviderDisabled");
		locManager.removeUpdates(this);
		StartTracking();
	}
	
	/**
	* Called when the provider is enabled by the user.
	*/
	@Override
	public void onProviderEnabled(String provider){
		Log.i(LOG_TAG, "Provider enabled");
	}
	
	/**
	* Called when the provider status changes
	*/
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras){
		Log.i(LOG_TAG, "Status changed");
	}
	
 }