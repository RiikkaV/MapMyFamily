package riikka.mapmyfamily;

/**
 * MapMyFamilyActivity
 * 
 * TODO: 
 * - add hardcoded strings to resources (localized?)
 * - create own class for handling notifications
 * - create utility package?
 * - modify layout: add information such as current location provider, events sent to server (status/time) NOTE! 
 * 		requires changes on the server side, change view items colors to indicate status 
 * 		(i.e. red when network connection is not enabled?), landscape layout support, layout for bigger screens,
 * 		geocode coordinates to address?
 * 
 */

import java.util.Calendar;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;


public class MapMyFamilyActivity extends Activity implements LocationManagerObserver, ServiceObserver{
	
	private final String LOG_TAG = "MapMyFamilyActivity";
	private MapMyFamilyService mLocationSenderService;
	MapMyFamilyLocationManager locManager;
	boolean mIsBound;
	private TextView locationView;
	private TextView connectionView;
	
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
        	mLocationSenderService = ((MapMyFamilyService.LocalBinder)service).getService();
        	mLocationSenderService.setObserver ( MapMyFamilyActivity.this );
        }
        
        public void onServiceDisconnected(ComponentName className) {
        	mLocationSenderService = null;
        }
    };
    
    void doBindService() {
        bindService(new Intent(this, 
                MapMyFamilyService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
		if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        
		Intent serviceIntent = new Intent(getApplicationContext(), MapMyFamilyService.class);
	    startService(serviceIntent);
	    
		 // create location manager
	    if( null == locManager ){
	        Context context = getBaseContext();	
			locManager = new MapMyFamilyLocationManager( context );
			locManager.setObserver( this );
			locManager.StartTracking();
	    }
	    locationView = (TextView)findViewById(R.id.locationView);
	    connectionView = (TextView)findViewById(R.id.serverConnectionView);
        
    }
    @Override
    protected void onStart() {
        super.onStart();
        
        if (!mIsBound ){
            Intent bindIntent = new Intent(getApplicationContext(), MapMyFamilyService.class);
            mIsBound = bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);         	
        }

    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        }
    
    @Override
    public void notifyLocationChanged( JSONObject message ){
    	
    	if( null != mLocationSenderService && mIsBound ){
    		
    		mLocationSenderService.sendData( message.toString() );
    		
        	Object longitude=null;
        	Object latitude=null;
        	Object timestamp=null;
        	  
        	try{
        		longitude = message.get( "latitude" );
        		latitude = message.get( "longitude" );
        		timestamp = message.get( "time" );
        	}catch ( JSONException e){
        		Log.i(LOG_TAG, "JSON exception");
        	}

        	if (longitude != null && null != latitude && null != timestamp) {
        		
        		String localTime = parseUTCTime( timestamp.toString() );
        		locationView.setText("Location received: " + longitude.toString() + "," 
        				+ latitude.toString() + " " + localTime);
        	}   		
    	}
    	
    }

	private String parseUTCTime( String timestamp ) {
		String returnTime;
		
		long timeInMilliseconds = Long.valueOf( timestamp );
		Calendar locationDate = Calendar.getInstance();
		locationDate.setTimeInMillis( timeInMilliseconds );
		int date = locationDate.get(Calendar.DATE);
		int month = locationDate.get(Calendar.MONTH) + 1;
		int year = locationDate.get(Calendar.YEAR);
		int hour = locationDate.get(Calendar.HOUR_OF_DAY);
		int minute = locationDate.get(Calendar.MINUTE);
		returnTime = date + "." + month + "." + year + " " + hour + ":" + minute;
		
		return returnTime;
	}

	@Override
	public void notifyLocationProviderChanged(String event) {
		locationView.setText(event);
	}

	@Override
	public void socketConnectionStatusChanged(String status) {
		connectionView.setText("WebSocket status changed:" + status );
		
	}

	@Override
	public void networkConnectionDisabled() {
		connectionView.setText("Please, enable network connection");
	}

	@Override
	public void locationSentToServer() {
		locationView.setText("Location sent to server");
	}
}
