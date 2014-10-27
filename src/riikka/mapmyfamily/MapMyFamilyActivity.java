package riikka.mapmyfamily;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class MapMyFamilyActivity extends Activity implements LocationManagerObserver{
	
	private MapMyFamilyService mLocationSenderService;
	MapMyFamilyLocationManager locManager;
	boolean mIsBound;
	
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
        	mLocationSenderService = ((MapMyFamilyService.LocalBinder)service).getService();
        }
        
        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
        	mLocationSenderService = null;
        }
    };
    
    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
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
        // The activity is being created.
        // Set the user interface layout for this Activity
        // The layout file is defined in the project res/layout/main_activity.xml file
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
		
		// TODO: settings: server URL, location require time (interval)
        
    }
    @Override
    protected void onStart() {
        super.onStart();
        
        if (!mIsBound ){
            // The activity is about to become visible
            Intent bindIntent = new Intent(getApplicationContext(), MapMyFamilyService.class);
            mIsBound = bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);         	
        }

    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed
        doUnbindService();
        }
    
    public void notifyLocationChanged( String newLocation ){
    	
    	if( null != mLocationSenderService && mIsBound ){
    		mLocationSenderService.sendData( newLocation );
    	}
    	
    }
}
