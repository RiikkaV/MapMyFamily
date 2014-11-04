package riikka.mapmyfamily;

/**
 * MapMyFamilyActivity
 * 
 * The main (and only) activity. If layout for small screens is in use
 * creates fragments and actionbar/viewpager for navigating between fragments.
 * Otherwise both fragments are cretead through the main_layout.xml (layout-large).
 * 
 * Creates also LocationManager for obtaining the current location and MapMyFamilyService to send
 * location data to the server.
 */

import java.util.ArrayList;
import org.json.JSONObject;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;


public class MapMyFamilyActivity extends FragmentActivity implements LocationManagerObserver, ServiceObserver{
	
	private final String LOG_TAG = "MapMyFamilyActivity";
	private MapMyFamilyService mLocationSenderService;
	MapMyFamilyLocationManager locManager;
	boolean mIsBound;
	LocationFragment locationFragment=null;
	NetworkFragment networkFragment=null;
	FragmentAdapter mAdapter;
    ViewPager mPager;
	 
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
	    
	    // create fragments if this is single fragment layout (small screen sizes)
	    if (findViewById(R.id.singleFragmentLayout) != null) {
	    	
            if (savedInstanceState != null) {
                return;
            }
            
    	    final ActionBar actionBar = getActionBar();
    	    // specify that action bar should display tabs
    	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    	    
    	    mAdapter = new FragmentAdapter( getSupportFragmentManager() );
    	    
            // Create fragments
            locationFragment = new LocationFragment();
            networkFragment = new NetworkFragment();
            
		    ArrayList<Fragment> fragments = new ArrayList<Fragment>(2);
		    fragments.add( locationFragment );
		    fragments.add( networkFragment );
		    mAdapter.setFragments( fragments );
		    
    	    mPager = (ViewPager) findViewById(R.id.pager);
    	    mPager.setAdapter( mAdapter );

			ActionBar.TabListener tabListener = new ActionBar.TabListener() {
				@Override
				public void onTabReselected(Tab tab,
						android.app.FragmentTransaction ft) {
				}

				@Override
				public void onTabSelected(Tab tab,
						android.app.FragmentTransaction ft) {
			        int position = tab.getPosition();
		            mPager.setCurrentItem(position);
				}

				@Override
				public void onTabUnselected(Tab tab,
						android.app.FragmentTransaction ft) {
				}
			};
			
			;

		    String[] tabs = {
		    		this.getString( R.string.locationInfo ), this.getString( R.string.networkInfo )
		    };
			
		    for (int i = 0; i < tabs.length; i++) {
		        actionBar.addTab(
		               actionBar.newTab()
		                .setText( tabs[i] )
		                .setTabListener( tabListener ));
		    }
	    
    	    // With swipe change the fragment
    	   mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
    	        @Override
    	        public void onPageSelected(int position) {
    	            actionBar.setSelectedNavigationItem(position);
    	        }
    	    });
		    

	    }
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
    	}
    	
    	if( null != locationFragment && locationFragment.isVisible() ){
        	// pass the message to the location fragment
        	locationFragment.updatecurrentLocation( message );
    	}
    }

	@Override
	public void notifyLocationProviderChanged( String event ) {
    	// TODO: or toast!
		if( null != locationFragment && locationFragment.isVisible() ){
        	// pass the message to the location fragment
        	locationFragment.locationProviderChanged ( event );
    	}
	}

	@Override
	public void socketConnectionStatusChanged( NetworkStatus status ) {
		if( null != networkFragment && networkFragment.isVisible() ){
        	// pass the message to the location fragment
			networkFragment.networkStatusChanged( status );
    	}
	}


	@Override
	public void messageReceived( String payload ) {
		// TODO: messages from server not ready yet
		if( null != networkFragment ){
        	
    	}
	}


}
